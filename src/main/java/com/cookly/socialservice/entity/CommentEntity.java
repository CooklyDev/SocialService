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
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "comments")
public class CommentEntity {
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

  @Column(nullable = false, columnDefinition = "text")
  private String text;

  @Column(name = "created_at", nullable = false)
  private Instant createdAt;

  @Column(name = "updated_at", nullable = false)
  private Instant updatedAt;

  protected CommentEntity() {}

  public CommentEntity(
      UUID id, UUID userId, SocialTargetType targetType, UUID targetId, String text) {
    this.id = id;
    this.userId = userId;
    this.targetType = targetType;
    this.targetId = targetId;
    this.text = text;
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

  public String getText() {
    return text;
  }

  public void setText(String text) {
    this.text = text;
    this.updatedAt = Instant.now();
  }

  public Instant getCreatedAt() {
    return createdAt;
  }

  public Instant getUpdatedAt() {
    return updatedAt;
  }
}
