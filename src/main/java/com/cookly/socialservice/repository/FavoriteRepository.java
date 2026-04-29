package com.cookly.socialservice.repository;

import com.cookly.socialservice.domain.SocialTargetType;
import com.cookly.socialservice.entity.FavoriteEntity;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FavoriteRepository extends JpaRepository<FavoriteEntity, UUID> {
  Optional<FavoriteEntity> findByUserIdAndTargetTypeAndTargetId(
      UUID userId, SocialTargetType targetType, UUID targetId);

  List<FavoriteEntity> findByUserIdAndTargetTypeOrderByCreatedAtDesc(
      UUID userId, SocialTargetType targetType);

  long countByTargetTypeAndTargetId(SocialTargetType targetType, UUID targetId);

  boolean existsByUserIdAndTargetTypeAndTargetId(
      UUID userId, SocialTargetType targetType, UUID targetId);

  void deleteByUserIdAndTargetTypeAndTargetId(
      UUID userId, SocialTargetType targetType, UUID targetId);
}
