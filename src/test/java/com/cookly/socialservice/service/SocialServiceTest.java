package com.cookly.socialservice.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.cookly.socialservice.domain.AuthenticatedUser;
import com.cookly.socialservice.domain.SocialTargetType;
import com.cookly.socialservice.dto.CreateCommentRequest;
import com.cookly.socialservice.dto.UpsertRatingRequest;
import com.cookly.socialservice.entity.CommentEntity;
import com.cookly.socialservice.entity.RatingEntity;
import com.cookly.socialservice.exception.UnauthorizedException;
import com.cookly.socialservice.external.AuthClient;
import com.cookly.socialservice.external.ContentClient;
import com.cookly.socialservice.repository.CommentRepository;
import com.cookly.socialservice.repository.FavoriteRepository;
import com.cookly.socialservice.repository.RatingRepository;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SocialServiceTest {
  private static final String SESSION_ID = "session-id";

  @Mock private AuthClient authClient;
  @Mock private ContentClient contentClient;
  @Mock private RatingRepository ratingRepository;
  @Mock private CommentRepository commentRepository;
  @Mock private FavoriteRepository favoriteRepository;

  private SocialService socialService;

  @BeforeEach
  void setUp() {
    socialService =
        new SocialService(
            authClient, contentClient, ratingRepository, commentRepository, favoriteRepository);
  }

  @Test
  void upsertRatingCreatesRatingForCurrentUser() {
    UUID userId = UUID.randomUUID();
    UUID targetId = UUID.randomUUID();
    when(authClient.resolve(SESSION_ID))
        .thenReturn(Optional.of(new AuthenticatedUser(userId, UUID.randomUUID())));
    when(ratingRepository.findByUserIdAndTargetTypeAndTargetId(
            userId, SocialTargetType.RECIPE, targetId))
        .thenReturn(Optional.empty());
    when(ratingRepository.save(any(RatingEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

    var response =
        socialService.upsertRating(
            SocialTargetType.RECIPE,
            targetId.toString(),
            SESSION_ID,
            new UpsertRatingRequest(4));

    assertEquals(userId, response.userId());
    assertEquals(targetId, response.targetId());
    assertEquals(4, response.value());
    verify(contentClient).ensureAccessible(SocialTargetType.RECIPE, targetId, SESSION_ID);
  }

  @Test
  void createCommentTrimsText() {
    UUID userId = UUID.randomUUID();
    UUID targetId = UUID.randomUUID();
    when(authClient.resolve(SESSION_ID))
        .thenReturn(Optional.of(new AuthenticatedUser(userId, UUID.randomUUID())));
    when(commentRepository.save(any(CommentEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

    var response =
        socialService.createComment(
            SocialTargetType.COLLECTION,
            targetId.toString(),
            SESSION_ID,
            new CreateCommentRequest("  useful notes  "));

    assertEquals("useful notes", response.text());
    verify(contentClient).ensureAccessible(SocialTargetType.COLLECTION, targetId, SESSION_ID);
  }

  @Test
  void missingSessionIsRejected() {
    assertThrows(
        UnauthorizedException.class,
        () ->
            socialService.upsertRating(
                SocialTargetType.RECIPE,
                UUID.randomUUID().toString(),
                null,
                new UpsertRatingRequest(5)));
  }
}
