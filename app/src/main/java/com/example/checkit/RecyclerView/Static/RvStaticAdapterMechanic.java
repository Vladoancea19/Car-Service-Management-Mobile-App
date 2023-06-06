package com.example.checkit.RecyclerView.Static;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.checkit.Models.CarDamageInfoModel;
import com.example.checkit.Models.HomeDynamicRvModel;
import com.example.checkit.Models.MechanicStaticRvModel;
import com.example.checkit.R;
import com.example.checkit.RecyclerView.Interface.RvUpdate;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class RvStaticAdapterMechanic extends RecyclerView.Adapter<RvStaticAdapterMechanic.RvStaticViewHolderMechanic> {

    private ArrayList<MechanicStaticRvModel> items;
    private String mechanicPhoneNumber;
    int row_index = -1;
    RvUpdate rvUpdate;
    Activity activity;
    boolean check = true, select = true;

    public RvStaticAdapterMechanic(ArrayList<MechanicStaticRvModel> items, String mechanicPhoneNumber, Activity activity, RvUpdate rvUpdate) {
        this.items = items;
        this.mechanicPhoneNumber = mechanicPhoneNumber;
        this.activity = activity;
        this.rvUpdate = rvUpdate;
    }

    @NonNull
    @Override
    public RvStaticViewHolderMechanic onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_static_item, parent, false);
        RvStaticViewHolderMechanic rvStaticViewHolderMechanic = new RvStaticViewHolderMechanic(view);

        return rvStaticViewHolderMechanic;
    }

    @Override
    public void onBindViewHolder(@NonNull RvStaticViewHolderMechanic holder, @SuppressLint("RecyclerView") final int position) {
        MechanicStaticRvModel currentItem = items.get(position);
        holder.imageView.setImageResource(currentItem.getImage());
        holder.textView.setText(currentItem.getText());

        if(check) {
            ArrayList<HomeDynamicRvModel> dynamicItems = new ArrayList<>();

            DatabaseReference clientReference = FirebaseDatabase.getInstance("https://checkit-cd40f-default-rtdb.europe-west1.firebasedatabase.app/").getReference("reparations");

            clientReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot reparationSnapshot : snapshot.getChildren()) {

                        String state = reparationSnapshot.child("state").getValue(String.class);
                        String uniqueID = reparationSnapshot.child("uniqueID").getValue(String.class);

                        if(state.equals("inprogress")) {
                            ArrayList<CarDamageInfoModel> carDamageInfoList = new ArrayList<>();

                            for (DataSnapshot carDamageInfoSnapshot : reparationSnapshot.child("carDamageInfoList").getChildren()) {
                                String description, damageState;
                                int cost;

                                description = carDamageInfoSnapshot.child("description").getValue(String.class);
                                damageState = carDamageInfoSnapshot.child("state").getValue(String.class);
                                cost = carDamageInfoSnapshot.child("cost").getValue(Integer.class);

                                CarDamageInfoModel carDamageInfo = new CarDamageInfoModel(description, cost, damageState);
                                carDamageInfoList.add(carDamageInfo);
                            }

                            String plateNumber = reparationSnapshot.child("carInfo").child("plateNumber").getValue(String.class);
                            String carModel = reparationSnapshot.child("carInfo").child("carModel").getValue(String.class);

                            float noFinished = 0, procent;
                            int progress;

                            for (CarDamageInfoModel carDamageInfo : carDamageInfoList) {
                                if (carDamageInfo.getState().equals("finished"))
                                    noFinished++;
                            }
                            procent = noFinished / carDamageInfoList.size();
                            progress = (int) (procent * 100);

                            dynamicItems.add(new HomeDynamicRvModel(progress, progress + "%", plateNumber, carModel, uniqueID));
                            rvUpdate.callback(position, dynamicItems);
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

            check = false;
        }

        holder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                row_index = position;
                notifyDataSetChanged();

                ArrayList<HomeDynamicRvModel> dynamicItems = new ArrayList<>();

                if (position == 0) {
                    DatabaseReference clientReference = FirebaseDatabase.getInstance("https://checkit-cd40f-default-rtdb.europe-west1.firebasedatabase.app/").getReference("reparations");

                    clientReference.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            dynamicItems.clear();

                            for (DataSnapshot reparationSnapshot : snapshot.getChildren()) {

                                String state = reparationSnapshot.child("state").getValue(String.class);
                                String mechanicPhoneNum = reparationSnapshot.child("mechanicPhoneNumber").getValue(String.class);
                                String uniqueID = reparationSnapshot.child("uniqueID").getValue(String.class);

                                if(state.equals("inprogress") && mechanicPhoneNum.equals(mechanicPhoneNumber)) {
                                    ArrayList<CarDamageInfoModel> carDamageInfoList = new ArrayList<>();

                                    for (DataSnapshot carDamageInfoSnapshot : reparationSnapshot.child("carDamageInfoList").getChildren()) {
                                        String description, carDamageInfoState;
                                        int cost;

                                        description = carDamageInfoSnapshot.child("description").getValue(String.class);
                                        carDamageInfoState = carDamageInfoSnapshot.child("state").getValue(String.class);
                                        cost = carDamageInfoSnapshot.child("cost").getValue(Integer.class);

                                        CarDamageInfoModel carDamageInfo = new CarDamageInfoModel(description, cost, carDamageInfoState);
                                        carDamageInfoList.add(carDamageInfo);
                                    }

                                    String plateNumber = reparationSnapshot.child("carInfo").child("plateNumber").getValue(String.class);
                                    String carModel = reparationSnapshot.child("carInfo").child("carModel").getValue(String.class);

                                    float noFinished = 0, procent;
                                    int progress;

                                    for (CarDamageInfoModel carDamageInfo : carDamageInfoList) {
                                        if (carDamageInfo.getState().equals("finished"))
                                            noFinished++;
                                    }
                                    procent = noFinished / carDamageInfoList.size();
                                    progress = (int) (procent * 100);

                                    dynamicItems.add(new HomeDynamicRvModel(progress, progress + "%", plateNumber, carModel, uniqueID));
                                    rvUpdate.callback(position, dynamicItems);
                                }
                            }
                            rvUpdate.callback(position, dynamicItems);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
                else if (position == 1) {
                    DatabaseReference clientReference = FirebaseDatabase.getInstance("https://checkit-cd40f-default-rtdb.europe-west1.firebasedatabase.app/").getReference("reparations");

                    clientReference.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            dynamicItems.clear();

                            for (DataSnapshot reparationSnapshot : snapshot.getChildren()) {

                                String state = reparationSnapshot.child("state").getValue(String.class);
                                String mechanicPhoneNum = reparationSnapshot.child("mechanicPhoneNumber").getValue(String.class);
                                String uniqueID = reparationSnapshot.child("uniqueID").getValue(String.class);

                                if(state.equals("done") && mechanicPhoneNum.equals(mechanicPhoneNumber)) {
                                    ArrayList<CarDamageInfoModel> carDamageInfoList = new ArrayList<>();

                                    for (DataSnapshot carDamageInfoSnapshot : reparationSnapshot.child("carDamageInfoList").getChildren()) {
                                        String description, carDamageInfoState;
                                        int cost;

                                        description = carDamageInfoSnapshot.child("description").getValue(String.class);
                                        carDamageInfoState = carDamageInfoSnapshot.child("state").getValue(String.class);
                                        cost = carDamageInfoSnapshot.child("cost").getValue(Integer.class);

                                        CarDamageInfoModel carDamageInfo = new CarDamageInfoModel(description, cost, carDamageInfoState);
                                        carDamageInfoList.add(carDamageInfo);
                                    }

                                    String plateNumber = reparationSnapshot.child("carInfo").child("plateNumber").getValue(String.class);
                                    String carModel = reparationSnapshot.child("carInfo").child("carModel").getValue(String.class);

                                    dynamicItems.add(new HomeDynamicRvModel(plateNumber, carModel, uniqueID));
                                    rvUpdate.callback(position, dynamicItems);
                                }
                            }
                            rvUpdate.callback(position, dynamicItems);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
                else if (position == 2) {
                    DatabaseReference clientReference = FirebaseDatabase.getInstance("https://checkit-cd40f-default-rtdb.europe-west1.firebasedatabase.app/").getReference("reparations");

                    clientReference.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            dynamicItems.clear();

                            for (DataSnapshot reparationSnapshot : snapshot.getChildren()) {

                                String state = reparationSnapshot.child("state").getValue(String.class);
                                String mechanicPhoneNum = reparationSnapshot.child("mechanicPhoneNumber").getValue(String.class);
                                String uniqueID = reparationSnapshot.child("uniqueID").getValue(String.class);

                                if(state.equals("pending") && mechanicPhoneNum.equals(mechanicPhoneNumber)) {
                                    ArrayList<CarDamageInfoModel> carDamageInfoList = new ArrayList<>();

                                    for (DataSnapshot carDamageInfoSnapshot : reparationSnapshot.child("carDamageInfoList").getChildren()) {
                                        String description, carDamageInfoState;
                                        int cost;

                                        description = carDamageInfoSnapshot.child("description").getValue(String.class);
                                        carDamageInfoState = carDamageInfoSnapshot.child("state").getValue(String.class);
                                        cost = carDamageInfoSnapshot.child("cost").getValue(Integer.class);

                                        CarDamageInfoModel carDamageInfo = new CarDamageInfoModel(description, cost, carDamageInfoState);
                                        carDamageInfoList.add(carDamageInfo);
                                    }

                                    String plateNumber = reparationSnapshot.child("carInfo").child("plateNumber").getValue(String.class);
                                    String carModel = reparationSnapshot.child("carInfo").child("carModel").getValue(String.class);

                                    dynamicItems.add(new HomeDynamicRvModel(plateNumber, carModel, uniqueID));
                                    rvUpdate.callback(position, dynamicItems);
                                }
                            }
                            rvUpdate.callback(position, dynamicItems);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
                else if (position == 3) {
                    DatabaseReference clientReference = FirebaseDatabase.getInstance("https://checkit-cd40f-default-rtdb.europe-west1.firebasedatabase.app/").getReference("reparations");

                    clientReference.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            dynamicItems.clear();

                            for (DataSnapshot reparationSnapshot : snapshot.getChildren()) {

                                String state = reparationSnapshot.child("state").getValue(String.class);
                                String mechanicPhoneNum = reparationSnapshot.child("mechanicPhoneNumber").getValue(String.class);
                                String uniqueID = reparationSnapshot.child("uniqueID").getValue(String.class);

                                if(state.equals("rejected") && mechanicPhoneNum.equals(mechanicPhoneNumber)) {

                                    String plateNumber = reparationSnapshot.child("carInfo").child("plateNumber").getValue(String.class);
                                    String carModel = reparationSnapshot.child("carInfo").child("carModel").getValue(String.class);

                                    dynamicItems.add(new HomeDynamicRvModel(plateNumber, carModel, uniqueID));
                                    rvUpdate.callback(position, dynamicItems);
                                }
                            }
                            rvUpdate.callback(position, dynamicItems);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            }
        });

        if(select) {
            if(position == 0)
                holder.linearLayout.setBackgroundResource(R.drawable.rv_static_item_selected_bkg);

            select = false;
        }
        else {
            if(row_index == position)
                holder.linearLayout.setBackgroundResource(R.drawable.rv_static_item_selected_bkg);
            else
                holder.linearLayout.setBackgroundResource(R.drawable.rv_static_item_bkg);
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class RvStaticViewHolderMechanic extends RecyclerView.ViewHolder {

        TextView textView;
        ImageView imageView;
        LinearLayout linearLayout;

        public RvStaticViewHolderMechanic(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.category_image);
            textView = itemView.findViewById(R.id.category_title);
            linearLayout = itemView.findViewById(R.id.rv_static_item_container);
        }
    }
}
