package com.mss301.fe.edu.vn.hsf302_fptu_library_system.cronJob;

import com.mss301.fe.edu.vn.hsf302_fptu_library_system.constant.EBookCopyStatus;
import com.mss301.fe.edu.vn.hsf302_fptu_library_system.constant.EBorrowRequestStatus;
import com.mss301.fe.edu.vn.hsf302_fptu_library_system.entity.BookCopy;
import com.mss301.fe.edu.vn.hsf302_fptu_library_system.entity.BorrowRequest;
import com.mss301.fe.edu.vn.hsf302_fptu_library_system.repository.BookCopyRepository;
import com.mss301.fe.edu.vn.hsf302_fptu_library_system.repository.BorrowRequestRepository;
import com.mss301.fe.edu.vn.hsf302_fptu_library_system.service.EmailService;
import jakarta.transaction.Transactional;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
@Transactional
public class BorrowAssignmentJob {
    private final BorrowRequestRepository borrowRequestRepository;
    private final BookCopyRepository bookCopyRepository;
    private final EmailService emailService;

    @Scheduled(cron = "0 */3 * * * *")
    public void assignBooksToApprovedRequests() {
        log.info("Start borrow assignment job");
        List<BorrowRequest> requests = borrowRequestRepository.findByStatusOrderByCreatedAtAsc(EBorrowRequestStatus.APPROVED);
        for (BorrowRequest request : requests) {
            Optional<BookCopy> availableCopy = bookCopyRepository
                            .findFirstByBookAndStatusAndDeleteFlagFalse(
                                    request.getBook(),
                                    EBookCopyStatus.AVAILABLE
                            );
            if (availableCopy.isEmpty()) {
                continue;
            }
            BookCopy copy = availableCopy.get();
            // cập nhật copy
            copy.setStatus(EBookCopyStatus.RESERVED);
            // cập nhật request
            request.setReservedCopy(copy);
            request.setStatus(EBorrowRequestStatus.WAITING);
            bookCopyRepository.save(copy);
            borrowRequestRepository.save(request);
            try {
                emailService.sendWaitingBookNotification(
                        request.getUser().getEmail(),
                        request.getUser().getFullName(),
                        request.getBook().getTitle()
                );
            } catch (Exception e) {
                log.error("Send mail failed", e);
            }
            log.info("Assigned copy {} to request {}",
                    copy.getCopyId(),
                    request.getRequestId());
        }
        log.info("Borrow assignment job finished");
    }
}
