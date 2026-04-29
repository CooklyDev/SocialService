CREATE TABLE ratings (
  id UUID PRIMARY KEY,
  user_id UUID NOT NULL,
  target_type VARCHAR(32) NOT NULL,
  target_id UUID NOT NULL,
  value INTEGER NOT NULL,
  created_at TIMESTAMPTZ NOT NULL,
  updated_at TIMESTAMPTZ NOT NULL,
  CONSTRAINT chk_ratings_target_type CHECK (target_type IN ('RECIPE', 'COLLECTION')),
  CONSTRAINT chk_ratings_value CHECK (value BETWEEN 1 AND 5),
  CONSTRAINT uk_ratings_user_target UNIQUE (user_id, target_type, target_id)
);

CREATE INDEX idx_ratings_target ON ratings (target_type, target_id);
CREATE INDEX idx_ratings_user ON ratings (user_id, target_type);

CREATE TABLE comments (
  id UUID PRIMARY KEY,
  user_id UUID NOT NULL,
  target_type VARCHAR(32) NOT NULL,
  target_id UUID NOT NULL,
  text TEXT NOT NULL,
  created_at TIMESTAMPTZ NOT NULL,
  updated_at TIMESTAMPTZ NOT NULL,
  CONSTRAINT chk_comments_target_type CHECK (target_type IN ('RECIPE', 'COLLECTION')),
  CONSTRAINT chk_comments_text CHECK (length(trim(text)) > 0)
);

CREATE INDEX idx_comments_target ON comments (target_type, target_id, created_at);
CREATE INDEX idx_comments_user ON comments (user_id, target_type, created_at);

CREATE TABLE favorites (
  id UUID PRIMARY KEY,
  user_id UUID NOT NULL,
  target_type VARCHAR(32) NOT NULL,
  target_id UUID NOT NULL,
  created_at TIMESTAMPTZ NOT NULL,
  CONSTRAINT chk_favorites_target_type CHECK (target_type IN ('RECIPE', 'COLLECTION')),
  CONSTRAINT uk_favorites_user_target UNIQUE (user_id, target_type, target_id)
);

CREATE INDEX idx_favorites_target ON favorites (target_type, target_id);
CREATE INDEX idx_favorites_user ON favorites (user_id, target_type, created_at);
