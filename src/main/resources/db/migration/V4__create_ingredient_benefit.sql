CREATE TABLE ingredient_benefits (
    id BIGSERIAL PRIMARY KEY,
    ingredient_id BIGINT NOT NULL,
    benefit VARCHAR(50) NOT NULL,      -- ACNE, WRINKLES и т.д.
    efficiency_score DOUBLE PRECISION DEFAULT 0.5, -- Насколько эффективно (0.0 - 1.0)

    FOREIGN KEY (ingredient_id) REFERENCES ingredients(id) ON DELETE CASCADE,
    -- Уникальность: один ингредиент не может иметь два одинаковых свойства Niacinamide → ACNE (0.8)
    -- Niacinamide → ACNE (0.9)
    UNIQUE(ingredient_id, benefit),

    CONSTRAINT check_score CHECK (efficiency_score >= 0 AND efficiency_score <= 1)
);

CREATE INDEX idx_benefit_type ON ingredient_benefits(benefit);