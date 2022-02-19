package application;

/**
 * Every word should be unique in the dictionary
 */
public class InvalidCountException extends Exception {
    public InvalidCountException(String str) {
        super(str);
    }
}
