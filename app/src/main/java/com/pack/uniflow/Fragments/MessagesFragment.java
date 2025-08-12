package com.pack.uniflow.Fragments;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.content.Intent;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.pack.uniflow.Models.MockMessage;
import com.pack.uniflow.Adapters.MessageAdapter;
import com.pack.uniflow.R;

import java.util.ArrayList;
import java.util.List;

public class MessagesFragment extends Fragment {

    private RecyclerView recyclerView;
    private MessageAdapter adapter;
    private List<MockMessage> mockMessages = new ArrayList<>();

    public MessagesFragment() {}

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_messages, container, false);

        recyclerView = view.findViewById(R.id.message_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        loadMockMessages();
        adapter = new MessageAdapter(mockMessages, getContext());
        recyclerView.setAdapter(adapter);

        return view;
    }

    private void loadMockMessages() {
        mockMessages.add(new MockMessage("Welcome!", "Admin Alice", "Hello student, welcome to UniFlow. Let’s get you started..."));
        mockMessages.add(new MockMessage("Exam Update", "Admin Bob", "The midterms have been postponed to next week..."));
        mockMessages.add(new MockMessage("New Club", "Admin Carol", "We are launching a new Photography Club next month..."));
    }
}
