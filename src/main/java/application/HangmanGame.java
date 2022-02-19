package application;

import ui.GameResult;
import javafx.beans.property.*;

import java.util.*;

public class HangmanGame {

    private final DictionaryManager dictionaryManager;

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
    List<Set<Character>> positionEnteredLetters;
    private Set<String> activeWords;
    // words that we need to count possibilities for each letter
    private Set<String> wordsSubset;

    private List<Map<Integer, ArrayList<Character>>> positionLetterProbs;

    private IntegerProperty totalPoints = new SimpleIntegerProperty(this, "totalPoints", 0);

    private LinkedList<GameResult> last5Games = new LinkedList();


    public HangmanGame(DictionaryManager dictionaryManager) {
        this.dictionaryManager = dictionaryManager;
    }

    public int getTotalPoints() {
        return totalPoints.get();
    }

    public IntegerProperty totalPointsProperty() {
        return totalPoints;
    }

    public void setTotalPoints(int totalPoints) {
        this.totalPoints.set(totalPoints);
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


    public List<Set<Character>> getPositionEnteredLetters() {
        return positionEnteredLetters;
    }

    public List<Map<Integer, ArrayList<Character>>> getPositionLetterProbs() {
        return this.positionLetterProbs;
    }

    public LinkedList<GameResult> getLast5Games() {
        return last5Games;
    }

    private String pickRandomWord(Set<String> words) {
        int size = words.size();
        int item = RANDOM.nextInt(size);
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
//        letters.clear();

        activeWords = dictionaryManager.getActiveDictWords();
        wordToFind = pickRandomWord(activeWords);
        initializeWordsSubset(activeWords, wordToFind);
        System.out.println("Subset of words");
        System.out.println(Arrays.toString(wordsSubset.toArray()));


        char[] tmpWordFound = new char[wordToFind.length()];
        Arrays.fill(tmpWordFound, '⏡'); // '_';
        setWordFound(String.valueOf(tmpWordFound));
        positionEnteredLetters = new ArrayList<>();
        for (int i = 0; i < wordToFind.length(); i++) {
            positionEnteredLetters.add(new HashSet<>());
        }
        System.out.println("New game, Calculating probabilities");
        calculateLetterProbabilities(wordsSubset, String.valueOf(tmpWordFound));
    }

    private void initializeWordsSubset(Set<String> words, String ourWord) {
        wordsSubset = words;
        wordsSubset.removeIf(element -> element.length() != ourWord.length() || element.equals(ourWord));
    }

    /**
     * Our data structure will be an array equal to word size
     * each letter has an object with probabilities and array for each probability
     * List<Map<String,Integer>> maps = new ArrayList<Map<String,Letter[]>>();
     * ...
     * maps.add(new HashMap<String,Integer>());
     *
     * @param words     a Set of Strings representing the
     *                  subset of words from the dictionary that
     *                  letter probabilities are derived from
     * @param wordFound a String representing the word
     *                  that user has found so far
     */
    private void calculateLetterProbabilities(Set<String> words, String wordFound) {
        List<Map<Integer, ArrayList<Character>>> maps = new ArrayList<>();
        // TODO skip found positions
        for (int i = 0; i < wordFound.length(); i++) {
            char positionLetter = wordFound.charAt(i);
            if (positionLetter != '⏡') {
                // skip calculated letter
                System.out.println(String.format("Skipping letter probability for letter: '%c'",
                        positionLetter));
                maps.add(null);
                continue;
            }
            // TODO for all valid letters of this position
            Map<Integer, ArrayList<Character>> lettersProbs = new HashMap<>() {{
                put(5, new ArrayList<>());
                put(10, new ArrayList<>());
                put(15, new ArrayList<>());
                put(30, new ArrayList<>());
            }};
            for (int letterAscii = 65; letterAscii <= 90; letterAscii++) {
                final char letter = (char) letterAscii;
                // skip already entered letters for this position
                if (positionEnteredLetters.get(i).contains(letter)) {
                    continue;
                }
                int instancesFound = 0;
                for (String word : words) {
                    if (word.charAt(i) == letter) instancesFound++;
                }
//                System.out.println("Probability for position: " + i + " => " + probability);
                double probability = (double) instancesFound / words.size();
                if (probability >= 0.6) {
                    lettersProbs.get(5).add(letter);
                } else if (probability >= 0.4) {
                    lettersProbs.get(10).add(letter);
                } else if (probability >= 0.25) {
                    lettersProbs.get(15).add(letter);
                } else {
                    lettersProbs.get(30).add(letter);
                }
            }
            lettersProbs.forEach((key, value) -> System.out.println(key + ":" + value));
            System.out.println("END================" + lettersProbs.size());
            maps.add(lettersProbs);
        }
        this.positionLetterProbs = maps;
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
        if (positionEnteredLetters.get(index).contains(letter)) {
            System.out.println("has already clicked this letter for this position");
            return false;
        }

        positionEnteredLetters.get(index).add(letter);
        boolean result;
        if (wordToFind.charAt(index) == letter) {
            result = true;
            this.handleCorrectLetter(index, letter);
        } else {
            result = false;
            this.handleWrongLetter(index, letter);
        }
        attemptNo++;
        setSuccessPercentage(100 - (((double) getNbErrors() / attemptNo) * 100));
        System.out.println("Percentage now" + getSuccessPercentage());
        return result;
    }

    private void handleCorrectLetter(int index, char letter) {
        StringBuilder tmpWordFound = new StringBuilder(getWordFound());
        tmpWordFound.setCharAt(index, letter);
        String newWordFound = tmpWordFound.toString();
        /**
         * Remove words from subset that didn't have
         * entered character in this position
         */
        wordsSubset.removeIf(element -> element.charAt(index) != letter);
        // Get points of entered letter
        Integer letterPoints = findLetterPoints(index, letter);
        int currentPoints = getTotalPoints();
        System.out.println(String.format("Points for index: %d, letter: '%c' = %d", index, letter
                , letterPoints));
        setTotalPoints(currentPoints + letterPoints);

        // recalculate probabilities
        this.calculateLetterProbabilities(this.wordsSubset, newWordFound);
        // Kick off listeners on wordFound after new probabilities are correctly set
        setWordFound(newWordFound);
    }

    private void handleWrongLetter(int index, char letter) {
        /**
         * Remove words from subset that had
         * entered character in this position
         */
        wordsSubset.removeIf(element -> element.charAt(index) == letter);
        String wordFound = getWordFound();
        // recalculate probabilities
        this.calculateLetterProbabilities(this.wordsSubset, wordFound);
        setNbErrors(getNbErrors() + 1);

        // Remove 15 points
        int currentPoints = getTotalPoints();
        int totalPoints = Math.max((currentPoints - 15), 0);
        setTotalPoints(totalPoints);
        // invoke listener on error too
        // Kick off listeners on wordFound after new probabilities are correctly set
        setWordFound(getWordFound());
    }

    private Integer findLetterPoints(int index, Character letter) {
        Map<Integer, ArrayList<Character>> indexLetters = positionLetterProbs.get(index);
        for (Map.Entry<Integer, ArrayList<Character>> entry : indexLetters.entrySet()) {
            System.out.println("Key = " + entry.getKey() +
                    ", Value = " + entry.getValue());
            if (entry.getValue().contains(letter)) {
                return entry.getKey();
            }
        }
        return null;
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
            this.handleVictory();
            this.endGame();
            return true;
        }

        System.out.println("\n=> Nb tries remaining : " + (MAX_ERRORS - getNbErrors()));
        if (getNbErrors() == MAX_ERRORS) {
            // user lost
            System.out.println("\nYou lose!");
            System.out.println("=> Word to find was : " + wordToFind);
            this.handleDefeat();
            this.endGame();
            return false;
        }
        return letterWasCorrect;
    }

    private void handleVictory() {
        GameResult victory = new GameResult(this.wordToFind, getNbErrors(),
                GameResult.Winner.HUMAN);
        this.last5Games.add(victory);
        if (this.last5Games.size() > 5) this.last5Games.removeFirst();
    }

    private void handleDefeat() {
        GameResult defeat = new GameResult(this.wordToFind, getNbErrors(),
                GameResult.Winner.COMPUTER);

        this.last5Games.add(defeat);
        if (this.last5Games.size() > 5) this.last5Games.removeFirst();
    }

    /**
     * TODO reload game state on end
     * clear errors, points, success percentage, wordToFind
     */
    private void endGame() {
        this.setNbErrors(0);
        this.setTotalPoints(0);
        this.setSuccessPercentage(0.00);
        this.wordToFind = null;
    }
}
