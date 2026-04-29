package com.cookly.socialservice.repository;

import com.cookly.socialservice.domain.SocialTargetType;
import com.cookly.socialservice.entity.RatingEntity;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RatingRepository extends JpaRepository<RatingEntity, UUID> {
  Optional<RatingEntity> findByUserIdAndTargetTypeAndTargetId(
      UUID userId, SocialTargetType targetType, UUID targetId);

  List<RatingEntity> findByUserIdAndTargetTypeOrderByUpdatedAtDesc(
      UUID userId, SocialTargetType targetType);

  long countByTargetTypeAndTargetId(SocialTargetType targetType, UUID targetId);

  void deleteByUserIdAndTargetTypeAndTargetId(
      UUID userId, SocialTargetType targetType, UUID targetId);

  @Query(
      """
      select avg(r.value)
      from RatingEntity r
      where r.targetType = :targetType and r.targetId = :targetId
      """)
  Double averageValue(
      @Param("targetType") SocialTargetType targetType, @Param("targetId") UUID targetId);
}
