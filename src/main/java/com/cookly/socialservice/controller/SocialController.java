package com.cookly.socialservice.controller;

import com.cookly.socialservice.config.OpenApiConfig;
import com.cookly.socialservice.domain.SocialTargetType;
import com.cookly.socialservice.dto.CommentResponse;
import com.cookly.socialservice.dto.CreateCommentRequest;
import com.cookly.socialservice.dto.FavoriteResponse;
import com.cookly.socialservice.dto.RatingResponse;
import com.cookly.socialservice.dto.SocialSummaryResponse;
import com.cookly.socialservice.dto.UpdateCommentRequest;
import com.cookly.socialservice.dto.UpsertRatingRequest;
import com.cookly.socialservice.service.SocialService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
@Tag(name = "social", description = "Ratings, comments, and favorites")
@SecurityRequirement(name = OpenApiConfig.SESSION_SECURITY_SCHEME)
public class SocialController {
  private static final String SESSION_HEADER = "X-Session-ID";

  private final SocialService socialService;

  public SocialController(SocialService socialService) {
    this.socialService = socialService;
  }

  @PutMapping("/recipes/{recipeId}/ratings/me")
  @Operation(summary = "Create or update current user's recipe rating")
  public RatingResponse rateRecipe(
      @PathVariable String recipeId,
      @RequestHeader(SESSION_HEADER) String sessionId,
      @Valid @RequestBody UpsertRatingRequest request) {
    return socialService.upsertRating(SocialTargetType.RECIPE, recipeId, sessionId, request);
  }

  @GetMapping("/recipes/{recipeId}/ratings/me")
  @Operation(summary = "Get current user's recipe rating")
  public ResponseEntity<RatingResponse> getMyRecipeRating(
      @PathVariable String recipeId,
      @RequestHeader(SESSION_HEADER) String sessionId) {
    return ResponseEntity.of(socialService.getMyRating(SocialTargetType.RECIPE, recipeId, sessionId));
  }

  @DeleteMapping("/recipes/{recipeId}/ratings/me")
  @Operation(summary = "Delete current user's recipe rating")
  public ResponseEntity<Void> deleteMyRecipeRating(
      @PathVariable String recipeId,
      @RequestHeader(SESSION_HEADER) String sessionId) {
    socialService.deleteMyRating(SocialTargetType.RECIPE, recipeId, sessionId);
    return ResponseEntity.noContent().build();
  }

  @PutMapping("/recipes/{recipeId}/favorites/me")
  @Operation(summary = "Add recipe to current user's favorites")
  public FavoriteResponse addRecipeFavorite(
      @PathVariable String recipeId,
      @RequestHeader(SESSION_HEADER) String sessionId) {
    return socialService.addFavorite(SocialTargetType.RECIPE, recipeId, sessionId);
  }

  @DeleteMapping("/recipes/{recipeId}/favorites/me")
  @Operation(summary = "Remove recipe from current user's favorites")
  public ResponseEntity<Void> deleteRecipeFavorite(
      @PathVariable String recipeId,
      @RequestHeader(SESSION_HEADER) String sessionId) {
    socialService.deleteFavorite(SocialTargetType.RECIPE, recipeId, sessionId);
    return ResponseEntity.noContent().build();
  }

  @PostMapping("/recipes/{recipeId}/comments")
  @Operation(summary = "Create recipe comment")
  public ResponseEntity<CommentResponse> createRecipeComment(
      @PathVariable String recipeId,
      @RequestHeader(SESSION_HEADER) String sessionId,
      @Valid @RequestBody CreateCommentRequest request) {
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(socialService.createComment(SocialTargetType.RECIPE, recipeId, sessionId, request));
  }

  @GetMapping("/recipes/{recipeId}/comments")
  @Operation(summary = "Get recipe comments")
  public List<CommentResponse> getRecipeComments(
      @PathVariable String recipeId,
      @RequestHeader(SESSION_HEADER) String sessionId) {
    return socialService.getComments(SocialTargetType.RECIPE, recipeId, sessionId);
  }

  @GetMapping("/recipes/{recipeId}/social")
  @Operation(summary = "Get recipe social summary")
  public SocialSummaryResponse getRecipeSummary(
      @PathVariable String recipeId,
      @RequestHeader(SESSION_HEADER) String sessionId) {
    return socialService.getSummary(SocialTargetType.RECIPE, recipeId, sessionId);
  }

  @PutMapping("/collections/{collectionId}/ratings/me")
  @Operation(summary = "Create or update current user's collection rating")
  public RatingResponse rateCollection(
      @PathVariable String collectionId,
      @RequestHeader(SESSION_HEADER) String sessionId,
      @Valid @RequestBody UpsertRatingRequest request) {
    return socialService.upsertRating(SocialTargetType.COLLECTION, collectionId, sessionId, request);
  }

  @GetMapping("/collections/{collectionId}/ratings/me")
  @Operation(summary = "Get current user's collection rating")
  public ResponseEntity<RatingResponse> getMyCollectionRating(
      @PathVariable String collectionId,
      @RequestHeader(SESSION_HEADER) String sessionId) {
    return ResponseEntity.of(
        socialService.getMyRating(SocialTargetType.COLLECTION, collectionId, sessionId));
  }

  @DeleteMapping("/collections/{collectionId}/ratings/me")
  @Operation(summary = "Delete current user's collection rating")
  public ResponseEntity<Void> deleteMyCollectionRating(
      @PathVariable String collectionId,
      @RequestHeader(SESSION_HEADER) String sessionId) {
    socialService.deleteMyRating(SocialTargetType.COLLECTION, collectionId, sessionId);
    return ResponseEntity.noContent().build();
  }

  @PutMapping("/collections/{collectionId}/favorites/me")
  @Operation(summary = "Add collection to current user's favorites")
  public FavoriteResponse addCollectionFavorite(
      @PathVariable String collectionId,
      @RequestHeader(SESSION_HEADER) String sessionId) {
    return socialService.addFavorite(SocialTargetType.COLLECTION, collectionId, sessionId);
  }

  @DeleteMapping("/collections/{collectionId}/favorites/me")
  @Operation(summary = "Remove collection from current user's favorites")
  public ResponseEntity<Void> deleteCollectionFavorite(
      @PathVariable String collectionId,
      @RequestHeader(SESSION_HEADER) String sessionId) {
    socialService.deleteFavorite(SocialTargetType.COLLECTION, collectionId, sessionId);
    return ResponseEntity.noContent().build();
  }

  @PostMapping("/collections/{collectionId}/comments")
  @Operation(summary = "Create collection comment")
  public ResponseEntity<CommentResponse> createCollectionComment(
      @PathVariable String collectionId,
      @RequestHeader(SESSION_HEADER) String sessionId,
      @Valid @RequestBody CreateCommentRequest request) {
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(
            socialService.createComment(
                SocialTargetType.COLLECTION, collectionId, sessionId, request));
  }

  @GetMapping("/collections/{collectionId}/comments")
  @Operation(summary = "Get collection comments")
  public List<CommentResponse> getCollectionComments(
      @PathVariable String collectionId,
      @RequestHeader(SESSION_HEADER) String sessionId) {
    return socialService.getComments(SocialTargetType.COLLECTION, collectionId, sessionId);
  }

  @GetMapping("/collections/{collectionId}/social")
  @Operation(summary = "Get collection social summary")
  public SocialSummaryResponse getCollectionSummary(
      @PathVariable String collectionId,
      @RequestHeader(SESSION_HEADER) String sessionId) {
    return socialService.getSummary(SocialTargetType.COLLECTION, collectionId, sessionId);
  }

  @PutMapping("/comments/{commentId}")
  @Operation(summary = "Update current user's comment")
  public CommentResponse updateComment(
      @PathVariable String commentId,
      @RequestHeader(SESSION_HEADER) String sessionId,
      @Valid @RequestBody UpdateCommentRequest request) {
    return socialService.updateComment(commentId, sessionId, request);
  }

  @DeleteMapping("/comments/{commentId}")
  @Operation(summary = "Delete current user's comment")
  public ResponseEntity<Void> deleteComment(
      @PathVariable String commentId,
      @RequestHeader(SESSION_HEADER) String sessionId) {
    socialService.deleteComment(commentId, sessionId);
    return ResponseEntity.noContent().build();
  }

  @GetMapping("/me/favorites/recipes")
  @Operation(summary = "Get current user's favorite recipes")
  public List<FavoriteResponse> getMyRecipeFavorites(
      @RequestHeader(SESSION_HEADER) String sessionId) {
    return socialService.getMyFavorites(SocialTargetType.RECIPE, sessionId);
  }

  @GetMapping("/me/favorites/collections")
  @Operation(summary = "Get current user's favorite collections")
  public List<FavoriteResponse> getMyCollectionFavorites(
      @RequestHeader(SESSION_HEADER) String sessionId) {
    return socialService.getMyFavorites(SocialTargetType.COLLECTION, sessionId);
  }

  @GetMapping("/me/comments/recipes")
  @Operation(summary = "Get current user's recipe comments")
  public List<CommentResponse> getMyRecipeComments(
      @RequestHeader(SESSION_HEADER) String sessionId) {
    return socialService.getMyComments(SocialTargetType.RECIPE, sessionId);
  }

  @GetMapping("/me/comments/collections")
  @Operation(summary = "Get current user's collection comments")
  public List<CommentResponse> getMyCollectionComments(
      @RequestHeader(SESSION_HEADER) String sessionId) {
    return socialService.getMyComments(SocialTargetType.COLLECTION, sessionId);
  }
}
