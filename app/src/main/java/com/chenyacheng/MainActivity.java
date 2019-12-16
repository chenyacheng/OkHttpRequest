package com.chenyacheng;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView textView = findViewById(R.id.text_view);

        Button get = findViewById(R.id.get_button);
        get.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                OkHttpRequest request = new OkHttpRequest(true, MainActivity.this, OkHttpRequest.GET, null);
                request.setUrl("http://192.168.1.77:8080/get");
                request.execute();
                request.setResponseListener(new ResponseListener() {
                    @Override
                    public void success(Object data) {
                        textView.setText(data.toString());
                    }

                    @Override
                    public void failure(String error) {
                        textView.setText(error);
                    }
                });
            }
        });

        Button post = findViewById(R.id.post_button);
        post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Map<String, String> map = new HashMap<>(16);
                map.put("a", "a");
                map.put("b", "b");
                OkHttpRequest request = new OkHttpRequest(true, MainActivity.this, OkHttpRequest.POST, map);
                request.setUrl("http://192.168.1.77:8080/post");
                request.execute();
                request.setResponseListener(new ResponseListener() {
                    @Override
                    public void success(Object data) {
                        textView.setText(data.toString());
                    }

                    @Override
                    public void failure(String error) {
                        textView.setText(error);
                    }
                });
            }
        });
    }
}
