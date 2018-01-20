package com.github.novskey.novabot.Util;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class UtilityFunctions {

    public static final ZoneId UTC = ZoneId.of("UTC");

    public static String capitaliseFirst(final String string) {
        final char[] chars = string.toCharArray();
        chars[0] = Character.toUpperCase(chars[0]);
        return new String(chars);
    }

    public static ZonedDateTime getCurrentTime(ZoneId timezone) {
        return ZonedDateTime.now(timezone);
    }

    public static void main(String[] args) {
        System.out.println(parseList("[]"));
    }

    public static ArrayList<String> parseList(String strList) {
        ArrayList<String> list = new ArrayList<>();

        String[] idStrings = strList.substring(1, strList.length() - 1).split(",");

        for (String idString : idStrings) {
            if (!idString.isEmpty()) {
                list.add(idString.trim());
            }
        }

        return list;
    }

    public static String arrayToString(Object[] strings) {
        String str = "";

        for (int i = 0; i < strings.length; i++) {
            if (i != 0){
                str += ",";
            }
            str += strings[i];
        }
        return str;
    }

    public static <K,V> ConcurrentMap<K,V> concurrentFilterByKey (ConcurrentMap<K, V> map, Predicate<K> predicate) {
        return map.entrySet()
                .stream()
                .filter(entry -> predicate.test(entry.getKey()))
                .collect(Collectors.toConcurrentMap(ConcurrentMap.Entry::getKey, ConcurrentMap.Entry::getValue));
    }

    public static <K, V> ConcurrentMap<K, V> concurrentFilterByValue(ConcurrentMap<K, V> map, Predicate<V> predicate) {
        return map.entrySet()
                .stream()
                .filter(m -> predicate.test(m.getValue()))
                .collect(Collectors.toConcurrentMap(ConcurrentMap.Entry::getKey, ConcurrentMap.Entry::getValue));
    }


    public static <K, V> Map<K, V> filterByValue(Map<K, V> map, Predicate<V> predicate) {
        return map.entrySet()
                .stream()
                .filter(entry -> predicate.test(entry.getValue()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    public static <K, V> Map<K, V> filterByKey(Map<K, V> map, Predicate<K> predicate) {
        return map.entrySet()
                .stream()
                .filter(m -> predicate.test(m.getKey()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }
}
