package com.pranjal.dailyquotes;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class PostsAdapter extends RecyclerView.Adapter<PostsAdapter.PostsViewHolder> {

    ArrayList<PostModel> postList;
    Context context;
    onItemClickListner listner;

    public PostsAdapter(ArrayList<PostModel> postList, Context context) {
        this.postList = postList;
        this.context = context;
    }

    @NonNull
    @Override
    public PostsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.post_card, parent, false);
        return new PostsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostsViewHolder holder, int position) {
        holder.textViewTitle.setText(postList.get(position).getTitle());
        holder.textViewDescription.setText(postList.get(position).getDescription());
        holder.textViewAuthorName.setText("Written By: "+postList.get(position).getAuthorName());
        holder.textViewDate.setText(postList.get(position).getDate());
    }

    @Override
    public int getItemCount() {
        return postList.size();
    }


    class PostsViewHolder extends RecyclerView.ViewHolder {
        TextView textViewTitle, textViewDescription, textViewAuthorName, textViewDate;



        public PostsViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewTitle = itemView.findViewById(R.id.textViewHeadingShowPost);
            textViewDescription = itemView.findViewById(R.id.textViewDescriptionShow);
            textViewAuthorName = itemView.findViewById(R.id.textViewAuthorName);
            textViewDate = itemView.findViewById(R.id.textViewDateShow);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if(listner != null && (position != RecyclerView.NO_POSITION)){
                        listner.onItemClick(postList.get(position));
                    }
                }
            });
        }
    }

    public interface onItemClickListner{
        void onItemClick(PostModel postModel);
    }

    public void setOnItemClickListner(onItemClickListner listner){
        this.listner = listner;
    }
}


