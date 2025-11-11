SELECT *from ecommerce.public.products;





DROP TABLE IF EXISTS products CASCADE;
CREATE TABLE products
(
    id         BIGSERIAL PRIMARY KEY,
    name       VARCHAR(255) NOT NULL,
    stock      INTEGER      NOT NULL,
    category   VARCHAR(500),
    is_active  BOOLEAN   DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

DROP TABLE IF EXISTS orders CASCADE;
CREATE TABLE orders
(
    id            BIGSERIAL PRIMARY KEY,
    customer_name VARCHAR(255)   NOT NULL,
    customer_mail VARCHAR(255)   NOT NULL,
    status        VARCHAR(50) DEFAULT 'COMPLETED',
    total_amount  DECIMAL(10, 2) NOT NULL CHECK (total_amount >= 0),
    created_at    TIMESTAMP   DEFAULT CURRENT_TIMESTAMP
);


DROP TABLE IF EXISTS order_items CASCADE;
CREATE TABLE order_items
(
    id          SERIAL PRIMARY KEY,
    orders_id    INT            NOT NULL,
    product_id  INT            NOT NULL,
    quantity    INT            NOT NULL CHECK (quantity > 0),
    unit_price  NUMERIC(10, 2) NOT NULL CHECK (unit_price >= 0),
    total_price NUMERIC(10, 2) GENERATED ALWAYS AS (quantity * unit_price) STORED,
    FOREIGN KEY (orders_id) REFERENCES orders (id),
    FOREIGN KEY (product_id) REFERENCES products (id)
);

INSERT INTO products (name, stock, category, is_active)
VALUES
    ('Laptop', 10, 'Electronics', TRUE),
    ('Smartphone', 20, 'Electronics', TRUE),
    ('Book', 50, 'Books', TRUE),
    ('Desk Chair', 15, 'Furniture', TRUE),
    ('Headphones', 25, 'Electronics', TRUE);


INSERT INTO orders (customer_name, customer_mail, status, total_amount)
VALUES
    ('Asilbek', 'asilbek@example.com', 'COMPLETED', 1500.00),
    ('Dilshod', 'dilshod@example.com', 'COMPLETED', 300.00),
    ('Nilufar', 'nilufar@example.com', 'PENDING', 450.00);

INSERT INTO order_items (orders_id, product_id, quantity, unit_price)
VALUES
    (1, 1, 1, 1000.00),  -- Asilbekning Laptopi
    (1, 5, 5, 100.00),   -- Asilbekning Headphones
    (2, 3, 2, 150.00),   -- Dilshodning Book
    (3, 2, 1, 300.00);   -- Nilufarning Smartphone


SELECT * from products;
SELECT *from orders;
SELECT *from order_items;


ALTER TABLE products
    ADD COLUMN price NUMERIC(10,2) DEFAULT 0 NOT NULL;
UPDATE products
SET price = CASE
                WHEN name = 'Laptop' THEN 1000
                WHEN name = 'Smartphone' THEN 500
                WHEN name = 'Book' THEN 50
                WHEN name = 'Desk Chair' THEN 200
                WHEN name = 'Headphones' THEN 100
    END;
