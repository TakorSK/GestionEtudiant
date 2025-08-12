package com.pack.uniflow.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.pack.uniflow.Activities.MessageDetailActivity;
import com.pack.uniflow.Models.MockMessage;
import com.pack.uniflow.R;

import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {

    private List<MockMessage> messages;
    private Context context;

    public MessageAdapter(List<MockMessage> messages, Context context) {
        this.messages = messages;
        this.context = context;
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_message_card, parent, false);
        return new MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        MockMessage msg = messages.get(position);
        holder.title.setText(msg.getTitle());
        holder.snippet.setText(msg.getBody().length() > 60 ? msg.getBody().substring(0, 60) + "..." : msg.getBody());
        holder.sender.setText(msg.getSender());

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, MessageDetailActivity.class);
            intent.putExtra("title", msg.getTitle());
            intent.putExtra("sender", msg.getSender());
            intent.putExtra("body", msg.getBody());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    static class MessageViewHolder extends RecyclerView.ViewHolder {
        TextView title, sender, snippet;

        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);
            title   = itemView.findViewById(R.id.message_title);
            sender  = itemView.findViewById(R.id.message_sender);
            snippet = itemView.findViewById(R.id.message_snippet);
        }
    }
}
