CREATE TABLE skin_analyses (
                               analysis_id      BIGSERIAL PRIMARY KEY,
                               user_id          BIGINT NOT NULL,
                               photo_id         BIGINT NOT NULL UNIQUE,
                               acne_severity    VARCHAR(50) NOT NULL, -- БЫЛО primary_concern, СТАЛО acne_severity
                               confidence       DOUBLE PRECISION,
                               created_at       TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                               CONSTRAINT fk_analysis_user FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
                               CONSTRAINT fk_analysis_photo FOREIGN KEY (photo_id) REFERENCES user_photos(photo_id) ON DELETE CASCADE
);