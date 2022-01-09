package com.example.hangman;

/**
 * A dictionary should include at least 20 words
 */
public class UndersizeException extends Exception {
    public UndersizeException(String str) {
        super(str);
    }
}
