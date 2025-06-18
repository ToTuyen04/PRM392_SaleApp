package com.salesapp.exception;

import java.util.List;

public class MultiAppException extends RuntimeException {
  List<AppException> appExceptions;
    public MultiAppException(List<AppException> appExceptions) {
        this.appExceptions = appExceptions;
    }
}
