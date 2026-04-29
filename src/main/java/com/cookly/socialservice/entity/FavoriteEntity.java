package com.cookly.socialservice.entity;

import com.cookly.socialservice.domain.SocialTargetType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(
    name = "favorites",
    uniqueConstraints = {
      @UniqueConstraint(
          name = "uk_favorites_user_target",
          columnNames = {"user_id", "target_type", "target_id"})
    })
public class FavoriteEntity {
  @Id
  @Column(nullable = false)
  private UUID id;

  @Column(name = "user_id", nullable = false)
  private UUID userId;

  @Enumerated(EnumType.STRING)
  @Column(name = "target_type", nullable = false, length = 32)
  private SocialTargetType targetType;

  @Column(name = "target_id", nullable = false)
  private UUID targetId;

  @Column(name = "created_at", nullable = false)
  private Instant createdAt;

  protected FavoriteEntity() {}

  public FavoriteEntity(UUID id, UUID userId, SocialTargetType targetType, UUID targetId) {
    this.id = id;
    this.userId = userId;
    this.targetType = targetType;
    this.targetId = targetId;
    this.createdAt = Instant.now();
  }

  @PrePersist
  void onCreate() {
    if (createdAt == null) {
      createdAt = Instant.now();
    }
  }

  public UUID getId() {
    return id;
  }

  public UUID getUserId() {
    return userId;
  }

  public SocialTargetType getTargetType() {
    return targetType;
  }

  public UUID getTargetId() {
    return targetId;
  }

  public Instant getCreatedAt() {
    return createdAt;
  }
}
