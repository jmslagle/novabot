package parser.exceptions;

/**
 * Created by Paris on 4/04/2017.
 */
public class TooManyArgumentsException extends CommandException {

    @Override
    public String getMessage() {
        return "Input had too many arguments for the command";
    }
}
