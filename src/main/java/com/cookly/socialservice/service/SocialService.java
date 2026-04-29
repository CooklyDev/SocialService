package com.cookly.socialservice.service;

import com.cookly.socialservice.domain.AuthenticatedUser;
import com.cookly.socialservice.domain.SocialTargetType;
import com.cookly.socialservice.dto.CommentResponse;
import com.cookly.socialservice.dto.CreateCommentRequest;
import com.cookly.socialservice.dto.FavoriteResponse;
import com.cookly.socialservice.dto.RatingResponse;
import com.cookly.socialservice.dto.SocialSummaryResponse;
import com.cookly.socialservice.dto.UpdateCommentRequest;
import com.cookly.socialservice.dto.UpsertRatingRequest;
import com.cookly.socialservice.entity.CommentEntity;
import com.cookly.socialservice.entity.FavoriteEntity;
import com.cookly.socialservice.entity.RatingEntity;
import com.cookly.socialservice.exception.InvalidInputException;
import com.cookly.socialservice.exception.TargetNotFoundException;
import com.cookly.socialservice.exception.UnauthorizedException;
import com.cookly.socialservice.external.AuthClient;
import com.cookly.socialservice.external.ContentClient;
import com.cookly.socialservice.repository.CommentRepository;
import com.cookly.socialservice.repository.FavoriteRepository;
import com.cookly.socialservice.repository.RatingRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
public class SocialService {
  private final AuthClient authClient;
  private final ContentClient contentClient;
  private final RatingRepository ratingRepository;
  private final CommentRepository commentRepository;
  private final FavoriteRepository favoriteRepository;

  public SocialService(
      AuthClient authClient,
      ContentClient contentClient,
      RatingRepository ratingRepository,
      CommentRepository commentRepository,
      FavoriteRepository favoriteRepository) {
    this.authClient = authClient;
    this.contentClient = contentClient;
    this.ratingRepository = ratingRepository;
    this.commentRepository = commentRepository;
    this.favoriteRepository = favoriteRepository;
  }

  @Transactional
  public RatingResponse upsertRating(
      SocialTargetType targetType, String rawTargetId, String sessionId, UpsertRatingRequest request) {
    UUID targetId = parseUuid(rawTargetId, "targetId");
    int value = ratingValue(request);
    AuthenticatedUser user = requireUser(sessionId);
    contentClient.ensureAccessible(targetType, targetId, sessionId);

    RatingEntity rating =
        ratingRepository
            .findByUserIdAndTargetTypeAndTargetId(user.userId(), targetType, targetId)
            .orElseGet(
                () ->
                    new RatingEntity(
                        UUID.randomUUID(), user.userId(), targetType, targetId, value));
    rating.setValue(value);

    return toRatingResponse(ratingRepository.save(rating));
  }

  @Transactional(readOnly = true)
  public Optional<RatingResponse> getMyRating(
      SocialTargetType targetType, String rawTargetId, String sessionId) {
    UUID targetId = parseUuid(rawTargetId, "targetId");
    AuthenticatedUser user = requireUser(sessionId);
    contentClient.ensureAccessible(targetType, targetId, sessionId);

    return ratingRepository
        .findByUserIdAndTargetTypeAndTargetId(user.userId(), targetType, targetId)
        .map(this::toRatingResponse);
  }

  @Transactional
  public void deleteMyRating(SocialTargetType targetType, String rawTargetId, String sessionId) {
    UUID targetId = parseUuid(rawTargetId, "targetId");
    AuthenticatedUser user = requireUser(sessionId);

    ratingRepository.deleteByUserIdAndTargetTypeAndTargetId(user.userId(), targetType, targetId);
  }

  @Transactional
  public FavoriteResponse addFavorite(SocialTargetType targetType, String rawTargetId, String sessionId) {
    UUID targetId = parseUuid(rawTargetId, "targetId");
    AuthenticatedUser user = requireUser(sessionId);
    contentClient.ensureAccessible(targetType, targetId, sessionId);

    FavoriteEntity favorite =
        favoriteRepository
            .findByUserIdAndTargetTypeAndTargetId(user.userId(), targetType, targetId)
            .orElseGet(
                () ->
                    favoriteRepository.save(
                        new FavoriteEntity(
                            UUID.randomUUID(), user.userId(), targetType, targetId)));

    return toFavoriteResponse(favorite);
  }

  @Transactional
  public void deleteFavorite(SocialTargetType targetType, String rawTargetId, String sessionId) {
    UUID targetId = parseUuid(rawTargetId, "targetId");
    AuthenticatedUser user = requireUser(sessionId);

    favoriteRepository.deleteByUserIdAndTargetTypeAndTargetId(user.userId(), targetType, targetId);
  }

  @Transactional(readOnly = true)
  public List<FavoriteResponse> getMyFavorites(SocialTargetType targetType, String sessionId) {
    AuthenticatedUser user = requireUser(sessionId);
    return favoriteRepository.findByUserIdAndTargetTypeOrderByCreatedAtDesc(user.userId(), targetType).stream()
        .map(this::toFavoriteResponse)
        .toList();
  }

  @Transactional
  public CommentResponse createComment(
      SocialTargetType targetType, String rawTargetId, String sessionId, CreateCommentRequest request) {
    UUID targetId = parseUuid(rawTargetId, "targetId");
    AuthenticatedUser user = requireUser(sessionId);
    contentClient.ensureAccessible(targetType, targetId, sessionId);

    CommentEntity comment =
        new CommentEntity(
            UUID.randomUUID(), user.userId(), targetType, targetId, normalizeText(request.text()));
    return toCommentResponse(commentRepository.save(comment));
  }

  @Transactional(readOnly = true)
  public List<CommentResponse> getComments(
      SocialTargetType targetType, String rawTargetId, String sessionId) {
    UUID targetId = parseUuid(rawTargetId, "targetId");
    requireUser(sessionId);
    contentClient.ensureAccessible(targetType, targetId, sessionId);

    return commentRepository.findByTargetTypeAndTargetIdOrderByCreatedAtAsc(targetType, targetId).stream()
        .map(this::toCommentResponse)
        .toList();
  }

  @Transactional(readOnly = true)
  public List<CommentResponse> getMyComments(SocialTargetType targetType, String sessionId) {
    AuthenticatedUser user = requireUser(sessionId);
    return commentRepository.findByUserIdAndTargetTypeOrderByCreatedAtDesc(user.userId(), targetType).stream()
        .map(this::toCommentResponse)
        .toList();
  }

  @Transactional
  public CommentResponse updateComment(String rawCommentId, String sessionId, UpdateCommentRequest request) {
    UUID commentId = parseUuid(rawCommentId, "commentId");
    AuthenticatedUser user = requireUser(sessionId);

    CommentEntity comment =
        commentRepository.findById(commentId).orElseThrow(() -> new TargetNotFoundException("comment"));
    if (!comment.getUserId().equals(user.userId())) {
      throw new TargetNotFoundException("comment");
    }

    comment.setText(normalizeText(request.text()));
    return toCommentResponse(commentRepository.save(comment));
  }

  @Transactional
  public void deleteComment(String rawCommentId, String sessionId) {
    UUID commentId = parseUuid(rawCommentId, "commentId");
    AuthenticatedUser user = requireUser(sessionId);

    CommentEntity comment =
        commentRepository.findById(commentId).orElseThrow(() -> new TargetNotFoundException("comment"));
    if (!comment.getUserId().equals(user.userId())) {
      throw new TargetNotFoundException("comment");
    }

    commentRepository.delete(comment);
  }

  @Transactional(readOnly = true)
  public SocialSummaryResponse getSummary(
      SocialTargetType targetType, String rawTargetId, String sessionId) {
    UUID targetId = parseUuid(rawTargetId, "targetId");
    AuthenticatedUser user = requireUser(sessionId);
    contentClient.ensureAccessible(targetType, targetId, sessionId);

    long ratingCount = ratingRepository.countByTargetTypeAndTargetId(targetType, targetId);
    Double averageRating = round(ratingRepository.averageValue(targetType, targetId));
    long commentCount = commentRepository.countByTargetTypeAndTargetId(targetType, targetId);
    long favoriteCount = favoriteRepository.countByTargetTypeAndTargetId(targetType, targetId);
    Integer myRating =
        ratingRepository
            .findByUserIdAndTargetTypeAndTargetId(user.userId(), targetType, targetId)
            .map(RatingEntity::getValue)
            .orElse(null);
    boolean favoriteByMe =
        favoriteRepository.existsByUserIdAndTargetTypeAndTargetId(user.userId(), targetType, targetId);

    return new SocialSummaryResponse(
        targetId,
        targetType,
        ratingCount,
        averageRating,
        commentCount,
        favoriteCount,
        myRating,
        favoriteByMe);
  }

  private AuthenticatedUser requireUser(String sessionId) {
    if (!StringUtils.hasText(sessionId)) {
      throw new UnauthorizedException();
    }
    return authClient.resolve(sessionId).orElseThrow(UnauthorizedException::new);
  }

  private UUID parseUuid(String value, String fieldName) {
    if (!StringUtils.hasText(value)) {
      throw new InvalidInputException("Invalid " + fieldName);
    }
    try {
      return UUID.fromString(value);
    } catch (IllegalArgumentException exception) {
      throw new InvalidInputException("Invalid " + fieldName + " format");
    }
  }

  private int ratingValue(UpsertRatingRequest request) {
    if (request == null || request.value() == null || request.value() < 1 || request.value() > 5) {
      throw new InvalidInputException("Rating value must be from 1 to 5");
    }
    return request.value();
  }

  private String normalizeText(String text) {
    if (!StringUtils.hasText(text)) {
      throw new InvalidInputException("Comment text must not be blank");
    }
    String trimmed = text.trim();
    if (trimmed.length() > 5000) {
      throw new InvalidInputException("Comment text must not exceed 5000 characters");
    }
    return trimmed;
  }

  private Double round(Double value) {
    if (value == null) {
      return null;
    }
    return Math.round(value * 100.0) / 100.0;
  }

  private RatingResponse toRatingResponse(RatingEntity rating) {
    return new RatingResponse(
        rating.getId(),
        rating.getUserId(),
        rating.getTargetType(),
        rating.getTargetId(),
        rating.getValue(),
        rating.getCreatedAt(),
        rating.getUpdatedAt());
  }

  private CommentResponse toCommentResponse(CommentEntity comment) {
    return new CommentResponse(
        comment.getId(),
        comment.getUserId(),
        comment.getTargetType(),
        comment.getTargetId(),
        comment.getText(),
        comment.getCreatedAt(),
        comment.getUpdatedAt());
  }

  private FavoriteResponse toFavoriteResponse(FavoriteEntity favorite) {
    return new FavoriteResponse(
        favorite.getId(),
        favorite.getUserId(),
        favorite.getTargetType(),
        favorite.getTargetId(),
        favorite.getCreatedAt());
  }
}
