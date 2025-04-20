package com.pack.uniflow.Adapters;

import static org.junit.Assert.*;
import static androidx.test.espresso.matcher.ViewMatchers.*; // For visibility checks

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.pack.uniflow.Models.Post; // Import your Post model
import com.pack.uniflow.R;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Instrumented tests for PostAdapter.
 */
@RunWith(AndroidJUnit4.class)
public class PostAdapterTest {

    private Context context;
    private List<Post> testPostList;
    private PostAdapter adapter;

    // Helper to create Post objects (adjust based on your Post constructor/fields)
    private Post createTestPost(int id, String title, String desc, String imageUriStr, int authorId) {
        Post post = new Post(title, desc, imageUriStr, authorId); // Use your constructor
        post.setId(id); // Assuming setId exists
        // post.setCreatedAt(...); // Set if needed and not handled by constructor
        return post;
    }

    @Before
    public void setUp() {
        context = ApplicationProvider.getApplicationContext();

        // Create sample data
        Post post1 = createTestPost(1, "First Post", "Desc 1", null, 101); // No image
        Post post2 = createTestPost(2, "Second Post", "Desc 2", "android.resource://" + context.getPackageName() + "/" + R.drawable.placeholder, 102); // With image URI

        testPostList = new ArrayList<>(Arrays.asList(post1, post2));
        adapter = new PostAdapter(context, testPostList);
    }

    @Test
    public void getItemCount_returnsInitialSize() {
        // Explanation: Checks initial item count.
        assertEquals(testPostList.size(), adapter.getItemCount());
    }

    @Test
    public void onCreateViewHolder_inflatesCorrectLayout() {
        // Explanation: Checks layout inflation and ViewHolder creation.
        FrameLayout parent = new FrameLayout(context);
        PostAdapter.PostViewHolder viewHolder = adapter.onCreateViewHolder(parent, 0);
        assertNotNull(viewHolder);
        assertNotNull(viewHolder.itemView);
    }

    @Test
    public void onBindViewHolder_setsTextDataCorrectly() {
        // Explanation: Checks if title and description are bound correctly.
        FrameLayout parent = new FrameLayout(context);
        PostAdapter.PostViewHolder holder = adapter.onCreateViewHolder(parent, 0);
        int position = 0; // Test first post
        Post expectedPost = testPostList.get(position);

        adapter.onBindViewHolder(holder, position);

        assertEquals(expectedPost.getTitle(), holder.title.getText().toString());
        assertEquals(expectedPost.getDescription(), holder.description.getText().toString());
    }

    @Test
    public void onBindViewHolder_withoutImage_hidesImageView() {
        // Explanation: Checks if the ImageView is hidden when the Post has no image URI.
        FrameLayout parent = new FrameLayout(context);
        PostAdapter.PostViewHolder holder = adapter.onCreateViewHolder(parent, 0);
        int position = 0; // Post 1 has null imageUri

        adapter.onBindViewHolder(holder, position);

        // Check visibility directly on the view
        assertEquals(View.GONE, holder.image.getVisibility());
    }

    @Test
    public void onBindViewHolder_withImage_showsImageView() {
        // Explanation: Checks if the ImageView is visible when the Post has an image URI.
        // Note: Doesn't verify Glide actually loaded the image content.
        FrameLayout parent = new FrameLayout(context);
        PostAdapter.PostViewHolder holder = adapter.onCreateViewHolder(parent, 0);
        int position = 1; // Post 2 has an imageUri

        adapter.onBindViewHolder(holder, position);

        // Check visibility directly on the view
        assertEquals(View.VISIBLE, holder.image.getVisibility());
    }

    @Test
    public void updatePosts_updatesItemCount() {
        // Explanation: Verifies that calling updatePosts changes the adapter's item count.
        // Arrange: Create a new list
        Post post3 = createTestPost(3, "Third Post", "Desc 3", null, 103);
        List<Post> newList = new ArrayList<>(Arrays.asList(post3));
        int initialCount = adapter.getItemCount();

        // Act: Update the adapter's list
        adapter.updatePosts(newList);
        int newCount = adapter.getItemCount();

        // Assert
        assertNotEquals(initialCount, newCount);
        assertEquals(newList.size(), newCount);
    }
}