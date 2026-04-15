CREATE TABLE ingredient_conflicts (
                                      id               BIGSERIAL PRIMARY KEY,
                                      ingredient_id_1  BIGINT NOT NULL,
                                      ingredient_id_2  BIGINT NOT NULL,
                                      severity         VARCHAR(20),
                                      reason           TEXT,

                                      CONSTRAINT fk_conflict_ing1 FOREIGN KEY (ingredient_id_1) REFERENCES ingredients(id) ON DELETE CASCADE,
                                      CONSTRAINT fk_conflict_ing2 FOREIGN KEY (ingredient_id_2) REFERENCES ingredients(id) ON DELETE CASCADE,
                                      CONSTRAINT uq_conflict_pair UNIQUE (ingredient_id_1, ingredient_id_2)
);