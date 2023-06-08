package com.example.checkit.Repair;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import com.example.checkit.Chat.ChatActivityClient;
import com.example.checkit.Dashboard.Home.HomeFragmentClient;
import com.example.checkit.Models.ClientRepairDynamicRvModel;
import com.example.checkit.R;
import com.example.checkit.RecyclerView.Dynamic.RvDynamicAdapterRepairClient;
import com.example.checkit.RecyclerView.Interface.RvUpdateRepairClient;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class RepairActivityClient extends AppCompatActivity implements RvUpdateRepairClient {

    private GestureDetector gestureDetector;
    private static final int SWIPE_THRESHOLD = 200;
    private static final int SWIPE_VELOCITY_THRESHOLD = 200;
    private String repairID;
    private RecyclerView dynamicRecyclerView;
    private RvDynamicAdapterRepairClient rvDynamicAdapterRepairClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.repair_activity_client);

        // Elements to variables
        TextView estimatedTime = findViewById(R.id.estimated_completion_time);
        TextView costText = findViewById(R.id.cost);
        TextView plateNumber = findViewById(R.id.plate_number);
        TextView carModel = findViewById(R.id.car_model);
        TextView manufactureYear = findViewById(R.id.manufacture_year);
        TextView transmissionType = findViewById(R.id.transmission_type);
        TextView fuelType = findViewById(R.id.fuel_type);
        MaterialButton pickedUpCarButton = findViewById(R.id.picked_up_car);

        Intent intent = getIntent();
        repairID = intent.getStringExtra("repairID");

        FirebaseDatabase database = FirebaseDatabase.getInstance("https://checkit-cd40f-default-rtdb.europe-west1.firebasedatabase.app/");

        DatabaseReference repRef = database.getReference("reparations").child(repairID).child("mechanicOk");

        repRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists())
                    if(snapshot.getValue().equals("ok"))
                        pickedUpCarButton.setEnabled(true);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        pickedUpCarButton.setOnClickListener(v -> {
            DatabaseReference repRef1 = database.getReference("reparations").child(repairID).child("state");
            repRef1.setValue("done");

            Intent intent1 = new Intent(RepairActivityClient.this, HomeFragmentClient.class);
            startActivity(intent1);
        });

        DatabaseReference carInfoReference = database.getReference("reparations").child(repairID).child("carInfo");

        carInfoReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                plateNumber.setText(snapshot.child("plateNumber").getValue(String.class));
                carModel.setText(snapshot.child("carModel").getValue(String.class));
                manufactureYear.setText(snapshot.child("manufactureYear").getValue(String.class));
                transmissionType.setText(snapshot.child("transmissionType").getValue(String.class));
                fuelType.setText(snapshot.child("fuelType").getValue(String.class));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        DatabaseReference estimatedTimeReference = database.getReference("reparations").child(repairID).child("estimatedTime");

        estimatedTimeReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                estimatedTime.setText(snapshot.getValue(String.class));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        DatabaseReference costReference = database.getReference("reparations").child(repairID).child("carDamageInfoList");
        RvUpdateRepairClient rvUpdate = this;

        costReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int cost = 0;

                for(DataSnapshot childSnapshot : snapshot.getChildren()) {
                    Long costValue = childSnapshot.child("cost").getValue(Long.class);
                    cost += costValue;
                }

                costText.setText(String.valueOf(cost));

                ArrayList<ClientRepairDynamicRvModel> dynamicItems = new ArrayList<>();

                for(DataSnapshot childSnapshot : snapshot.getChildren()) {
                    String description = childSnapshot.child("description").getValue(String.class);

                    dynamicItems.add(new ClientRepairDynamicRvModel(description));
                    rvUpdate.callbackRepairClient(dynamicItems);
                }
                rvUpdate.callbackRepairClient(dynamicItems);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        ArrayList<ClientRepairDynamicRvModel> dynamicItems2 = new ArrayList<>();
        dynamicRecyclerView = findViewById(R.id.dynamic_rv_repair);
        rvDynamicAdapterRepairClient = new RvDynamicAdapterRepairClient(dynamicItems2, repairID);
        dynamicRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        dynamicRecyclerView.setAdapter(rvDynamicAdapterRepairClient);

        gestureDetector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onFling(MotionEvent event1, MotionEvent event2, float velocityX, float velocityY) {
                float deltaX = event2.getX() - event1.getX();
                float deltaY = event2.getY() - event1.getY();

                if(Math.abs(deltaX) > Math.abs(deltaY) && Math.abs(deltaX) > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                    if(deltaX < 0) {
                        Intent intent = new Intent(RepairActivityClient.this, ChatActivityClient.class);
                        intent.putExtra("repairID", repairID);
                        startActivity(intent);
                        finish();
                    }
                }
                return false;
            }
        });

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return gestureDetector.onTouchEvent(event);
    }

    @Override
    public void callbackRepairClient(ArrayList<ClientRepairDynamicRvModel> items) {
        rvDynamicAdapterRepairClient = new RvDynamicAdapterRepairClient(items, repairID);
        rvDynamicAdapterRepairClient.notifyDataSetChanged();
        dynamicRecyclerView.setAdapter(rvDynamicAdapterRepairClient);
    }
}