package parser;

/**
 * Created by Paris on 3/04/2017.
 */
public class Argument {


    private ArgType type;
    private Object[] params;

    public Argument (ArgType argType, Object[] args){

    }


    public Argument() {

    }

    public Argument(String group) {

    }

    public void setType(ArgType type) {
        this.type = type;
    }

    public void setParams(Object[] params) {
        this.params = params;
    }

    public ArgType getType() {
        return type;
    }

    public Object[] getParams() {
        return params;
    }

    public boolean fullyParsed() {
        for (Object param : params) {
            if(param == null) return false;
        }

        return true;
    }
}


