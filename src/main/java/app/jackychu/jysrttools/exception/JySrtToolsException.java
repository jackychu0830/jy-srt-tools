package app.jackychu.jysrttools.exception;

public class JySrtToolsException extends Throwable {
    public JySrtToolsException() {
    }

    public JySrtToolsException(String message) {
        super(message);
    }

    public JySrtToolsException(String message, Throwable th) {
        super(message, th);
    }

}
