package com.example.trendlog.global.docs;

import com.example.trendlog.dto.request.post.PostCommentRequest;
import com.example.trendlog.dto.request.post.PostCreateUpdateRequest;
import com.example.trendlog.dto.response.post.PostPagedResponse;
import com.example.trendlog.dto.response.post.PostPopularPagedResponse;
import com.example.trendlog.dto.response.post.PostResponse;
import com.example.trendlog.global.dto.DataResponse;
import com.example.trendlog.global.dto.ErrorResponse;
import io.micrometer.common.lang.Nullable;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@Tag(name = "Posts", description = "게시글(경험) 관련 API")
public interface PostSwaggerSpec {

    @Operation(summary = "게시글 작성", description = "새로운 게시글을 작성합니다. 현재 존재하는 감정은 JOY, EXCITEMENT, NOSTALGIA, SURPRISE, LOVE 입니다.")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "게시글 작성 성공"),
            @ApiResponse(responseCode = "400", description = "빈 필드 존재(POST-004)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 회원 (USER-001)/존재하지 않는 트렌드(TREND-002)/존재하지 않는 감정(POST-001)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자(USER-011)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류 (COMMON-005)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<DataResponse<Void>> createPost(
            Principal principal,
            @RequestBody PostCreateUpdateRequest request);


    @Operation(summary = "게시글 목록 조회"
            , description = "정렬 및 감정 필터와 페이지네이션을 통해 게시글 목록을 조회합니다.\n " +
            "현재 존재하는 감정은 JOY, EXCITEMENT, NOSTALGIA, SURPRISE, LOVE 입니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "게시글 목록 조회 성공")
    })
    public ResponseEntity<DataResponse<PostPagedResponse>> getPostList(
            @RequestParam(defaultValue = "latest") String sort,
            @RequestParam(defaultValue = "all") String emotion,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size
    );


    @Operation(summary = "게시글 상세 조회", description = "특정 게시글의 상세 정보를 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "게시글 상세 조회 성공"),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 회원 (USER-001)/존재하지 않는 게시글(POST-002)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<DataResponse<PostResponse>> getPostDetail(
            @Nullable Principal principal,
            @PathVariable Long postId
    );


    @Operation(summary = "인기 게시글 목록 조회", description = "(지금 뜨는 경험) 일주일 내로 만들어진 게시글 중에서 좋아요 + 스크랩 수 높은 순 자정마다 업데이트되는 인기 게시글을 페이지 단위로 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "인기 게시글 목록 조회 성공")
    })
    public ResponseEntity<DataResponse<PostPopularPagedResponse>> getPopularPosts(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size
    );


    @Operation(summary = "게시글 수정", description = "기존 게시글의 내용을 수정합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "게시글 수정 성공"),
            @ApiResponse(responseCode = "400", description = "빈 필드 존재(POST-004)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "작성자 아님(POST-003)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 회원 (USER-001)/존재하지 않는 트렌드(TREND-002)/존재하지 않는 감정(POST-001)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자(USER-011)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류 (COMMON-005)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<DataResponse<Void>> updatePost(
            Principal principal,
            @PathVariable Long postId,
            @RequestBody PostCreateUpdateRequest request
    );


    @Operation(summary = "게시글 삭제", description = "게시글을 삭제합니다. (소프트 삭제)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "게시글 삭제 성공"),
            @ApiResponse(responseCode = "403", description = "작성자 아님(POST-003)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 회원 (USER-001)/존재하지 않는 게시글(POST-002)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자(USER-011)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<DataResponse<Void>> deletePost(
            Principal principal,
            @PathVariable Long postId
    );


    @Operation(summary = "게시글 좋아요", description = "게시글에 좋아요를 누릅니다. 다시 누르면 취소됩니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "게시글 좋아요 성공"),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 회원 (USER-001)/존재하지 않는 게시글(POST-002)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자(USER-011)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<DataResponse<Void>> likePost(
            Principal principal,
            @PathVariable Long postId
    );


    @Operation(summary = "게시글 스크랩", description = "게시글을 스크랩합니다. 다시 누르면 취소됩니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "게시글 스크랩 성공"),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 회원 (USER-001)/존재하지 않는 게시글(POST-002)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자(USER-011)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<DataResponse<Void>> scrapPost(
            Principal principal,
            @PathVariable Long postId
    );


    @Operation(summary = "게시글 댓글 작성", description = "게시글에 댓글을 작성합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "게시글의 댓글 작성 성공"),
            @ApiResponse(responseCode = "400", description = "빈 필드 존재(POST-004)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 회원 (USER-001)/존재하지 않는 게시글(POST-002)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자(USER-011)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<DataResponse<Void>> addComment(
            Principal principal,
            @PathVariable Long postId,
            @RequestBody PostCommentRequest content
    );

    @Operation(summary = "게시글 댓글 삭제", description = "게시글의 댓글을 삭제합니다. (소프트 삭제)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "게시글의 댓글 삭제 성공"),
            @ApiResponse(responseCode = "403", description = "작성자 아님(POST-003)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 회원 (USER-001)/존재하지 않는 게시글(POST-002)/존재하지 않는 댓글(POST-005)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자(USER-011)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<DataResponse<Void>> deleteComment(
            Principal principal,
            @PathVariable Long postId,
            @PathVariable Long commentId
    );

    @Operation(summary = "게시글 댓글 좋아요", description = "댓글에 좋아요를 누르거나 취소합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "게시글 작성 성공"),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 회원 (USER-001)/존재하지 않는 게시글(POST-002)/존재하지 않는 댓글(POST-005)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자(USER-011)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<DataResponse<Void>> likeComment(
            Principal principal,
            @PathVariable Long postId,
            @PathVariable Long commentId
    );

    // 게시글 검색
    @Operation(summary = "게시글 검색", description = "키워드, 감정, 정렬(latest/trend) 조건으로 게시글을 검색합니다.\n " +
            "현재 존재하는 감정은 JOY, EXCITEMENT, NOSTALGIA, SURPRISE, LOVE 입니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "게시글 검색 성공")
    })
    public ResponseEntity<DataResponse<PostPagedResponse>> searchPosts(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "all") String emotion,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "latest") String sortBy
    );

    // 내 게시글 검색
    @Operation(summary = "내 게시글 검색", description = "내가 작성한 게시글을 키워드, 감정, 정렬(latest/trend) 조건으로 검색합니다.\n " +
            "현재 존재하는 감정은 JOY, EXCITEMENT, NOSTALGIA, SURPRISE, LOVE 입니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "내 게시글 검색 성공"),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자(USER-011)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<DataResponse<PostPagedResponse>> searchMyPosts(
            Principal principal,
            @RequestParam String keyword,
            @RequestParam(defaultValue = "all") String emotion,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "latest") String sortBy
    );

    // 내가 스크랩한 게시글 검색
    @Operation(summary = "내가 스크랩한 게시글 검색", description = "내가 스크랩한 게시글을 키워드, 감정, 정렬(latest/trend) 조건으로 검색합니다.\n " +
            "현재 존재하는 감정은 JOY, EXCITEMENT, NOSTALGIA, SURPRISE, LOVE 입니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "내가 스크랩한 게시글 검색 성공"),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자(USER-011)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<DataResponse<PostPagedResponse>> searchScrappedPosts(
            Principal principal,
            @RequestParam String keyword,
            @RequestParam(defaultValue = "all") String emotion,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "latest") String sortBy
    );
}
