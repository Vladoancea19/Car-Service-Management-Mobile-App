package com.example.checkit.RecyclerView.Dynamic;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.checkit.Models.MechanicDynamicRvModel;
import com.example.checkit.R;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class RvDynamicAdapterClient extends RecyclerView.Adapter<RvDynamicAdapterClient.RvDynamicViewHolderClient> {

    public ArrayList<MechanicDynamicRvModel> mechanicDynamicRvModels;
    public int pos;
    public View view;

    public RvDynamicAdapterClient(ArrayList<MechanicDynamicRvModel> mechanicDynamicRvModels, int pos) {
        this.mechanicDynamicRvModels = mechanicDynamicRvModels;
        this.pos = pos;
    }

    public class RvDynamicViewHolderClient extends RecyclerView.ViewHolder {

        public ProgressBar progress;
        public TextView plateNumber, carModel, progressText;

        public RvDynamicViewHolderClient(@NonNull View itemView) {
            super(itemView);

            progress = itemView.findViewById(R.id.progress_bar);
            plateNumber = itemView.findViewById(R.id.plate_number_container);
            carModel = itemView.findViewById(R.id.car_model_container);
            progressText = itemView.findViewById(R.id.progress_text);
        }
    }

    @NonNull
    @Override
    public RvDynamicViewHolderClient onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(pos == 0) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_dynamic_item_in_progress, parent, false);
            return new RvDynamicViewHolderClient(view);
        }
        else if(pos == 2){
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_dynamic_item_pending_client, parent, false);
            return new RvDynamicViewHolderClient(view);
        }
        else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_dynamic_item_rejected, parent, false);
            return new RvDynamicViewHolderClient(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RvDynamicViewHolderClient holder, int position) {
        MechanicDynamicRvModel items = mechanicDynamicRvModels.get(position);

        if(pos == 0) {
            holder.progress.setProgress(items.getProgress());
            holder.progressText.setText(items.getProgressText());
        }
        if(pos == 2) {
            MaterialButton acceptButton = view.findViewById(R.id.accept_button);

            acceptButton.setOnClickListener(v -> {
                DatabaseReference clientReference = FirebaseDatabase.getInstance("https://checkit-cd40f-default-rtdb.europe-west1.firebasedatabase.app/").getReference("reparations");

                clientReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot reparationSnapshot : snapshot.getChildren()) {
                            String uniqueID = reparationSnapshot.child("uniqueID").getValue(String.class);
                            String uid = items.getUniqueID();

                            if(uid.equals(uniqueID)) {
                                reparationSnapshot.child("state").getRef().setValue("inprogress");
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            });
        }

        holder.carModel.setText(items.getCarModel());
        holder.plateNumber.setText(items.getPlateNumber());
    }

    @Override
    public int getItemCount() {
        return mechanicDynamicRvModels.size();
    }
}