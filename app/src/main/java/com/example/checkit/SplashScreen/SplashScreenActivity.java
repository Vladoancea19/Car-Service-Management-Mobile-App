package com.example.checkit.SplashScreen;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ActivityOptions;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Pair;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.checkit.Chat.ChatActivity;
import com.example.checkit.Dashboard.DashboardActivityClient;
import com.example.checkit.Dashboard.DashboardActivityMechanic;
import com.example.checkit.Login.LoginActivity;
import com.example.checkit.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

@SuppressLint("CustomSplashScreen")
public class SplashScreenActivity extends AppCompatActivity {

    Animation topAnimation, bottomAnimation;
    ImageView logoImage;
    TextView logoText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.splash_screen_activity);

        //Elements to variables
        logoImage = findViewById(R.id.logo_image);
        logoText = findViewById(R.id.logo_text);

        //Animations
        topAnimation = AnimationUtils.loadAnimation(this, R.anim.splash_screen_top_animation);
        bottomAnimation = AnimationUtils.loadAnimation(this, R.anim.splash_screen_bottom_animation);

        logoImage.setAnimation(topAnimation);
        logoText.setAnimation(bottomAnimation);

        rememberMe();
    }

    public void rememberMe() {
        SharedPreferences sharedPreferences = getSharedPreferences("rememberMe", MODE_PRIVATE);

        if(!sharedPreferences.getAll().isEmpty()) {
            if(sharedPreferences.getBoolean("clientAccount", false)) {
                DatabaseReference mechanicReference = FirebaseDatabase.getInstance("https://checkit-cd40f-default-rtdb.europe-west1.firebasedatabase.app/").getReference("client_users");
                Query checkMechanicUser = mechanicReference.orderByChild("phoneNumber").equalTo(sharedPreferences.getString("phoneNumber", ""));

                checkMechanicUser.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String firstNameStored = snapshot.child(sharedPreferences.getString("phoneNumber", "")).child("firstName").getValue(String.class);
                        String lastNameStored = snapshot.child(sharedPreferences.getString("phoneNumber", "")).child("lastName").getValue(String.class);
                        String fullNameStored = firstNameStored + " " + lastNameStored;
                        String emailStored = snapshot.child(sharedPreferences.getString("phoneNumber", "")).child("email").getValue(String.class);
                        String phoneNumberStored = snapshot.child(sharedPreferences.getString("phoneNumber", "")).child("phoneNumber").getValue(String.class);

                        //Animation delay + transition to login page
                        int SPLASH_SCREEN = 3000;
                        new Handler().postDelayed(() -> {
                            Intent intent = new Intent(SplashScreenActivity.this, DashboardActivityClient.class);

                            Pair[] pairs = new Pair[2];
                            pairs[0] = new Pair<View, String>(logoImage, "logo_image_transition");
                            pairs[1] = new Pair<View, String>(logoText, "logo_text_transition");

                            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(SplashScreenActivity.this, pairs);

                            intent.putExtra("fullName", fullNameStored);
                            intent.putExtra("email", emailStored);
                            intent.putExtra("phoneNumber", phoneNumberStored);

                            startActivity(intent, options.toBundle());
                            finish();
                        }, SPLASH_SCREEN);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
            else if(sharedPreferences.getBoolean("mechanicAccount", false)) {
                DatabaseReference mechanicReference = FirebaseDatabase.getInstance("https://checkit-cd40f-default-rtdb.europe-west1.firebasedatabase.app/").getReference("mechanic_users");
                Query checkMechanicUser = mechanicReference.orderByChild("phoneNumber").equalTo(sharedPreferences.getString("phoneNumber", ""));

                checkMechanicUser.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String firstNameStored = snapshot.child(sharedPreferences.getString("phoneNumber", "")).child("firstName").getValue(String.class);
                        String lastNameStored = snapshot.child(sharedPreferences.getString("phoneNumber", "")).child("lastName").getValue(String.class);
                        String fullNameStored = firstNameStored + " " + lastNameStored;
                        String emailStored = snapshot.child(sharedPreferences.getString("phoneNumber", "")).child("email").getValue(String.class);
                        String phoneNumberStored = snapshot.child(sharedPreferences.getString("phoneNumber", "")).child("phoneNumber").getValue(String.class);

                        //Animation delay + transition to login page
                        int SPLASH_SCREEN = 3000;
                        new Handler().postDelayed(() -> {
                            Intent intent = new Intent(SplashScreenActivity.this, DashboardActivityMechanic.class);

                            Pair[] pairs = new Pair[2];
                            pairs[0] = new Pair<View, String>(logoImage, "logo_image_transition");
                            pairs[1] = new Pair<View, String>(logoText, "logo_text_transition");

                            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(SplashScreenActivity.this, pairs);

                            intent.putExtra("fullName", fullNameStored);
                            intent.putExtra("email", emailStored);
                            intent.putExtra("phoneNumber", phoneNumberStored);

                            startActivity(intent, options.toBundle());
                            finish();
                        }, SPLASH_SCREEN);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        }
        else {
            //Animation delay + transition to login page
            int SPLASH_SCREEN = 3000;
            new Handler().postDelayed(() -> {
                Intent intent = new Intent(SplashScreenActivity.this, LoginActivity.class);

                Pair[] pairs = new Pair[2];
                pairs[0] = new Pair<View, String>(logoImage, "logo_image_transition");
                pairs[1] = new Pair<View, String>(logoText, "logo_text_transition");

                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(SplashScreenActivity.this, pairs);

                startActivity(intent, options.toBundle());
                finish();
            }, SPLASH_SCREEN);
        }
    }

}