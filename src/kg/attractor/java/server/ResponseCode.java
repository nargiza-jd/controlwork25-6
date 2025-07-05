package kg.attractor.java.server;
public enum ResponseCode {
    OK(200), NOT_FOUND(404), REDIRECT_303(303);
    private final int code;
    ResponseCode(int c){ code = c; }
    public int get() { return code; }
}
