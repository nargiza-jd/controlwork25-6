package kg.attractor.java.server;

public enum HttpStatusCode {
    OK(200),
    SEE_OTHER(303),
    BAD_REQUEST(400),
    NOT_FOUND(404),
    METHOD_NOT_ALLOWED(405),
    INTERNAL_SERVER_ERROR(500);

    private final int code;

    HttpStatusCode(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public static HttpStatusCode fromCode(int statusCode) {
        for (HttpStatusCode c : values()) {
            if (c.code == statusCode) {
                return c;
            }
        }
        return INTERNAL_SERVER_ERROR;
    }
}