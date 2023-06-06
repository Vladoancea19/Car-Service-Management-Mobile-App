package com.example.checkit.Chat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.checkit.Models.ChatModel;
import com.example.checkit.R;
import com.example.checkit.RecyclerView.Dynamic.RvDynamicAdapterChatClient;
import com.example.checkit.RecyclerView.Interface.RvUpdateChat;
import com.example.checkit.Repair.RepairActivityClient;
import com.google.firebase.Timestamp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class ChatActivityClient extends AppCompatActivity implements RvUpdateChat {

    private static final int CALL_PERMISSION_REQUEST_CODE = 123;
    private GestureDetector gestureDetector;
    private static final int SWIPE_THRESHOLD = 200;
    private static final int SWIPE_VELOCITY_THRESHOLD = 200;
    private RecyclerView dynamicRecyclerView;
    private RvDynamicAdapterChatClient rvDynamicAdapterChatClient;
    private String repairID, phoneNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_activity_client);

        Window window = this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(this.getResources().getColor(R.color.chat_status_bar_color));

        Intent intent = getIntent();
        repairID = intent.getStringExtra("repairID");

        ImageView goBackButton = findViewById(R.id.go_back_button);
        EditText messageBox = findViewById(R.id.input_message_box);
        FrameLayout sendButton = findViewById(R.id.send_button);
        TextView name = findViewById(R.id.name);
        ImageView profilePicture = findViewById(R.id.profile_picture);
        ImageView callButton = findViewById(R.id.call_button);

        goBackButton.setOnClickListener(v -> {
            Intent intent12 = new Intent(ChatActivityClient.this, RepairActivityClient.class);
            intent12.putExtra("repairID", repairID);
            startActivity(intent12);
            finish();
        });

        callButton.setOnClickListener(v -> {
            if(ContextCompat.checkSelfPermission(ChatActivityClient.this, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
                Intent intent1 = new Intent(Intent.ACTION_CALL);
                intent1.setData(Uri.parse("tel:" + phoneNumber));

                startActivity(intent1);
            }
            else {
                ActivityCompat.requestPermissions(ChatActivityClient.this, new String[]{Manifest.permission.CALL_PHONE}, CALL_PERMISSION_REQUEST_CODE);
            }
        });

        FirebaseDatabase database0 = FirebaseDatabase.getInstance("https://checkit-cd40f-default-rtdb.europe-west1.firebasedatabase.app/");
        DatabaseReference repairReference = database0.getReference("reparations").child(repairID);
        repairReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                DatabaseReference reference = database0.getReference("mechanic_users").child(snapshot.child("mechanicPhoneNumber").getValue().toString());

                reference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String uri = dataSnapshot.child("profileImage").getValue(String.class);
                        String firstName = dataSnapshot.child("firstName").getValue(String.class);
                        String lastName = dataSnapshot.child("lastName").getValue(String.class);
                        phoneNumber = dataSnapshot.child("phoneNumber").getValue(String.class);

                        name.setText(firstName + " " + lastName);

                        if(!uri.isEmpty()) {
                            Picasso.get().load(uri).into(profilePicture);
                        }
                        else {
                            profilePicture.setImageResource(R.drawable.profile_picture);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        FirebaseDatabase database = FirebaseDatabase.getInstance("https://checkit-cd40f-default-rtdb.europe-west1.firebasedatabase.app/");
        DatabaseReference chatReference = database.getReference("chats").child(repairID).child("messages");
        RvUpdateChat rvUpdate = this;

        chatReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<ChatModel> dynamicItems = new ArrayList<>();

                for(DataSnapshot childSnapshot : snapshot.getChildren()) {
                    String message = childSnapshot.child("message").getValue(String.class);
                    String timestamp = childSnapshot.child("timestamp").getValue(String.class);
                    String sender = childSnapshot.child("sender").getValue(String.class);

                    dynamicItems.add(new ChatModel(repairID, message, sender, timestamp));
                    rvUpdate.callbackChat(dynamicItems);
                }
                rvUpdate.callbackChat(dynamicItems);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        ArrayList<ChatModel> dynamicItems2 = new ArrayList<>();
        dynamicRecyclerView = findViewById(R.id.dynamic_rv_chat);
        rvDynamicAdapterChatClient = new RvDynamicAdapterChatClient(dynamicItems2, repairID);
        dynamicRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        dynamicRecyclerView.setAdapter(rvDynamicAdapterChatClient);
        dynamicRecyclerView.scrollToPosition( rvDynamicAdapterChatClient.getItemCount() - 1);

        sendButton.setOnClickListener(v -> {
            if(!messageBox.getText().toString().isEmpty()) {
                String message = messageBox.getText().toString();

                Timestamp timestamp = new Timestamp(new Date());
                SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd HH:mm");
                String formattedDateTime = dateFormat.format(timestamp.toDate());

                messageBox.setText("");

                ChatModel chatModel = new ChatModel(repairID, message, "client", formattedDateTime);
                DatabaseReference newReference = chatReference.push();
                newReference.setValue(chatModel);
            }
        });

        gestureDetector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onFling(MotionEvent event1, MotionEvent event2, float velocityX, float velocityY) {
                float deltaX = event2.getX() - event1.getX();
                float deltaY = event2.getY() - event1.getY();

                if(Math.abs(deltaX) > Math.abs(deltaY) && Math.abs(deltaX) > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                    if(deltaX > 0) {
                        Intent intent = new Intent(ChatActivityClient.this, RepairActivityClient.class);
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
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == CALL_PERMISSION_REQUEST_CODE) {
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Intent intent = new Intent(Intent.ACTION_CALL);
                intent.setData(Uri.parse("tel:" + phoneNumber));

                startActivity(intent);
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return gestureDetector.onTouchEvent(event);
    }

    @Override
    public void callbackChat(ArrayList<ChatModel> items) {
        rvDynamicAdapterChatClient = new RvDynamicAdapterChatClient(items, repairID);
        rvDynamicAdapterChatClient.notifyDataSetChanged();
        dynamicRecyclerView.setAdapter(rvDynamicAdapterChatClient);
        dynamicRecyclerView.scrollToPosition( rvDynamicAdapterChatClient.getItemCount() - 1);
    }
}