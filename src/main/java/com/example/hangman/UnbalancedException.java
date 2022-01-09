package com.example.hangman;

/**
 * 20% of words in a dictionary
 * should have 9 or more letters
 */
public class UnbalancedException extends Exception {
    public UnbalancedException(String str) {
        super(str);
    }
}
