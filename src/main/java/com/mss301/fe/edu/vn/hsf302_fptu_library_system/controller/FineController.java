package com.mss301.fe.edu.vn.hsf302_fptu_library_system.controller;

import com.mss301.fe.edu.vn.hsf302_fptu_library_system.dto.FineCreateRequest;
import com.mss301.fe.edu.vn.hsf302_fptu_library_system.service.FinePaymentService;
import com.mss301.fe.edu.vn.hsf302_fptu_library_system.service.FineService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
public class FineController {

    private final FineService fineService;
    private final FinePaymentService finePaymentService;

    @GetMapping("/reader/fines")
    public String listFines(Model model) {
        model.addAttribute("fines", fineService.getFinesForCurrentReader());
        return "pages/fine-list";
    }

    @GetMapping("/reader/fines/{fineId}/pay")
    public String payFine(@PathVariable Integer fineId, RedirectAttributes redirectAttributes) {
        try {
            String checkoutUrl = finePaymentService.initiatePayment(fineId);
            return "redirect:" + checkoutUrl;
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
            return "redirect:/reader/fines";
        }
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
