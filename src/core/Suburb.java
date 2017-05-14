package core;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class Suburb
{
    private static final ArrayList<String> suburbs = new ArrayList<String>();

    public static boolean isSuburb(final String suburbStr) {
        return Suburb.suburbs.contains(suburbStr);
    }

    static{
        File file = new File("suburbs.txt");

        try {
            Scanner in = new Scanner(file);

            while(in.hasNext()){
                suburbs.add(in.nextLine());
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        suburbs.forEach(System.out::println);
    }

    public static int indexOf(String suburb){
        return suburbs.indexOf(suburb);
    }
}
