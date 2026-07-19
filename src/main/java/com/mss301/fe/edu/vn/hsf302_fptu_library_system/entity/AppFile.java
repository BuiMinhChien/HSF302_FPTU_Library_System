package com.mss301.fe.edu.vn.hsf302_fptu_library_system.entity;

import com.mss301.fe.edu.vn.hsf302_fptu_library_system.constant.EFilePurpose;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@Entity
@Table(name = "app_files")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
@Slf4j
public class AppFile extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "file_id")
    Long fileId;

    @Column(name = "file_name", nullable = false, columnDefinition = "NVARCHAR(255)")
    String fileName;

    @Column(name = "s3_key", nullable = true, length = 500)
    private String s3Key;

    @Column(name = "file_url", nullable = false, length = 500)
    String fileUrl;

    @Column(name = "extension", length = 20)
    String extension;

    @Enumerated(EnumType.STRING)
    @Column(name = "purpose", length = 50)
    EFilePurpose purpose;
}
