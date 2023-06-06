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
        public TextView plateNumber, carModel, progressText;
        ConstraintLayout constraintLayout;

        public RvDynamicViewHolderMechanic(@NonNull View itemView) {
            super(itemView);

            progress = itemView.findViewById(R.id.progress_bar);
            plateNumber = itemView.findViewById(R.id.plate_number_container);
            carModel = itemView.findViewById(R.id.car_model_container);
            progressText = itemView.findViewById(R.id.progress_text);
            constraintLayout = itemView.findViewById(R.id.constraint_layout);
        }
    }

    @NonNull
    @Override
    public RvDynamicViewHolderMechanic onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        if(pos == 0) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_dynamic_item_in_progress, parent, false);
            return new RvDynamicViewHolderMechanic(view);
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

        holder.carModel.setText(items.getCarModel());
        holder.plateNumber.setText(items.getPlateNumber());
    }

    @Override
    public int getItemCount() {
        return homeDynamicRvModels.size();
    }
}