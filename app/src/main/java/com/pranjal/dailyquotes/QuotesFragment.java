package com.pranjal.dailyquotes;

import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class QuotesFragment extends Fragment {

    RecyclerView recyclerView;
    ArrayList<String> quoteList = new ArrayList<>();
    StorageReference reference;
    ProgressBar progressBar;

    public static Fragment newInstance(){
        return new QuotesFragment();
    }

    public QuotesFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_quotes, container, false);
        recyclerView = view.findViewById(R.id.recyclerViewQuotes);
        progressBar = view.findViewById(R.id.progressBarQuoteRecycler);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(requireContext());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);

        recyclerView.setLayoutManager(linearLayoutManager);
        QuotesRecyclerAdapter quotesRecyclerAdapter = new QuotesRecyclerAdapter(quoteList,requireContext());



        reference = FirebaseStorage.getInstance().getReference().child("image quotes/");
        reference.listAll().addOnSuccessListener(new OnSuccessListener<ListResult>() {
            @Override
            public void onSuccess(ListResult listResult) {
                for(StorageReference quoteRef : listResult.getItems()){
                    quoteRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            quoteList.add(uri.toString());
                        }
                    }).addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            progressBar.setVisibility(View.INVISIBLE);
                            recyclerView.setAdapter(quotesRecyclerAdapter);
                            recyclerView.hasFixedSize();
                            quotesRecyclerAdapter.notifyDataSetChanged();

                        }
                    });
                }
            }
        });
        return view;
    }
}