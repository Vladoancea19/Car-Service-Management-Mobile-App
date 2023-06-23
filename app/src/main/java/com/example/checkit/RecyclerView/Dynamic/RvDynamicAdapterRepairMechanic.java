package com.example.checkit.RecyclerView.Dynamic;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.checkit.Models.MechanicRepairDynamicRvModel;
import com.example.checkit.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class RvDynamicAdapterRepairMechanic extends RecyclerView.Adapter<RvDynamicAdapterRepairMechanic.RvDynamicViewHolderRepairMechanic> {

    public ArrayList<MechanicRepairDynamicRvModel> mechanicRepairDynamicRvModels;
    public String repairID;
    public String notificationDescription;
    public String tokenDevice;
    public Context context;
    public String clientPhoneNumber;
    private String plateNumber;

    private static final String FCM_API = "https://fcm.googleapis.com/fcm/send";
    private static final String SERVER_KEY = "AAAAf7rl5ZA:APA91bGGCzYnilYE-Owxfrbv-EYPzHzqKCbw0RZsxN3jGJRC3kiAXJ4p3GOpvkjpNgDlrPjJaLWZgiDlHqu0-VOPo85jztOGgEnF5UAYoMMIQi9q6_mNVPBXZKpOQYOWlC3gxbzNaatD";
    private static final String CONTENT_TYPE = "application/json";
    private RequestQueue requestQueue;

    public RvDynamicAdapterRepairMechanic(ArrayList<MechanicRepairDynamicRvModel> mechanicRepairDynamicRvModels, String repairID, Context context, String plateNumber) {
        this.mechanicRepairDynamicRvModels = mechanicRepairDynamicRvModels;
        this.repairID = repairID;
        this.context = context;
        this.plateNumber = plateNumber;
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

        holder.checkBox.setOnClickListener(v -> {
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
            clientReference2.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    clientPhoneNumber = snapshot.child("clientPhoneNumber").getValue(String.class);

                    DatabaseReference tokenReference = FirebaseDatabase.getInstance("https://checkit-cd40f-default-rtdb.europe-west1.firebasedatabase.app/").getReference("client_users").child(clientPhoneNumber);
                    tokenReference.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            tokenDevice = snapshot.child("deviceToken").getValue(String.class);

                            assert tokenDevice != null;
                            if(!tokenDevice.equals("")) {
                                notificationDescription = holder.description.getText() + " is done!";

                                JSONObject notification = new JSONObject();
                                JSONObject notificationBody = new JSONObject();

                                try {
                                    notificationBody.put("title", plateNumber);
                                    notificationBody.put("message", notificationDescription);
                                    notification.put("to", tokenDevice);
                                    notification.put("data", notificationBody);
                                    Log.e("TAG", "try");
                                } catch (JSONException e) {
                                    Log.e("TAG", "onCreate: " + e.getMessage());
                                }

                                sendNotification(notification);
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

    private RequestQueue getRequestQueue() {
        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(context);
        }
        return requestQueue;
    }

    private void sendNotification(JSONObject notification) {
        Log.e("TAG", "sendNotification");
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, FCM_API, notification,
                response -> Log.i("TAG", "onResponse: " + response.toString()),
                error -> Log.i("TAG", "onErrorResponse: Didn't work")) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> params = new HashMap<>();
                params.put("Authorization", "key=" + SERVER_KEY);
                params.put("Content-Type", CONTENT_TYPE);
                return params;
            }
        };
        getRequestQueue().add(jsonObjectRequest);
    }
}