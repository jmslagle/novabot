package com.github.novskey.novabot.core;


import java.io.IOException;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.Scanner;
import java.util.TreeSet;

public class SuburbManager {
    private final TreeSet<String> suburbs = new TreeSet<>();

    public SuburbManager(Path file, NovaBot novaBot) {
        try {
            Scanner in = new Scanner(file);

            suburbs.clear();

            while (in.hasNext()) {
                suburbs.add(in.nextLine().toLowerCase().trim());
            }

        } catch (NoSuchFileException e) {
            novaBot.novabotLog.warn("Couldn't find suburbs.txt, ignoring");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean isSuburb(final String suburbStr) {
        return suburbs.contains(suburbStr);
    }

    public boolean notEmpty() {
        return suburbs.size() > 0;
    }

    public String getListMessage() {
        StringBuilder stringBuilder = new StringBuilder();

        for (String suburb : suburbs) {
            stringBuilder.append(String.format("`%s`%n",suburb));
        }

        return stringBuilder.toString();
    }
}
