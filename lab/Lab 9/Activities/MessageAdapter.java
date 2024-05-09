package com.android.lab9;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {

    private List<Message> messageList;

    public MessageAdapter(List<Message> messageList) {
        this.messageList = messageList;
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_item, parent, false);
        return new MessageViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        Message message = messageList.get(position);
        if (message.getSender().equals(Message.SENT_BY_USER)) {
            holder.botMessageLayout.setVisibility(View.GONE);
            holder.userMessageLayout.setVisibility(View.VISIBLE);
            holder.userMessageTextView.setText(message.getContent());
        } else {
            holder.userMessageLayout.setVisibility(View.GONE);
            holder.botMessageLayout.setVisibility(View.VISIBLE);
            holder.botMessageTextView.setText(message.getContent());
        }
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    public static class MessageViewHolder extends RecyclerView.ViewHolder {
        LinearLayout botMessageLayout, userMessageLayout;
        TextView botMessageTextView, userMessageTextView;

        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);
            botMessageLayout = itemView.findViewById(R.id.bot_message_layout);
            userMessageLayout = itemView.findViewById(R.id.user_message_layout);
            botMessageTextView = itemView.findViewById(R.id.bot_message_text);
            userMessageTextView = itemView.findViewById(R.id.user_message_text);
        }
    }
}
