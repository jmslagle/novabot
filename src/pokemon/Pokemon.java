package pokemon;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import core.Location;
import core.Region;
import core.Types;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;

public class Pokemon
{
    private static final ArrayList<String> VALID_NAMES;
    public final String name;
    public float miniv;
    public float maxiv;
    private Location location;

    private static JsonObject baseStats;
    private static JsonObject pokemonInfo;
    private static JsonObject movesInfo;

    static {
//        VALID_NAMES = new ArrayList<String>(Arrays.asList("bulbasaur", "ivysaur", "venusaur", "charmander", "charmeleon", "charizard", "squirtle", "wartortle", "blastoise", "caterpie", "metapod", "butterfree", "weedle", "kakuna", "beedrill", "pidgey", "pidgeotto", "pidgeot", "rattata", "raticate", "spearow", "fearow", "ekans", "arbok", "pikachu", "raichu", "sandshrew", "sandslash", "nidoran\u2640", "nidorina", "nidoqueen", "nidoran\u2642", "nidorino", "nidoking", "clefairy", "clefable", "vulpix", "ninetales", "jigglypuff", "wigglytuff", "zubat", "golbat", "oddish", "gloom", "vileplume", "paras", "parasect", "venonat", "venomoth", "diglett", "dugtrio", "meowth", "persian", "psyduck", "golduck", "mankey", "primeape", "growlithe", "arcanine", "poliwag", "poliwhirl", "poliwrath", "abra", "kadabra", "alakazam", "machop", "machoke", "machamp", "bellsprout", "weepinbell", "victreebel", "tentacool", "tentacruel", "geodude", "graveler", "golem", "ponyta", "rapidash", "slowpoke", "slowbro", "magnemite", "magneton", "farfetch'd", "doduo", "dodrio", "seel", "dewgong", "grimer", "muk", "shellder", "cloyster", "gastly", "haunter", "gengar", "onix", "drowzee", "krabby", "kingler","hypno", "voltorb", "electrode", "exeggcute", "exeggutor", "cubone", "marowak", "hitmonlee", "hitmonchan", "lickitung", "koffing", "weezing", "rhyhorn", "rhydon", "chansey", "tangela", "kangaskhan", "horsea", "seadra", "goldeen", "seaking", "staryu", "starmie", "mr. mime", "scyther", "jynx", "electabuzz", "magmar", "pinsir", "tauros", "magikarp", "gyarados", "lapras", "ditto", "eevee", "vaporeon", "jolteon", "flareon", "porygon", "omanyte", "omastar", "kabuto", "kabutops", "aerodactyl", "snorlax", "articuno", "zapdos", "moltres", "dratini", "dragonair", "dragonite", "mewtwo", "mew", "chikorita", "bayleef", "meganium", "cyndaquil", "quilava", "typhlosion", "totodile", "croconaw", "feraligatr", "sentret", "furret", "hoothoot", "noctowl", "ledyba", "ledian", "spinarak", "ariados", "crobat", "chinchou", "lanturn", "pichu", "cleffa", "igglybuff", "togepi", "togetic", "natu", "xatu", "mareep", "flaaffy", "ampharos", "bellossom", "marill", "azumarill", "sudowoodo", "politoed", "hoppip", "skiploom", "jumpluff", "aipom", "sunkern", "sunflora", "yanma", "wooper", "quagsire", "espeon", "umbreon", "murkrow", "slowking", "misdreavus", "unown", "wobbuffet", "girafarig", "pineco", "forretress", "dunsparce", "gligar", "steelix", "snubbull", "granbull", "qwilfish", "scizor", "shuckle", "heracross", "sneasel", "teddiursa", "ursaring", "slugma", "magcargo", "swinub", "piloswine", "corsola", "remoraid", "octillery", "delibird", "mantine", "skarmory", "houndour", "houndoom", "kingdra", "phanpy", "donphan", "porygon2", "stantler", "smeargle", "tyrogue", "hitmontop", "smoochum", "elekid", "magby", "miltank", "blissey", "raikou", "entei", "suicune", "larvitar", "pupitar", "tyranitar", "lugia", "ho-oh", "celebi", "treecko", "grovyle", "sceptile", "torchic", "combusken", "blaziken", "mudkip", "marshtomp", "swampert", "poochyena", "mightyena", "zigzagoon", "linoone", "wurmple", "silcoon", "beautifly", "cascoon", "dustox", "lotad", "lombre", "ludicolo", "seedot", "nuzleaf", "shiftry", "taillow", "swellow", "wingull", "pelipper", "ralts", "kirlia", "gardevoir", "surskit", "masquerain", "shroomish", "breloom", "slakoth", "vigoroth", "slaking", "nincada", "ninjask", "shedinja", "whismur", "loudred", "exploud", "makuhita", "hariyama", "azurill", "nosepass", "skitty", "delcatty", "sableye", "mawile", "aron", "lairon", "aggron", "meditite", "medicham", "electrike", "manectric", "plusle", "minun", "volbeat", "illumise", "roselia", "gulpin", "swalot", "carvanha", "sharpedo", "wailmer", "wailord", "numel", "camerupt", "torkoal", "spoink", "grumpig", "spinda", "trapinch", "vibrava", "flygon", "cacnea", "cacturne", "swablu", "altaria", "zangoose", "seviper", "lunatone", "solrock", "barboach", "whiscash", "corphish", "crawdaunt", "baltoy", "claydol", "lileep", "cradily", "anorith", "armaldo", "feebas", "milotic", "castform", "kecleon", "shuppet", "banette", "duskull", "dusclops", "tropius", "chimecho", "absol", "wynaut", "snorunt", "glalie", "spheal", "sealeo", "walrein", "clamperl", "huntail", "gorebyss", "relicanth", "luvdisc", "bagon", "shelgon", "salamence", "beldum", "metang", "metagross", "regirock", "regice", "registeel", "latias", "latios", "kyogre", "groudon", "rayquaza", "jirachi", "deoxys", "turtwig", "grotle", "torterra", "chimchar", "monferno", "infernape", "piplup", "prinplup", "empoleon", "starly", "staravia", "staraptor", "bidoof", "bibarel", "kricketot", "kricketune", "shinx", "luxio", "luxray", "budew", "roserade", "cranidos", "rampardos", "shieldon", "bastiodon", "burmy", "wormadam", "mothim", "combee", "vespiquen", "pachirisu", "buizel", "floatzel", "cherubi", "cherrim", "shellos", "gastrodon", "ambipom", "drifloon", "drifblim", "buneary", "lopunny", "mismagius", "honchkrow", "glameow", "purugly", "chingling", "stunky", "skuntank", "bronzor", "bronzong", "bonsly", "mime jr.", "happiny", "chatot", "spiritomb", "gible", "gabite", "garchomp", "munchlax", "riolu", "lucario", "hippopotas", "hippowdon", "skorupi", "drapion", "croagunk", "toxicroak", "carnivine", "finneon", "lumineon", "mantyke", "snover", "abomasnow", "weavile", "magnezone", "lickilicky", "rhyperior", "tangrowth", "electivire", "magmortar", "togekiss", "yanmega", "leafeon", "glaceon", "gliscor", "mamoswine", "porygon-z", "gallade", "probopass", "dusknoir", "froslass", "rotom", "uxie", "mesprit", "azelf", "dialga", "palkia", "heatran", "regigigas", "giratina", "cresselia", "phione", "manaphy", "darkrai", "shaymin", "arceus", "victini", "snivy", "servine", "serperior", "tepig", "pignite", "emboar", "oshawott", "dewott", "samurott", "patrat", "watchog", "lillipup", "herdier", "stoutland", "purrloin", "liepard", "pansage", "simisage", "pansear", "simisear", "panpour", "simipour", "munna", "musharna", "pidove", "tranquill", "unfezant", "blitzle", "zebstrika", "roggenrola", "boldore", "gigalith", "woobat", "swoobat", "drilbur", "excadrill", "audino", "timburr", "gurdurr", "conkeldurr", "tympole", "palpitoad", "seismitoad", "throh", "sawk", "sewaddle", "swadloon", "leavanny", "venipede", "whirlipede", "scolipede", "cottonee", "whimsicott", "petilil", "lilligant", "basculin", "sandile", "krokorok", "krookodile", "darumaka", "darmanitan", "maractus", "dwebble", "crustle", "scraggy", "scrafty", "sigilyph", "yamask", "cofagrigus", "tirtouga", "carracosta", "archen", "archeops", "trubbish", "garbodor", "zorua", "zoroark", "minccino", "cinccino", "gothita", "gothorita", "gothitelle", "solosis", "duosion", "reuniclus", "ducklett", "swanna", "vanillite", "vanillish", "vanilluxe", "deerling", "sawsbuck", "emolga", "karrablast", "escavalier", "foongus", "amoonguss", "frillish", "jellicent", "alomomola", "joltik", "galvantula", "ferroseed", "ferrothorn", "klink", "klang", "klinklang", "tynamo", "eelektrik", "eelektross", "elgyem", "beheeyem", "litwick", "lampent", "chandelure", "axew", "fraxure", "haxorus", "cubchoo", "beartic", "cryogonal", "shelmet", "accelgor", "stunfisk", "mienfoo", "mienshao", "druddigon", "golett", "golurk", "pawniard", "bisharp", "bouffalant", "rufflet", "braviary", "vullaby", "mandibuzz", "heatmor", "durant", "deino", "zweilous", "hydreigon", "larvesta", "volcarona", "cobalion", "terrakion", "virizion", "tornadus", "thundurus", "reshiram", "zekrom", "landorus", "kyurem", "keldeo", "meloetta", "genesect", "chespin", "quilladin", "chesnaught", "fennekin", "braixen", "delphox", "froakie", "frogadier", "greninja", "bunnelby", "diggersby", "fletchling", "fletchinder", "talonflame", "scatterbug", "spewpa", "vivillon", "litleo", "pyroar", "flab\u00e9b\u00e9", "floette", "florges", "skiddo", "gogoat", "pancham", "pangoro", "furfrou", "espurr", "meowstic", "honedge", "doublade", "aegislash", "spritzee", "aromatisse", "swirlix", "slurpuff", "inkay", "malamar", "binacle", "barbaracle", "skrelp", "dragalge", "clauncher", "clawitzer", "helioptile", "heliolisk", "tyrunt", "tyrantrum", "amaura", "aurorus", "sylveon", "hawlucha", "dedenne", "carbink", "goomy", "sliggoo", "goodra", "klefki", "phantump", "trevenant", "pumpkaboo", "gourgeist", "bergmite", "avalugg", "noibat", "noivern", "xerneas", "yveltal", "zygarde", "diancie", "hoopa", "volcanion"));
        VALID_NAMES = new ArrayList<String>(Arrays.asList("bulbasaur","ivysaur","venusaur","charmander","charmeleon","charizard","squirtle","wartortle","blastoise","caterpie","metapod","butterfree","weedle","kakuna","beedrill","pidgey","pidgeotto","pidgeot","rattata","raticate","spearow","fearow","ekans","arbok","pikachu","raichu","sandshrew","sandslash","nidoran♀","nidorina","nidoqueen","nidoran♂","nidorino","nidoking","clefairy","clefable","vulpix","ninetales","jigglypuff","wigglytuff","zubat","golbat","oddish","gloom","vileplume","paras","parasect","venonat","venomoth","diglett","dugtrio","meowth","persian","psyduck","golduck","mankey","primeape","growlithe","arcanine","poliwag","poliwhirl","poliwrath","abra","kadabra","alakazam","machop","machoke","machamp","bellsprout","weepinbell","victreebel","tentacool","tentacruel","geodude","graveler","golem","ponyta","rapidash","slowpoke","slowbro","magnemite","magneton","farfetch'd","doduo","dodrio","seel","dewgong","grimer","muk","shellder","cloyster","gastly","haunter","gengar","onix","drowzee","hypno","krabby","kingler","voltorb","electrode","exeggcute","exeggutor","cubone","marowak","hitmonlee","hitmonchan","lickitung","koffing","weezing","rhyhorn","rhydon","chansey","tangela","kangaskhan","horsea","seadra","goldeen","seaking","staryu","starmie","mr. mime","scyther","jynx","electabuzz","magmar","pinsir","tauros","magikarp","gyarados","lapras","ditto","eevee","vaporeon","jolteon","flareon","porygon","omanyte","omastar","kabuto","kabutops","aerodactyl","snorlax","articuno","zapdos","moltres","dratini","dragonair","dragonite","mewtwo","mew","chikorita","bayleef","meganium","cyndaquil","quilava","typhlosion","totodile","croconaw","feraligatr","sentret","furret","hoothoot","noctowl","ledyba","ledian","spinarak","ariados","crobat","chinchou","lanturn","pichu","cleffa","igglybuff","togepi","togetic","natu","xatu","mareep","flaaffy","ampharos","bellossom","marill","azumarill","sudowoodo","politoed","hoppip","skiploom","jumpluff","aipom","sunkern","sunflora","yanma","wooper","quagsire","espeon","umbreon","murkrow","slowking","misdreavus","unown","wobbuffet","girafarig","pineco","forretress","dunsparce","gligar","steelix","snubbull","granbull","qwilfish","scizor","shuckle","heracross","sneasel","teddiursa","ursaring","slugma","magcargo","swinub","piloswine","corsola","remoraid","octillery","delibird","mantine","skarmory","houndour","houndoom","kingdra","phanpy","donphan","porygon2","stantler","smeargle","tyrogue","hitmontop","smoochum","elekid","magby","miltank","blissey","raikou","entei","suicune","larvitar","pupitar","tyranitar","lugia","ho-oh","celebi","treecko","grovyle","sceptile","torchic","combusken","blaziken","mudkip","marshtomp","swampert","poochyena","mightyena","zigzagoon","linoone","wurmple","silcoon","beautifly","cascoon","dustox","lotad","lombre","ludicolo","seedot","nuzleaf","shiftry","taillow","swellow","wingull","pelipper","ralts","kirlia","gardevoir","surskit","masquerain","shroomish","breloom","slakoth","vigoroth","slaking","nincada","ninjask","shedinja","whismur","loudred","exploud","makuhita","hariyama","azurill","nosepass","skitty","delcatty","sableye","mawile","aron","lairon","aggron","meditite","medicham","electrike","manectric","plusle","minun","volbeat","illumise","roselia","gulpin","swalot","carvanha","sharpedo","wailmer","wailord","numel","camerupt","torkoal","spoink","grumpig","spinda","trapinch","vibrava","flygon","cacnea","cacturne","swablu","altaria","zangoose","seviper","lunatone","solrock","barboach","whiscash","corphish","crawdaunt","baltoy","claydol","lileep","cradily","anorith","armaldo","feebas","milotic","castform","kecleon","shuppet","banette","duskull","dusclops","tropius","chimecho","absol","wynaut","snorunt","glalie","spheal","sealeo","walrein","clamperl","huntail","gorebyss","relicanth","luvdisc","bagon","shelgon","salamence","beldum","metang","metagross","regirock","regice","registeel","latias","latios","kyogre","groudon","rayquaza","jirachi","deoxys"));

        JsonParser parser = new JsonParser();

        try {
            JsonElement element = parser.parse(new FileReader("data/base_stats.json"));

            if (element.isJsonObject()) {
                baseStats = element.getAsJsonObject();
            }

            element = parser.parse(new FileReader("data/pokemon.json"));

            if (element.isJsonObject()){
                pokemonInfo = element.getAsJsonObject();
            }

            element = parser.parse(new FileReader("data/moves.json"));

            if (element.isJsonObject()){
                movesInfo = element.getAsJsonObject();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

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
        System.out.println(Types.getStrengths(getMoveType(279)));
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

    public static String getFilterName(int id){

        if (id > 2010) return "Unown";

        return Pokemon.pokemonInfo.getAsJsonObject(Integer.toString(id)).get("name").getAsString();
    }

    public static String getSize(int id, float height, float weight){
        float baseStats[] = getBaseStats(id);

        float weightRatio = weight / baseStats[0];
        float heightRatio = height / baseStats[1];

        float size = heightRatio + weightRatio;

        if (size < 1.5){
            return "tiny";
        }
        if (size <= 1.75){
            return "small";
        }
        if (size < 2.25){
            return "normal";
        }
        if (size <= 2.5){
            return "large";
        }
        return "big";
    }

    private static float[] getBaseStats(int id) {
        JsonObject statsObj = baseStats.getAsJsonObject(Integer.toString(id));

        float stats[] = new float[2];

        stats[0] = statsObj.get("weight").getAsFloat();
        stats[1] = statsObj.get("height").getAsFloat();

        return stats;
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
        String url = "https://bytebucket.org/anzmap/sprites/raw/7f31b4ddb8a3ca6c942c7a1f39e3143de0f1a8d8/";
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

    public static ArrayList<String> getTypes(int bossId) {
        JsonArray types = pokemonInfo.getAsJsonObject(Integer.toString(bossId)).getAsJsonArray("types");

        ArrayList<String> typesList = new ArrayList<>();

        for (JsonElement type : types) {
            typesList.add(type.getAsString());
        }
        return typesList;
    }

    public static String getMoveType(int moveId) {
         return movesInfo.getAsJsonObject(Integer.toString(moveId)).get("type").getAsString();
    }
}
