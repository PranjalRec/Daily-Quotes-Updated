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

public class SavedQuotesRecyclerAdapter extends RecyclerView.Adapter<SavedQuotesViewHolder>{

    ArrayList<SavedQuoteModel> quoteList;
    ArrayList<String> quoteKeyList;
    Context context;

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference reference;

    FirebaseAuth auth = FirebaseAuth.getInstance();
    FirebaseUser user;
    int adapterPosition = -1;

    String uid, quoteKey;


    public SavedQuotesRecyclerAdapter(ArrayList<SavedQuoteModel> quoteList, Context context) {
        this.quoteList = quoteList;
        this.context = context;
//        this.quoteKeyList = quoteKeyList;
    }

    @NonNull
    @Override
    public SavedQuotesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.quote_card,parent, false);
        return new SavedQuotesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SavedQuotesViewHolder holder, int position) {
        Picasso.get().load(quoteList.get(position).getQuote()).into(holder.imageViewQuote, new Callback() {
            @Override
            public void onSuccess() {
                holder.progressBar.setVisibility(View.INVISIBLE);
                holder.imageButtonShare.setVisibility(View.VISIBLE);
                holder.imageButtonDelete.setVisibility(View.VISIBLE);
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

        holder.imageButtonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reference = database.getReference();
                user = auth.getCurrentUser();

                adapterPosition = holder.getLayoutPosition();



                if(user != null){
                    uid = user.getUid();
                    if(quoteList.size() != 0){
                        quoteKey = quoteList.get(adapterPosition).getQuoteKey();
                        reference.child("users").child(uid).child("savedQuotes").child(quoteKey).removeValue();
                        notifyDataSetChanged();
                    }
                    else{
                        notifyItemRemoved(0);
                    }
                }

            }
        });

    }

    @Override
    public int getItemCount() {
        return quoteList.size();
    }

    @Override
    public void onViewRecycled(@NonNull SavedQuotesViewHolder holder) {
        holder.progressBar.setVisibility(View.VISIBLE);
        holder.imageButtonDelete.setVisibility(View.INVISIBLE);
        holder.imageButtonShare.setVisibility(View.INVISIBLE);
        super.onViewRecycled(holder);
    }

    private void shareImageandText(Bitmap bitmap) {
        Uri uri = getImageToShare(bitmap);
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_STREAM, uri);
        intent.putExtra(Intent.EXTRA_TEXT, "Hello I got an amazing quote from daily quotes app");
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

class SavedQuotesViewHolder extends RecyclerView.ViewHolder {
    ImageView imageViewQuote;
    ImageButton imageButtonShare, imageButtonDelete;
    ProgressBar progressBar;

    public SavedQuotesViewHolder(@NonNull View itemView) {
        super(itemView);

        imageViewQuote = itemView.findViewById(R.id.imageViewQuote);
        imageButtonShare = itemView.findViewById(R.id.imageButtonShare);
        imageButtonDelete = itemView.findViewById(R.id.imageButtonDelete);
        progressBar = itemView.findViewById(R.id.progressBarQuoteCard);

    }
}