package com.lastcalleats.common.exception;

import lombok.Getter;

@Getter
public enum ErrorCode {

    // General
    BAD_REQUEST(400, "Bad request"),
    UNAUTHORIZED(401, "Unauthorized"),
    FORBIDDEN(403, "Forbidden"),
    NOT_FOUND(404, "Resource not found"),
    INTERNAL_ERROR(500, "Internal server error"),

    // Auth
    INVALID_CREDENTIALS(401, "Invalid email or password"),
    TOKEN_EXPIRED(401, "Token expired"),
    TOKEN_INVALID(401, "Token invalid"),
    EMAIL_ALREADY_EXISTS(400, "Email already registered"),

    // User
    USER_NOT_FOUND(404, "User not found"),

    // Merchant
    MERCHANT_NOT_FOUND(404, "Merchant not found"),

    // Product
    TEMPLATE_NOT_FOUND(404, "Product template not found"),
    LISTING_NOT_FOUND(404, "Product listing not found"),
    LISTING_NOT_AVAILABLE(400, "Product listing is not available"),
    LISTING_SOLD_OUT(400, "Product listing is sold out"),

    // Order
    ORDER_NOT_FOUND(404, "Order not found"),
    ORDER_ALREADY_EXISTS(400, "You have already ordered this item today"),
    ORDER_PAYMENT_EXPIRED(400, "Payment time has expired"),
    ORDER_STATUS_INVALID(400, "Invalid order status for this operation"),

    // Pickup
    PICKUP_CODE_INVALID(400, "Invalid pickup code"),
    PICKUP_CODE_ALREADY_USED(400, "Pickup code has already been used"),

    // Payment
    PAYMENT_FAILED(400, "Payment failed"),
    PAYMENT_METHOD_NOT_SUPPORTED(400, "Payment method not supported"),

    // Review
    REVIEW_ALREADY_EXISTS(400, "You have already reviewed this order"),
    REVIEW_NOT_ALLOWED(400, "Order must be completed before reviewing"),

    // Post
    POST_NOT_FOUND(404, "Post not found"),
    POST_FORBIDDEN(403, "You are not allowed to delete this post"),

    // Favorite
    FAVORITE_ALREADY_EXISTS(400, "Already favorited"),
    FAVORITE_NOT_FOUND(404, "Favorite not found");

    private final int httpStatus;
    private final String message;

    ErrorCode(int httpStatus, String message) {
        this.httpStatus = httpStatus;
        this.message = message;
    }
}
