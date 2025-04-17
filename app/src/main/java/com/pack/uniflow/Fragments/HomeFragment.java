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

        String placeholderUri = "android.resource://" + requireContext().getPackageName() + "/" + R.drawable.placeholder;

        posts.add(new Post("First Debug Post!", "This is its Description + a Placeholder Image!", placeholderUri, 1));
        posts.add(new Post("Second Debug Post", "This one has no image, just text.", null, 1));
        posts.add(new Post("Third Debug Post", "This is its Description + a Placeholder Image!", placeholderUri, 2));
        posts.add(new Post("First Debug Announcement", "This is its Description.", null, 2));
        posts.add(new Post("Second Debug Announcement", "This is its Description + a Placeholder Image!", placeholderUri, 3));

        return posts;
    }
}
