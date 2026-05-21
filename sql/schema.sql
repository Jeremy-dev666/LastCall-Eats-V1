-- LastCall Eats Database Schema
-- PostgreSQL 16+
-- Run: createdb lastcall_eats

-- =====================
-- User table
-- =====================
CREATE TABLE IF NOT EXISTS "user" (
    id              BIGINT          GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    email           VARCHAR(255)    NOT NULL,
    password_hash   VARCHAR(255)    NOT NULL,
    nickname        VARCHAR(100)    NOT NULL,
    avatar_url      VARCHAR(500)    DEFAULT NULL,
    is_active       BOOLEAN         NOT NULL DEFAULT TRUE,
    created_at      TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_user_email UNIQUE (email)
);

-- =====================
-- Merchant table
-- =====================
CREATE TABLE IF NOT EXISTS merchant (
    id              BIGINT          GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    email           VARCHAR(255)    NOT NULL,
    password_hash   VARCHAR(255)    NOT NULL,
    name            VARCHAR(255)    NOT NULL,
    address         VARCHAR(500)    NOT NULL,
    business_hours  VARCHAR(255)    DEFAULT NULL,
    is_active       BOOLEAN         NOT NULL DEFAULT TRUE,
    created_at      TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_merchant_email UNIQUE (email)
);

-- =====================
-- Product template table
-- =====================
CREATE TABLE IF NOT EXISTS product_template (
    id              BIGINT          GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    merchant_id     BIGINT          NOT NULL,
    name            VARCHAR(255)    NOT NULL,
    description     TEXT            DEFAULT NULL,
    original_price  DECIMAL(10, 2)  NOT NULL,
    is_active       BOOLEAN         NOT NULL DEFAULT TRUE,
    created_at      TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_template_merchant FOREIGN KEY (merchant_id) REFERENCES merchant (id)
);
CREATE INDEX IF NOT EXISTS idx_template_merchant ON product_template (merchant_id);

-- =====================
-- Product listing table
-- =====================
CREATE TABLE IF NOT EXISTS product_listing (
    id                  BIGINT          GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    merchant_id         BIGINT          NOT NULL,
    template_id         BIGINT          NOT NULL,
    discount_price      DECIMAL(10, 2)  NOT NULL,
    quantity            INT             NOT NULL,
    remaining_quantity  INT             NOT NULL,
    pickup_start        TIME            NOT NULL,
    pickup_end          TIME            NOT NULL,
    date                DATE            NOT NULL,
    is_available        BOOLEAN         NOT NULL DEFAULT TRUE,
    created_at          TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at          TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_listing_merchant FOREIGN KEY (merchant_id) REFERENCES merchant (id),
    CONSTRAINT fk_listing_template FOREIGN KEY (template_id) REFERENCES product_template (id)
);
CREATE INDEX IF NOT EXISTS idx_listing_merchant ON product_listing (merchant_id);
CREATE INDEX IF NOT EXISTS idx_listing_template ON product_listing (template_id);
CREATE INDEX IF NOT EXISTS idx_listing_date ON product_listing (date);

-- =====================
-- User favorite table
-- =====================
CREATE TABLE IF NOT EXISTS user_favorite (
    id          BIGINT      GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    user_id     BIGINT      NOT NULL,
    listing_id  BIGINT      NOT NULL,
    created_at  TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_user_favorite UNIQUE (user_id, listing_id),
    CONSTRAINT fk_user_favorite_user FOREIGN KEY (user_id) REFERENCES "user" (id),
    CONSTRAINT fk_user_favorite_listing FOREIGN KEY (listing_id) REFERENCES product_listing (id)
);
CREATE INDEX IF NOT EXISTS idx_user_favorite_user ON user_favorite (user_id);
CREATE INDEX IF NOT EXISTS idx_user_favorite_listing ON user_favorite (listing_id);

-- =====================
-- Orders table
-- =====================
CREATE TABLE IF NOT EXISTS orders (
    id          BIGINT          GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    user_id     BIGINT          NOT NULL,
    listing_id  BIGINT          NOT NULL,
    merchant_id BIGINT          NOT NULL,
    price       DECIMAL(10, 2)  NOT NULL,
    status      VARCHAR(20)     NOT NULL,
    created_at  TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_order_user FOREIGN KEY (user_id) REFERENCES "user" (id),
    CONSTRAINT fk_order_merchant FOREIGN KEY (merchant_id) REFERENCES merchant (id),
    CONSTRAINT fk_order_listing FOREIGN KEY (listing_id) REFERENCES product_listing (id)
);
CREATE INDEX IF NOT EXISTS idx_order_user ON orders (user_id);
CREATE INDEX IF NOT EXISTS idx_order_merchant ON orders (merchant_id);
CREATE INDEX IF NOT EXISTS idx_order_listing ON orders (listing_id);

-- =====================
-- Pickup code table
-- =====================
CREATE TABLE IF NOT EXISTS pickup_code (
    id              BIGINT      GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    order_id        BIGINT      NOT NULL,
    numeric_code    VARCHAR(6)  NOT NULL,
    qr_code         TEXT        DEFAULT NULL,
    used            BOOLEAN     NOT NULL DEFAULT FALSE,
    created_at      TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_pickup_order UNIQUE (order_id),
    CONSTRAINT fk_pickup_order FOREIGN KEY (order_id) REFERENCES orders (id)
);

-- =====================
-- Review table
-- =====================
CREATE TABLE IF NOT EXISTS review (
    id          BIGINT      GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    order_id    BIGINT      NOT NULL,
    user_id     BIGINT      NOT NULL,
    merchant_id BIGINT      NOT NULL,
    template_id BIGINT      NOT NULL,
    rating      INT         NOT NULL,
    content     TEXT        DEFAULT NULL,
    image_urls  JSONB       DEFAULT NULL,
    is_visible  BOOLEAN     NOT NULL DEFAULT TRUE,
    created_at  TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_review_order UNIQUE (order_id),
    CONSTRAINT fk_review_order FOREIGN KEY (order_id) REFERENCES orders (id),
    CONSTRAINT fk_review_user FOREIGN KEY (user_id) REFERENCES "user" (id),
    CONSTRAINT fk_review_merchant FOREIGN KEY (merchant_id) REFERENCES merchant (id),
    CONSTRAINT fk_review_template FOREIGN KEY (template_id) REFERENCES product_template (id)
);
CREATE INDEX IF NOT EXISTS idx_review_user ON review (user_id);
CREATE INDEX IF NOT EXISTS idx_review_merchant ON review (merchant_id);
CREATE INDEX IF NOT EXISTS idx_review_template ON review (template_id);

-- =====================
-- Post table
-- =====================
CREATE TABLE IF NOT EXISTS post (
    id              BIGINT      GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    user_id         BIGINT      NOT NULL,
    merchant_id     BIGINT      DEFAULT NULL,
    content         TEXT        NOT NULL,
    image_urls      JSONB       DEFAULT NULL,
    like_count      INT         NOT NULL DEFAULT 0,
    comment_count   INT         NOT NULL DEFAULT 0,
    is_visible      BOOLEAN     NOT NULL DEFAULT TRUE,
    created_at      TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_post_user FOREIGN KEY (user_id) REFERENCES "user" (id),
    CONSTRAINT fk_post_merchant FOREIGN KEY (merchant_id) REFERENCES merchant (id)
);
CREATE INDEX IF NOT EXISTS idx_post_user ON post (user_id);
CREATE INDEX IF NOT EXISTS idx_post_merchant ON post (merchant_id);

-- =====================
-- Auto-update updated_at trigger
-- =====================
CREATE OR REPLACE FUNCTION update_updated_at()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

DO $$
DECLARE
    t TEXT;
BEGIN
    FOR t IN
        SELECT unnest(ARRAY['user', 'merchant', 'product_template', 'product_listing',
                             'orders', 'review', 'post'])
    LOOP
        EXECUTE format(
            'CREATE OR REPLACE TRIGGER trg_%s_updated_at
             BEFORE UPDATE ON %I
             FOR EACH ROW EXECUTE FUNCTION update_updated_at()',
            replace(t, '"', ''), t
        );
    END LOOP;
END;
$$;
