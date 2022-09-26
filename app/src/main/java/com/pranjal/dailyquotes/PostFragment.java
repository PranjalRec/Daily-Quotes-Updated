package com.pranjal.dailyquotes;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class PostFragment extends Fragment {
    FloatingActionButton fabNewPost;
    RecyclerView recyclerView;
    ArrayList<PostModel> postList = new ArrayList<>();

    FirebaseDatabase database;
    DatabaseReference reference;

    PostsAdapter postsAdapter;

    FirebaseAuth auth = FirebaseAuth.getInstance();
    FirebaseUser user;

    public static Fragment newInstance(){
        return new PostFragment();
    }

    public PostFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_post, container, false);
        fabNewPost = view.findViewById(R.id.floatingActionButtonPosts);
        recyclerView = view.findViewById(R.id.recyclerViewPosts);

        database = FirebaseDatabase.getInstance();
        reference = database.getReference();
        user = auth.getCurrentUser();

        reference.child("posts").addValueEventListener(new ValueEventListener() {
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
                    postsAdapter.notifyDataSetChanged();
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(requireContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        postsAdapter = new PostsAdapter(postList, requireContext());
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(requireContext());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(postsAdapter);



        postsAdapter.setOnItemClickListner(new PostsAdapter.onItemClickListner() {
            @Override
            public void onItemClick(PostModel postModel) {
                Bundle bundle = new Bundle();
                bundle.putString("title", postModel.getTitle());
                bundle.putString("description", postModel.getDescription());
                bundle.putString("author", postModel.getAuthorName());
                bundle.putString("date", postModel.getDate());
                bundle.putString("postKey", postModel.getPostKey());

                Intent intent = new Intent(getActivity(), PostOpenActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

        fabNewPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                user = auth.getCurrentUser();
                if(user != null){
                    Intent intent = new Intent(requireContext(),AddNewPost.class);
                    startActivity(intent);
                }
                else{
                    Intent i = new Intent(requireContext(), SignInActivity.class);
                    startActivity(i);
                }

            }
        });
        return view;
    }
}