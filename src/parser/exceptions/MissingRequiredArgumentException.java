package parser.exceptions;

/**
 * Created by Paris on 4/04/2017.
 */
public class MissingRequiredArgumentException extends CommandException {
    @Override
    public String getMessage() {
        return "Input is missing a required argument for the command";
    }
}
