-- 4. Таблица изображений продуктов
CREATE TABLE images (
                        image_id BIGSERIAL PRIMARY KEY,
                        product_id BIGINT NOT NULL,
                        image_url TEXT NOT NULL,
                        image_type VARCHAR(50) NOT NULL,
                        image_angle VARCHAR(50),
                        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                        updated_at TIMESTAMP,

                        CONSTRAINT fk_images_product
                            FOREIGN KEY (product_id)
                                REFERENCES products(id)
                                ON DELETE CASCADE
);