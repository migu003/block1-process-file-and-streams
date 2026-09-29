package ejercicios.practica1;

public class InvalidLineFormatException extends Exception {

    private final int lineNumber;

    public InvalidLineFormatException(int lineNumber, String message) {
        super(message);
        this.lineNumber = lineNumber;
    }

    public int getLineNumber() {
        return lineNumber;
    }
}