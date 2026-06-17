package com.mss301.fe.edu.vn.hsf302_fptu_library_system.exception;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;

@Slf4j
@ControllerAdvice
public class GlobalViewExceptionHandler {
    @ExceptionHandler(Exception.class)
    public String handleException(
            Exception e,
            HttpServletRequest request,
            Model model
    ) {
        log.error("Unhandled exception", e);
        buildErrorModel(
                model,
                request,
                500,
                "INTERNAL_SERVER_ERROR",
                "Something went wrong",
                e.getMessage()
        );
        return "pages/error-page";
    }

    private void buildErrorModel(
            Model model,
            HttpServletRequest request,
            int status,
            String error,
            String message,
            String detail
    ) {
        model.addAttribute("timestamp", LocalDateTime.now());
        model.addAttribute("status", status);
        model.addAttribute("error", error);
        model.addAttribute("message", message);
        model.addAttribute("detail", detail);
        model.addAttribute("path", request.getRequestURI());
    }
}