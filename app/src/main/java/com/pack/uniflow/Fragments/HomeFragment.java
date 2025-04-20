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
import com.pack.uniflow.UniflowDB;

import java.util.List;
import java.util.concurrent.Executors;

public class HomeFragment extends Fragment {

    private RecyclerView recyclerView;
    private PostAdapter adapter;
    private UniflowDB db;

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
        adapter = new PostAdapter(getContext(), List.of()); // initialize with empty list
        recyclerView.setAdapter(adapter);

        db = DatabaseClient.getInstance(requireContext()).getDatabase();

        loadPostsFromDatabase();
    }

    private void loadPostsFromDatabase() {
        Executors.newSingleThreadExecutor().execute(() -> {
            List<Post> posts = db.postDao().getAllPosts(); // must be a suspend function or regular @Query
            requireActivity().runOnUiThread(() -> adapter.updatePosts(posts));
        });
    }
}
