package com.example.hangman;

import javafx.beans.property.*;

import java.util.ArrayList;
import java.util.Random;

public class HangmanGame {
    public static final String[] WORDS = {
            "ABSTRACT", "ASSERT", "BOOLEAN", "BREAK", "BYTE",
            "CASE", "CATCH", "CHAR", "CLASS", "CONST",
            "CONTINUE", "DEFAULT", "DOUBLE", "DO", "ELSE",
            "ENUM", "EXTENDS", "FALSE", "FINAL", "FINALLY",
            "FLOAT", "FOR", "GOTO", "IF", "IMPLEMENTS",
            "IMPORT", "INSTANCEOF", "INT", "INTERFACE",
            "LONG", "NATIVE", "NEW", "NULL", "PACKAGE",
            "PRIVATE", "PROTECTED", "PUBLIC", "RETURN",
            "SHORT", "STATIC", "STRICTFP", "SUPER", "SWITCH",
            "SYNCHRONIZED", "THIS", "THROW", "THROWS",
            "TRANSIENT", "TRUE", "TRY", "VOID", "VOLATILE", "WHILE"
    };

    public static final Random RANDOM = new Random();
    // Users loses at 6 errors
    public static final int MAX_ERRORS = 6;
    // Word to find
    private String wordToFind;
    // Word found stored in a char array to show word progression
    private StringProperty wordFound = new SimpleStringProperty(this, "wordFound", "");
    // number of errors
//    private int nbErrors;
    private IntegerProperty nbErrors = new SimpleIntegerProperty(this, "nbErrors", 0);
    // letters already entered by user
    private int attemptNo = 0;
    private DoubleProperty successPercentage = new SimpleDoubleProperty(this, "successPercentage", 0.00);
    private ArrayList<Character> letters = new ArrayList<Character>();

    public int getNbErrors() {
        return nbErrors.get();
    }

    public IntegerProperty nbErrorsProperty() {
        return nbErrors;
    }

    public void setNbErrors(int nbErrors) {
        this.nbErrors.set(nbErrors);
    }

    public double getSuccessPercentage() {
        return successPercentage.get();
    }

    public DoubleProperty successPercentageProperty() {
        return successPercentage;
    }

    public void setSuccessPercentage(double successPercentage) {
        this.successPercentage.set(successPercentage);
    }

    public StringProperty wordFoundProperty() {
        return wordFound;
    }

    public String getWordFound() {
        return wordFound.get();
    }

    public void setWordFound(String wordFound) {
        this.wordFound.set(wordFound);
    }

    private String pickRandomWord() {
        return WORDS[RANDOM.nextInt(WORDS.length)];
    }

    /**
     * Actions performed on new game
     * - Set number of errors to 0
     * - Clear the list of letters entered by the user
     * - Let the computer pick a random word
     * - Initialize the word found array needed for the word progression
     * (letters are represented with an underscore '_'
     */
    public void newGame() {
        setNbErrors(0);
        attemptNo = 0;
        letters.clear();
        wordToFind = pickRandomWord();

        char[] tmpWordFound = new char[wordToFind.length()];
        for (int i = 0; i < tmpWordFound.length; i++) {
            tmpWordFound[i] = '_';
        }
        setWordFound(String.valueOf(tmpWordFound));
    }

    /**
     * Method that determines if user has found the word
     */
    public boolean gameWon() {
        return wordToFind.contentEquals(getWordFound());
    }

    /**
     * Returns the state of the word being found by the user under String format
     * Adds spaces for better readability
     */
    private String wordFoundContent() {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < getWordFound().length(); i++) {
            builder.append(getWordFound().charAt(i));

            if (i < getWordFound().length() - 1) {
                builder.append(' ');
            }
        }
        return builder.toString();
    }

    /**
     * Updates game state when user enters letter
     * - check if user has entered the letter
     * - check if our word contains the letter
     * - if letter exists replace its underscore
     */
    private void enterLetter(char letter) {
        if (letters.contains(letter)) return;

        if (wordToFind.contains(String.valueOf(letter))) {
            int index = wordToFind.indexOf(letter);

            StringBuilder tmpWordFound = new StringBuilder(getWordFound());
            while (index >= 0) {
                tmpWordFound.setCharAt(index, letter);
                index = wordToFind.indexOf(letter, index + 1);
            }
            setWordFound(tmpWordFound.toString());
        } else {
//            nbErrors++;
            setNbErrors(getNbErrors() + 1);
        }
        attemptNo++;
        setSuccessPercentage(100 - (((double) getNbErrors() / attemptNo) * 100));
        System.out.println("Percentage now" + getSuccessPercentage());
        letters.add(letter);
    }

    /**
     * Assemble all methods in a play method
     * - check if number of error < max
     * - get a letter from the user
     * - Update the word found with entered letter
     * - Check if word is found, if Yes display win message, else
     * display number of tries remaining
     * - lose message when errors = max
     */
    public void handleNewLetter(char letter) {
        enterLetter(letter);
        // display current state
        System.out.println("\n" + wordFoundContent());

        if (gameWon()) {
            System.out.println("\nYou win!");
            return;
        }

        System.out.println("\n=> Nb tries remaining : " + (MAX_ERRORS - getNbErrors()));
        if (getNbErrors() == MAX_ERRORS) {
            // user lost
            System.out.println("\nYou lose!");
            System.out.println("=> Word to find was : " + wordToFind);
        }
    }

    public static void main(String[] args) {
        System.out.println("hangman game :=)\n");
        HangmanGame hangmanGame = new HangmanGame();
        hangmanGame.newGame();
//        hangmanGame.play();
    }
}
