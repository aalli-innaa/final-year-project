-- Создание таблицы ингредиентов
CREATE TABLE ingredients (
                             id BIGSERIAL PRIMARY KEY,
                             name VARCHAR(255) NOT NULL UNIQUE,          -- Название ингредиента
                             description TEXT,                           -- Описание для пользователя
                             irritation_level INT DEFAULT 0,             -- Уровень раздражения (0-5)
                             comedogenic_level INT DEFAULT 0,            -- Уровень комедогенности (0-5)
                             is_active BOOLEAN DEFAULT TRUE,             -- Активен ли в системе

    -- Ограничения (чтобы нельзя было вписать число больше 5)
                             CONSTRAINT check_irritation CHECK (irritation_level >= 0 AND irritation_level <= 5),
                             CONSTRAINT check_comedogenic CHECK (comedogenic_level >= 0 AND comedogenic_level <= 5)
);

-- Индекс для быстрого поиска по названию
CREATE INDEX idx_ingredients_name ON ingredients(name);