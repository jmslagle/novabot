package nests;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.ValueRange;
import core.Pokemon;
import core.Region;

import java.io.File;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class NestSheetManager
{
    private static final String NEST_DATA_ID = "1Scx_49MhfziXhugkW1SK-6X4gDZ7pA-EMSL-WlV17CM";
    private static NetHttpTransport httpTransport;
    private static Sheets service;
    private static final String APPLICATION_NAME = "CBR Sightings Nests";
    private static final JacksonFactory JSON_FACTORY;
    private static HttpTransport HTTP_TRANSPORT;
    private static final List<String> SCOPES;

    public static void main(final String[] args) {
        final String spreadsheetId = "1Scx_49MhfziXhugkW1SK-6X4gDZ7pA-EMSL-WlV17CM";
    }

    public static ArrayList<Nest> getNests(final NestSearch nestSearch) {
        final String range = "Data_Entry!B2:K";
        final ArrayList<Nest> nests = new ArrayList<Nest>();
        ValueRange response = null;
        try {
            response = NestSheetManager.service.spreadsheets().values().get("1Scx_49MhfziXhugkW1SK-6X4gDZ7pA-EMSL-WlV17CM", range).execute();
            final List<List<Object>> values = response.getValues();
            if (values == null || values.size() == 0) {
                System.out.println("No data found.");
            }
            else {
                for (final List<Object> value : values) {
                    if (value.get(6).toString().isEmpty()) {
                        continue;
                    }
                    final Pokemon poke = new Pokemon(value.get(6).toString());
                    final NestStatus status = NestStatus.fromString(value.get(5).toString().toLowerCase());
                    for (final Pokemon pokemon : nestSearch.getPokemon()) {
                        if (poke.name == null) {
                            System.out.println("ouchi");
                        }
                        if (poke.name.equals(pokemon.name)) {
                            for (final NestStatus s : nestSearch.getStatuses()) {
                                if (status == s) {
                                    nests.add(new Nest(Region.fromNestString(value.get(0).toString().toLowerCase()),
                                            (String) value.get(1),
                                            (String) value.get(2),
                                            (String) value.get(3),
                                            NestType.fromString((String) value.get(4)),
                                            status,
                                            poke,
                                            (String) value.get(9)));
                                    System.out.print("Found: ");
                                    System.out.println(value);
                                    System.out.println(nests.get(nests.size() - 1));
                                }
                            }
                        }
                    }
                }
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return nests;
    }

    public static ArrayList<Nest> getNestsByPokemon(final Pokemon pokemon) {
        System.out.println("Searching for " + pokemon.name + " nest");
        final String range = "Data_Entry!B2:K";
        final ArrayList<Nest> nests = new ArrayList<Nest>();
        ValueRange response = null;
        try {
            response = NestSheetManager.service.spreadsheets().values().get("1Scx_49MhfziXhugkW1SK-6X4gDZ7pA-EMSL-WlV17CM", range).execute();
            final List<List<Object>> values = response.getValues();
            if (values == null || values.size() == 0) {
                System.out.println("No data found.");
            }
            else {
                for (final List<Object> value : values) {
                    if (value.get(6).toString().isEmpty()) {
                        continue;
                    }
                    final Pokemon poke = new Pokemon(value.get(6).toString());
                    if (!poke.name.equals(pokemon.name)) {
                        continue;
                    }
                    nests.add(new Nest(Region.fromNestString(value.get(0).toString().toLowerCase()),
                            (String) value.get(1),
                            (String) value.get(2),
                            (String) value.get(3),
                            NestType.fromString((String) value.get(4)),
                            NestStatus.fromString((String) value.get(5)),
                            poke,
                            (String) value.get(9)));
                    System.out.print("Found: ");
                    System.out.println(value);
                    System.out.println(nests.get(nests.size() - 1));
                }
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return nests;
    }

    public static ArrayList<Nest> getNestsByStatus(final NestStatus[] statuses) {
        System.out.println("Searching for nests");
        final String range = "Data_Entry!B2:K";
        final ArrayList<Nest> nests = new ArrayList<Nest>();
        ValueRange response = null;
        try {
            response = NestSheetManager.service.spreadsheets().values().get("1Scx_49MhfziXhugkW1SK-6X4gDZ7pA-EMSL-WlV17CM", range).execute();
            final List<List<Object>> values = response.getValues();
            if (values == null || values.size() == 0) {
                System.out.println("No data found.");
            }
            else {
                for (final List<Object> value : values) {
                    if (value.get(6).toString().isEmpty()) {
                        continue;
                    }
                    final NestStatus status = NestStatus.fromString(value.get(5).toString().toLowerCase());
                    if (!contains(statuses, status)) {
                        continue;
                    }
                    final Pokemon poke = new Pokemon((String) value.get(6));
                    nests.add(new Nest(Region.fromNestString(value.get(0).toString().toLowerCase()),
                            (String) value.get(1),
                            (String) value.get(2),
                            (String) value.get(3),
                            NestType.fromString((String) value.get(4)),
                            NestStatus.fromString((String) value.get(5)),
                            poke,
                            (String) value.get(9)));
                    System.out.print("Found: ");
                    System.out.println(value);
                    System.out.println(nests.get(nests.size() - 1));
                }
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return nests;
    }

    private static boolean contains(final NestStatus[] statuses, final NestStatus status) {
        for (final NestStatus s : statuses) {
            if (s == status) {
                return true;
            }
        }
        return false;
    }

    private static Sheets getSheetsService() throws IOException {
        Credential credential = null;
        try {
            credential = getCredential();
        }
        catch (GeneralSecurityException e) {
            e.printStackTrace();
        }
        return new Sheets.Builder(NestSheetManager.HTTP_TRANSPORT, NestSheetManager.JSON_FACTORY, credential).setApplicationName("CBR Sightings Nests").build();
    }

    private static Credential getCredential() throws GeneralSecurityException, IOException {
        final File p12 = new File("./key.p12");
        NestSheetManager.httpTransport = new NetHttpTransport();
        final com.google.api.client.json.jackson.JacksonFactory jsonFactory = new com.google.api.client.json.jackson.JacksonFactory();
        return new GoogleCredential.Builder().setTransport(NestSheetManager.httpTransport).setJsonFactory(jsonFactory).setServiceAccountId("715488264349-compute@developer.gserviceaccount.com").setServiceAccountScopes(NestSheetManager.SCOPES).setServiceAccountPrivateKeyFromP12File(p12).build();
    }

    static {
        JSON_FACTORY = JacksonFactory.getDefaultInstance();
        SCOPES = Collections.singletonList("https://www.googleapis.com/auth/spreadsheets.readonly");
        try {
            NestSheetManager.HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
            NestSheetManager.service = getSheetsService();
        }
        catch (Throwable t) {
            t.printStackTrace();
            System.exit(1);
        }
    }
}
