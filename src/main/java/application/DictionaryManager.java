package application;

import java.io.*;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.*;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * A service for managing dictionaries
 * that are retrieved from OpenLibrary API
 *
 * @author Markos Girgis
 * @version 1.5
 * @since 1.0
 */
public class DictionaryManager {
    private final String DICT_DIRECTORY = "medialab";
    private String activeDictionary = null;
    private IntegerProperty activeDictionaryLength = new SimpleIntegerProperty(
            this,
            "activeDictionaryLength",
            0);

    private BooleanProperty dictionaryLoaded = new SimpleBooleanProperty(this, "dictionaryLoaded"
            , false);

    private Set<String> activeDictWords = new HashSet<>();

    /**
     * A flag showing whether a dictionary
     * has been loaded yet or not
     *
     * @return boolean simple property
     */
    public BooleanProperty dictionaryLoadedProperty() {
        return dictionaryLoaded;
    }

    private void setDictionaryLoaded(boolean dictionaryLoaded) {
        this.dictionaryLoaded.set(dictionaryLoaded);
    }

    /**
     * Creates a dictionary
     *
     * @param dictionaryId  An alphanumeric value which
     *                      will be user to identify the dictionary
     * @param openLibraryId The Open Library ID which needs to match a book from
     *                      https://openlibary.org.works API
     * @throws UndersizeException  Not enough words
     * @throws UnbalancedException Not enough long words
     */
    public void createDictionary(String dictionaryId, String openLibraryId) throws
            UndersizeException, UnbalancedException {
        String description = fetchBookDescription(openLibraryId);
        if (description == null) throw new Error("Description key is missing in API response");
        Set<String> words = parseDescription(description);
        System.out.println(words);
        int dictSize = words.size();
        if (dictSize < 20) throw new UndersizeException("Dictionary has " + dictSize + " words");
        int moreThan9Letters = 0;
        for (String word : words) {
            if (word.length() >= 9) moreThan9Letters++;
        }
        double longWordsPercentage = (((double) moreThan9Letters / dictSize) * 100);
        if (longWordsPercentage < 20)
            throw new UnbalancedException("Percentage of words with 9 or more letters is " + longWordsPercentage);

        createDictionaryFile(dictionaryId, words);
    }


    /**
     * Reads local directory for saved dictionaries
     *
     * @return list of dictionary names
     */
    public List<String> getDictionaryChoices() {

        List<String> choices = new ArrayList<>();
        File dir = new File(DICT_DIRECTORY);

        if (!dir.exists()) {
            dir.mkdir();
        }
        File[] listOfFiles = dir.listFiles();

        for (File listOfFile : listOfFiles) {
            if (listOfFile.isFile()) {
                System.out.println("File " + listOfFile.getName());
                String fileNameWithOutExt = listOfFile.getName().replaceFirst("[.][^.]+$", "");
                choices.add(fileNameWithOutExt);
            } else if (listOfFile.isDirectory()) {
                System.out.println("Directory " + listOfFile.getName());
            }
        }
        return choices;
    }

    /**
     * @return the name of the active Dictionary
     */
    public String getActiveDictionary() {
        return this.activeDictionary;
    }

    /**
     * Reads the dictionary from local directory
     * and sets it as active
     *
     * @param dictionaryId the identifier of the dictionary
     *                     that we wish to set as active
     */
    public void setActiveDictionary(String dictionaryId) {
        activeDictWords.clear();
        setActiveDictionaryLength(0);
        activeDictionary = dictionaryId;
        try {
            File myObj = new File(DICT_DIRECTORY + File.separator + dictionaryId + ".txt");
            Scanner myReader = new Scanner(myObj);
            while (myReader.hasNextLine()) {
                String data = myReader.nextLine();
//                System.out.println(data);
                activeDictWords.add(data);
            }
            myReader.close();
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
        setActiveDictionaryLength(activeDictWords.size());
        this.setDictionaryLoaded(true);
    }

    /**
     * @return the number of words in the current
     * active dictionary
     */
    public IntegerProperty activeDictionaryLengthProperty() {
        return activeDictionaryLength;
    }

    private void setActiveDictionaryLength(int activeDictionaryLength) {
        this.activeDictionaryLength.set(activeDictionaryLength);
    }

    /**
     * @return all the words in the current
     * active dictionary
     */
    public Set<String> getActiveDictWords() {
        return activeDictWords;
    }

    /**
     * Fetches book description from openlibrary.org/works API
     *
     * @param bookId the openLibraryId for the desired book
     *               e.g. OL45883W
     * @return value description of parsed Json
     */
    public String fetchBookDescription(String bookId) {
        String queryParams = "?format=json";
        String url = "https://openlibrary.org/works/" + bookId + ".json" + queryParams;
        System.out.println("url: " + url);

        // https://www.baeldung.com/java-9-http-client
        HttpClient client = HttpClient.newBuilder()
                .followRedirects(HttpClient.Redirect.ALWAYS)
                .build();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .timeout(Duration.of(10, ChronoUnit.SECONDS))
                .build();
        String response = client
                .sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(resp -> {
                    int status = resp.statusCode();
                    if (status != 200) {
                        // throw not found exception
                        System.err.println("Error status code: " + status);
                    } else {
                        System.out.println("Success status code: " + status);
                    }
                    return resp;
                })
                // lambda expression, use body method of HttpResponse class
                .thenApply(HttpResponse::body)
                .thenApply(DictionaryManager::parseJson).join(); // if we don't join we won't see
        // anything
        return response;
    }

    private static String parseJson(String responseBody) {
        System.out.println("responseBody" + responseBody);
        try {
            JSONObject work = new JSONObject(responseBody);
//            String description = work.getString("description");
            JSONObject description = work.getJSONObject("description");
            String value = description.getString("value");
            System.out.println("description is" + value);
            return value;

//            System.out.println("description is" + description);
//            return description;
        } catch (JSONException e) {
            return null;
        }
    }

    private Set<String> parseDescription(String description) {
        String[] tokens = description.split("[\\W]");
        Set<String> words = new HashSet<>();
        for (int i = 0; i < tokens.length; i++) {
            if (tokens[i].length() < 6) continue;
            words.add(tokens[i].toUpperCase(Locale.ROOT));
        }
        return words;
    }

    private void createDictionaryFile(String dictionaryId, Set<String> words) {
        try {
            File dir = new File(DICT_DIRECTORY);
            if (!dir.exists()) {
                dir.mkdir();
            }

            BufferedWriter writer =
                    new BufferedWriter(new FileWriter(DICT_DIRECTORY + File.separator + dictionaryId + ".txt"));
//            for (String word : words) {
//                writer.write(word);
//                writer.newLine();
//            }
            Iterator<String> iterator = words.iterator();
            while (iterator.hasNext()) {
                String word = iterator.next();
                writer.write(word);
                if (iterator.hasNext()) writer.newLine();
            }

            writer.close();
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }
}
