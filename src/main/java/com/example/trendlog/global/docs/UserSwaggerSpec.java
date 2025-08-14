package com.example.trendlog.global.docs;

import com.example.trendlog.dto.request.user.PasswordChangeRequest;
import com.example.trendlog.dto.response.post.PostMapResponse;
import com.example.trendlog.dto.response.post.PostPagedResponse;
import com.example.trendlog.dto.response.user.UserInfoResponse;
import com.example.trendlog.dto.request.user.UserUpdateRequest;
import com.example.trendlog.global.dto.DataResponse;
import com.example.trendlog.global.dto.ErrorResponse;
import com.example.trendlog.global.security.userdetails.UserDetailsImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.util.List;

@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Users", description = "유저 관련 API")
public interface UserSwaggerSpec {
    @Operation(summary = "내 정보 조회", description = "로그인된 사용자의 정보를 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "내 정보 조회 성공"),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 회원 (USER-001)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자 (USER-011)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<DataResponse<UserInfoResponse>> getMyInfo(Principal principal);

    @Operation(summary = "회원 정보 수정", description = "사용자의 정보를 수정합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "회원 정보 수정 성공"),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 회원 (USER-001)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "400", description = "중복된 닉네임/ 유효하지 않은 사용자 요청 (USER-001/ USER-007/ USER-014)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자 (USER-011)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    })
    public ResponseEntity<DataResponse<Void>> updatUserInfo(Principal principal, @Valid @RequestBody UserUpdateRequest request);

    @Operation(summary = "비밀번호 수정", description = "새 비밀번호로 사용자 비밀번호를 변경합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "비밀번호 수정 성공"),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 회원(USER-001)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "400", description = "기존 비밀번호와 동일 (USER-015)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자 (USER-011)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    })
    public ResponseEntity<DataResponse<Void>> updatePassword(Principal principal, @Valid @RequestBody PasswordChangeRequest request);

    @Operation(summary = "회원 탈퇴", description = "현재 로그인한 사용자를 삭제합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "회원 탈퇴 성공"),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 회원 (USER-001)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "400", description = "이미 탈퇴한 사용자 (USER-013)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자 (USER-011)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    })
    public ResponseEntity<DataResponse<Void>> deleteUser(@AuthenticationPrincipal UserDetailsImpl userDetails);


    @Operation(
            summary = "프로필 이미지 업로드",
            description = "현재 url로 이미지를 업로드 합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "프로필 이미지 업로드 성공"),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 회원 (USER-001)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자 (USER-011)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "파일 업로드에 실패/ 파일 삭제 실패/ 파일 복사 실패(FILE-001/ FILE-002/ FILE-003)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    })
    public ResponseEntity<DataResponse<String>> uploadProfileImage(Principal principal,
                                                                   @Parameter(
                                                                           name = "file",
                                                                           description = "업로드할 이미지 파일",
                                                                           required = true,
                                                                           content = @Content(
                                                                                   mediaType = MediaType.APPLICATION_OCTET_STREAM_VALUE,
                                                                                   schema = @Schema(type = "string", format = "binary")
                                                                           )
                                                                   )
                                                                   @RequestPart("file") MultipartFile file);

    @Operation(summary = "내 게시글 지역별 마커 조회", description = "사용자가 작성한 게시글들을 서울 안의 지역(구)별로 묶어 마커 정보를 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "내 게시글 지역별 마커 조회 성공"),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 회원 (USER-001)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자(USER-011)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<DataResponse<List<PostMapResponse>>> getMyPostMarkersInArea(Principal principal);


    @Operation(summary = "내 게시글 목록 조회", description = "사용자가 작성한 게시글 목록을 조회합니다. 지역 필터링(district)과 페이지네이션을 지원합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "내 게시글 목록 조회 성공"),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 회원 (USER-001)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자(USER-011)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<DataResponse<PostPagedResponse>> getMyPostList(
            Principal principal,
            @RequestParam(defaultValue = "all") String district,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size
    );
}
