package com.cookly.socialservice.entity;

import com.cookly.socialservice.domain.SocialTargetType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(
    name = "ratings",
    uniqueConstraints = {
      @UniqueConstraint(
          name = "uk_ratings_user_target",
          columnNames = {"user_id", "target_type", "target_id"})
    })
public class RatingEntity {
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

  @Column(nullable = false)
  private int value;

  @Column(name = "created_at", nullable = false)
  private Instant createdAt;

  @Column(name = "updated_at", nullable = false)
  private Instant updatedAt;

  protected RatingEntity() {}

  public RatingEntity(
      UUID id, UUID userId, SocialTargetType targetType, UUID targetId, int value) {
    this.id = id;
    this.userId = userId;
    this.targetType = targetType;
    this.targetId = targetId;
    this.value = value;
    Instant now = Instant.now();
    this.createdAt = now;
    this.updatedAt = now;
  }

  @PrePersist
  void onCreate() {
    Instant now = Instant.now();
    if (createdAt == null) {
      createdAt = now;
    }
    updatedAt = now;
  }

  @PreUpdate
  void onUpdate() {
    updatedAt = Instant.now();
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

  public int getValue() {
    return value;
  }

  public void setValue(int value) {
    this.value = value;
    this.updatedAt = Instant.now();
  }

  public Instant getCreatedAt() {
    return createdAt;
  }

  public Instant getUpdatedAt() {
    return updatedAt;
  }
}
