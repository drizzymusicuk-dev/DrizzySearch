package com.memeitizer.search;

import android.content.*;
import android.os.*;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import java.io.IOException;
import okhttp3.*;

public class LoginActivity extends AppCompatActivity {
    EditText user, pass; Button loginBtn;

    protected void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.activity_login);

        user = findViewById(R.id.username);
        pass = findViewById(R.id.password);
        loginBtn = findViewById(R.id.loginBtn);

        loginBtn.setOnClickListener(v -> {
            ApiClient.login(user.getText().toString(), pass.getText().toString(), new Callback() {
                public void onFailure(Call c, IOException e) {
                    runOnUiThread(() -> Toast.makeText(LoginActivity.this,"Network error",Toast.LENGTH_SHORT).show());
                }
                public void onResponse(Call c, Response r) throws IOException {
                    if (r.isSuccessful() && r.body().string().contains("\"success\":true")) {
                        getSharedPreferences("drizzy", MODE_PRIVATE)
                            .edit().putBoolean("logged", true).apply();
                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                        finish();
                    } else {
                        runOnUiThread(() -> Toast.makeText(LoginActivity.this,"Invalid credentials",Toast.LENGTH_SHORT).show());
                    }
                }
            });
        });
    }
}
