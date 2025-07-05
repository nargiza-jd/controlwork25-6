package kg.attractor.java.server;

public enum ResponseCode {
    OK(200),
    SEE_OTHER(303),
    NOT_FOUND(404),
    METHOD_NOT_ALLOWED(405),
    INTERNAL_SERVER_ERROR(500);

    private final int code;

    ResponseCode(int c) {
        this.code = c;
    }

    public int getCode() {
        return code;
    }
}