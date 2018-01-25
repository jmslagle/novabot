package com.github.novskey.novabot.core;


import java.io.IOException;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Scanner;

public class SuburbManager {
    private final ArrayList<String> suburbs = new ArrayList<>();

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

    public int indexOf(String suburb) {
        return suburbs.indexOf(suburb);
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
