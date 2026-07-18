package com.mss301.fe.edu.vn.hsf302_fptu_library_system.service;

import com.mss301.fe.edu.vn.hsf302_fptu_library_system.constant.EFineStatus;
import com.mss301.fe.edu.vn.hsf302_fptu_library_system.dto.BorrowHistoryOptionDTO;
import com.mss301.fe.edu.vn.hsf302_fptu_library_system.dto.FineCreateRequest;
import com.mss301.fe.edu.vn.hsf302_fptu_library_system.dto.FineSearchRequest;
import com.mss301.fe.edu.vn.hsf302_fptu_library_system.dto.FineViewDTO;
import com.mss301.fe.edu.vn.hsf302_fptu_library_system.entity.BorrowHistory;
import com.mss301.fe.edu.vn.hsf302_fptu_library_system.entity.Fine;
import com.mss301.fe.edu.vn.hsf302_fptu_library_system.entity.User;
import com.mss301.fe.edu.vn.hsf302_fptu_library_system.repository.BorrowHistoryRepository;
import com.mss301.fe.edu.vn.hsf302_fptu_library_system.repository.FineRepository;
import com.mss301.fe.edu.vn.hsf302_fptu_library_system.util.CommonFunction;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class FineServiceImpl implements FineService {

    private final FineRepository fineRepository;
    private final BorrowHistoryRepository borrowHistoryRepository;
    private final CommonFunction commonFunction;

    @Override
    @Transactional
    public Page<FineViewDTO> getFinesForCurrentReader(FineSearchRequest request) {
        User reader = commonFunction.getCurrentUser();
        Pageable pageable = PageRequest.of(
                request.getPage(),
                request.getSize(),
                Sort.by(Sort.Direction.DESC, "createdAt")
        );
        return fineRepository.searchByUser(
                        reader.getUserId(),
                        request.getBookTitle(),
                        request.getStatus(),
                        pageable
                )
                .map(this::toViewDTO);
    }

    @Override
    @Transactional
    public Page<FineViewDTO> getAllFines(FineSearchRequest request) {
        Pageable pageable = PageRequest.of(
                request.getPage(),
                request.getSize(),
                Sort.by(Sort.Direction.DESC, "createdAt")
        );
        return fineRepository.search(
                        request.getBookTitle(),
                        request.getFullName(),
                        request.getStudentCode(),
                        request.getStatus(),
                        pageable
                )
                .map(this::toViewDTO);
    }

    @Override
    public void createFine(FineCreateRequest request) {
        BorrowHistory borrow = borrowHistoryRepository.findById(request.getBorrowRecordId())
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy phiếu mượn"));

        if (fineRepository.existsByBorrowHistory_BorrowIdAndDeleteFlagFalse(borrow.getBorrowId())) {
            throw new IllegalArgumentException("Phiếu mượn này đã có khoản phạt");
        }

        Fine fine = Fine.builder()
                .borrowHistory(borrow)
                .user(borrow.getUser())
                .amount(request.getAmount())
                .reason(request.getReason())
                .status(EFineStatus.UNPAID)
                .build();
        fineRepository.save(fine);
    }

    @Override
    @Transactional
    public List<BorrowHistoryOptionDTO> getBorrowOptionsForCreateFine() {
        return borrowHistoryRepository.findReturnedWithoutFine().stream()
                .map(this::toBorrowOption)
                .toList();
    }

    @Override
    @Transactional
    public Fine getFineForPayment(Integer fineId) {
        return fineRepository.findByFineIdWithUser(fineId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy khoản phạt"));
    }

    @Override
    public void markFineAsPaid(Integer fineId) {
        Fine fine = getFineForPayment(fineId);
        fine.setStatus(EFineStatus.PAID);
        fineRepository.save(fine);
    }

    private FineViewDTO toViewDTO(Fine fine) {
        String bookTitle = "";
        if (fine.getBorrowHistory() != null
                && fine.getBorrowHistory().getCopy() != null
                && fine.getBorrowHistory().getCopy().getBook() != null) {
            bookTitle = fine.getBorrowHistory().getCopy().getBook().getTitle();
        }

        return FineViewDTO.builder()
                .fineId(fine.getFineId())
                .borrowId(fine.getBorrowHistory() != null ? fine.getBorrowHistory().getBorrowId() : null)
                .readerName(fine.getUser() != null ? fine.getUser().getFullName() : "")
                .readerCode(fine.getUser() != null ? fine.getUser().getCode() : "")
                .bookTitle(bookTitle)
                .reason(fine.getReason())
                .amount(fine.getAmount())
                .status(fine.getStatus())
                .statusLabel(fine.getStatus() == EFineStatus.PAID ? "Đã thanh toán" : "Chưa thanh toán")
                .createdAt(fine.getCreatedAt())
                .canPay(fine.getStatus() == EFineStatus.UNPAID)
                .build();
    }

    private BorrowHistoryOptionDTO toBorrowOption(BorrowHistory bh) {
        String bookTitle = bh.getCopy() != null && bh.getCopy().getBook() != null
                ? bh.getCopy().getBook().getTitle()
                : "Không rõ sách";
        String readerName = bh.getUser() != null ? bh.getUser().getFullName() : "Không rõ";
        String readerCode = bh.getUser() != null ? bh.getUser().getCode() : "";

        return BorrowHistoryOptionDTO.builder()
                .borrowId(bh.getBorrowId())
                .label(String.format("#%d - %s (%s) - %s", bh.getBorrowId(), bookTitle, readerCode, readerName))
                .build();
    }
}
