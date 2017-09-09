package pokemon;

import core.Location;
import core.Region;

import java.util.*;

public class Pokemon
{
    private static final ArrayList<String> VALID_NAMES;
    public final String name;
    public float miniv;
    public float maxiv;
    private Location location;

    public Pokemon(final String name) {
        this.miniv = 0.0f;
        this.maxiv = 100.0f;
        if (nameToID(name.toLowerCase()) == 0) {
            if (name.toLowerCase().equals("nidoran f")) {
                this.name = "nidoranf";
            }
            else if (name.toLowerCase().equals("nidoran m")) {
                this.name = "nidoranm";
            }
            else {
                this.name = null;
            }
        }
        else {
            this.name = name.toLowerCase();
        }
    }

    private Pokemon(final String name, final Region region) {
        this(name);
    }

    private Pokemon(final String name, final Region region, final float miniv) {
        this(name, region);
        this.miniv = miniv;
    }

    public Pokemon(final String name, final Region region, final float miniv, final float maxiv) {
        this(name, region, miniv);
        this.maxiv = maxiv;
    }

    public Pokemon(final int id, final float min_iv, final float max_iv) {
        this(idToName(id));
        this.miniv = min_iv;
        this.maxiv = max_iv;
    }

    public Pokemon(final String pokeName, final Location location, final float miniv, final float maxiv) {
        this(pokeName);
        this.location = location;
        this.miniv = miniv;
        this.maxiv = maxiv;
    }

    private Pokemon(final int id) {
        this.miniv = 0.0f;
        this.maxiv = 100.0f;
        this.name = idToName(id);
    }

    public Pokemon(final int id, final Location location, final float miniv, final float maxiv) {
        this(id);
        this.location = location;
        this.miniv = miniv;
        this.maxiv = maxiv;
    }

    public static void main(final String[] args) {
        System.out.println(getIcon(new Pokemon("unown").getID()));
        for (int i = 1; i <= 26; ++i) {
            System.out.println(getIcon(new Pokemon(2010 + i).getID()));
        }
        for (int i = 1; i < 251; ++i) {
            System.out.println(getIcon(new Pokemon(i).getID()));
        }
    }

    public static int nameToID(final String pokeName) {
        switch (pokeName) {
            case "unowna": {
                return 2011;
            }
            case "unownb": {
                return 2012;
            }
            case "unownc": {
                return 2013;
            }
            case "unownd": {
                return 2014;
            }
            case "unowne": {
                return 2015;
            }
            case "unownf": {
                return 2016;
            }
            case "unowng": {
                return 2017;
            }
            case "unownh": {
                return 2018;
            }
            case "unowni": {
                return 2019;
            }
            case "unownj": {
                return 2020;
            }
            case "unownk": {
                return 2021;
            }
            case "unownl": {
                return 2022;
            }
            case "unownm": {
                return 2023;
            }
            case "unownn": {
                return 2024;
            }
            case "unowno": {
                return 2025;
            }
            case "unownp": {
                return 2026;
            }
            case "unownq": {
                return 2027;
            }
            case "unownr": {
                return 2028;
            }
            case "unowns": {
                return 2029;
            }
            case "unownt": {
                return 2030;
            }
            case "unownu": {
                return 2031;
            }
            case "unownv": {
                return 2032;
            }
            case "unownw": {
                return 2033;
            }
            case "unownx": {
                return 2034;
            }
            case "unowny": {
                return 2035;
            }
            case "unownz": {
                return 2036;
            }
            default: {
                return Pokemon.VALID_NAMES.indexOf(pokeName) + 1;
            }
        }
    }

    public static String idToName(final int id) {
        switch (id) {
            case 2011: {
                return "unowna";
            }
            case 2012: {
                return "unownb";
            }
            case 2013: {
                return "unownc";
            }
            case 2014: {
                return "unownd";
            }
            case 2015: {
                return "unowne";
            }
            case 2016: {
                return "unownf";
            }
            case 2017: {
                return "unowng";
            }
            case 2018: {
                return "unownh";
            }
            case 2019: {
                return "unowni";
            }
            case 2020: {
                return "unownj";
            }
            case 2021: {
                return "unownk";
            }
            case 2022: {
                return "unownl";
            }
            case 2023: {
                return "unownm";
            }
            case 2024: {
                return "unownn";
            }
            case 2025: {
                return "unowno";
            }
            case 2026: {
                return "unownp";
            }
            case 2027: {
                return "unownq";
            }
            case 2028: {
                return "unownr";
            }
            case 2029: {
                return "unowns";
            }
            case 2030: {
                return "unownt";
            }
            case 2031: {
                return "unownu";
            }
            case 2032: {
                return "unownv";
            }
            case 2033: {
                return "unownw";
            }
            case 2034: {
                return "unownx";
            }
            case 2035: {
                return "unowny";
            }
            case 2036: {
                return "unownz";
            }
            default: {
                return Pokemon.VALID_NAMES.get(id - 1);
            }
        }
    }

    public static String listToString(final Pokemon[] pokemon) {
        String str = "";
        if (pokemon.length == 1) {
            return pokemon[0].toString();
        }
        for (int i = 0; i < pokemon.length; ++i) {
            if (i == pokemon.length - 1) {
                str = str + "and " + pokemon[i].toString();
            }
            else {
                str += ((i == pokemon.length - 2) ? (pokemon[i].toString() + " ") : (pokemon[i].toString() + ", "));
            }
        }
        return str;
    }

    public static String getIcon(final int id) {
        String url = "https://bytebucket.org/anzmap/sprites/raw/b9ddf9736a8fef8b3888d00d2f2e85410ef11327/";
        if (id >= 2011) {
            final int form = id % 201;
            url = url + "201-" + form;
        }
        else {
            url += id;
        }
        return url + ".png";
    }

    public static Character intToForm(final int i) {
        if (i == 0) {
            return null;
        }
        if (i <= 26) {
            return (char)(64 + i);
        }
        if (i == 27) {
            return '?';
        }
        if (i == 28) {
            return '!';
        }
        return null;
    }

    @Override
    public String toString() {
        return this.name;
    }

    @Override
    public int hashCode() {
        return this.name.hashCode();
    }

    @Override
    public boolean equals(final Object obj) {
        assert obj.getClass().getName().equals("pokemon.Pokemon");
        final Pokemon poke = (Pokemon)obj;
        return poke.name.equals(this.name);
    }

    public int getID() {
//        System.out.println("getting id of " + this.name);
        return nameToID(this.name);
    }

    public Location getLocation() {
        return this.location;
    }

    static {
        VALID_NAMES = new ArrayList<String>(Arrays.asList("bulbasaur", "ivysaur", "venusaur", "charmander", "charmeleon", "charizard", "squirtle", "wartortle", "blastoise", "caterpie", "metapod", "butterfree", "weedle", "kakuna", "beedrill", "pidgey", "pidgeotto", "pidgeot", "rattata", "raticate", "spearow", "fearow", "ekans", "arbok", "pikachu", "raichu", "sandshrew", "sandslash", "nidoranf", "nidorina", "nidoqueen", "nidoranm", "nidorino", "nidoking", "clefairy", "clefable", "vulpix", "ninetales", "jigglypuff", "wigglytuff", "zubat", "golbat", "oddish", "gloom", "vileplume", "paras", "parasect", "venonat", "venomoth", "diglett", "dugtrio", "meowth", "persian", "psyduck", "golduck", "mankey", "primeape", "growlithe", "arcanine", "poliwag", "poliwhirl", "poliwrath", "abra", "kadabra", "alakazam", "machop", "machoke", "machamp", "bellsprout", "weepinbell", "victreebel", "tentacool", "tentacruel", "geodude", "graveler", "golem", "ponyta", "rapidash", "slowpoke", "slowbro", "magnemite", "magneton", "farfetch'd", "doduo", "dodrio", "seel", "dewgong", "grimer", "muk", "shellder", "cloyster", "gastly", "haunter", "gengar", "onix", "drowzee", "hypno", "krabby", "kingler", "voltorb", "electrode", "exeggcute", "exeggutor", "cubone", "marowak", "hitmonlee", "hitmonchan", "lickitung", "koffing", "weezing", "rhyhorn", "rhydon", "chansey", "tangela", "kangaskhan", "horsea", "seadra", "goldeen", "seaking", "staryu", "starmie", "mr. mime", "scyther", "jynx", "electabuzz", "magmar", "pinsir", "tauros", "magikarp", "gyarados", "lapras", "ditto", "eevee", "vaporeon", "jolteon", "flareon", "porygon", "omanyte", "omastar", "kabuto", "kabutops", "aerodactyl", "snorlax", "articuno", "zapdos", "moltres", "dratini", "dragonair", "dragonite", "mewtwo", "mew", "chikorita", "bayleef", "meganium", "cyndaquil", "quilava", "typhlosion", "totodile", "croconaw", "feraligatr", "sentret", "furret", "hoothoot", "noctowl", "ledyba", "ledian", "spinarak", "ariados", "crobat", "chinchou", "lanturn", "pichu", "cleffa", "igglybuff", "togepi", "togetic", "natu", "xatu", "mareep", "flaaffy", "ampharos", "bellossom", "marill", "azumarill", "sudowoodo", "politoed", "hoppip", "skiploom", "jumpluff", "aipom", "sunkern", "sunflora", "yanma", "wooper", "quagsire", "espeon", "umbreon", "murkrow", "slowking", "misdreavus", "unown", "wobbuffet", "girafarig", "pineco", "forretress", "dunsparce", "gligar", "steelix", "snubbull", "granbull", "qwilfish", "scizor", "shuckle", "heracross", "sneasel", "teddiursa", "ursaring", "slugma", "magcargo", "swinub", "piloswine", "corsola", "remoraid", "octillery", "delibird", "mantine", "skarmory", "houndour", "houndoom", "kingdra", "phanpy", "donphan", "porygon2", "stantler", "smeargle", "tyrogue", "hitmontop", "smoochum", "elekid", "magby", "miltank", "blissey", "raikou", "entei", "suicune", "larvitar", "pupitar", "tyranitar", "lugia", "ho-oh", "celebi","azurill","marill","azumarill","wynaut","wobbuffet","treecko","grovyle","sceptile","torchic","combusken","blaziken","mudkip","marshtomp","swampert","poochyena","mightyena","zigzagoon","linoone","wurmple","cascoon","dustox","wurmple","silcoon","beautifly","lotad","lombre","ludicolo","seedot","nuzleaf","shiftry","taillow","swellow","wingull","pelipper","ralts","kirlia","gardevoir","surskit","masquerain","shroomish","breloom","slakoth","vigoroth","slaking","nincada","ninjask","hedinja","whismur","loudred","exploud","makuhita","hariyama","azurill","marill","azumarill","nosepass","probopass","skitty","delcatty","sableye","mawile","aron","lairon","aggron","meditite","medicham","electrike","manectric","plusle","minun","volbeat","illumise","gulpin","swalot","carvanha","sharpedo","wailmer","wailord","numel","camerupt","torkoal","spoink","grumpig","spinda","trapinch","vibrava","flygon","cacnea","cacturne","swablu","altaria","zangoose","seviper","lunatone","solrock","barboach","whiscash","corphish","crawdaunt","baltoy","claydol","lileep","cradily","anorith","armaldo","feebas","milotic","castform","kecleon","shuppet","banette","duskull","dusclops","dusknoir","tropius","absol","wynaut","wobbuffet","snorunt","snorunt","glalie","spheal","sealeo","walrein","clamperl","huntail","clamperl","gorebyss","relicanth","luvdisc","bagon","shelgon","salamence","beldum","metang","metagross","regirock","regice","registeel","latias","latios","kyogre","groudon","rayquaza","jirachi","deoxys"));
    }
}
