package core;

public enum Team {
    Uncontested,
    Valor,
    Instinct,
    Mystic;

    public Team fromString (String s) {
        switch (s.toLowerCase()){
            case "valor":
                return Valor;
            case "instinct":
                return Instinct;
            case "mystic":
                return Mystic;
        }
        return null;
    }

    public static Team fromId(int i) {
        switch (i){
            case 0:
                return Uncontested;
            case 1:
                return Mystic;
            case 2:
                return Valor;
            case 3:
                return Instinct;
        }
        return null;
    }

    @Override
    public String toString() {
        switch (this){
            case Uncontested:
                return "Uncontested";
            case Valor:
                return "Valor";
            case Mystic:
                return "Mystic";
            case Instinct:
                return "Instinct";
        }
        return null;
    }
}
