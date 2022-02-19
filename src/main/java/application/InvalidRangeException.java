package application;

/**
 * No word should have less than 6 letters
 */
public class InvalidRangeException extends Exception {
    public InvalidRangeException(String str) {
        super(str);
    }
}
