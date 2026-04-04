CREATE TABLE CATEGORIES (
    category_id BIGINT PRIMARY KEY,
    name VARCHAR(512) NOT NULL,
    description VARCHAR(4000)
);

CREATE TABLE PRODUCTS (
    product_id BIGINT PRIMARY KEY,
    name VARCHAR(512) NOT NULL,
    description VARCHAR(4000),
    category_id BIGINT NOT NULL,
    price DECIMAL(19, 2) NOT NULL,
    stock_quantity INT NOT NULL,
    image_url VARCHAR(1024),
    created_at DATE NOT NULL,
    updated_at DATE NOT NULL,
    CONSTRAINT fk_products_category FOREIGN KEY (category_id) REFERENCES CATEGORIES (category_id)
);
