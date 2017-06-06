package core;

import java.util.ArrayList;

public class Util
{
    public static String capitaliseFirst(final String string) {
        final char[] chars = string.toCharArray();
        chars[0] = Character.toUpperCase(chars[0]);
        return new String(chars);
    }


    public static ArrayList<String> parseList(String strList){
        ArrayList<String> list = new ArrayList<>();

        String[] idStrings = strList.substring(1,strList.length()-1).split(",");

        for (String idString : idStrings) {
            list.add(idString.trim());
        }

        return list;
    }
}
