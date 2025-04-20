package com.pack.uniflow;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.pack.uniflow.Models.Post;

import java.util.List;
@Dao
public interface PostDao {
    @Insert
    long insert(Post post);

    @Query("SELECT * FROM posts ORDER BY created_at DESC")
    List<Post> getAllPosts();

    @Query("SELECT * FROM posts WHERE author_id = :authorId ORDER BY created_at DESC")
    List<Post> getPostsByAuthor(int authorId);

    @Delete
    void delete(Post post);
}