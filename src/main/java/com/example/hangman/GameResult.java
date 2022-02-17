package com.example.hangman;

public class GameResult {
    public enum Winner {
        HUMAN, COMPUTER
    }

    public String selectedWord;
    public int numberOfTries;
    public Winner winner;

    public GameResult(String selectedWord, int numberOfTries, Winner winner) {
        this.selectedWord = selectedWord;
        this.numberOfTries = numberOfTries;
        this.winner = winner;
    }
}
