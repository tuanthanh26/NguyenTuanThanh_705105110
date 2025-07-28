package com.example.nguyentuanthanh_705105110.data;


public class Result<T> {
    private Result() {}

    public static final class Success<T> extends Result<T> {
        private final T data;
        public Success(T data) { this.data = data; }
        public T getData() { return this.data; }
    }

    public static final class Error extends Result {
        private final Exception error;
        public Error(Exception error) { this.error = error; }
        public Exception getError() { return this.error; }
    }
}