package com.example.demo;

import com.example.demo.entity.User;
import com.example.demo.mapper.UserMapper;
import com.example.demo.mapper.AdminMapper;
import com.example.demo.mapper.ReservationMapper;
import com.example.demo.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceHashingTest {

    @Mock
    private UserMapper userMapper;

    @Mock
    private AdminMapper adminMapper;

    @Mock
    private ReservationMapper reservationMapper;

    private UserService userService;
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        passwordEncoder = new BCryptPasswordEncoder();
        userService = new UserService(userMapper, adminMapper, reservationMapper, passwordEncoder);
    }

    @Test
    void hashesPasswordWhenCreatingUser() {
        User user = new User();
        user.setName("Taro");
        user.setEmail("taro@example.com");
        user.setPassword("plain-password");

        when(userMapper.selectCount(any())).thenReturn(0L);

        userService.createUser(user);

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userMapper).insert(captor.capture());
        assertFalse("plain-password".equals(captor.getValue().getPassword()));
        assertTrue(passwordEncoder.matches("plain-password", captor.getValue().getPassword()));
    }

    @Test
    void hashesPasswordWhenUpdatingPassword() {
        User user = new User();
        user.setId(1L);
        user.setPassword("old-password");
        user.setEmailVerified(Boolean.TRUE);

        when(userMapper.selectById(1L)).thenReturn(user);

        userService.updatePassword(1L, "new-password");

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userMapper).updateById(captor.capture());
        assertFalse("new-password".equals(captor.getValue().getPassword()));
        assertTrue(passwordEncoder.matches("new-password", captor.getValue().getPassword()));
    }

    @Test
    void rejectsPasswordUpdateWhenEmailNotVerified() {
        User user = new User();
        user.setId(1L);
        user.setPassword("old-password");
        user.setEmailVerified(Boolean.FALSE);

        when(userMapper.selectById(1L)).thenReturn(user);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> userService.updatePassword(1L, "new-password"));

        assertEquals("メール認証完了後にパスワード変更が可能です。", exception.getMessage());
    }

    @Test
    void loginMatchesHashedPassword() {
        User user = new User();
        user.setEmail("user@example.com");
        user.setPassword(passwordEncoder.encode("password123"));
        user.setEmailVerified(Boolean.TRUE);

        when(userMapper.selectOne(any())).thenReturn(user);

        User result = userService.login("user@example.com", "password123");

        assertSame(user, result);
    }

    @Test
    void rejectsLoginWhenEmailNotVerified() {
        User user = new User();
        user.setEmail("user@example.com");
        user.setPassword(passwordEncoder.encode("password123"));
        user.setEmailVerified(Boolean.FALSE);

        when(userMapper.selectOne(any())).thenReturn(user);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> userService.login("user@example.com", "password123"));

        assertEquals("メール認証完了後にログインできます。確認メールのリンクを開いてください。", exception.getMessage());
    }
}
