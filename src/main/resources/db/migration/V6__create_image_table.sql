CREATE TABLE images (
                        image_id   BIGSERIAL PRIMARY KEY,
                        product_id BIGINT NOT NULL UNIQUE, -- Одно фото на один продукт
                        image_url  TEXT NOT NULL,
                        created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                        updated_at TIMESTAMP,

                        CONSTRAINT fk_images_product
                            FOREIGN KEY (product_id)
                                REFERENCES products(id)
                                ON DELETE CASCADE
);