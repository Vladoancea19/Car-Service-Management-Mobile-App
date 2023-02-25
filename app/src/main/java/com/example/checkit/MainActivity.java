package com.example.checkit;

import androidx.appcompat.app.AppCompatActivity;

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

public class MainActivity extends AppCompatActivity {

    //TODO: Crearea unei reparatii

    //TODO: Completare pagina profil mecanic
    //TODO: Meniu dashboard mecanic
    //TODO: Verificare OTP
    //TODO: Suprascriere utilizator existent
    //TODO: Optiune de remember me
    //TODO: Adaugare camp pentru autorizatie mecanic + verificare
    //TODO: Telefon + parola identice pentru un client si un mecanic
    //TODO: Editare email din pagine de profil

    //Variables for SplashScreen page elements
    Animation topAnimation, bottomAnimation;
    ImageView logoImage;
    TextView logoText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        //Elements to variables
        logoImage = findViewById(R.id.imageView);
        logoText = findViewById(R.id.textView);

        //Animations
        topAnimation = AnimationUtils.loadAnimation(this, R.anim.top_animation);
        bottomAnimation = AnimationUtils.loadAnimation(this, R.anim.bottom_animation);

        logoImage.setAnimation(topAnimation);
        logoText.setAnimation(bottomAnimation);

        //Animation delay + transition to Login page
        int SPLASH_SCREEN = 3000;
        new Handler().postDelayed(() -> {
            Intent intent = new Intent(MainActivity.this, Login.class);

            Pair[] pairs = new Pair[2];
            pairs[0] = new Pair<View, String>(logoImage, "logo_image");
            pairs[1] = new Pair<View, String>(logoText, "logo_text");

            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(MainActivity.this, pairs);

            startActivity(intent, options.toBundle());
            finish();
        }, SPLASH_SCREEN);
    }
}