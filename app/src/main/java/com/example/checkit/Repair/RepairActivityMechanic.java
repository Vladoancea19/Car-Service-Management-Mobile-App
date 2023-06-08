package com.example.checkit.Repair;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.checkit.Chat.ChatActivityMechanic;
import com.example.checkit.Models.MechanicRepairDynamicRvModel;
import com.example.checkit.R;
import com.example.checkit.RecyclerView.Dynamic.RvDynamicAdapterRepairMechanic;
import com.example.checkit.RecyclerView.Interface.RvUpdateRepairMechanic;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class RepairActivityMechanic extends AppCompatActivity implements RvUpdateRepairMechanic {

    private GestureDetector gestureDetector;
    private static final int SWIPE_THRESHOLD = 200;
    private static final int SWIPE_VELOCITY_THRESHOLD = 200;
    private String repairID;
    private RecyclerView dynamicRecyclerView;
    private RvDynamicAdapterRepairMechanic rvDynamicAdapterRepairMechanic;
    private AlertDialog dialog1;
    private MaterialButton doneButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.repair_activity_mechanic);

        // Elements to variables
        TextView estimatedTime = findViewById(R.id.estimated_completion_time);
        TextView plateNumber = findViewById(R.id.plate_number);
        TextView carModel = findViewById(R.id.car_model);
        TextView manufactureYear = findViewById(R.id.manufacture_year);
        TextView transmissionType = findViewById(R.id.transmission_type);
        TextView fuelType = findViewById(R.id.fuel_type);
        doneButton = findViewById(R.id.done_button);

        Intent intent = getIntent();
        repairID = intent.getStringExtra("repairID");

        FirebaseDatabase database = FirebaseDatabase.getInstance("https://checkit-cd40f-default-rtdb.europe-west1.firebasedatabase.app/");

        DatabaseReference repairRef = database.getReference("reparations").child(repairID).child("carDamageInfoList");

        repairRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean allFinished = true;

                for(DataSnapshot childSnapshot : snapshot.getChildren()) {
                    if(childSnapshot.child("state").getValue().equals("notfinished")) {
                        allFinished = false;
                        break;
                    }
                }

                if(allFinished) {
                    doneButton.setEnabled(true);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        doneButton.setOnClickListener(v -> {
            if(!doneButton.isEnabled())
                return;

            finishRepair();
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
        RvUpdateRepairMechanic rvUpdate = this;

        costReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<MechanicRepairDynamicRvModel> dynamicItems = new ArrayList<>();

                for(DataSnapshot childSnapshot : snapshot.getChildren()) {
                    String description = childSnapshot.child("description").getValue(String.class);
                    Long cost = childSnapshot.child("cost").getValue(Long.class);

                    dynamicItems.add(new MechanicRepairDynamicRvModel(description, String.valueOf(cost)));
                    rvUpdate.callbackRepairMechanic(dynamicItems);
                }
                rvUpdate.callbackRepairMechanic(dynamicItems);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        ArrayList<MechanicRepairDynamicRvModel> dynamicItems2 = new ArrayList<>();
        dynamicRecyclerView = findViewById(R.id.dynamic_rv_repair);
        rvDynamicAdapterRepairMechanic = new RvDynamicAdapterRepairMechanic(dynamicItems2, repairID);
        dynamicRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        dynamicRecyclerView.setAdapter(rvDynamicAdapterRepairMechanic);

        gestureDetector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onFling(MotionEvent event1, MotionEvent event2, float velocityX, float velocityY) {
                float deltaX = event2.getX() - event1.getX();
                float deltaY = event2.getY() - event1.getY();

                if(Math.abs(deltaX) > Math.abs(deltaY) && Math.abs(deltaX) > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                    if(deltaX < 0) {
                        Intent intent = new Intent(RepairActivityMechanic.this, ChatActivityMechanic.class);
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
    public void callbackRepairMechanic(ArrayList<MechanicRepairDynamicRvModel> items) {
        rvDynamicAdapterRepairMechanic = new RvDynamicAdapterRepairMechanic(items, repairID);
        rvDynamicAdapterRepairMechanic.notifyDataSetChanged();
        dynamicRecyclerView.setAdapter(rvDynamicAdapterRepairMechanic);
    }

    private void finishRepair() {
        AlertDialog.Builder dialogBuilder1 = new AlertDialog.Builder(this);
        final View popupView = getLayoutInflater().inflate(R.layout.repair_popup, null);

        //Elements to variables
        MaterialButton closePopupButton = popupView.findViewById(R.id.close_popup_button);
        MaterialButton finishRepairtButton = popupView.findViewById(R.id.finish_repair);
        EditText serviceNote = popupView.findViewById(R.id.service_note);

        //Managing buttons actions
        finishRepairtButton.setOnClickListener(v -> {
            FirebaseDatabase database = FirebaseDatabase.getInstance("https://checkit-cd40f-default-rtdb.europe-west1.firebasedatabase.app/");

            DatabaseReference repairRef = database.getReference("reparations").child(repairID);
            repairRef.child("mechanicOk").setValue("ok");
            repairRef.child("serviceNote").setValue(serviceNote.getText().toString());

            doneButton.setEnabled(false);

            dialog1.dismiss();
        });

        //Create + show popup window
        dialogBuilder1.setView(popupView);
        dialog1 = dialogBuilder1.create();
        dialog1.show();

        //Close popup window
        closePopupButton.setOnClickListener(v -> dialog1.dismiss());
    }
}