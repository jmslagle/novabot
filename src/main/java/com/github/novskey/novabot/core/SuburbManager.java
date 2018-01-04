package com.github.novskey.novabot.core;


import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class SuburbManager {
    private final ArrayList<String> suburbs = new ArrayList<>();

    public SuburbManager(File file, NovaBot novaBot) {
        try {
            Scanner in = new Scanner(file);

            suburbs.clear();

            while (in.hasNext()) {
                suburbs.add(in.nextLine().toLowerCase());
            }

        } catch (FileNotFoundException e) {
            novaBot.novabotLog.warn("Couldn't find suburbs.txt, ignoring");
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
}
