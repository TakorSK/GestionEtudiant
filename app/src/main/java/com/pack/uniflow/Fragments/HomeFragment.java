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
import com.pack.uniflow.Models.Post;
import com.pack.uniflow.R;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private RecyclerView recyclerView;
    private PostAdapter adapter;
    private List<Post> postList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.HomeRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        postList = generateMockPosts();
        adapter = new PostAdapter(getContext(), postList);
        recyclerView.setAdapter(adapter);
    }

    private List<Post> generateMockPosts() {
        List<Post> posts = new ArrayList<>();

        posts.add(new Post("First Post", "Here's something cool I found today!", R.drawable.placeholder));
        posts.add(new Post("Second Post", "This one has no image, just text.", 0));
        posts.add(new Post("Event Update", "Join us for the club fair tomorrow! ", R.drawable.placeholder));
        posts.add(new Post("Announcement", "No classes this Friday due to maintenance.", 0));
        posts.add(new Post("Photoshoot Day", "Captured some memories ", R.drawable.placeholder));

        return posts;
    }
}
