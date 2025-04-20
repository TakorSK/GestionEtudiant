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

import com.pack.uniflow.Adapters.PostAdapter;
import com.pack.uniflow.DatabaseClient;
import com.pack.uniflow.Models.Post;
import com.pack.uniflow.R;
import com.pack.uniflow.Student;
import com.pack.uniflow.UniflowDB;
import com.pack.uniflow.Activities.LoginActivity.LoginType;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

public class HomeFragment extends Fragment {

    private RecyclerView recyclerView;
    private PostAdapter adapter;
    private UniflowDB db;
    private LoginType currentUserType;
    private int currentUniversityId = -1;
    private boolean isAdmin = false;

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
            currentUniversityId = args.getInt("UNIVERSITY_ID", -1);
            isAdmin = currentUserType == LoginType.DEBUG_ADMIN ||
                    currentUserType == LoginType.STUDENT_ADMIN;
        }

        recyclerView = view.findViewById(R.id.HomeRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new PostAdapter(getContext(), new ArrayList<>());
        recyclerView.setAdapter(adapter);

        db = DatabaseClient.getInstance(requireContext()).getDatabase();
        loadPostsFromDatabase();
    }

    private void loadPostsFromDatabase() {
        Executors.newSingleThreadExecutor().execute(() -> {
            List<Post> allPosts = db.postDao().getAllPosts();
            List<Post> filteredPosts = filterPostsBasedOnUserType(allPosts);

            requireActivity().runOnUiThread(() -> adapter.updatePosts(filteredPosts));
        });
    }

    private List<Post> filterPostsBasedOnUserType(List<Post> allPosts) {
        List<Post> filteredPosts = new ArrayList<>();

        for (Post post : allPosts) {
            // Debug admin can see all posts
            if (currentUserType == LoginType.DEBUG_ADMIN) {
                filteredPosts.add(post);
                continue;
            }

            boolean isAdminPost = post.getAuthorId() == -1 || post.getAuthorId() == -2;
            boolean isFromCurrentUniversity = false;

            // For university admin posts (authorId = -universityId)
            if (post.getAuthorId() < 0) {
                isFromCurrentUniversity = (-post.getAuthorId()) == currentUniversityId;
            }
            // For student posts (authorId > 0)
            else {
                // Need to check the student's university
                Student postAuthor = db.studentDao().getStudentById(post.getAuthorId());
                if (postAuthor != null) {
                    isFromCurrentUniversity = postAuthor.uniId == currentUniversityId;
                }
            }

            // Apply visibility rules
            if (currentUserType == LoginType.UNIVERSITY_ADMIN) {
                // University admins see their posts and admin posts
                if (isAdminPost || isFromCurrentUniversity) {
                    filteredPosts.add(post);
                }
            }
            else {
                // Students see admin posts and their university's posts
                if (isAdminPost || isFromCurrentUniversity) {
                    filteredPosts.add(post);
                }
            }
        }
        return filteredPosts;
    }

    // Factory method to create fragment with arguments
    public static HomeFragment newInstance(LoginType loginType, int universityId) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString("LOGIN_TYPE", loginType.name());
        args.putInt("UNIVERSITY_ID", universityId);
        fragment.setArguments(args);
        return fragment;
    }
}