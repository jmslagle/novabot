package parser.exceptions;

/**
 * Created by Paris on 4/04/2017.
 */
public class NotEnoughArgumentsException extends CommandException {

    @Override
    public String getMessage() {
        return "Input had not enough arguments for the command";
    }
}
