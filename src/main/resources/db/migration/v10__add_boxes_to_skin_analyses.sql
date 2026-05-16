-- V2__add_boxes_to_skin_analyses.sql
ALTER TABLE skin_analyses ADD COLUMN boxes TEXT;
ALTER TABLE skin_analyses ADD COLUMN acne_count INTEGER;
ALTER TABLE skin_analyses ADD COLUMN image_width INTEGER;
ALTER TABLE skin_analyses ADD COLUMN image_height INTEGER;