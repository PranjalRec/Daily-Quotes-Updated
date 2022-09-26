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

public class MyPostsAdapter extends RecyclerView.Adapter<MyPostsAdapter.MyPostsViewHolder> {

    ArrayList<PostModel> postList;
    Context context;

    PostsAdapter.onItemClickListner listner;

    public MyPostsAdapter(ArrayList<PostModel> postList, Context context) {
        this.postList = postList;
        this.context = context;
    }

    @NonNull
    @Override
    public MyPostsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.post_card, parent, false);
        return new MyPostsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyPostsViewHolder holder, int position) {
        holder.textViewTitle.setText(postList.get(position).getTitle());
        holder.textViewDescription.setText(postList.get(position).getDescription());
        holder.textViewAuthorName.setText(postList.get(position).getAuthorName());
        holder.textViewDate.setText(postList.get(position).getDate());

    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    class MyPostsViewHolder extends RecyclerView.ViewHolder {
        TextView textViewTitle, textViewDescription, textViewAuthorName, textViewDate;

        public MyPostsViewHolder(@NonNull View itemView) {
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

    public void setOnItemClickListner(PostsAdapter.onItemClickListner listner){
        this.listner = listner;
    }

}

