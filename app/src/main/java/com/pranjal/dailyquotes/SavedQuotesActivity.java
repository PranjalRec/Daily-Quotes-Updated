package com.pranjal.dailyquotes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class SavedQuotesActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    ArrayList<String> quoteList = new ArrayList<>();
    ArrayList<String> quoteKeyList = new ArrayList<>();
    ArrayList<SavedQuoteModel> quoteModels = new ArrayList<>();
    FirebaseAuth auth = FirebaseAuth.getInstance();
    FirebaseUser user;
    FirebaseDatabase database;
    DatabaseReference reference;
    String uid;
    String quoteUri, quoteKey;
    SavedQuotesRecyclerAdapter quoteAdapter;
    TextView textViewNoSavedQuotes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getSupportActionBar().setTitle("Saved Quotes");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_quotes);
        recyclerView = findViewById(R.id.recyclerViewSavedQuotes);
        textViewNoSavedQuotes = findViewById(R.id.textviewNoSavedQuotes);



        user = auth.getCurrentUser();
        database = FirebaseDatabase.getInstance();
        reference = database.getReference();

        quoteAdapter = new SavedQuotesRecyclerAdapter(quoteModels, SavedQuotesActivity.this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(SavedQuotesActivity.this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);


        if(user != null){
            uid = user.getUid();
            reference.child("users").child(uid).child("savedQuotes").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    quoteModels.clear();
                    if(snapshot.exists()){
                        for(DataSnapshot dsp : snapshot.getChildren()){
                            if(dsp.exists()){
                                quoteUri = dsp.getValue().toString();
                                quoteKey = dsp.getKey();
                                quoteList.add(quoteUri);
                                quoteKeyList.add(quoteKey);
                                quoteModels.add(new SavedQuoteModel(quoteUri, quoteKey));

                            }
                        }
                        recyclerView.setAdapter(quoteAdapter);
                        quoteAdapter.notifyDataSetChanged();
                        if(!quoteModels.isEmpty()){
                            textViewNoSavedQuotes.setVisibility(View.INVISIBLE);
                        }else{
                            textViewNoSavedQuotes.setVisibility(View.VISIBLE);
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(SavedQuotesActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });

        }
        else{
            Toast.makeText(SavedQuotesActivity.this, "Login First.", Toast.LENGTH_SHORT).show();
            finish();
        }
    }
}