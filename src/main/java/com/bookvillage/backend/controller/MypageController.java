package com.bookvillage.backend.controller;

import com.bookvillage.backend.dto.ReviewDto;
import com.bookvillage.backend.dto.ReviewUpdateRequest;
import com.bookvillage.backend.security.UserPrincipal;
import com.bookvillage.backend.service.LearningFeatureService;
import com.bookvillage.backend.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/mypage")
@RequiredArgsConstructor
public class MypageController {

    private final LearningFeatureService learningFeatureService;
    private final ReviewService reviewService;

    @GetMapping("/recent-views")
    public ResponseEntity<List<Map<String, Object>>> recentViews(@AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(learningFeatureService.getRecentViews(principal.getUserId()));
    }

    @GetMapping("/wishlist")
    public ResponseEntity<List<Map<String, Object>>> wishlist(@AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(learningFeatureService.getWishlist(principal.getUserId()));
    }

    @PostMapping("/wishlist")
    public ResponseEntity<Void> addWishlist(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestBody Map<String, Object> request) {
        Long bookId = request.get("bookId") == null ? null : Long.valueOf(String.valueOf(request.get("bookId")));
        learningFeatureService.addWishlist(principal.getUserId(), bookId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/wishlist/{wishlistId}")
    public ResponseEntity<Void> deleteWishlist(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long wishlistId) {
        learningFeatureService.deleteWishlist(principal.getUserId(), wishlistId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/wallet")
    public ResponseEntity<Map<String, Object>> wallet(@AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(learningFeatureService.getWallet(principal.getUserId()));
    }

    /**
     * [CTF Lab] 포인트 충전 API
     * 취약점: 파라미터 변조 (CWE-20 / 비즈니스 로직 우회)
     * - amount 파라미터에 대한 상한선/하한선 검증 없음
     * - 음수 입력 시 포인트 차감 가능 (타인의 포인트 잔액 조작 시나리오)
     * - 정수 오버플로우 또는 999,999,999 등 비정상 금액 입력 가능
     */
    @PostMapping("/points/charge")
    public ResponseEntity<Map<String, Object>> chargePoints(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestBody Map<String, Object> request) {
        // [취약점] amount 값에 대한 서버 측 검증 없음: 음수, 0, 비정상적으로 큰 값 허용
        Integer amount = request.get("amount") == null ? 0 : Integer.valueOf(String.valueOf(request.get("amount")));
        return ResponseEntity.ok(learningFeatureService.chargePoints(principal.getUserId(), amount));
    }

    @GetMapping("/summary")
    public ResponseEntity<Map<String, Object>> summary(@AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(learningFeatureService.getMypageSummary(principal.getUserId()));
    }

    @PostMapping("/orders/{orderId}/cancel")
    public ResponseEntity<Void> cancelOrder(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long orderId,
            @RequestBody(required = false) Map<String, String> request) {
        String reason = request != null ? request.get("reason") : null;
        learningFeatureService.requestOrderCancel(principal.getUserId(), orderId, reason);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/orders/{orderId}/return")
    public ResponseEntity<Void> returnOrder(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long orderId,
            @RequestBody(required = false) Map<String, String> request) {
        String reason = request != null ? request.get("reason") : null;
        String proofFileName = request != null ? request.get("proofFileName") : null;
        learningFeatureService.requestOrderReturn(principal.getUserId(), orderId, reason, proofFileName);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/orders/{orderId}/exchange")
    public ResponseEntity<Void> exchangeOrder(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long orderId,
            @RequestBody(required = false) Map<String, String> request) {
        String reason = request != null ? request.get("reason") : null;
        String proofFileName = request != null ? request.get("proofFileName") : null;
        learningFeatureService.requestOrderExchange(principal.getUserId(), orderId, reason, proofFileName);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/favorite-posts")
    public ResponseEntity<List<Map<String, Object>>> favoritePosts(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestParam(defaultValue = "false") boolean includePrivate) {
        return ResponseEntity.ok(learningFeatureService.getFavoritePosts(principal.getUserId(), includePrivate));
    }

    @DeleteMapping("/favorite-posts/{postId}")
    public ResponseEntity<Void> deleteFavoritePost(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long postId) {
        learningFeatureService.deleteFavoritePost(principal.getUserId(), postId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/reviews")
    public ResponseEntity<List<ReviewDto>> myReviews(@AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(reviewService.getMyReviews(principal.getUserId()));
    }

    @PutMapping("/reviews/{reviewId}")
    public ResponseEntity<ReviewDto> updateReview(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long reviewId,
            @RequestBody(required = false) ReviewUpdateRequest request) {
        Integer rating = request != null ? request.getRating() : null;
        String content = request != null ? request.getContent() : null;
        return ResponseEntity.ok(reviewService.updateMyReview(principal.getUserId(), reviewId, rating, content));
    }

    @DeleteMapping("/reviews/{reviewId}")
    public ResponseEntity<Void> deleteReview(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long reviewId,
            @RequestHeader(value = "X-CSRF-TOKEN", required = false) String csrfToken) {
        reviewService.deleteMyReview(principal.getUserId(), reviewId, csrfToken);
        return ResponseEntity.noContent().build();
    }
}
