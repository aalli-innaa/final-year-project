CREATE TABLE skin_analyses (
                               analysis_id      BIGSERIAL PRIMARY KEY,
                               user_id          BIGINT NOT NULL,
                               photo_id         BIGINT NOT NULL UNIQUE, -- Один анализ = одно фото
                               primary_concern  VARCHAR(50) NOT NULL,   -- ACNE, WRINKLES и т.д.
                               confidence       DOUBLE PRECISION,           -- Точность ИИ (0.9500)
                               created_at       TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

                               CONSTRAINT fk_analysis_user
                                   FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE, -- ИСПРАВЛЕНО: user_id вместо id

                               CONSTRAINT fk_analysis_photo
                                   FOREIGN KEY (photo_id) REFERENCES user_photos(photo_id) ON DELETE CASCADE
);

CREATE INDEX idx_analysis_user_id ON skin_analyses(user_id);