package parser.exceptions;

/**
 * Created by Paris on 4/04/2017.
 */
public class MalformedArgumentException extends CommandException {
    @Override
    public String getMessage() {
        return "Couldn't parse one or more arguments";
    }
}
