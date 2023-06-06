package com.example.checkit.RecyclerView.Dynamic;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.checkit.Models.ChatModel;
import com.example.checkit.R;

import java.util.ArrayList;

public class RvDynamicAdapterChatClient extends RecyclerView.Adapter<RvDynamicAdapterChatClient.RvDynamicViewHolderChatClient> {

    private static final int MECHANIC_VIEW_TYPE = 0;
    private static final int CLIENT_VIEW_TYPE = 1;
    public ArrayList<ChatModel> chatModels;
    public String repairID;

    public RvDynamicAdapterChatClient(ArrayList<ChatModel> chatModels, String repairID) {
        this.chatModels = chatModels;
        this.repairID = repairID;
    }

    public class RvDynamicViewHolderChatClient extends RecyclerView.ViewHolder {
        TextView sentMessage, receivedMessage, sentDateTime, receivedDateTime;

        public RvDynamicViewHolderChatClient(@NonNull View itemView) {
            super(itemView);

            sentMessage = itemView.findViewById(R.id.sentMessage);
            receivedMessage = itemView.findViewById(R.id.receivedMessage);
            sentDateTime = itemView.findViewById(R.id.sentDateTime);
            receivedDateTime = itemView.findViewById(R.id.receivedDateTime);
        }
    }

    @NonNull
    @Override
    public RvDynamicViewHolderChatClient onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType == CLIENT_VIEW_TYPE) {
            return new RvDynamicAdapterChatClient.RvDynamicViewHolderChatClient(LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_dynamic_item_sent_message, parent, false));
        }
        else {
            return new RvDynamicAdapterChatClient.RvDynamicViewHolderChatClient(LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_dynamic_item_received_message, parent, false));
        }
    }

    @Override
    public int getItemViewType(int position) {
        ChatModel chatModel = chatModels.get(position);
        String sender = chatModel.getSender();

        if(sender.equals("mechanic")) {
            return MECHANIC_VIEW_TYPE;
        }
        else {
            return CLIENT_VIEW_TYPE;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RvDynamicAdapterChatClient.RvDynamicViewHolderChatClient holder, int position) {
        ChatModel items = chatModels.get(position);
        String sender = items.getSender();

        if(sender.equals("client")) {
            holder.sentMessage.setText(items.getMessage());
            holder.sentDateTime.setText(items.getTimestamp());
        }
        else if(sender.equals("mechanic")) {
            holder.receivedMessage.setText(items.getMessage());
            holder.receivedDateTime.setText(items.getTimestamp());
        }
    }

    @Override
    public int getItemCount() {
        return chatModels.size();
    }
}
