package com.pranjal.dailyquotes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MyPostsActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    ArrayList<PostModel> postList = new ArrayList<>();
    FirebaseAuth auth = FirebaseAuth.getInstance();
    FirebaseUser user;
    FirebaseDatabase database;
    DatabaseReference reference;
    String uid;

    MyPostsAdapter postsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getSupportActionBar().setTitle("My Posts");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_posts);


        recyclerView = findViewById(R.id.recyclerViewMyPosts);
        user = auth.getCurrentUser();
        database = FirebaseDatabase.getInstance();
        reference = database.getReference();

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(MyPostsActivity.this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);


        postsAdapter = new MyPostsAdapter(postList, MyPostsActivity.this);

        if(user != null){
            uid = user.getUid();
            reference.child("users").child(uid).child("posts").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    postList.clear();
                    if(snapshot.exists()){
                        for(DataSnapshot dsp : snapshot.getChildren()){
                            if(dsp.exists()){
                                PostModel postModel = dsp.getValue(PostModel.class);
                                postList.add(postModel);
                            }
                        }

                        recyclerView.setLayoutManager(linearLayoutManager);
                        recyclerView.setAdapter(postsAdapter);
                        postsAdapter.notifyDataSetChanged();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
        else{
            Toast.makeText(MyPostsActivity.this, "Login First.", Toast.LENGTH_SHORT).show();
        }

        postsAdapter.setOnItemClickListner(new PostsAdapter.onItemClickListner() {
            @Override
            public void onItemClick(PostModel postModel) {
                Bundle bundle = new Bundle();
                bundle.putString("title", postModel.getTitle());
                bundle.putString("description", postModel.getDescription());
                bundle.putString("author", postModel.getAuthorName());
                bundle.putString("date", postModel.getDate());
                bundle.putString("postKey", postModel.getPostKey());

                Intent intent = new Intent(MyPostsActivity.this, PostOpenActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);
                Toast.makeText(MyPostsActivity.this, postModel.getAuthorName(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}