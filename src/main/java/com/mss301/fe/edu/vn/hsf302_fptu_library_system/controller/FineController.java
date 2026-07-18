package com.mss301.fe.edu.vn.hsf302_fptu_library_system.controller;

import com.mss301.fe.edu.vn.hsf302_fptu_library_system.dto.FineCreateRequest;
import com.mss301.fe.edu.vn.hsf302_fptu_library_system.dto.FinePaymentDTO;
import com.mss301.fe.edu.vn.hsf302_fptu_library_system.dto.FinePaymentHistorySearchRequest;
import com.mss301.fe.edu.vn.hsf302_fptu_library_system.dto.FinePaymentHistoryViewDTO;
import com.mss301.fe.edu.vn.hsf302_fptu_library_system.dto.FineSearchRequest;
import com.mss301.fe.edu.vn.hsf302_fptu_library_system.dto.FineViewDTO;
import com.mss301.fe.edu.vn.hsf302_fptu_library_system.service.FinePaymentService;
import com.mss301.fe.edu.vn.hsf302_fptu_library_system.service.FineService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Map;

@Controller
@RequiredArgsConstructor
public class FineController {

    private final FineService fineService;
    private final FinePaymentService finePaymentService;

    @GetMapping("/reader/fines")
    public String listFines(@ModelAttribute FineSearchRequest search, Model model) {
        Page<FineViewDTO> finePage = fineService.getFinesForCurrentReader(search);
        model.addAttribute("finePage", finePage);
        model.addAttribute("fines", finePage.getContent());
        model.addAttribute("search", search);
        return "pages/fine-list";
    }

    @GetMapping("/reader/fines/payments")
    public String paymentHistory(@ModelAttribute FinePaymentHistorySearchRequest search, Model model) {
        var paymentPage = finePaymentService.getPaymentHistoryForCurrentReader(search);
        model.addAttribute("paymentPage", paymentPage);
        model.addAttribute("payments", paymentPage.getContent());
        model.addAttribute("search", search);
        return "pages/fine-payment-history";
    }

    @GetMapping("/reader/fines/{fineId}/pay")
    public String payFine(@PathVariable Integer fineId, Model model, RedirectAttributes redirectAttributes) {
        try {
            model.addAttribute("payment", finePaymentService.prepareCheckout(fineId));
            return "pages/fine-payment-checkout";
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
            return "redirect:/reader/fines";
        }
    }

    @GetMapping("/reader/fines/pay/checkout")
    public String checkout(@RequestParam Long orderCode, Model model, RedirectAttributes redirectAttributes) {
        try {
            model.addAttribute("payment", finePaymentService.getCheckoutInfo(orderCode));
            return "pages/fine-payment-checkout";
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
            return "redirect:/reader/fines";
        }
    }

    @GetMapping("/reader/fines/{fineId}/pay/init")
    @ResponseBody
    public ResponseEntity<?> initPayment(@PathVariable Integer fineId) {
        try {
            return ResponseEntity.ok(finePaymentService.prepareCheckout(fineId));
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body(Map.of("message", ex.getMessage()));
        }
    }

    @GetMapping("/reader/fines/pay/status")
    @ResponseBody
    public FinePaymentDTO paymentStatus(@RequestParam Long orderCode) {
        return finePaymentService.syncPaymentStatus(orderCode);
    }

    @PostMapping("/reader/fines/pay/complete")
    public String completePayment(@RequestParam Long orderCode, RedirectAttributes redirectAttributes) {
        try {
            return "redirect:/payment/payos/success?orderCode=" + orderCode;
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
            return "redirect:/reader/fines";
        }
    }

    @PostMapping("/reader/fines/pay/complete-json")
    @ResponseBody
    public FinePaymentDTO completePaymentJson(@RequestParam Long orderCode) {
        return finePaymentService.handleSuccess(orderCode);
    }

    @GetMapping("/librarian/fines/manage")
    public String manageFines(@ModelAttribute FineSearchRequest search, Model model) {
        Page<FineViewDTO> finePage = fineService.getAllFines(search);
        model.addAttribute("finePage", finePage);
        model.addAttribute("fines", finePage.getContent());
        model.addAttribute("search", search);
        return "pages/fine-manage";
    }

    @GetMapping("/librarian/fines/create")
    public String showCreateForm(Model model) {
        if (!model.containsAttribute("fineCreateRequest")) {
            model.addAttribute("fineCreateRequest", new FineCreateRequest());
        }
        model.addAttribute("borrowOptions", fineService.getBorrowOptionsForCreateFine());
        return "pages/fine-create";
    }

    @PostMapping("/librarian/fines/create")
    public String createFine(
            @Valid @ModelAttribute("fineCreateRequest") FineCreateRequest request,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes
    ) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("borrowOptions", fineService.getBorrowOptionsForCreateFine());
            return "pages/fine-create";
        }
        try {
            fineService.createFine(request);
            redirectAttributes.addFlashAttribute("success", "Tạo phiếu phạt thành công");
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/librarian/fines/create";
    }
}
