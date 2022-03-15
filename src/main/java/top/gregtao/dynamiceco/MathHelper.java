package top.gregtao.dynamiceco;

public class MathHelper {

    public static int parseInt(String str) {
        int integer;
        try {
            integer = Integer.parseInt(str);
        } catch (NumberFormatException e) {
            return -1;
        }
        return integer;
    }

    public static float parseFloat(String str) {
        float num;
        try {
            num = Float.parseFloat(str);
        } catch (NumberFormatException e) {
            return -1;
        }
        return num;
    }

}
