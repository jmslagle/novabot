package parser.exceptions;

/**
 * Created by Paris on 4/04/2017.
 */
public class InvalidCommandException extends CommandException {

    @Override
    public String getMessage() {
        return "Input was not a valid command";
    }
}
