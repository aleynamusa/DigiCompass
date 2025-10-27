

ALTER TABLE review_images
    ADD CONSTRAINT uc_review_images_images UNIQUE (images_id);

