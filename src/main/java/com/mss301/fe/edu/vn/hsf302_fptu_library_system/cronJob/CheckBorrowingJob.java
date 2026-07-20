package com.mss301.fe.edu.vn.hsf302_fptu_library_system.cronJob;

import com.mss301.fe.edu.vn.hsf302_fptu_library_system.constant.EBookCopyStatus;
import com.mss301.fe.edu.vn.hsf302_fptu_library_system.constant.EBorrowHistoryStatus;
import com.mss301.fe.edu.vn.hsf302_fptu_library_system.constant.EBorrowRequestStatus;
import com.mss301.fe.edu.vn.hsf302_fptu_library_system.entity.BookCopy;
import com.mss301.fe.edu.vn.hsf302_fptu_library_system.entity.BorrowHistory;
import com.mss301.fe.edu.vn.hsf302_fptu_library_system.entity.BorrowRequest;
import com.mss301.fe.edu.vn.hsf302_fptu_library_system.repository.BookCopyRepository;
import com.mss301.fe.edu.vn.hsf302_fptu_library_system.repository.BorrowHistoryRepository;
import com.mss301.fe.edu.vn.hsf302_fptu_library_system.repository.BorrowRequestRepository;
import com.mss301.fe.edu.vn.hsf302_fptu_library_system.service.EmailService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
@Transactional
public class CheckBorrowingJob {
    private final BorrowHistoryRepository borrowHistoryRepository;
    private final EmailService emailService;

    @Value("${borrow.history.reminder}")
    private int reminder;

    @Scheduled(cron = "0 */3 * * * *")
    @Transactional
    public void checkBorrowDueDate() {
        log.info("Start checking borrowing due dates");
        LocalDate today = LocalDate.now();
        List<BorrowHistory> borrowingHistories = borrowHistoryRepository.findByStatus(EBorrowHistoryStatus.BORROWING);
        for (BorrowHistory history : borrowingHistories) {
            LocalDate dueDate = history.getDueDate().toLocalDate();
            // Đã quá hạn
            if (today.isAfter(dueDate)) {
                history.setStatus(EBorrowHistoryStatus.OVERDUE);
                borrowHistoryRepository.save(history);
                log.info("Borrow history {} marked as OVERDUE", history.getBorrowId());
                continue;
            }
            // Còn đúng 3 ngày đến hạn và chưa gửi mail
            if (!history.isReminderSent() && ChronoUnit.DAYS.between(today, dueDate) == reminder) {
                try {
                    emailService.sendReturnReminderEmail(
                            history.getUser().getEmail(),
                            history.getUser().getFullName(),
                            history.getCopy().getBook().getTitle(),
                            dueDate
                    );
                    history.setReminderSent(true);
                    borrowHistoryRepository.save(history);
                    log.info("Reminder email sent for borrow history {}", history.getBorrowId());
                } catch (Exception e) {
                    log.error("Failed to send reminder email for borrow history {}", history.getBorrowId(), e);
                }
            }
        }
        log.info("Finished checking borrowing due dates");
    }
}
