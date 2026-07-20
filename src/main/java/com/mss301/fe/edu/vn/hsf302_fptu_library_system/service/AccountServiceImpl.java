package com.mss301.fe.edu.vn.hsf302_fptu_library_system.service;

import com.mss301.fe.edu.vn.hsf302_fptu_library_system.constant.EFilePurpose;
import com.mss301.fe.edu.vn.hsf302_fptu_library_system.constant.ERole;
import com.mss301.fe.edu.vn.hsf302_fptu_library_system.dto.AccountFormDto;
import com.mss301.fe.edu.vn.hsf302_fptu_library_system.dto.UploadResult;
import com.mss301.fe.edu.vn.hsf302_fptu_library_system.dto.UserProfileDto;
import com.mss301.fe.edu.vn.hsf302_fptu_library_system.entity.AppFile;
import com.mss301.fe.edu.vn.hsf302_fptu_library_system.entity.User;
import com.mss301.fe.edu.vn.hsf302_fptu_library_system.repository.UserRepository;
import com.mss301.fe.edu.vn.hsf302_fptu_library_system.util.CommonFunction;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
@RequiredArgsConstructor
@Transactional
public class AccountServiceImpl implements AccountService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final S3Service s3Service;
    private final CommonFunction commonFunction;

    @Override
    public Page<UserProfileDto> searchAccounts(
            String keyword,
            ERole role,
            Boolean status,
            int page,
            int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("userId").descending());
        return userRepository
                .searchAccounts(keyword, role, status, pageable)
                .map(this::toUserProfileDto);
    }

    @Override
    public void toggleAccountStatus(Integer userId) {
        //lấy taài khoản tu data lên
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy tài khoản này!"));
        
      //đảo ngược trạng thái,nếu bật thì tắt, nếu tắt thì bật
        user.setStatus(!user.getStatus());
        
        //lưu lại
        userRepository.save(user);
    }

    @Override
    public void saveAccount(AccountFormDto dto) {
        User user;
        if (dto.getUserId() == null) {
            if (userRepository.existsByCode(dto.getCode())) {
                throw new RuntimeException("Mã người dùng đã tồn tại");
            }
            if (userRepository.existsByEmail(dto.getEmail())) {
                throw new RuntimeException("Email đã được sử dụng");
            }
            if (userRepository.existsByPhone(dto.getPhone())) {
                throw new RuntimeException("Số điện thoại đã được sử dụng");
            }
            user = new User();
            user.setCode(dto.getCode().trim());
            user.setStatus(true);
            String newPassword = commonFunction.generateRandomPassword();
            user.setPassword(passwordEncoder.encode(newPassword));
        } else {
            user = userRepository.findById(dto.getUserId()).orElseThrow(() ->
                            new RuntimeException("Không tìm thấy tài khoản"));
            if (userRepository.existsByCodeAndUserIdNot(dto.getCode(), dto.getUserId())) {
                throw new RuntimeException("Mã người dùng đã tồn tại");
            }
            if (userRepository.existsByEmailAndUserIdNot(dto.getEmail(), dto.getUserId())) {
                throw new RuntimeException("Email đã được sử dụng");
            }
            if (userRepository.existsByPhoneAndUserIdNot(dto.getPhone(), dto.getUserId())) {
                throw new RuntimeException("Số điện thoại đã được sử dụng");
            }
            user.setCode(dto.getCode().trim());
        }
        user.setFullName(dto.getFullName().trim());
        user.setEmail(dto.getEmail().trim());
        user.setPhone(dto.getPhone());
        user.setAddress(dto.getAddress());
        user.setRole(dto.getRole());

        MultipartFile avatar = dto.getAvatarFile();
        if (avatar != null && !avatar.isEmpty()) {
            try {
                // Upload trước
                UploadResult upload = s3Service.uploadFile(avatar);
                // Upload thành công mới xóa ảnh cũ
                if (user.getAvatar() != null && user.getAvatar().getS3Key() != null) {
                    s3Service.deleteFileWithKey(user.getAvatar().getS3Key());
                }
                AppFile appFile = new AppFile();
                appFile.setFileName(avatar.getOriginalFilename());
                appFile.setFileUrl(upload.url());
                appFile.setS3Key(upload.key());
                String original = avatar.getOriginalFilename();
                if (original != null && original.contains(".")) {
                    appFile.setExtension(original.substring(original.lastIndexOf('.') + 1));
                }
                appFile.setPurpose(EFilePurpose.AVATAR);
                user.setAvatar(appFile);
            } catch (IOException e) {
                throw new RuntimeException("Không thể tải ảnh lên S3", e);
            } catch (Exception e) {
                throw new RuntimeException("Có lỗi xảy ra khi xử lý ảnh", e);
            }
        }
        userRepository.save(user);
    }

    @Override
    public AccountFormDto getAccountForEdit(Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy tài khoản"));
        AccountFormDto dto = new AccountFormDto();
        dto.setUserId(user.getUserId());
        dto.setCode(user.getCode());
        dto.setFullName(user.getFullName());
        dto.setEmail(user.getEmail());
        dto.setPhone(user.getPhone());
        dto.setAddress(user.getAddress());
        dto.setRole(user.getRole());
        dto.setStatus(user.getStatus());
        dto.setAvatarUrl(user.getAvatar() != null ? user.getAvatar().getFileUrl() : null);
        // Không set avatarFile vì MultipartFile chỉ dùng khi upload từ form
        return dto;
    }

    private UserProfileDto toUserProfileDto(User user) {
        return UserProfileDto.builder()
                .userId(user.getUserId())
                .fullName(user.getFullName())
                .code(user.getCode())
                .email(user.getEmail())
                .phone(user.getPhone())
                .address(user.getAddress())
                .role(user.getRole())
                .status(Boolean.TRUE.equals(user.getStatus()))
                .avatarUrl(user.getAvatar() != null ? user.getAvatar().getFileUrl() : null)
                .build();
    }
}
