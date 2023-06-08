package com.example.checkit.RecyclerView.Dynamic;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.checkit.Models.HomeDynamicRvModel;
import com.example.checkit.R;
import com.example.checkit.Repair.RepairActivityMechanic;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class RvDynamicAdapterMechanic extends RecyclerView.Adapter<RvDynamicAdapterMechanic.RvDynamicViewHolderMechanic> {

    public ArrayList<HomeDynamicRvModel> homeDynamicRvModels;
    public int pos;
    View view;
    public Context context;

    public RvDynamicAdapterMechanic(Context context, ArrayList<HomeDynamicRvModel> homeDynamicRvModels, int pos) {
        this.context = context;
        this.homeDynamicRvModels = homeDynamicRvModels;
        this.pos = pos;
    }

    public class RvDynamicViewHolderMechanic extends RecyclerView.ViewHolder {

        public ProgressBar progress;
        public TextView plateNumber, carModel, progressText, damageInfoList, getDamageInfoListCost, serviceNote;;
        ConstraintLayout constraintLayout;

        public RvDynamicViewHolderMechanic(@NonNull View itemView) {
            super(itemView);

            progress = itemView.findViewById(R.id.progress_bar);
            plateNumber = itemView.findViewById(R.id.plate_number_container);
            carModel = itemView.findViewById(R.id.car_model_container);
            progressText = itemView.findViewById(R.id.progress_text);
            constraintLayout = itemView.findViewById(R.id.constraint_layout);
            damageInfoList = itemView.findViewById(R.id.damage_info_list);
            getDamageInfoListCost = itemView.findViewById(R.id.damage_info_list_cost);
            serviceNote = itemView.findViewById(R.id.service_note);
        }
    }

    @NonNull
    @Override
    public RvDynamicViewHolderMechanic onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        if(pos == 0) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_dynamic_item_in_progress, parent, false);
            return new RvDynamicViewHolderMechanic(view);
        }
        else if(pos == 1) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_dynamic_item_done, parent, false);
            return new RvDynamicAdapterMechanic.RvDynamicViewHolderMechanic(view);
        }
        else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_dynamic_item_rejected, parent, false);
            return new RvDynamicViewHolderMechanic(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RvDynamicViewHolderMechanic holder, int position) {
        HomeDynamicRvModel items = homeDynamicRvModels.get(position);

        if(pos == 0) {
            holder.progress.setProgress(items.getProgress());
            holder.progressText.setText(items.getProgressText());

            holder.constraintLayout.setOnClickListener(v -> {
                Intent intent = new Intent(context, RepairActivityMechanic.class);

                intent.putExtra("repairID", homeDynamicRvModels.get(position).getUniqueID());

                context.startActivity(intent);
            });
        }
        if(pos == 1) {
            DatabaseReference repairReference = FirebaseDatabase.getInstance("https://checkit-cd40f-default-rtdb.europe-west1.firebasedatabase.app/").getReference("reparations").child(items.getUniqueID()).child("carDamageInfoList");

            repairReference.addListenerForSingleValueEvent(new ValueEventListener() {
                String description = "";
                String cost = "";
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot reparationSnapshot : snapshot.getChildren()) {
                        description += (reparationSnapshot.child("description").getValue(String.class) + "\n");
                        cost += (reparationSnapshot.child("cost").getValue(Long.class).toString() + "\n");
                    }

                    holder.damageInfoList.setText(description);
                    holder.getDamageInfoListCost.setText(cost);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

            DatabaseReference repairRef = FirebaseDatabase.getInstance("https://checkit-cd40f-default-rtdb.europe-west1.firebasedatabase.app/").getReference("reparations").child(items.getUniqueID()).child("serviceNote");

            repairRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    holder.serviceNote.setText(snapshot.getValue(String.class));
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }

        holder.carModel.setText(items.getCarModel());
        holder.plateNumber.setText(items.getPlateNumber());
    }

    @Override
    public int getItemCount() {
        return homeDynamicRvModels.size();
    }
}