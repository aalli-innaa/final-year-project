CREATE TABLE user_photos (
                             photo_id    BIGSERIAL PRIMARY KEY,
                             user_id     BIGINT NOT NULL,
                             image_url   TEXT NOT NULL, -- Путь: /faces/{userId}/{uuid}.jpg
                             created_at  TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

                             CONSTRAINT fk_user_photos_user
                                 FOREIGN KEY (user_id)
                                     REFERENCES users(user_id) -- ИСПРАВЛЕНО: здесь должно быть user_id
                                     ON DELETE CASCADE
);