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
import java.util.Collections;
import java.util.Comparator;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.pack.uniflow.Adapters.PostAdapter;
import com.pack.uniflow.Models.Post;
import com.pack.uniflow.R;
import com.pack.uniflow.Activities.LoginActivity.LoginType;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private RecyclerView recyclerView;
    private PostAdapter adapter;
    private LoginType currentUserType;
    private String currentUniversityId;
    private boolean isAdmin;
    private List<String> userTags;

    // Firebase reference
    private final DatabaseReference postsRef = FirebaseDatabase.getInstance().getReference("posts");

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Get arguments from MainActivity
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

        // Pass tags to adapter
        adapter = new PostAdapter(getContext(), new ArrayList<>(), isAdmin ? null : userTags);
        recyclerView.setAdapter(adapter);

        loadPostsFromFirebase();
    }

    private void loadPostsFromFirebase() {
        postsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Post> allPosts = new ArrayList<>();
                for (DataSnapshot postSnap : snapshot.getChildren()) {
                    Post post = postSnap.getValue(Post.class);
                    if (post != null) {
                        post.setId(postSnap.getKey());
                        allPosts.add(post);
                    }
                }

                // Sort newest first
                Collections.sort(allPosts, new Comparator<Post>() {
                    @Override
                    public int compare(Post p1, Post p2) {
                        if (p1.getCreatedAt() == null || p2.getCreatedAt() == null) return 0;
                        return p2.getCreatedAt().compareTo(p1.getCreatedAt());
                    }
                });

                if (isAdmin) {
                    // Admins see all posts, skip tag filtering
                    adapter.updatePosts(allPosts);
                } else {
                    adapter.updatePosts(allPosts);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Optionally log the error
            }
        });
    }

    // Updated factory method to include user tags
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
