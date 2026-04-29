package com.cookly.socialservice.repository;

import com.cookly.socialservice.domain.SocialTargetType;
import com.cookly.socialservice.entity.CommentEntity;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<CommentEntity, UUID> {
  List<CommentEntity> findByTargetTypeAndTargetIdOrderByCreatedAtAsc(
      SocialTargetType targetType, UUID targetId);

  List<CommentEntity> findByUserIdAndTargetTypeOrderByCreatedAtDesc(
      UUID userId, SocialTargetType targetType);

  long countByTargetTypeAndTargetId(SocialTargetType targetType, UUID targetId);
}
