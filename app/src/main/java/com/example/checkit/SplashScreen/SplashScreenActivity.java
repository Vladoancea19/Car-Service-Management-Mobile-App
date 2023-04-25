package com.example.checkit.SplashScreen;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ActivityOptions;
import android.content.Intent;
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
import com.example.checkit.Login.LoginActivity;
import com.example.checkit.R;

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

        //Animation delay + transition to login page
        int SPLASH_SCREEN = 3000;
        new Handler().postDelayed(() -> {
            Intent intent = new Intent(SplashScreenActivity.this, ChatActivity.class); // TODO: LoginActivity

            Pair[] pairs = new Pair[2];
            pairs[0] = new Pair<View, String>(logoImage, "logo_image_transition");
            pairs[1] = new Pair<View, String>(logoText, "logo_text_transition");

            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(SplashScreenActivity.this, pairs);

            startActivity(intent, options.toBundle());
            finish();
        }, SPLASH_SCREEN);
    }
}