-- Создаём таблицу user_profiles
CREATE TABLE user_profiles (
                               id BIGSERIAL PRIMARY KEY,
                               user_id BIGINT NOT NULL UNIQUE,
                               skin_type VARCHAR(50) NOT NULL,
                               updated_at TIMESTAMP,
                               FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
);

-- Индекс для быстрого поиска по user_id
CREATE INDEX idx_user_profiles_user_id ON user_profiles(user_id);