package top.gregtao.dynamiceco;

public enum TitleColor {
    BLACK("��0"),
    BLUE("��1"),
    GREEN("��2"),
    AQUA("��3"),
    RED("��4"),
    PURPLE("��5"),
    ORANGE("��6"),
    LIGHT_GREY("��7"),
    GREY("��8"),
    LIGHT_BLUE("��9"),
    LIGHT_GREEN("��a"),
    LIGHT_AQUA("��b"),
    LIGHT_RED("��c"),
    PINK("��d"),
    YELLOW("��e"),
    WHITE("��f"),
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