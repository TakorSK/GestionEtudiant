package com.pack.uniflow.Fragments;

import android.os.Bundle;
import android.util.Log;
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
import com.pack.uniflow.Models.TagConstants;
import com.pack.uniflow.R;
import com.pack.uniflow.Activities.LoginActivity.LoginType;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class HomeFragment extends Fragment {

    private RecyclerView recyclerView;
    private PostAdapter adapter;
    private LoginType currentUserType;
    private String currentUniversityId;
    private boolean isAdmin;
    private List<String> userTags;

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

        Bundle args = getArguments();
        if (args != null) {
            currentUserType = LoginType.valueOf(args.getString("LOGIN_TYPE", LoginType.REGULAR_STUDENT.name()));
            currentUniversityId = args.getString("UNIVERSITY_ID");
            userTags = args.getStringArrayList("USER_TAGS");
            if (userTags == null) userTags = new ArrayList<>();
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

        adapter = new PostAdapter(getContext(), new ArrayList<>(), userTags);
        recyclerView.setAdapter(adapter);

        loadPostsFromFirebase();
    }

    private void loadPostsFromFirebase() {
        postsRef.addListenerForSingleValueEvent(new ValueEventListener() {
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

                // Load students once to get authors info
                studentsRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot studentSnapshot) {
                        Map<String, Student> studentsMap = new HashMap<>();
                        for (DataSnapshot snap : studentSnapshot.getChildren()) {
                            Student student = snap.getValue(Student.class);
                            if (student != null) {
                                studentsMap.put(snap.getKey(), student);
                            }
                        }

                        List<Post> filteredPosts = filterPostsForUser(allPosts, studentsMap);

                        adapter.updatePosts(filteredPosts);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e("HomeFragment", "Failed to load students: " + error.getMessage());
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("HomeFragment", "Failed to load posts: " + databaseError.getMessage());
            }
        });
    }

    private boolean isVisibleToUser(Post post, Map<String, Student> studentsMap) {
        List<String> postTags = post.getTags();
        if (postTags == null || postTags.isEmpty()) {
            return false; // Posts with no tags are hidden from everyone
        }

        switch (currentUserType) {
            case DEBUG_ADMIN:
                return postTags.contains(TagConstants.GlobalAnnouncement) ||
                        postTags.contains(TagConstants.DEBUG_ADMINS);

            case UNIVERSITY_ADMIN:
            case STUDENT_ADMIN:
                return postTags.contains(TagConstants.THIS_UNI) ||
                        postTags.contains(TagConstants.GlobalAnnouncement);

            case REGULAR_STUDENT:
                return postTags.contains(TagConstants.THIS_UNI) ||
                        postTags.contains(TagConstants.THIS_CLUB) ||
                        postTags.contains(TagConstants.GlobalAnnouncement);

            default:
                return false;
        }
    }



    private List<Post> filterPostsForUser(List<Post> allPosts, Map<String, Student> studentsMap) {
        List<Post> filtered = new ArrayList<>();
        for (Post post : allPosts) {
            if (isVisibleToUser(post, studentsMap)) {
                filtered.add(post);
            }
        }
        return filtered;
    }


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
