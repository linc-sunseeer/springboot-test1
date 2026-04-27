package com.example.demo.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.demo.entity.Reservation;
import com.example.demo.entity.User;
import com.example.demo.mapper.ReservationMapper;
import com.example.demo.mapper.UserMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 弁当予約リマインダー
 * 毎日締切前にまだ予約していない利用者へメールでリマインドする
 */
@Service
public class ReservationReminderService {

    private static final Logger log = LoggerFactory.getLogger(ReservationReminderService.class);

    private final UserMapper userMapper;
    private final ReservationMapper reservationMapper;
    private final MailService mailService;
    private final SystemSettingService systemSettingService;

    public ReservationReminderService(UserMapper userMapper,
                                      ReservationMapper reservationMapper,
                                      MailService mailService,
                                      SystemSettingService systemSettingService) {
        this.userMapper = userMapper;
        this.reservationMapper = reservationMapper;
        this.mailService = mailService;
        this.systemSettingService = systemSettingService;
    }

    /**
     * 平日朝9:00（または設定されたcron）に実行し、
     * 本日分をまだ予約していない利用者へリマインダーメールを送信する
     */
    @Scheduled(cron = "${app.mail.reminder-cron:0 0 9 * * MON-FRI}")
    public void sendDailyReservationReminders() {
        LocalDate today = LocalDate.now();
        int threshold = systemSettingService.getMinReservationThreshold();

        // 本日予約済みのユーザーIDを取得
        List<Reservation> todayReservations = reservationMapper.selectList(
                new LambdaQueryWrapper<Reservation>()
                        .eq(Reservation::getReservationDate, today)
                        .ne(Reservation::getOrderStatus, "CANCELLED"));

        Set<Long> reservedUserIds = todayReservations.stream()
                .map(Reservation::getUserId)
                .collect(Collectors.toSet());

        // 全ユーザー取得
        List<User> allUsers = userMapper.selectList(null);

        int sentCount = 0;
        for (User user : allUsers) {
            if (reservedUserIds.contains(user.getId())) {
                continue; // 予約済みはスキップ
            }
            if (user.getEmail() == null || user.getEmail().trim().isEmpty()) {
                continue; // メール未設定もスキップ
            }

            String cutoffTime = systemSettingService.getReservationCutoffTime().toString();
            int currentCount = todayReservations.size();
            int remaining = threshold - currentCount;
            String remainingText = remaining > 0
                    ? String.format("あと %d 件で成団確定です。", remaining)
                    : "成団済みです。";

            String body = user.getName() + " 様\n\n"
                    + "本日（" + today + "）の弁当予約締切は " + cutoffTime + " です。\n"
                    + "まだ予約されていないようですので、ご確認ください。\n\n"
                    + "現在の予約状況: " + currentCount + " / " + threshold + " 件\n"
                    + remainingText + "\n\n"
                    + "予約はこちらから:\n"
                    + "http://localhost:5174/menus/today";

            mailService.sendMail(user.getEmail(), "【リマインド】本日の弁当予約締切間近", body);
            sentCount++;
        }

        log.info("Daily reservation reminder sent to {} users. Today reservations: {}, Threshold: {}",
                sentCount, todayReservations.size(), threshold);
    }
}