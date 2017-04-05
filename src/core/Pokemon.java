package core;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by Paris on 18/03/2017.
 */
public class Pokemon {

    public static final ArrayList<String> VALID_NAMES = new ArrayList<String>(Arrays.asList(new String[]{
            "bulbasaur",
            "ivysaur",
            "venusaur",
            "charmander",
            "charmeleon",
            "charizard",
            "squirtle",
            "wartortle",
            "blastoise",
            "caterpie",
            "metapod",
            "butterfree",
            "weedle",
            "kakuna",
            "beedrill",
            "pidgey",
            "pidgeotto",
            "pidgeot",
            "rattata",
            "raticate",
            "spearow",
            "fearow",
            "ekans",
            "arbok",
            "pikachu",
            "raichu",
            "sandshrew",
            "sandslash",
            "nidoranf",
            "nidorina",
            "nidoqueen",
            "nidoranm",
            "nidorino",
            "nidoking",
            "clefairy",
            "clefable",
            "vulpix",
            "ninetales",
            "jigglypuff",
            "wigglytuff",
            "zubat",
            "golbat",
            "oddish",
            "gloom",
            "vileplume",
            "paras",
            "parasect",
            "venonat",
            "venomoth",
            "diglett",
            "dugtrio",
            "meowth",
            "persian",
            "psyduck",
            "golduck",
            "mankey",
            "primeape",
            "growlithe",
            "arcanine",
            "poliwag",
            "poliwhirl",
            "poliwrath",
            "abra",
            "kadabra",
            "alakazam",
            "machop",
            "machoke",
            "machamp",
            "bellsprout",
            "weepinbell",
            "victreebel",
            "tentacool",
            "tentacruel",
            "geodude",
            "graveler",
            "golem",
            "ponyta",
            "rapidash",
            "slowpoke",
            "slowbro",
            "magnemite",
            "magneton",
            "farfetch'd",
            "doduo",
            "dodrio",
            "seel",
            "dewgong",
            "grimer",
            "muk",
            "shellder",
            "cloyster",
            "gastly",
            "haunter",
            "gengar",
            "onix",
            "drowzee",
            "hypno",
            "krabby",
            "kingler",
            "voltorb",
            "electrode",
            "exeggcute",
            "exeggutor",
            "cubone",
            "marowak",
            "hitmonlee",
            "hitmonchan",
            "lickitung",
            "koffing",
            "weezing",
            "rhyhorn",
            "rhydon",
            "chansey",
            "tangela",
            "kangaskhan",
            "horsea",
            "seadra",
            "goldeen",
            "seaking",
            "staryu",
            "starmie",
            "mr. mime",
            "scyther",
            "jynx",
            "electabuzz",
            "magmar",
            "pinsir",
            "tauros",
            "magikarp",
            "gyarados",
            "lapras",
            "ditto",
            "eevee",
            "vaporeon",
            "jolteon",
            "flareon",
            "porygon",
            "omanyte",
            "omastar",
            "kabuto",
            "kabutops",
            "aerodactyl",
            "snorlax",
            "articuno",
            "zapdos",
            "moltres",
            "dratini",
            "dragonair",
            "dragonite",
            "mewtwo",
            "mew",
            "chikorita",
            "bayleef",
            "meganium",
            "cyndaquil",
            "quilava",
            "typhlosion",
            "totodile",
            "croconaw",
            "feraligatr",
            "sentret",
            "furret",
            "hoothoot",
            "noctowl",
            "ledyba",
            "ledian",
            "spinarak",
            "ariados",
            "crobat",
            "chinchou",
            "lanturn",
            "pichu",
            "cleffa",
            "igglybuff",
            "togepi",
            "togetic",
            "natu",
            "xatu",
            "mareep",
            "flaaffy",
            "ampharos",
            "bellossom",
            "marill",
            "azumarill",
            "sudowoodo",
            "politoed",
            "hoppip",
            "skiploom",
            "jumpluff",
            "aipom",
            "sunkern",
            "sunflora",
            "yanma",
            "wooper",
            "quagsire",
            "espeon",
            "umbreon",
            "murkrow",
            "slowking",
            "misdreavus",
            "unown",
            "wobbuffet",
            "girafarig",
            "pineco",
            "forretress",
            "dunsparce",
            "gligar",
            "steelix",
            "snubbull",
            "granbull",
            "qwilfish",
            "scizor",
            "shuckle",
            "heracross",
            "sneasel",
            "teddiursa",
            "ursaring",
            "slugma",
            "magcargo",
            "swinub",
            "piloswine",
            "corsola",
            "remoraid",
            "octillery",
            "delibird",
            "mantine",
            "skarmory",
            "houndour",
            "houndoom",
            "kingdra",
            "phanpy",
            "donphan",
            "porygon2",
            "stantler",
            "smeargle",
            "tyrogue",
            "hitmontop",
            "smoochum",
            "elekid",
            "magby",
            "miltank",
            "blissey",
            "raikou",
            "entei",
            "suicune",
            "larvitar",
            "pupitar",
            "tyranitar",
            "lugia",
            "ho-oh",
            "celebi"
    }));

    public String name;

    float miniv = 0;
    float maxiv = 100;

    Region region = Region.All;

    public Pokemon(String name) {
        if(!VALID_NAMES.contains(name.toLowerCase()))
            if(name.toLowerCase().equals("nidoran f")){
                this.name = "nidoranf";
            }else if(name.toLowerCase().equals("nidoran m")){
                this.name = "nidoranm";
            }else {
                this.name = null;
            }
        else
            this.name = name.toLowerCase();
    }

    public Pokemon(String name, Region region) {
        this(name);
        this.region = region;
    }

    public Pokemon(String name, Region region, float miniv){
        this(name, region);
        this.miniv = miniv;
    }

    public Pokemon(String name, Region region, float miniv, float maxiv){
        this(name,region,miniv);
        this.maxiv = maxiv;
    }

    public Pokemon(int id, float min_iv, float max_iv) {
        this(VALID_NAMES.get(id-1));
        this.miniv=min_iv;
        this.maxiv=max_iv;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        assert obj.getClass().getName().equals("core.Pokemon");

        Pokemon poke = (Pokemon) obj;
        return poke.name.equals(this.name);
    }

    public static void main(String[] args) {
        System.out.println(new Pokemon("dragonite",Region.All,80,100));
    }

    public int getID() {
        return VALID_NAMES.indexOf(name) + 1;
    }

    public static int nameToID(String pokeName) {
        return VALID_NAMES.indexOf(pokeName) + 1;
    }

    public static String idToName(int id) {
        return VALID_NAMES.get(id-1);
    }

    public static String listToString(Pokemon[] pokemon) {
        String str = "";

        if(pokemon.length == 1){
            return pokemon[0].toString();
        }

        for (int i = 0; i < pokemon.length; i++) {
            if(i == pokemon.length-1)
                str += "or " + pokemon[i].toString();
            else
                str += i == pokemon.length - 2 ? pokemon[i].toString() + " " : pokemon[i].toString() +", ";
        }

        return str;
    }
}
