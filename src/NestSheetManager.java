import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.api.services.sheets.v4.model.ValueRange;
import com.google.gdata.data.spreadsheet.SpreadsheetEntry;
import com.google.gdata.data.spreadsheet.WorksheetEntry;

import java.io.File;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
/**
 * Created by Paris on 23/03/2017.
 */
public class NestSheetManager {

    public static final String GOOGLE_ACCOUNT_USERNAME = "cbrsightings@gmail.com";
    public static final String GOOGLE_ACCOUNT_PASSWORD = "k4n30M!M!P@ri$d0mP3ggy";

    public static final String SPREADSHEET_URL = "https://docs.google.com/spreadsheets/d/19xKJHV6PaOLaBQVegGYNREd89YBNg6g83Jdy5kJCNx0/edit?usp=sharing";
    private static final String NEST_DATA_ID = "1Scx_49MhfziXhugkW1SK-6X4gDZ7pA-EMSL-WlV17CM";

    private static NetHttpTransport httpTransport;

    private static SpreadsheetEntry spreadsheet;

    private static WorksheetEntry nestData;
    private static Sheets service;

    /** Application name. */
    private static final String APPLICATION_NAME =
            "CBR Sightings Nests";

    /** Global instance of the {@link FileDataStoreFactory}. */
    private static FileDataStoreFactory DATA_STORE_FACTORY;

    /** Global instance of the JSON factory. */
    private static final com.google.api.client.json.jackson2.JacksonFactory JSON_FACTORY =
            com.google.api.client.json.jackson2.JacksonFactory.getDefaultInstance();

    /** Global instance of the HTTP transport. */
    private static HttpTransport HTTP_TRANSPORT;

    /** Global instance of the scopes required by this quickstart.
     *
     * If modifying these scopes, delete your previously saved credentials
     * at ~/.credentials/sheets.googleapis.com-java-quickstart
     */
    private static final List<String> SCOPES =
            Arrays.asList(SheetsScopes.SPREADSHEETS_READONLY);

    static {
        try {
            HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
//            DATA_STORE_FACTORY = new FileDataStoreFactory(DATA_STORE_DIR);
            service = getSheetsService();
        } catch (Throwable t) {
            t.printStackTrace();
            System.exit(1);
        }
    }

    public static void main(String[] args) {
        String spreadsheetId = NEST_DATA_ID;

//        getNestsByPokemon(new Pokemon("Girafarig"));

//        NestStatus[] statuses = new ArrayList<>();
//        statuses.add(NestStatus.Confirmed);

//        System.out.println(getNestsByStatus(statuses));
    }



    public static ArrayList<Nest> getNests(Pokemon[] pokeList, NestStatus[] statuses) {
        String range = "Data_Entry!B2:K";

        ArrayList<Nest> nests = new ArrayList<>();

        ValueRange response = null;
        try {
            response = service.spreadsheets().values()
                    .get(NEST_DATA_ID, range)
                    .execute();
            List<List<Object>> values = response.getValues();
            if (values == null || values.size() == 0) {
                System.out.println("No data found.");
            } else {
                for (List<Object> value : values) {

                    if(value.get(6).toString().isEmpty()) continue;
                    Pokemon poke = new Pokemon((String) value.get(6));

                    NestStatus status = NestStatus.fromString(value.get(5).toString().toLowerCase());

                    for (Pokemon pokemon : pokeList) {
                        if(poke.name.equals(pokemon.name)){

                            for (NestStatus s : statuses) {
                                if(status == s){
                                    nests.add(
                                            new Nest(
                                                    Region.fromString(value.get(0).toString().toLowerCase()),
                                                    (String) value.get(1),
                                                    (String) value.get(2),
                                                    (String) value.get(3),
                                                    NestType.fromString((String) value.get(4)),
                                                    status,
                                                    poke,
                                                    ((String) value.get(9))
                                            )
                                    );
                                    System.out.print("Found: ");
                                    System.out.println(value);
                                    System.out.println(nests.get(nests.size()-1));
                                }
                            }
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return nests;
    }

    public static ArrayList<Nest> getNestsByPokemon(Pokemon pokemon){
        System.out.println("Searching for " + pokemon.name + " nest");
        String range = "Data_Entry!B2:K";

        ArrayList<Nest> nests = new ArrayList<>();

        ValueRange response = null;
        try {
            response = service.spreadsheets().values()
                    .get(NEST_DATA_ID, range)
                    .execute();
            List<List<Object>> values = response.getValues();
            if (values == null || values.size() == 0) {
                System.out.println("No data found.");
            } else {
                for (List<Object> value : values) {

                    if(value.get(6).toString().isEmpty()) continue;
                    Pokemon poke = new Pokemon((String) value.get(6));

                    if(poke.name.equals(pokemon.name)){
                        nests.add(
                            new Nest(
                                Region.fromString(value.get(0).toString().toLowerCase()),
                                (String) value.get(1),
                                (String) value.get(2),
                                (String) value.get(3),
                                NestType.fromString((String) value.get(4)),
                                NestStatus.fromString(value.get(5).toString().toLowerCase()),
                                poke,
                                ((String) value.get(9))
                            )
                        );
                        System.out.print("Found: ");
                        System.out.println(value);
                        System.out.println(nests.get(nests.size()-1));
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return nests;
    }

    public static ArrayList<Nest> getNestsByStatus(NestStatus[] statuses){
        System.out.println("Searching for nests");
        String range = "Data_Entry!B2:K";

        ArrayList<Nest> nests = new ArrayList<>();

        ValueRange response = null;
        try {
            response = service.spreadsheets().values()
                    .get(NEST_DATA_ID, range)
                    .execute();
            List<List<Object>> values = response.getValues();
            if (values == null || values.size() == 0) {
                System.out.println("No data found.");
            } else {
                for (List<Object> value : values) {

                    if(value.get(6).toString().isEmpty()) continue;


                    NestStatus status = NestStatus.fromString(value.get(5).toString().toLowerCase());

                    if(contains(statuses,status)){
                        Pokemon poke = new Pokemon((String) value.get(6));

                        nests.add(
                                new Nest(
                                        Region.fromString(value.get(0).toString().toLowerCase()),
                                        (String) value.get(1),
                                        (String) value.get(2),
                                        (String) value.get(3),
                                        NestType.fromString((String) value.get(4)),
                                        NestStatus.fromString(value.get(5).toString().toLowerCase()),
                                        poke,
                                        ((String) value.get(9))
                                )
                        );
                        System.out.print("Found: ");
                        System.out.println(value);
                        System.out.println(nests.get(nests.size()-1));
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return nests;
    }

    private static boolean contains(NestStatus[] statuses, NestStatus status) {
        for (NestStatus s : statuses) {
            if(s == status) return true;
        }
        return false;
    }

    /**
     * Build and return an authorized Sheets API client service.
     * @return an authorized Sheets API client service
     * @throws IOException
     */
    public static Sheets getSheetsService() throws IOException {
        Credential credential = null;
        try {
            credential = getCredential();
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        }
        return new Sheets.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential)
                .setApplicationName(APPLICATION_NAME)
                .build();
    }

    private static Credential getCredential() throws GeneralSecurityException, IOException {
        File p12 = new File("./key.p12");

        httpTransport = new NetHttpTransport();
        JacksonFactory jsonFactory = new JacksonFactory();
        String[] SCOPESArray = {"https://spreadsheets.google.com/feeds", "https://spreadsheets.google.com/feeds/spreadsheets/private/full", "https://docs.google.com/feeds"};
        GoogleCredential credential = new GoogleCredential.Builder()
                .setTransport(httpTransport)
                .setJsonFactory(jsonFactory)
                .setServiceAccountId("715488264349-compute@developer.gserviceaccount.com")
                .setServiceAccountScopes(SCOPES)
                .setServiceAccountPrivateKeyFromP12File(p12)
                .build();

        return credential;
    }

}

