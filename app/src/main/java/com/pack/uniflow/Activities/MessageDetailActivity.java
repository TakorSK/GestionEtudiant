package com.pack.uniflow.Activities;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.pack.uniflow.R;

public class MessageDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_detail);

        TextView title  = findViewById(R.id.detail_title);
        TextView sender = findViewById(R.id.detail_sender);
        TextView body   = findViewById(R.id.detail_body);

        title.setText(getIntent().getStringExtra("title"));
        sender.setText("From: " + getIntent().getStringExtra("sender"));
        body.setText(getIntent().getStringExtra("body"));
    }
}
