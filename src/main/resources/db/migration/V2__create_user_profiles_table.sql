CREATE TABLE user_profiles (
                               id         BIGSERIAL   PRIMARY KEY,
                               user_id    BIGINT      NOT NULL UNIQUE,
                               skin_type  VARCHAR(50) NOT NULL,
                               birth_date DATE        NULL,
                               gender     VARCHAR(20) NULL,         -- MALE | FEMALE | OTHER
                               updated_at TIMESTAMP   NULL,

                               CONSTRAINT fk_user_profiles_user
                                   FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
);

CREATE INDEX idx_user_profiles_user_id ON user_profiles(user_id);