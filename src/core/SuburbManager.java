package core;

import net.dv8tion.jda.core.utils.SimpleLog;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class SuburbManager
{
    private final ArrayList<String> suburbs = new ArrayList<>();

    public SuburbManager(File file) {
        try {
            Scanner in = new Scanner(file);

            suburbs.clear();

            while(in.hasNext()){
                suburbs.add(in.nextLine().toLowerCase());
            }

        } catch (FileNotFoundException e) {
            MessageListener.novabotLog.log(SimpleLog.Level.WARNING, "Couldn't find suburbs.txt, ignoring");
        }
    }

    public boolean isSuburb(final String suburbStr) {
        return suburbs.contains(suburbStr);
    }

    public int indexOf(String suburb){
        return suburbs.indexOf(suburb);
    }
}
