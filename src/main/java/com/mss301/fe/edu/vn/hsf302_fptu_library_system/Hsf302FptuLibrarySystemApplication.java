package com.mss301.fe.edu.vn.hsf302_fptu_library_system;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableJpaAuditing(auditorAwareRef = "auditAwareImpl")
@EnableScheduling
public class Hsf302FptuLibrarySystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(Hsf302FptuLibrarySystemApplication.class, args);
    }

}
