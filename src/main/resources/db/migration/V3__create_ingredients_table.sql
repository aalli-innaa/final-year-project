CREATE TABLE ingredients (
                             id BIGSERIAL PRIMARY KEY,
                             name VARCHAR(255) NOT NULL UNIQUE,
                             description TEXT,
                             irritation_level INT DEFAULT 0,
                             comedogenic_level INT DEFAULT 0,
                             min_age INT DEFAULT 0,                    -- Добавлено: минимальный возраст
                             is_active BOOLEAN DEFAULT TRUE,

                             CONSTRAINT check_irritation CHECK (irritation_level >= 0 AND irritation_level <= 5),
                             CONSTRAINT check_comedogenic CHECK (comedogenic_level >= 0 AND comedogenic_level <= 5),
                             CONSTRAINT check_min_age CHECK (min_age >= 0) -- Добавлено: возраст не может быть < 0
);

CREATE INDEX idx_ingredients_name ON ingredients(name);