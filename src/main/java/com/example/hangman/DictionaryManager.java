package com.example.hangman;

import java.io.*;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.*;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.StringProperty;
import org.json.JSONException;
import org.json.JSONObject;

public class DictionaryManager {
    private String DICT_DIRECTORY = "medialab";
    private String activeDictionary = null;
    private IntegerProperty activeDictionaryLength = new SimpleIntegerProperty(
            this,
            "activeDictionaryLength",
            0);

    private Set<String> activeDictWords = new HashSet<String>();

    /**
     * Creates a dictionary
     *
     * @param dictionaryId  An alphanumeric value which
     *                      will be user to identify the dictionary
     * @param openLibraryId The Open Library ID which needs to match a book from
     *                      https://openlibary.org.works API
     * @throws UndersizeException
     * @throws UnbalancedException
     */
    public void createDictionary(String dictionaryId, String openLibraryId) throws
            UndersizeException, UnbalancedException {
//    throw new InvalidCountException("3 words founds");  //
        String description = fetchBookDescription(openLibraryId);
//        System.out.println("result is" + description);
        if (description == null) throw new Error("description key is missing");
        Set<String> words = parseDescription(description);
        System.out.println(words);
        int dictSize = words.size();
        if (dictSize < 20) throw new UndersizeException("Dictionary has " + dictSize + " words");
        int moreThan9Letters = 0;
        Iterator value = words.iterator();
        while (value.hasNext()) {
            String word = (String) value.next();
            if (word.length() >= 9) moreThan9Letters++;
        }
        double longWordsPercentage = (((double) moreThan9Letters / dictSize) * 100);
        if (longWordsPercentage < 20)
            throw new UnbalancedException("Percentage of words with 9 or more letters is " + longWordsPercentage);

        createDictionaryFile(dictionaryId, words);
    }


    public List<String> getDictionaryChoices() {

        List<String> choices = new ArrayList<>();
        File dir = new File(DICT_DIRECTORY);

        if (!dir.exists()) {
            dir.mkdir();
        }
        File[] listOfFiles = dir.listFiles();

        for (int i = 0; i < listOfFiles.length; i++) {
            if (listOfFiles[i].isFile()) {
                System.out.println("File " + listOfFiles[i].getName());
                String fileNameWithOutExt = listOfFiles[i].getName().replaceFirst("[.][^.]+$", "");
                choices.add(fileNameWithOutExt);
            } else if (listOfFiles[i].isDirectory()) {
                System.out.println("Directory " + listOfFiles[i].getName());
            }
        }
        return choices;
    }

    public String getActiveDictionary() {
        return this.activeDictionary;
    }

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
    }

    public int getActiveDictionaryLength() {
        return activeDictionaryLength.get();
    }

    public IntegerProperty activeDictionaryLengthProperty() {
        return activeDictionaryLength;
    }

    public void setActiveDictionaryLength(int activeDictionaryLength) {
        this.activeDictionaryLength.set(activeDictionaryLength);
    }

    public Set<String> getActiveDictWords() {
        return activeDictWords;
    }

    public void setActiveDictWords(Set<String> activeDictWords) {
        this.activeDictWords = activeDictWords;
    }

    // async
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

    // 3rd party library to parse json
    public static String parseJson(String responseBody) {
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

    public static void main(String[] args) {
        DictionaryManager dictionaryManager = new DictionaryManager();
        // here description is raw text
        String description = dictionaryManager.fetchBookDescription("OL45883W");
        // here it's object with value property
        // OL31390631M
        System.out.println("result is" + description);
    }
}
