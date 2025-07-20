package com.example.trendlog.service;

import com.example.trendlog.domain.User;
import com.example.trendlog.dto.response.user.UserInfoResponse;
import com.example.trendlog.dto.request.user.UserUpdateRequest;
import com.example.trendlog.global.exception.AppException;
import com.example.trendlog.global.exception.code.UserErrorCode;
import com.example.trendlog.global.exception.user.UserNotFoundException;
import com.example.trendlog.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.ConversionService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final GCSService gcsService;
    private final ConversionService conversionService;

    public User getUserByEmail(String email) {
        if (email == null || email.isBlank()) {
            throw new AppException(UserErrorCode.UNAUTHORIZED_USER); // USER-011
        }
        return userRepository.findByEmail(email)
                .orElseThrow(UserNotFoundException::new); // USER-001
    }

    //내 정보 조회
    public UserInfoResponse getMyInfo(Principal principal) {
        User user=getUserByEmail(principal.getName());
        return UserInfoResponse.from(user);
    }

    //회원탈퇴
    @Transactional
    public void deleteUser(Principal principal) {
        User user=getUserByEmail(principal.getName());
        if ("deleted".equals(user.getProvider())) {
            throw new AppException(UserErrorCode.ALREADY_DELETED_USER); // USER-013
        }
        userRepository.delete(user);
    }

    //회원 정보 수정
    @Transactional
    public void updateUserInfo(Principal principal, UserUpdateRequest request) {
        User user=getUserByEmail(principal.getName());
        // 모든 필드가 null일 경우
        if (request.getName() == null &&
                request.getStateMessage() == null &&
                request.getBirth() == null &&
                request.getAddress() == null &&
                request.getLocationTracing() == null &&
                request.getProfileImage() == null) {
            throw new AppException(UserErrorCode.INVALID_PROFILE_UPDATE); // USER-014
        }
        if (request.getName() != null) {
            if (!request.getName().equals(user.getName()) &&
                    userRepository.existsByName(request.getName())) {
                throw new AppException(UserErrorCode.DUPLICATE_NICKNAME); // USER-007
            }
            user.setName(request.getName());
        }
        if (request.getStateMessage() != null) user.setStateMessage(request.getStateMessage());
        if (request.getBirth() != null) user.setBirth(request.getBirth());
        if (request.getAddress() != null) user.setAddress(request.getAddress());
        if (request.getLocationTracing() != null) user.setLocationTracing(request.getLocationTracing());
        if (request.getProfileImage() != null){
            String newUrl = request.getProfileImage().trim();

            if (user.getProfileImage() != null && !user.getProfileImage().equals(newUrl)) {
                gcsService.deleteFile(gcsService.convertToRelativePath(user.getProfileImage()));
            }

            String newRelativePath = gcsService.convertToRelativePath(newUrl);

            if (newRelativePath.startsWith("temp/profile/")) {
                String finalRelativePath = "profiles/" + user.getId() + "_" + System.currentTimeMillis();
                gcsService.copyFile(newRelativePath, finalRelativePath);
                gcsService.deleteFile(newRelativePath);
                String finalUrl = "https://storage.googleapis.com/" + gcsService.getBucketName() + "/" + finalRelativePath;
                user.setProfileImage(finalUrl);
            } else {
                user.setProfileImage(newUrl);
            }
        }
    }

    //비밀번호 변경
    @Transactional
    public void changePassword(Principal principal, String newPassword) {
        User user=getUserByEmail(principal.getName());
        if (passwordEncoder.matches(newPassword, user.getPassword())) {
            throw new AppException(UserErrorCode.SAME_AS_OLD_PASSWORD); // USER-015
        }

        user.updatePassword(passwordEncoder.encode(newPassword));
    }
    public String uploadTempProfileImage(Principal principal, MultipartFile file) {
        UUID userId = getUserByEmail(principal.getName()).getId();
        return gcsService.uploadToTemp(file, userId);
    }

}
