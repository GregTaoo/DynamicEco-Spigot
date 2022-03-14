package top.gregtao.dynamiceco;

public enum TitleColor {
    BLACK("¡ì0"),
    BLUE("¡ì1"),
    GREEN("¡ì2"),
    AQUA("¡ì3"),
    RED("¡ì4"),
    PURPLE("¡ì5"),
    ORANGE("¡ì6"),
    LIGHT_GREY("¡ì7"),
    GREY("¡ì8"),
    LIGHT_BLUE("¡ì9"),
    LIGHT_GREEN("¡ìa"),
    LIGHT_AQUA("¡ìb"),
    LIGHT_RED("¡ìc"),
    PINK("¡ìd"),
    YELLOW("¡ìe"),
    WHITE("¡ìf"),
    ;

    public final String code;

    TitleColor(String code) {
        this.code = code;
    }

    public String getWith(String string) {
        return this.code + string;
    }

    public static String getWith(TitleColor color, String string) {
        return color.code + string;
    }

    public String code() {
        return this.code;
    }
}