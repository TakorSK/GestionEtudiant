package com.pack.uniflow;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.pack.uniflow.Models.Post;

import java.util.ArrayList;
import java.util.List;

public class PostDao {

    private final DatabaseReference postsRef;

    public PostDao() {
        postsRef = FirebaseDatabase.getInstance().getReference("posts");
    }

    // Equivalent to @Insert
    public void insert(Post post, InsertCallback callback) {
        String postId = postsRef.push().getKey();
        post.setId(postId);
        postsRef.child(postId).setValue(post)
                .addOnSuccessListener(aVoid -> callback.onInserted(postId))
                .addOnFailureListener(callback::onError);
    }

    // Equivalent to getAllPosts()
    public void getAllPosts(LoadCallback callback) {
        Query query = postsRef.orderByChild("createdAt");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<Post> posts = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Post post = snapshot.getValue(Post.class);
                    posts.add(0, post); // Reverse order (latest first)
                }
                callback.onLoaded(posts);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                callback.onError(databaseError.toException());
            }
        });
    }

    // Equivalent to getPostsByAuthor()
    public void getPostsByAuthor(String authorId, LoadCallback callback) {
        Query query = postsRef.orderByChild("authorId_createdAt")
                .startAt(authorId)
                .endAt(authorId + "\uf8ff");

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<Post> posts = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Post post = snapshot.getValue(Post.class);
                    if (post != null && authorId.equals(post.getAuthorId())) {
                        posts.add(0, post);
                    }
                }
                callback.onLoaded(posts);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                callback.onError(databaseError.toException());
            }
        });
    }

    // 🔹 New: Get posts by a specific tag
    public void getPostsByTag(String tag, LoadCallback callback) {
        postsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<Post> posts = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Post post = snapshot.getValue(Post.class);
                    if (post != null && post.getTags() != null && post.getTags().contains(tag)) {
                        posts.add(0, post);
                    }
                }
                callback.onLoaded(posts);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                callback.onError(databaseError.toException());
            }
        });
    }

    // Equivalent to @Delete
    public void delete(Post post, DeleteCallback callback) {
        postsRef.child(post.getId()).removeValue()
                .addOnSuccessListener(aVoid -> callback.onDeleted())
                .addOnFailureListener(callback::onError);
    }

    public interface InsertCallback {
        void onInserted(String postId);
        void onError(Exception e);
    }

    public interface LoadCallback {
        void onLoaded(List<Post> posts);
        void onError(Exception e);
    }

    public interface DeleteCallback {
        void onDeleted();
        void onError(Exception e);
    }
}
