package com.pranjal.dailyquotes;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;

public class QuotesRecyclerAdapter extends RecyclerView.Adapter<QuotesViewHolder>{

    ArrayList<String> quoteList;
    Context context;

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference reference;

    FirebaseAuth auth = FirebaseAuth.getInstance();
    FirebaseUser user;

    String uid;


    public QuotesRecyclerAdapter(ArrayList<String> quoteList, Context context) {
        this.quoteList = quoteList;
        this.context = context;
    }


    @NonNull
    @Override
    public QuotesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.quote_card,parent, false);
        return new QuotesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull QuotesViewHolder holder, int position) {
        Picasso.get().load(quoteList.get(position)).into(holder.imageViewQuote, new Callback() {
            @Override
            public void onSuccess() {
                holder.progressBar.setVisibility(View.INVISIBLE);
                holder.imageButtonShare.setVisibility(View.VISIBLE);
                holder.imageButtonDownload.setVisibility(View.VISIBLE);
                holder.setIsRecyclable(false);
            }

            @Override
            public void onError(Exception e) {
                Toast.makeText(context.getApplicationContext(), e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        holder.imageButtonShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //share image feature from https://www.geeksforgeeks.org/how-to-share-image-of-your-app-with-another-app-in-android/
                BitmapDrawable bitmapDrawable = (BitmapDrawable) holder.imageViewQuote.getDrawable();
                Bitmap bitmap = bitmapDrawable.getBitmap();
                shareImageandText(bitmap);

            }
        });

        holder.imageButtonDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reference = database.getReference();
                user = auth.getCurrentUser();

                String savedQuote = quoteList.get(holder.getAdapterPosition());

                if(user != null){
                    uid = user.getUid();
                    reference.child("users").child(uid).child("savedQuotes").push().setValue(savedQuote).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Toast.makeText(context.getApplicationContext(), "Quote saved! You can see it in Saved Quotes section", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(context.getApplicationContext(), e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }

            }
        });

    }

    @Override
    public int getItemCount() {
        return quoteList.size();
    }

    @Override
    public void onViewRecycled(@NonNull QuotesViewHolder holder) {
        holder.progressBar.setVisibility(View.VISIBLE);
        holder.imageButtonDownload.setVisibility(View.INVISIBLE);
        holder.imageButtonShare.setVisibility(View.INVISIBLE);
        super.onViewRecycled(holder);
    }

    private void shareImageandText(Bitmap bitmap) {
        Uri uri = getImageToShare(bitmap);
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_STREAM, uri);
        intent.putExtra(Intent.EXTRA_TEXT, "Hello! I got an amazing quote from daily quotes app");
        intent.putExtra(Intent.EXTRA_SUBJECT, "Motivational Quote");
        intent.setType("image/png");
        context.startActivity(Intent.createChooser(intent, "Share Via"));
    }

    private Uri getImageToShare(Bitmap bitmap) {
        File imagefolder = new File(context.getCacheDir(), "images");
        Uri uri = null;
        try {
            imagefolder.mkdirs();
            File file = new File(imagefolder, "shared_image.png");
            FileOutputStream outputStream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
            outputStream.flush();
            outputStream.close();
            uri = FileProvider.getUriForFile(context.getApplicationContext(), "com.pranjal.dailyquotes", file);
        } catch (Exception e) {
            Toast.makeText(context.getApplicationContext(), "" + e.getMessage(), Toast.LENGTH_LONG).show();
        }
        return uri;
    }
}

class QuotesViewHolder extends RecyclerView.ViewHolder {
    ImageView imageViewQuote;
    ImageButton imageButtonShare, imageButtonDownload;
    ProgressBar progressBar;

    public QuotesViewHolder(@NonNull View itemView) {
        super(itemView);

        imageViewQuote = itemView.findViewById(R.id.imageViewQuote);
        imageButtonShare = itemView.findViewById(R.id.imageButtonShare);
        imageButtonDownload = itemView.findViewById(R.id.imageButtonDownload);
        progressBar = itemView.findViewById(R.id.progressBarQuoteCard);

    }
}