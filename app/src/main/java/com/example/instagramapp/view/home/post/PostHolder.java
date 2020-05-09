package com.example.instagramapp.view.home.post;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.instagramapp.R;
import com.example.instagramapp.model.Post;
import com.squareup.picasso.Picasso;

public class PostHolder extends RecyclerView.ViewHolder {

    private ImageView imgAccount;
    private TextView tvName;
    private ImageView imgPost;
    private TextView tvTitle;
    private TextView tvDate;
    private TextView tvcoment;

    public ImageView imgLike;
    private TextView tvNumberOfLikes;

    public PostHolder(@NonNull View itemView) {
        super(itemView);
        initView();
    }

    private void initView() {
        imgAccount = itemView.findViewById(R.id.post_account_imageView);
        tvName = itemView.findViewById(R.id.post_account_name_textView);
        imgPost = itemView.findViewById(R.id.post_imageView);
        tvTitle = itemView.findViewById(R.id.post_title_textView);
        tvDate = itemView.findViewById(R.id.post_date_textView);

        tvcoment = itemView.findViewById(R.id.Post_comments_textView);

        imgLike = itemView.findViewById(R.id.post_like_imageView);
        tvNumberOfLikes = itemView.findViewById(R.id.post_numberOfLikes_textView);
    }

    void bindView(Post post) {
//        Picasso.get()
//                .load(post.getUser().getImage())
//                .placeholder(R.drawable.img_placeholder)
//                .into(imgAccount);

        Picasso.get()
                .load(post.getImage())
                .placeholder(R.drawable.img_placeholder)
                .into(imgPost);

       tvName.setText(post.getUser().getName());
        tvTitle.setText(post.getTitle());
        tvDate.setText(post.getDate());
        tvcoment.setText(post.getComment());

        String likes = post.getNumberOfLikes() + " Persons";
       tvNumberOfLikes.setText(likes);
    }
}
