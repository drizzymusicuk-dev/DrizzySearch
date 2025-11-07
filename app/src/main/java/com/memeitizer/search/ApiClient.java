package com.memeitizer.search;

import okhttp3.*;
import java.io.IOException;

public class ApiClient {
    private static final String BASE_URL = "https://memeitizer.com/search/";

    public static void search(String query, Callback callback) {
        OkHttpClient client = new OkHttpClient();
        RequestBody body = new FormBody.Builder()
                .add("query", query)
                .build();
        Request req = new Request.Builder()
                .url(BASE_URL + "search.php")
                .post(body)
                .build();
        client.newCall(req).enqueue(callback);
    }

    public static void login(String user, String pass, Callback callback) {
        OkHttpClient client = new OkHttpClient();
        RequestBody body = new FormBody.Builder()
                .add("username", user)
                .add("password", pass)
                .build();
        Request req = new Request.Builder()
                .url("https://memeitizer.com/search-backend/api-login.php")
                .post(body)
                .build();
        client.newCall(req).enqueue(callback);
    }
}
