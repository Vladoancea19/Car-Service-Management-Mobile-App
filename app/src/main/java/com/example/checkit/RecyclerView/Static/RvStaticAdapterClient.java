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
import com.example.checkit.Models.MechanicDynamicRvModel;
import com.example.checkit.Models.MechanicStaticRvModel;
import com.example.checkit.R;
import com.example.checkit.RecyclerView.Interface.Update;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class RvStaticAdapterClient extends RecyclerView.Adapter<RvStaticAdapterClient.RvStaticViewHolderClient> {

    private ArrayList<MechanicStaticRvModel> items;
    private String clientPhoneNumber;
    int row_index = -1;
    Update update;
    Activity activity;
    boolean check = true, select = true;

    public RvStaticAdapterClient(ArrayList<MechanicStaticRvModel> items, String clientPhoneNumber, Activity activity, Update update) {
        this.items = items;
        this.clientPhoneNumber = clientPhoneNumber;
        this.activity = activity;
        this.update = update;
    }

    @NonNull
    @Override
    public RvStaticViewHolderClient onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_static_item, parent, false);

        return new RvStaticViewHolderClient(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RvStaticViewHolderClient holder, @SuppressLint("RecyclerView") final int position) {
        MechanicStaticRvModel currentItem = items.get(position);
        holder.imageView.setImageResource(currentItem.getImage());
        holder.textView.setText(currentItem.getText());

        if(check) {
            ArrayList<MechanicDynamicRvModel> dynamicItems = new ArrayList<>();

            DatabaseReference clientReference = FirebaseDatabase.getInstance("https://checkit-cd40f-default-rtdb.europe-west1.firebasedatabase.app/").getReference("reparations");

            clientReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot reparationSnapshot : snapshot.getChildren()) {

                        String state = reparationSnapshot.child("state").getValue(String.class);
                        String uniqueID = reparationSnapshot.child("uniqueID").getValue(String.class);

                        assert state != null;
                        if(state.equals("inprogress")) {
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

                            dynamicItems.add(new MechanicDynamicRvModel(progress, progress + "%", plateNumber, carModel, uniqueID));
                            update.callback(position, dynamicItems);
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

            check = false;
        }

        holder.linearLayout.setOnClickListener(v -> {
            row_index = position;
            notifyDataSetChanged();

            ArrayList<MechanicDynamicRvModel> dynamicItems = new ArrayList<>();

            if (position == 0) {
                DatabaseReference clientReference = FirebaseDatabase.getInstance("https://checkit-cd40f-default-rtdb.europe-west1.firebasedatabase.app/").getReference("reparations");

                clientReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        dynamicItems.clear();

                        for (DataSnapshot reparationSnapshot : snapshot.getChildren()) {

                            String state = reparationSnapshot.child("state").getValue(String.class);
                            String clientPhoneNum = reparationSnapshot.child("clientPhoneNumber").getValue(String.class);
                            String uniqueID = reparationSnapshot.child("uniqueID").getValue(String.class);

                            if(state.equals("inprogress") && clientPhoneNum.equals(clientPhoneNumber)) {
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

                                dynamicItems.add(new MechanicDynamicRvModel(progress, progress + "%", plateNumber, carModel, uniqueID));
                                update.callback(position, dynamicItems);
                            }
                        }
                        update.callback(position, dynamicItems);
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
                            String clientPhoneNum = reparationSnapshot.child("clientPhoneNumber").getValue(String.class);
                            String uniqueID = reparationSnapshot.child("uniqueID").getValue(String.class);

                            if(state.equals("done") && clientPhoneNum.equals(clientPhoneNumber)) {
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

                                dynamicItems.add(new MechanicDynamicRvModel(plateNumber, carModel, uniqueID));
                                update.callback(position, dynamicItems);
                            }
                        }
                        update.callback(position, dynamicItems);
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
                            String clientPhoneNum = reparationSnapshot.child("clientPhoneNumber").getValue(String.class);
                            String uniqueID = reparationSnapshot.child("uniqueID").getValue(String.class);

                            if(state.equals("pending") && clientPhoneNum.equals(clientPhoneNumber)) {
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

                                dynamicItems.add(new MechanicDynamicRvModel(plateNumber, carModel, uniqueID));
                                update.callback(position, dynamicItems);
                            }
                        }
                        update.callback(position, dynamicItems);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
//                    dynamicItems.add(new MechanicDynamicRvModel(50, 50+"%", "B 23 MYE", "Skoda Fabia", "dummy"));
//                    dynamicItems.add(new MechanicDynamicRvModel(50, 50+"%", "B 23 MYE", "Skoda Octavia", "dummy"));
//                    dynamicItems.add(new MechanicDynamicRvModel(50, 50+"%", "B 23 MYE", "Dacia Logan", "dummy"));
//                    dynamicItems.add(new MechanicDynamicRvModel(50, 50+"%", "B 23 MYE", "Golf IV", "dummy"));
//                    dynamicItems.add(new MechanicDynamicRvModel(50, 50+"%", "B 23 MYE", "Buggati Veiron", "dummy"));
//                    update.callback(position, dynamicItems);
            }
            else if (position == 3) {
                DatabaseReference clientReference = FirebaseDatabase.getInstance("https://checkit-cd40f-default-rtdb.europe-west1.firebasedatabase.app/").getReference("reparations");

                clientReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        dynamicItems.clear();

                        for (DataSnapshot reparationSnapshot : snapshot.getChildren()) {

                            String state = reparationSnapshot.child("state").getValue(String.class);
                            String clientPhoneNum = reparationSnapshot.child("clientPhoneNumber").getValue(String.class);
                            String uniqueID = reparationSnapshot.child("uniqueID").getValue(String.class);

                            if(state.equals("rejected") && clientPhoneNum.equals(clientPhoneNumber)) {

                                String plateNumber = reparationSnapshot.child("carInfo").child("plateNumber").getValue(String.class);
                                String carModel = reparationSnapshot.child("carInfo").child("carModel").getValue(String.class);

                                dynamicItems.add(new MechanicDynamicRvModel(plateNumber, carModel, uniqueID));
                                update.callback(position, dynamicItems);
                            }
                        }
                        update.callback(position, dynamicItems);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
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

    public static class RvStaticViewHolderClient extends RecyclerView.ViewHolder {

        TextView textView;
        ImageView imageView;
        LinearLayout linearLayout;

        public RvStaticViewHolderClient(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.category_image);
            textView = itemView.findViewById(R.id.category_title);
            linearLayout = itemView.findViewById(R.id.rv_static_item_container);
        }
    }
}
