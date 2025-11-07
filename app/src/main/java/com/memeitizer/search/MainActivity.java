package com.memeitizer.search;

import android.os.*;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import okhttp3.*;
import org.json.*;
import java.io.IOException;
import java.util.*;

public class MainActivity extends AppCompatActivity {
    EditText queryBox; Button searchBtn; ListView list;
    List<Map<String,String>> results = new ArrayList<>();
    SearchAdapter adapter;

    protected void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.activity_main);

        queryBox = findViewById(R.id.queryBox);
        searchBtn = findViewById(R.id.searchBtn);
        list = findViewById(R.id.resultsList);
        adapter = new SearchAdapter(this, results);
        list.setAdapter(adapter);

        searchBtn.setOnClickListener(v -> doSearch());
    }

    void doSearch() {
        String q = queryBox.getText().toString().trim();
        if (q.isEmpty()) return;
        Toast.makeText(this, "Searching...", Toast.LENGTH_SHORT).show();
        ApiClient.search(q, new Callback() {
            public void onFailure(Call c, IOException e) {
                runOnUiThread(() -> Toast.makeText(MainActivity.this, "Error", Toast.LENGTH_SHORT).show());
            }
            public void onResponse(Call c, Response r) throws IOException {
                try {
                    JSONObject obj = new JSONObject(r.body().string());
                    String raw = obj.getString("results");
                    // very simple parse: split by newlines into pseudo results
                    results.clear();
                    for (String line : raw.split("\n")) {
                        if (line.trim().isEmpty()) continue;
                        Map<String,String> map = new HashMap<>();
                        map.put("title", line.length()>60?line.substring(0,60)+"...":line);
                        map.put("url", "https://drizzy.ai");
                        map.put("snippet", line);
                        results.add(map);
                    }
                    runOnUiThread(() -> adapter.notifyDataSetChanged());
                } catch (Exception ex) {
                    runOnUiThread(() -> Toast.makeText(MainActivity.this, "Parse error", Toast.LENGTH_SHORT).show());
                }
            }
        });
    }
}
