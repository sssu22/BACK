package com.example.trendlog.controller;

import com.example.trendlog.dto.request.user.PasswordChangeRequest;
import com.example.trendlog.dto.response.user.UserInfoResponse;
import com.example.trendlog.dto.request.user.UserUpdateRequest;
import com.example.trendlog.global.docs.UserSwaggerSpec;
import com.example.trendlog.global.dto.DataResponse;
import com.example.trendlog.service.GCSService;
import com.example.trendlog.service.RefreshTokenService;
import com.example.trendlog.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController implements UserSwaggerSpec {
    private final UserService userService;
    private final GCSService gcsService;
    private final RefreshTokenService refreshTokenService;
    @GetMapping("/me")
    public ResponseEntity<DataResponse<UserInfoResponse>> getMyInfo(Principal principal){
        return ResponseEntity.ok(DataResponse.from(userService.getMyInfo(principal)));
    }

    @PatchMapping("/me")
    public ResponseEntity<DataResponse<Void>> updatUserInfo(Principal principal, @Valid @RequestBody UserUpdateRequest request){
        userService.updateUserInfo(principal, request);
        return ResponseEntity.ok(DataResponse.ok());
    }
    @PatchMapping("/password")
    public ResponseEntity<DataResponse<Void>> updatePassword(Principal principal, @Valid @RequestBody PasswordChangeRequest request){
        userService.changePassword(principal,request.getNewPassword());
        return ResponseEntity.ok(DataResponse.ok());
    }

    @DeleteMapping("/me")
    public ResponseEntity<DataResponse<Void>> deleteUser(Principal principal){
        userService.deleteUser(principal);
        refreshTokenService.deleteRefreshToken(principal.getName());
        return ResponseEntity.ok(DataResponse.ok());
    }

//    @PostMapping("/profile-image")
@PostMapping(value = "/profile-image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<DataResponse<String>> uploadProfileImage(Principal principal, @RequestParam("file") MultipartFile file){
//        String imageUrl=userService.updateProfileImage(principal,file);
        String imageUrl = userService.uploadTempProfileImage(principal, file);
        return ResponseEntity.ok(DataResponse.from(imageUrl));
    }

}
