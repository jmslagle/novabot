package parser.exceptions;

/**
 * Created by Paris on 4/04/2017.
 */
public class InvalidArgumentException extends CommandException {

    @Override
    public String getMessage() {
        return "Input contained an invalid argument for the command type";
    }
}
