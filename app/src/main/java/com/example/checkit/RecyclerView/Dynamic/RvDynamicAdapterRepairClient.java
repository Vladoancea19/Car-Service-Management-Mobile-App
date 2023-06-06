package com.example.checkit.RecyclerView.Dynamic;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.checkit.Models.ClientRepairDynamicRvModel;
import com.example.checkit.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class RvDynamicAdapterRepairClient extends RecyclerView.Adapter<RvDynamicAdapterRepairClient.RvDynamicViewHolderRepairClient> {

    public ArrayList<ClientRepairDynamicRvModel> clientRepairDynamicRvModels;
    public String repairID;

    public RvDynamicAdapterRepairClient(ArrayList<ClientRepairDynamicRvModel> clientRepairDynamicRvModels, String repairID) {
        this.clientRepairDynamicRvModels = clientRepairDynamicRvModels;
        this.repairID = repairID;
    }

    @NonNull
    @Override
    public RvDynamicViewHolderRepairClient onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new RvDynamicViewHolderRepairClient(LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_dynamic_item_repair_client, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RvDynamicViewHolderRepairClient holder, int position) {
        ClientRepairDynamicRvModel items = clientRepairDynamicRvModels.get(position);

        holder.description.setText(items.getDescription());

        DatabaseReference clientReference = FirebaseDatabase.getInstance("https://checkit-cd40f-default-rtdb.europe-west1.firebasedatabase.app/").getReference("reparations").child(repairID).child("carDamageInfoList");

        clientReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int i = 0;

                for(DataSnapshot childSnapshot : snapshot.getChildren()) {
                    if(i == holder.getAdapterPosition() && childSnapshot.child("state").getValue().equals("finished")) {
                        holder.checkBox.setEnabled(false);
                        holder.checkBox.setChecked(true);
                        break;
                    }
                    i++;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return clientRepairDynamicRvModels.size();
    }

    public class RvDynamicViewHolderRepairClient extends RecyclerView.ViewHolder {
        public TextView description;
        public CheckBox checkBox;

        public RvDynamicViewHolderRepairClient(@NonNull View itemView) {
            super(itemView);

            description = itemView.findViewById(R.id.description);
            checkBox = itemView.findViewById(R.id.check_box);
        }
    }
}
