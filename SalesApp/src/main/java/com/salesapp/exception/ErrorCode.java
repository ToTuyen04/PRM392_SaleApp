package com.salesapp.exception;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public enum ErrorCode {

    /// User
    UNCATEGORIES_EXCEPTION(9999, "Uncategories exception", HttpStatus.INTERNAL_SERVER_ERROR),
    EMAL_EXIST(1001, "This email is already in used", HttpStatus.CONFLICT),
    PHONE_EXIST(1002, "This phone is already in used", HttpStatus.CONFLICT),
    UNAUTHORIZE(1003, "You do not have permission", HttpStatus.UNAUTHORIZED),
    UNAUTHENTICATED(1004, "Unauthenticated", HttpStatus.UNAUTHORIZED),
    USER_NOTFOUND(1005, "User not found",HttpStatus.NOT_FOUND),
    USER_NOT_FOUND(1006, "User not found", HttpStatus.NOT_FOUND),
    INCORECT_PASSWORD(1007, "Password is incorrect", HttpStatus.BAD_REQUEST),


    /// Category
    CATEGORY_NOT_FOUND(2001, "Category not found", HttpStatus.NOT_FOUND),
    CATEGORY_NAME_EXIST(2002, "Category name already exists", HttpStatus.CONFLICT),

    /// Product
    PRODUCT_NOT_FOUND(3001, "Product not found", HttpStatus.NOT_FOUND),
    PRODUCT_NAME_EXIST(3002, "Product name already exists", HttpStatus.CONFLICT),

    /// Cart & CartItem
    CART_NOT_FOUND(4001, "Cart not found", HttpStatus.NOT_FOUND),
    ITEM_NOT_FOUND(4002, "Cart item not found", HttpStatus.NOT_FOUND),

    /// Order
    ORDER_NOT_FOUND(5001, "Order not found", HttpStatus.NOT_FOUND),

    /// Payment
    PAYMENT_INVALID_SIGN(6001, "Payment signature is invalid", HttpStatus.BAD_REQUEST),
    ;
    ErrorCode(int code, String message, HttpStatusCode statusCode){
        this.code = code;
        this.message = message;
        this.statusCode = statusCode;
    }
    int code;
    String message;
    HttpStatusCode statusCode;
}
