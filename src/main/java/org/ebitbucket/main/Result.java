package org.ebitbucket.main;

@SuppressWarnings("unused")
public final class Result {
    public static final int OK = 0;
    public  static final int NOT_FOUND = 1;
    public  static final int INVALID_REQUEST = 2;
    public  static final int INCORRECT_REQUEST = 3;
    public  static final int UNKOWN_ERROR = 4;
    public  static final int USER_ALREADY_EXISTS = 5;

    private final int code;
    private final Object response;

    public Result(int code, Object response) {
        this.code = code;
        this.response = response;
    }

    public int getCode() {
        return code;
    }

    public Object getResponse() {
        return response;
    }

    public static Result ok(Object response) {
        return new Result(OK, response);
    }

    public static Result ok(){
        return new Result(OK,"OK");
    }

    public static Result notFound() {
        return new Result(NOT_FOUND, "Not found");
    }

    public static Result invalidReques(){
        return new Result(INVALID_REQUEST,"Invalid request");
    }

    public static Result incorrectRequest(){
        return new Result(INCORRECT_REQUEST,"Incorrect request");
    }

    public static Result unkownError(){
        return new Result(UNKOWN_ERROR,"Unkown error");
    }

    public static Result userAlreadyExists(){
        return new Result(USER_ALREADY_EXISTS,"User already exists");
    }
}
