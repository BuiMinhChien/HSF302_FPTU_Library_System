package com.mss301.fe.edu.vn.hsf302_fptu_library_system.cronJob;

import com.mss301.fe.edu.vn.hsf302_fptu_library_system.constant.EBookCopyStatus;
import com.mss301.fe.edu.vn.hsf302_fptu_library_system.constant.EBorrowRequestStatus;
import com.mss301.fe.edu.vn.hsf302_fptu_library_system.entity.BookCopy;
import com.mss301.fe.edu.vn.hsf302_fptu_library_system.entity.BorrowRequest;
import com.mss301.fe.edu.vn.hsf302_fptu_library_system.repository.BookCopyRepository;
import com.mss301.fe.edu.vn.hsf302_fptu_library_system.repository.BorrowRequestRepository;
import com.mss301.fe.edu.vn.hsf302_fptu_library_system.service.EmailService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
@Transactional
public class BorrowExpirationJob {
    private final BorrowRequestRepository borrowRequestRepository;
    private final BookCopyRepository bookCopyRepository;
    private final EmailService emailService;

    @Scheduled(cron = "0 */3 * * * *")
    public void expireWaitingRequests() {
        log.info("Start checking expired waiting requests");
        LocalDateTime expiredThreshold = LocalDateTime.now().minusDays(3);
        List<BorrowRequest> waitingRequests =
                borrowRequestRepository.findByStatusAndUpdatedAtBefore(
                        EBorrowRequestStatus.WAITING,
                        expiredThreshold
                );
        for (BorrowRequest request : waitingRequests) {
            request.setStatus(EBorrowRequestStatus.EXPIRED);
            BookCopy reservedCopy = request.getReservedCopy();
            if (reservedCopy != null) {
                reservedCopy.setStatus(EBookCopyStatus.AVAILABLE);
                bookCopyRepository.save(reservedCopy);
            }
            borrowRequestRepository.save(request);
            log.info(
                    "Request {} expired after 3 days waiting",
                    request.getRequestId()
            );
            try {
                emailService.sendHoldExpiredEmail(
                        request.getUser().getEmail(),
                        request.getUser().getFullName(),
                        request.getBook().getTitle()
                );
            } catch (Exception e) {
                throw new RuntimeException("Send mail failed");
            }
        }
        log.info("Finished checking expired waiting requests");
    }
}
