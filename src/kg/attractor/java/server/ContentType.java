package kg.attractor.java.server;
public enum ContentType {
    TEXT_HTML("text/html; charset=UTF-8"),
    TEXT_CSS ("text/css"),
    IMAGE_JPEG("image/jpeg"),
    IMAGE_PNG ("image/png"),
    TEXT_PLAIN("text/plain; charset=UTF-8");
    private final String value;
    ContentType(String v){ value = v; }
    @Override public String toString(){ return value; }
}
