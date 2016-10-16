package org.ebitbucket.main;

public final class Result<V> {
    public static final int OK = 0;
    public static final int NOT_FOUND = 1;
    public static final int INVALID_REQUEST = 2;
    public static final int INCORRECT_REQUEST = 3;
    public static final int UNKOWN_ERROR = 4;
    public static final int USER_ALREADY_EXISTS = 5;

    private final int code;
    private final V response;

    public Result(int code, V response) {
        this.code = code;
        this.response = response;
    }

    public int getCode() {
        return code;
    }

    public V getResponse() {
        return response;
    }

    public static <V> Result<V> ok(V response) {
        return new Result<V>(OK, response);
    }

    public static Result<String> notFound() {
        return new Result<>(NOT_FOUND, "Not found");
    }

    public static Result<String> invalidReques(){
        return new Result<>(INVALID_REQUEST,"Invalid request");
    }

    public static Result<String> incorrectRequest(){
        return new Result<>(INCORRECT_REQUEST,"Incorrect request");
    }

    public static Result<String> unkownError(){
        return new Result<>(UNKOWN_ERROR,"Unkown error");
    }

    public static Result<String> userAlreadyExists(){
        return new Result<>(USER_ALREADY_EXISTS,"User already exists");
    }
}
