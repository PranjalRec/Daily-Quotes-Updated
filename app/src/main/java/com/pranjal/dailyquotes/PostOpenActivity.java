package com.pranjal.dailyquotes;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

public class PostOpenActivity extends AppCompatActivity {

    TextView textViewTitle, textViewDescription, textViewDate, textViewAuthor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getSupportActionBar().setTitle("Post");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_open);

        textViewTitle = findViewById(R.id.textViewHeadingOpenPost);
        textViewDescription = findViewById(R.id.textViewDescriptionOpenPost);
        textViewDate = findViewById(R.id.textViewDateOpenPost);
        textViewAuthor = findViewById(R.id.textViewAuthorNameOpenPost);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();

        textViewTitle.setText(bundle.getString("title", "something went wrong"));
        textViewDescription.setText(bundle.getString("description", "something went wrong"));
        textViewDate.setText(bundle.getString("date", "something went wrong"));
        textViewAuthor.setText("Written By: "+bundle.getString("author", "something went wrong"));
    }
}