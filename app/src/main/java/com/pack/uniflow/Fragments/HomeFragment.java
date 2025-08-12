package com.pack.uniflow.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.pack.uniflow.Adapters.PostAdapter;
import com.pack.uniflow.Models.Post;
import com.pack.uniflow.Models.Student;
import com.pack.uniflow.R;
import com.pack.uniflow.Activities.LoginActivity.LoginType;

import java.util.ArrayList;
import java.util.List;
import java.util.Collections;
import java.util.Comparator;


public class HomeFragment extends Fragment {

    private RecyclerView recyclerView;
    private PostAdapter adapter;
    private LoginType currentUserType;
    private String currentUniversityId;
    private boolean isAdmin;
    private List<String> userTags;

    // Firebase references
    private final DatabaseReference postsRef = FirebaseDatabase.getInstance().getReference("posts");
    private final DatabaseReference studentsRef = FirebaseDatabase.getInstance().getReference("students");

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Get user type and university ID from arguments
        Bundle args = getArguments();
        if (args != null) {
            currentUserType = LoginType.valueOf(args.getString("LOGIN_TYPE", LoginType.REGULAR_STUDENT.name()));
            currentUniversityId = args.getString("UNIVERSITY_ID");
            userTags = args.getStringArrayList("USER_TAGS");
            isAdmin = currentUserType == LoginType.DEBUG_ADMIN ||
                    currentUserType == LoginType.STUDENT_ADMIN ||
                    currentUserType == LoginType.UNIVERSITY_ADMIN;
        } else {
            userTags = new ArrayList<>();
            currentUserType = LoginType.REGULAR_STUDENT;
            isAdmin = false;
        }

        recyclerView = view.findViewById(R.id.HomeRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Initialize adapter with empty posts list and empty user tags list
        adapter = new PostAdapter(getContext(), new ArrayList<>(), Collections.emptyList());
        recyclerView.setAdapter(adapter);

        loadPostsFromFirebase();
    }

    private void loadPostsFromFirebase() {
        postsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<Post> allPosts = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Post post = snapshot.getValue(Post.class);
                    if (post != null) {
                        post.setId(snapshot.getKey());
                        allPosts.add(post);
                    }
                }

                // Sort posts by createdAt descending (newest first)
                Collections.sort(allPosts, new Comparator<Post>() {
                    @Override
                    public int compare(Post p1, Post p2) {
                        if (p1.getCreatedAt() == null || p2.getCreatedAt() == null) return 0;
                        return p2.getCreatedAt().compareTo(p1.getCreatedAt());
                    }
                });

                filterAndDisplayPosts(allPosts);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Optionally log or show error
            }
        });
    }


    private void filterAndDisplayPosts(List<Post> allPosts) {
        if (currentUserType == LoginType.DEBUG_ADMIN) {
            // Debug admin sees all posts
            adapter.updatePosts(allPosts);
            return;
        }

        List<Post> filteredPosts = new ArrayList<>();
        for (Post post : allPosts) {
            checkPostVisibility(post, filteredPosts);
        }
        adapter.updatePosts(filteredPosts);
    }

    private void checkPostVisibility(Post post, List<Post> filteredPosts) {
        if (post.getAuthorId() == null) return;

        // Check if this is an admin post (authorId starts with "admin_")
        boolean isAdminPost = post.getAuthorId().startsWith("admin_");
        boolean isFromCurrentUniversity = false;

        if (isAdminPost) {
            // For admin posts, check if it's for this university
            if (post.getAuthorId().equals("admin_" + currentUniversityId)) {
                isFromCurrentUniversity = true;
            }
        } else {
            // For student posts, check the student's university
            studentsRef.child(post.getAuthorId())
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            Student author = snapshot.getValue(Student.class);
                            if (author != null && author.getUniId() != null &&
                                    author.getUniId().equals(currentUniversityId)) {
                                filteredPosts.add(post);
                                adapter.notifyDataSetChanged();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            // Handle error
                        }
                    });
            return; // We'll add this post later in the callback
        }

        // Apply visibility rules
        if (currentUserType == LoginType.UNIVERSITY_ADMIN) {
            // University admins see their posts and admin posts
            if (isAdminPost || isFromCurrentUniversity) {
                filteredPosts.add(post);
            }
        } else {
            // Students see admin posts and their university's posts
            if (isAdminPost || isFromCurrentUniversity) {
                filteredPosts.add(post);
            }
        }
    }

    // Factory method to create fragment with arguments
    public static HomeFragment newInstance(LoginType loginType, String universityId, ArrayList<String> userTags) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString("LOGIN_TYPE", loginType.name());
        args.putString("UNIVERSITY_ID", universityId);
        args.putStringArrayList("USER_TAGS", userTags);
        fragment.setArguments(args);
        return fragment;
    }
}