package com.pranjal.dailyquotes;

public class PostModel {
    public String title, description, author, date, likes, postKey;

    public PostModel(){

    }

    public PostModel(String title, String description, String authorName, String date, String likes, String postKey) {
        this.title = title;
        this.description = description;
        this.author = authorName;
        this.date = date;
        this.likes = likes;
        this.postKey = postKey;
    }

    public String getTitle() {
        return title;
    }

    public String getPostKey() {
        return postKey;
    }

    public void setPostKey(String postKey) {
        this.postKey = postKey;
    }

    public String getDescription() {
        return description;
    }

    public String getAuthorName() {
        return author;
    }

    public String getDate() {
        return date;
    }

    public String getLikes() {
        return likes;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setAuthorName(String authorName) {
        this.author = authorName;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setLikes(String likes) {
        this.likes = likes;
    }
}
