package com.ecommerce.search_service.Exception;

public class IndexingException extends SearchException {

    public IndexingException(String message) {
        super(message);
    }

    public IndexingException(String message, Throwable cause) {
        super(message, cause);
    }
}
