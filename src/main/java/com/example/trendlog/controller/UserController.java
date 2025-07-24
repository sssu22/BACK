package com.example.trendlog.controller;

import com.example.trendlog.dto.request.user.PasswordChangeRequest;
import com.example.trendlog.dto.response.post.PostMapResponse;
import com.example.trendlog.dto.response.post.PostPagedResponse;
import com.example.trendlog.dto.response.user.UserInfoResponse;
import com.example.trendlog.dto.request.user.UserUpdateRequest;
import com.example.trendlog.global.docs.UserSwaggerSpec;
import com.example.trendlog.global.dto.DataResponse;
import com.example.trendlog.service.GCSService;
import com.example.trendlog.service.PostService;
import com.example.trendlog.service.RefreshTokenService;
import com.example.trendlog.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController implements UserSwaggerSpec {
    private final UserService userService;
    private final GCSService gcsService;
    private final RefreshTokenService refreshTokenService;
    private final PostService postService;

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


    @Operation(summary = "내 게시글 지역별 마커 조회", description = "사용자가 작성한 게시글들을 서울 안의 지역(구)별로 묶어 마커 정보를 조회합니다.")
    @GetMapping("me/posts/map")
    public ResponseEntity<DataResponse<List<PostMapResponse>>> getMyPostMarkersInArea(Principal principal) {
        return ResponseEntity.ok(DataResponse.from(postService.getPostMarkersInArea(principal)));
    }

    @Operation(summary = "내 게시글 목록 조회", description = "사용자가 작성한 게시글 목록을 조회합니다. 지역 필터링(district)과 페이지네이션을 지원합니다.")
    @GetMapping("me/posts")
    public ResponseEntity<DataResponse<PostPagedResponse>> getMyPostList(
            Principal principal,
            @RequestParam(defaultValue = "all") String district,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(DataResponse.from(postService.getMyPostList(principal, district, page, size)));
    }

}
