package core;

public class Util
{
    public static String capitaliseFirst(final String string) {
        final char[] chars = string.toCharArray();
        chars[0] = Character.toUpperCase(chars[0]);
        return new String(chars);
    }
}
