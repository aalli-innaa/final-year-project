-- 1. Таблица самих продуктов
CREATE TABLE products (
                          id BIGSERIAL PRIMARY KEY,
                          name VARCHAR(255) NOT NULL,
                          brand VARCHAR(100) NOT NULL,
                          description TEXT,
                          usage_instructions TEXT,
                          image_url TEXT,
                          target_gender VARCHAR(20) DEFAULT 'UNISEX', -- MALE, FEMALE, UNISEX
                          is_active BOOLEAN DEFAULT TRUE,
                          created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                          updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 2. Таблица состава (Связь Продукт <-> Ингредиент)
CREATE TABLE product_ingredients (
                                     product_id BIGINT NOT NULL,
                                     ingredient_id BIGINT NOT NULL,
                                     PRIMARY KEY (product_id, ingredient_id),
                                     CONSTRAINT fk_prod_ing_product FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE,
                                     CONSTRAINT fk_prod_ing_ingredient FOREIGN KEY (ingredient_id) REFERENCES ingredients(id) ON DELETE CASCADE
);

-- 3. Таблица типов кожи (Для какой кожи этот крем)
CREATE TABLE product_skin_types (
                                    product_id BIGINT NOT NULL,
                                    skin_type VARCHAR(50) NOT NULL, -- OILY, DRY, COMBINATION, SENSITIVE
                                    PRIMARY KEY (product_id, skin_type),
                                    CONSTRAINT fk_skin_type_product FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE
);