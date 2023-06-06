package com.example.checkit.RecyclerView.Dynamic;

import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.checkit.Models.MechanicRepairDynamicRvModel;
import com.example.checkit.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class RvDynamicAdapterRepairMechanic extends RecyclerView.Adapter<RvDynamicAdapterRepairMechanic.RvDynamicViewHolderRepairMechanic> {

    public ArrayList<MechanicRepairDynamicRvModel> mechanicRepairDynamicRvModels;
    public String repairID;
    public String notificationDescription;
    public String tokenDevice;

    public RvDynamicAdapterRepairMechanic(ArrayList<MechanicRepairDynamicRvModel> mechanicRepairDynamicRvModels, String repairID) {
        this.mechanicRepairDynamicRvModels = mechanicRepairDynamicRvModels;
        this.repairID = repairID;
    }

    public class RvDynamicViewHolderRepairMechanic extends RecyclerView.ViewHolder {
        public TextView description, cost;
        public CheckBox checkBox;

        public RvDynamicViewHolderRepairMechanic(@NonNull View itemView) {
            super(itemView);

            description = itemView.findViewById(R.id.description);
            cost = itemView.findViewById(R.id.step_cost);
            checkBox = itemView.findViewById(R.id.check_box);
        }
    }

    @NonNull
    @Override
    public RvDynamicViewHolderRepairMechanic onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new RvDynamicAdapterRepairMechanic.RvDynamicViewHolderRepairMechanic(LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_dynamic_item_repair_mechanic, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RvDynamicAdapterRepairMechanic.RvDynamicViewHolderRepairMechanic holder, int position) {
        MechanicRepairDynamicRvModel items = mechanicRepairDynamicRvModels.get(position);

        holder.description.setText(items.getDescription());
        holder.cost.setText(items.getCost());

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

        holder.checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            holder.checkBox.setEnabled(false);
            holder.checkBox.setChecked(true);

            DatabaseReference clientReference1 = FirebaseDatabase.getInstance("https://checkit-cd40f-default-rtdb.europe-west1.firebasedatabase.app/").getReference("reparations").child(repairID).child("carDamageInfoList");

            clientReference1.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    int i = 0;

                    for(DataSnapshot childSnapshot : snapshot.getChildren()) {
                        if(i == holder.getAdapterPosition()) {
                            childSnapshot.child("state").getRef().setValue("finished");
                            break;
                        }
                        i++;
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

            DatabaseReference clientReference2 = FirebaseDatabase.getInstance("https://checkit-cd40f-default-rtdb.europe-west1.firebasedatabase.app/").getReference("reparations").child(repairID);
            clientReference2.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String clientPhoneNumber = snapshot.child("clientPhoneNumber").getValue(String.class);

                    DatabaseReference tokenReference = FirebaseDatabase.getInstance("https://checkit-cd40f-default-rtdb.europe-west1.firebasedatabase.app/").getReference("client_users").child(clientPhoneNumber);
                    tokenReference.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            tokenDevice = snapshot.child("deviceToken").getValue(String.class);

                            if(!tokenDevice.isEmpty()) {
                                notificationDescription = holder.description.getText() + " is done!";
                                new NetworkTask().execute();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        });
    }

    @Override
    public int getItemCount() {
        return mechanicRepairDynamicRvModels.size();
    }

    private class NetworkTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            try {
                try {
                    String title = "checkIt";
                    String message = notificationDescription;
                    URL url = new URL("https://fcm.googleapis.com/fcm/send");
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("POST");
                    connection.setRequestProperty("Content-Type", "application/json");
                    connection.setRequestProperty("Authorization", "key=" + "AAAAf7rl5ZA:APA91bH4vlBt0-TVvmTN7xFMBizAHKzr3yykwQdSksYca0aHeJBgFwAjvSc7_M1JyDNZ_s8MTHKDrcCGi2rUpKtEY1qo2ufVACkIMOF7aCD0t8b_X0NwCLKtQqGi0Uzirlamh6pY2Vld");
                    connection.setDoOutput(true);

                    String payload = "{\"to\": \"" + tokenDevice + "\", \"notification\": {\"title\": \"" + title + "\", \"body\": \"" + message + "\"}}";

                    OutputStream outputStream = connection.getOutputStream();
                    outputStream.write(payload.getBytes("UTF-8"));
                    outputStream.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}