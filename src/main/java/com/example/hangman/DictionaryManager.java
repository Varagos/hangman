package com.example.hangman;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import org.json.JSONException;
import org.json.JSONObject;

public class DictionaryManager {

    // async
    public String fetchBookDescription(String bookId) {
        String url = "https://openlibrary.org/works/" + bookId + ".json";

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).build();
        // we want are body response as string
        return client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(resp -> {
                    int status = resp.statusCode();
                    if (status != 200) {
                        // throw not found exception
                        System.err.println("Error" + status);
                    } else {
                        System.out.println("Success" + status);
                    }
                    return resp;
                })
                // lambda expression, use body method of HttpResponse class
                .thenApply(HttpResponse::body)
//                .thenAccept(System.out::println)
//                .thenApply(HttpService::parse)
                .thenApply(DictionaryManager::parseJson)
                .join(); // if we don't join we won't see anything
    }

    // 3rd party library to parse json
    public static String parseJson(String responseBody) {
        JSONObject work = new JSONObject(responseBody);
        try {
            String description = work.getString("description");
            System.out.println("description is" + description);
            return description;
        } catch (JSONException e) {
            return null;
        }
    }

    public static void main(String[] args) {
        DictionaryManager dictionaryManager = new DictionaryManager();
        String description = dictionaryManager.fetchBookDescription("OL45883W");
        System.out.println("result is" + description);
    }
}
