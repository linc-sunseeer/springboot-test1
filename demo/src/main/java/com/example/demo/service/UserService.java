package com.example.demo.service;

// 匯入 LambdaQueryWrapper，這個是 MyBatis-Plus 查詢條件寫法
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.example.demo.entity.User;
import com.example.demo.entity.Admin;
import com.example.demo.entity.Reservation;
import com.example.demo.mapper.UserMapper;
import com.example.demo.mapper.AdminMapper;
import com.example.demo.mapper.ReservationMapper;
// 匯入 PasswordEncoder，現在密碼要做 hash / 比對 hash
import org.springframework.security.crypto.password.PasswordEncoder;
// 匯入 Service，讓 Spring 知道這是一個 service 類別
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

<<<<<<< Updated upstream
import java.util.Optional; //導入 Optional
=======
import java.time.LocalDateTime;
>>>>>>> Stashed changes
import java.util.List;

// 這個 Service 專門處理使用者資料與登入相關邏輯
@Service
public class UserService {

    private final UserMapper userMapper;
    private final AdminMapper adminMapper;
    private final ReservationMapper reservationMapper;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserMapper userMapper, AdminMapper adminMapper, ReservationMapper reservationMapper, PasswordEncoder passwordEncoder) {
        this.userMapper = userMapper;
        this.adminMapper = adminMapper;
        this.reservationMapper = reservationMapper;
        this.passwordEncoder = passwordEncoder;
    }

    // 查詢全部使用者
    public List<User> getAllUsers() {
        return userMapper.selectList(null);
    }

    // 建立新使用者
    public User createUser(User user) {
        if (user == null) {
            throw new IllegalArgumentException("空欄できません");
        }

        long count = userMapper.selectCount(new LambdaQueryWrapper<User>().eq(User::getEmail, user.getEmail()));
        if (count > 0) {
            throw new IllegalArgumentException("このメールアドレスは既に使用されています");
        }

        // 這裡會把明文密碼轉成 hash 後再存進資料庫
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setEmailVerified(Boolean.FALSE);
        user.setRole("USER");
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        userMapper.insert(user);
        return user;
    }

<<<<<<< Updated upstream
    //登入
    public User login(String email, String password){
=======
    // 一般使用者登入驗證
    public User login(String email, String password) {
        User user = userMapper.selectOne(new LambdaQueryWrapper<User>().eq(User::getEmail, email).last("limit 1"));
>>>>>>> Stashed changes

        Optional<User> optionalUser = userRepository.findByEmail(email);
        User user = optionalUser
            .orElseThrow(() -> new IllegalArgumentException("ユーザーが存在しません"));

        // 這裡不是比明文，而是拿輸入密碼去比對 hash
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new IllegalArgumentException("パスワードが正しくありません");
        }

        if (!Boolean.TRUE.equals(user.getEmailVerified())) {
            throw new IllegalArgumentException("メール認証完了後にログインできます。確認メールのリンクを開いてください。");
        }

        return user;
    }

<<<<<<< Updated upstream
    //刪除使用者

}
=======
    // 用 id 查某個使用者
    public User getUserById(Long userId) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new IllegalArgumentException("ユーザーが存在しません");
        }
        return user;
    }

    public User findByEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return null;
        }

        return userMapper.selectOne(new LambdaQueryWrapper<User>().eq(User::getEmail, email.trim()).last("limit 1"));
    }

    public List<User> getVerifiedUsers() {
        return userMapper.selectList(new LambdaQueryWrapper<User>().eq(User::getEmailVerified, true));
    }

    public User markEmailVerified(Long userId) {
        User user = getUserById(userId);
        user.setEmailVerified(Boolean.TRUE);
        user.setUpdatedAt(LocalDateTime.now());
        userMapper.updateById(user);
        return user;
    }

    // 修改使用者姓名
    public User updateUserName(Long userId, String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("氏名を入力してください");
        }

        User user = getUserById(userId);
        user.setName(name.trim());
        user.setUpdatedAt(LocalDateTime.now());
        userMapper.updateById(user);
        return user;
    }

    // 用 userId 修改密碼
    public User updatePassword(Long userId, String password) {
        if (password == null || password.trim().isEmpty()) {
            throw new IllegalArgumentException("パスワードを入力してください");
        }

        User user = getUserById(userId);
        if (!Boolean.TRUE.equals(user.getEmailVerified())) {
            throw new IllegalArgumentException("メール認証完了後にパスワード変更が可能です。");
        }
        user.setPassword(passwordEncoder.encode(password));
        user.setUpdatedAt(LocalDateTime.now());
        userMapper.updateById(user);
        return user;
    }

    // 用 email 修改密碼，主要是忘記密碼流程會用到
    public User updatePasswordByEmail(String email, String password) {
        User user = findByEmail(email);
        if (user == null) {
            throw new IllegalArgumentException("ユーザーが存在しません");
        }

        user.setPassword(passwordEncoder.encode(password));
        user.setUpdatedAt(LocalDateTime.now());
        userMapper.updateById(user);
        return user;
    }

    // 更新飲食偏好設定（目標熱量 + 過敏原）
    public User updateDietPreferences(Long userId, Integer targetCalorie, String allergenSettings) {
        User user = getUserById(userId);
        if (targetCalorie != null && targetCalorie < 0) {
            throw new IllegalArgumentException("目標熱量は0以上で入力してください");
        }
        user.setTargetCalorie(targetCalorie);
        user.setAllergenSettings(allergenSettings);
        user.setUpdatedAt(LocalDateTime.now());

        // 使用 UpdateWrapper 強制更新，確保 null 值也會寫入資料庫
        UpdateWrapper<User> wrapper = new UpdateWrapper<>();
        wrapper.eq("id", userId);
        wrapper.set("target_calorie", targetCalorie);
        wrapper.set("allergen_settings", allergenSettings);
        wrapper.set("updated_at", LocalDateTime.now());
        userMapper.update(null, wrapper);

        return user;
    }

    // 管理者パスワードを検証してからユーザーを削除（関連予約も削除）
    @Transactional
    public void deleteUserWithPasswordVerification(Long userId, String adminPassword) {
        // 管理者パスワードの検証 — いずれかの管理者アカウントに一致すればOK
        List<Admin> admins = adminMapper.selectList(null);
        boolean passwordMatch = false;
        for (Admin admin : admins) {
            if (passwordEncoder.matches(adminPassword, admin.getPassword())) {
                passwordMatch = true;
                break;
            }
        }
        if (!passwordMatch) {
            throw new IllegalArgumentException("管理者パスワードが正しくありません。");
        }

        User user = getUserById(userId);

        // 関連予約を先に削除
        reservationMapper.delete(new LambdaQueryWrapper<Reservation>().eq(Reservation::getUserId, userId));

        // ユーザーを削除
        userMapper.deleteById(userId);
    }
}
>>>>>>> Stashed changes
