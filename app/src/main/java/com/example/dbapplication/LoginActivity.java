package com.example.dbapplication;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import java.io.IOException;

public class LoginActivity extends AppCompatActivity {

    private EditText phoneEditText;
    private EditText passwordEditText;
    private Button loginButton;
    private TextView resultTextView;

    private static final String LOGIN_URL = "http://10.0.2.2:8080/login"; // 使用你的服务器地址

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // 获取布局中的视图
        phoneEditText = findViewById(R.id.phoneEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        loginButton = findViewById(R.id.loginButton);
        resultTextView = findViewById(R.id.resultTextView);

        // 设置按钮点击事件
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performLogin();
            }
        });
    }

    private void performLogin() {
        String phone = phoneEditText.getText().toString();
        String password = passwordEditText.getText().toString();

        OkHttpClient client = new OkHttpClient();
        FormBody formBody = new FormBody.Builder()
                .add("phone", phone)
                .add("password", password)
                .build();

        Request request = new Request.Builder()
                .url(LOGIN_URL)
                .post(formBody)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() -> resultTextView.setText("Login failed: " + e.getMessage()));
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    runOnUiThread(() -> resultTextView.setText("Unexpected code: " + response));
                } else {
                    final String responseData = response.body().string();
                    runOnUiThread(() -> resultTextView.setText("Response: " + responseData));
                }
            }
        });
    }
}
