package com.pack.uniflow.Adapters;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.pack.uniflow.Models.Post;
import com.pack.uniflow.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewHolder> {

    private final Context context;
    private List<Post> postList;
    private final List<String> userTags; // ✅ Logged-in user's tags

    public PostAdapter(Context context, List<Post> postList, List<String> userTags) {
        this.context = context;
        this.userTags = userTags != null ? userTags : new ArrayList<>();
        setFilteredPosts(postList);
    }

    // ✅ Only keep posts where user has at least one matching tag
    private void setFilteredPosts(List<Post> allPosts) {
        if (allPosts == null) {
            this.postList = new ArrayList<>();
            return;
        }

        List<Post> filtered = new ArrayList<>();
        for (Post post : allPosts) {
            if (post.getTags() != null) {
                for (String tag : post.getTags()) {
                    if (userTags.contains(tag)) {
                        filtered.add(post);
                        break; // found a matching tag → no need to check further
                    }
                }
            }
        }
        this.postList = filtered;
    }

    // ✅ Update posts with filtering
    public void updatePosts(List<Post> newPosts) {
        setFilteredPosts(newPosts);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.fragment_item_post, parent, false);
        return new PostViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {
        Post post = postList.get(position);
        holder.title.setText(post.getTitle());
        holder.description.setText(post.getDescription());
        holder.authorName.setText("By " + post.getAuthorName());

        holder.postDate.setText(formatDate(post.getCreatedAt()));

        String imageUri = post.getImageUri();
        if (imageUri != null && !imageUri.isEmpty()) {
            holder.image.setVisibility(View.VISIBLE);
            Glide.with(context)
                    .load(Uri.parse(imageUri))
                    .placeholder(R.drawable.placeholder)
                    .error(R.drawable.placeholder)
                    .centerCrop()
                    .into(holder.image);
            setDividerTopMargin(holder.postDivider, 32);
        } else {
            holder.image.setVisibility(View.GONE);
            setDividerTopMargin(holder.postDivider, 8);
        }
    }

    @Override
    public int getItemCount() {
        return postList != null ? postList.size() : 0;
    }

    private void setDividerTopMargin(View divider, int dp) {
        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) divider.getLayoutParams();
        int px = dpToPx(dp);
        if (params.topMargin != px) {
            params.topMargin = px;
            divider.setLayoutParams(params);
        }
    }

    private int dpToPx(int dp) {
        float density = context.getResources().getDisplayMetrics().density;
        return Math.round(dp * density);
    }

    private String formatDate(String rawDate) {
        if (rawDate == null || rawDate.isEmpty()) return "";
        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        SimpleDateFormat outputFormat = new SimpleDateFormat("MMM d, yyyy h:mm a", Locale.getDefault());

        try {
            Date date = inputFormat.parse(rawDate);
            return outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return rawDate;
        }
    }

    public static class PostViewHolder extends RecyclerView.ViewHolder {
        TextView title, description, authorName, postDate;
        ImageView image;
        View postDivider;

        public PostViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.postTitle);
            description = itemView.findViewById(R.id.postDescription);
            image = itemView.findViewById(R.id.postImage);
            authorName = itemView.findViewById(R.id.postAuthor);
            postDate = itemView.findViewById(R.id.postDate);
            postDivider = itemView.findViewById(R.id.postDivider);
        }
    }
}

