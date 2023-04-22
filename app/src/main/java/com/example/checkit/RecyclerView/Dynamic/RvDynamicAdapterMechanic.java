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

import java.util.ArrayList;

public class RvDynamicAdapterMechanic extends RecyclerView.Adapter<RvDynamicAdapterMechanic.RvDynamicViewHolderMechanic> {

    public ArrayList<MechanicDynamicRvModel> mechanicDynamicRvModels;
    public int pos;
    View view;

    public RvDynamicAdapterMechanic(ArrayList<MechanicDynamicRvModel> mechanicDynamicRvModels, int pos) {
        this.mechanicDynamicRvModels = mechanicDynamicRvModels;
        this.pos = pos;
    }

    public class RvDynamicViewHolderMechanic extends RecyclerView.ViewHolder {

        public ProgressBar progress;
        public TextView plateNumber, carModel, progressText;

        public RvDynamicViewHolderMechanic(@NonNull View itemView) {
            super(itemView);

            progress = itemView.findViewById(R.id.progress_bar);
            plateNumber = itemView.findViewById(R.id.plate_number_container);
            carModel = itemView.findViewById(R.id.car_model_container);
            progressText = itemView.findViewById(R.id.progress_text);
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
        MechanicDynamicRvModel items = mechanicDynamicRvModels.get(position);

        if(pos == 0) {
            holder.progress.setProgress(items.getProgress());
            holder.progressText.setText(items.getProgressText());
        }

        holder.carModel.setText(items.getCarModel());
        holder.plateNumber.setText(items.getPlateNumber());
    }

    @Override
    public int getItemCount() {
        return mechanicDynamicRvModels.size();
    }
}