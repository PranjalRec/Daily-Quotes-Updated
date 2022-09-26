package com.pranjal.dailyquotes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class AddNewPost extends AppCompatActivity {

    EditText editTextTitle, editTextDescription;
    Button buttonSubmit;

    String title, description, uid, name, postKey, date;

    FirebaseDatabase database;
    DatabaseReference reference;

    FirebaseAuth auth = FirebaseAuth.getInstance();
    FirebaseUser user = auth.getCurrentUser();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getSupportActionBar().setTitle("Add New Post");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_post);

        editTextTitle = findViewById(R.id.editTextTitleNewPost);
        editTextDescription = findViewById(R.id.editTextDescriptionNewPost);
        buttonSubmit = findViewById(R.id.buttonSubmitPost);

        database = FirebaseDatabase.getInstance();
        reference = database.getReference();
        uid = user.getUid();

        reference.child("users").child(uid).child("name").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                name = snapshot.getValue().toString();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        buttonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                title = editTextTitle.getText().toString();
                description = editTextDescription.getText().toString();

                date = new SimpleDateFormat("dd MMM yyyy HH:mm:ss", Locale.getDefault()).format(new Date());

                if(title.matches("") || description.matches("")){
                    Toast.makeText(AddNewPost.this, "Enter the title and description first",
                            Toast.LENGTH_SHORT).show();
                }
                else{

                    DatabaseReference newPostRef = reference.child("posts").push();
                    postKey = newPostRef.getKey();

                    HashMap<String, Object> post = new HashMap<>();
                    post.put("title", title);
                    post.put("description", description);
                    post.put("likes", "0");
                    post.put("author", name);
                    post.put("date", date);
                    post.put("postKey", postKey);

                    newPostRef.setValue(post);
                    reference.child("users").child(uid).child("posts").child(postKey).setValue(post).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Toast.makeText(AddNewPost.this, "Post saved successfully", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(AddNewPost.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }
}