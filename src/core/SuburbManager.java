package core;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class SuburbManager
{
    private final ArrayList<String> suburbs = new ArrayList<String>();

    public SuburbManager(File file) {
        try {
            Scanner in = new Scanner(file);

            suburbs.clear();

            while(in.hasNext()){
                suburbs.add(in.nextLine().toLowerCase());
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public boolean isSuburb(final String suburbStr) {
        return suburbs.contains(suburbStr);
    }

    public int indexOf(String suburb){
        return suburbs.indexOf(suburb);
    }
}
