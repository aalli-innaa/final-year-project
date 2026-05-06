CREATE TABLE ingredient_benefits (
                                     id BIGSERIAL PRIMARY KEY,
                                     ingredient_id BIGINT NOT NULL,
                                     acne_severity VARCHAR(50) NOT NULL, -- БЫЛО benefit, СТАЛО acne_severity
                                     efficiency_score DOUBLE PRECISION DEFAULT 0.5,
                                     FOREIGN KEY (ingredient_id) REFERENCES ingredients(id) ON DELETE CASCADE,
                                     UNIQUE(ingredient_id, acne_severity), -- И здесь тоже замени
                                     CONSTRAINT check_score CHECK (efficiency_score >= 0 AND efficiency_score <= 1)
);
CREATE INDEX idx_benefit_type ON ingredient_benefits(acne_severity); -- И здесь