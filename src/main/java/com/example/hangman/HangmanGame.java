package com.example.hangman;

import javafx.beans.property.*;

import java.util.*;
import java.util.stream.IntStream;

public class HangmanGame {
    int A_ASCII_VALUE = 65;
    int Z_ASCII_VALUE = 90;
    private DictionaryManager dictionaryManager;
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
    private DoubleProperty successPercentage = new SimpleDoubleProperty(this, "successPercentage"
            , 0.00);
    private ArrayList<Character> letters = new ArrayList<Character>();
    List<Set<Character>> positionLetters = new ArrayList<Set<Character>>();
    private Set<String> activeWords;
    // words that we need to count possibilities for each letter
    private Set<String> wordsSubset;

    public HangmanGame(DictionaryManager dictionaryManager) {
        this.dictionaryManager = dictionaryManager;
    }

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

    public String getWordToFind() {
        return wordToFind;
    }

    public void setWordToFind(String wordToFind) {
        this.wordToFind = wordToFind;
    }

    public List<Set<Character>> getPositionLetters() {
        return positionLetters;
    }

    public void setPositionLetters(List<Set<Character>> positionLetters) {
        this.positionLetters = positionLetters;
    }

    private String pickRandomWord(Set<String> words) {

        int size = words.size();
        int item = RANDOM.nextInt(size); // In real life, the Random object should be
        // rather more shared than this
        int i = 0;
        for (String word : words) {
            if (i == item) {
                System.out.println("Picked random word " + word);
                return word;
            }
            i++;
        }
        throw new Error("Failed to pick random word");
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

        activeWords = dictionaryManager.getActiveDictWords();
        wordToFind = pickRandomWord(activeWords);
        initializeWordsSubset(activeWords, wordToFind);
        System.out.println("Subset of words");
        System.out.println(Arrays.toString(wordsSubset.toArray()));
        calculateLetterProbabilities(wordsSubset, wordToFind);


        char[] tmpWordFound = new char[wordToFind.length()];
        for (int i = 0; i < tmpWordFound.length; i++) {
//            tmpWordFound[i] = '_';
            tmpWordFound[i] = '⏡';
        }
        setWordFound(String.valueOf(tmpWordFound));
        positionLetters = new ArrayList<Set<Character>>();
        for (int i = 0; i < wordToFind.length(); i++) {
            positionLetters.add(new HashSet<Character>());
        }
    }

    private void initializeWordsSubset(Set<String> words, String ourWord) {
        wordsSubset = words;
        for (Iterator<String> i = wordsSubset.iterator(); i.hasNext(); ) {
            String element = i.next();
            if (element.length() != ourWord.length() || element == ourWord) {
                i.remove();
            }
        }

    }

    /**
     * Our data structure will be an array equal to word size
     * each letter has an object with probabilities and array for each probability
     * List<Map<String,Integer>> maps = new ArrayList<Map<String,Letter[]>>();
     * ...
     * maps.add(new HashMap<String,Integer>());
     *
     * @param words
     * @param ourWord
     */
    private void calculateLetterProbabilities(Set<String> words, String ourWord) {
        List<Map<String, char[]>> maps = new ArrayList<Map<String, char[]>>();
        // TODO skip found positions
        for (int i = 0; i < ourWord.length(); i++) {
            Map probabilities = new HashMap();
//            maps.add(probabilities);
            // TODO for all valid letters of this position
            // count their probability based on other words value
            // at that position
            char letter = ourWord.charAt(i);

            Map<Integer, ArrayList<Character>> lettersProbs = new HashMap<>() {{
                put(Integer.valueOf(5), new ArrayList<>());
                put(Integer.valueOf(10), new ArrayList<>());
                put(Integer.valueOf(15), new ArrayList<>());
                put(Integer.valueOf(30), new ArrayList<>());
            }};
            for (int letterAsci = 65; letterAsci <= 90; letterAsci++) {
                double probability = 0;
                for (Iterator<String> it = wordsSubset.iterator(); it.hasNext(); ) {
                    String word = it.next();
                    if (word.charAt(i) == (char) letterAsci) probability++;
                }
//                System.out.println("Probability for position: " + i + " => " + probability);
                Character letterChar = Character.valueOf((char) letterAsci);
                double result = probability / wordsSubset.size();
                if (result >= 0.6) {
                    lettersProbs.get(Integer.valueOf(5)).add(letterChar);
                } else if (result >= 0.4) {
                    lettersProbs.get(Integer.valueOf(10)).add(letterChar);
                } else if (result >= 0.25) {
                    lettersProbs.get(Integer.valueOf(15)).add(letterChar);
                } else {
                    lettersProbs.get(Integer.valueOf(30)).add(letterChar);
                }
            }
            lettersProbs.forEach((key, value) -> System.out.println(key + ":" + value));
            System.out.println("END================" + lettersProbs.size());
        }
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
     * - remove from wordsSubset words that didn't have
     * in that position the entered letter
     */
    private boolean enterLetter(int index, char letter) {
//        if (letters.contains(letter)) return;
        // has already clicked this letter for this position
        if (positionLetters.get(index).contains(letter)) {
            System.out.println("has already clicked this letter for this position");
            return false;
        }

        boolean result;
        if (wordToFind.charAt(index) == letter) {
            result = true;
            StringBuilder tmpWordFound = new StringBuilder(getWordFound());
            tmpWordFound.setCharAt(index, letter);
            setWordFound(tmpWordFound.toString());
        } else {
            result = false;
//            nbErrors++;
            setNbErrors(getNbErrors() + 1);

            // invoke listener on error too
            setWordFound(getWordFound());
        }
        attemptNo++;
        setSuccessPercentage(100 - (((double) getNbErrors() / attemptNo) * 100));
        System.out.println("Percentage now" + getSuccessPercentage());
        letters.add(letter);
        positionLetters.get(index).add(letter);
        return result;
    }

    /**
     * Assemble all methods in a play method
     * gets triggered when user enters a letter
     * - Update the word found with entered letter
     * - Check if word is found, if Yes display win message, else
     * display number of tries remaining
     * - lose message when errors = max
     */
    public boolean handleNewLetter(int index, char letter) {
        boolean letterWasCorrect = enterLetter(index, letter);
        // display current state
        System.out.println("\n" + wordFoundContent());

        if (gameWon()) {
            System.out.println("\nYou win!");
            return true;
        }

        System.out.println("\n=> Nb tries remaining : " + (MAX_ERRORS - getNbErrors()));
        if (getNbErrors() == MAX_ERRORS) {
            // user lost
            System.out.println("\nYou lose!");
            System.out.println("=> Word to find was : " + wordToFind);
            return false;
        }
        return letterWasCorrect;
    }
}
