package com.example.checkit.Dashboard.Profile;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.checkit.Login.LoginActivity;
import com.example.checkit.R;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class ProfileFragmentClient extends Fragment {

    private AlertDialog dialog;
    private TextView fullName, emailText, phoneNumberText;
    private ImageView profilePicture;
    private MaterialButton logOutButton;
    private static final int GALLERY_REQUEST = 1889;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.profile_fragment_client, container, false);

        //Elements to variables
        profilePicture = view.findViewById(R.id.profile_picture);
        fullName = view.findViewById(R.id.full_name);
        emailText = view.findViewById(R.id.email_text);
        phoneNumberText = view.findViewById(R.id.phone_number_text);
        logOutButton = view.findViewById(R.id.log_out_button);

        //Managing buttons actions
        profilePicture.setOnClickListener(v -> {
            if(ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                Intent cameraIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(cameraIntent, GALLERY_REQUEST);
            }
            else {
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 100);
            }
        });

        logOutButton.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), LoginActivity.class);

            SharedPreferences sharedPreferences = getActivity().getSharedPreferences("rememberMe", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.clear();
            editor.apply();

            Intent intent2 = requireActivity().getIntent();

            String userPhoneNumber = intent2.getStringExtra("phoneNumber");

            DatabaseReference clientReference = FirebaseDatabase.getInstance("https://checkit-cd40f-default-rtdb.europe-west1.firebasedatabase.app/").getReference("client_users");
            clientReference.child(userPhoneNumber).child("deviceToken").setValue("");

            startActivity(intent);
            getActivity().finish();
        });

        FirebaseDatabase database = FirebaseDatabase.getInstance("https://checkit-cd40f-default-rtdb.europe-west1.firebasedatabase.app/");
        DatabaseReference reference = database.getReference("client_users").child(requireActivity().getIntent().getStringExtra("phoneNumber")).child("profileImage");

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String uri = dataSnapshot.getValue(String.class);
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

        //Show user data
        showUserData();

        return view;
    }

    private void showUserData() {
        Intent intent = requireActivity().getIntent();

        String userFullName = intent.getStringExtra("fullName");
        String userEmail = intent.getStringExtra("email");
        String userPhoneNumber = intent.getStringExtra("phoneNumber");

        fullName.setText(userFullName);
        emailText.setText(userEmail);
        phoneNumberText.setText(userPhoneNumber);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == Activity.RESULT_OK && requestCode == GALLERY_REQUEST) {
            Uri uri = data.getData();
            Bitmap image = null;
            try {
                image = MediaStore.Images.Media.getBitmap(this.getActivity().getContentResolver(), uri);
            } catch (IOException e) {
                e.printStackTrace();
            }

            String fileName = requireActivity().getIntent().getStringExtra("phoneNumber") + "C.jpg";

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            image.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] imageData = baos.toByteArray();

            FirebaseStorage storage = FirebaseStorage.getInstance();

            final StorageReference imageRef = storage.getReference().child("profile_images/" + fileName);

            imageRef.getMetadata().addOnSuccessListener(metadata -> {
                imageRef.delete().addOnSuccessListener(aVoid -> {
                    imageRef.putBytes(imageData).addOnSuccessListener(taskSnapshot -> {
                        imageRef.getDownloadUrl().addOnSuccessListener(uri1 -> {
                            Picasso.get().load(uri1).into(profilePicture);
                            profilePicture.setBackgroundColor(Color.WHITE);

                            FirebaseDatabase database = FirebaseDatabase.getInstance("https://checkit-cd40f-default-rtdb.europe-west1.firebasedatabase.app/");
                            DatabaseReference reference = database.getReference("client_users");

                            reference.child(requireActivity().getIntent().getStringExtra("phoneNumber")).child("profileImage").setValue(uri1.toString());
                        }).addOnFailureListener(e -> {
                            Toast.makeText(getContext(), "Something went wrong!", Toast.LENGTH_SHORT).show();
                        });
                    }).addOnFailureListener(e -> {
                        Toast.makeText(getContext(), "Something went wrong!", Toast.LENGTH_SHORT).show();
                    });
                }).addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Something went wrong!", Toast.LENGTH_SHORT).show();
                });
            }).addOnFailureListener(e -> {
                imageRef.putBytes(imageData).addOnSuccessListener(taskSnapshot -> {
                    imageRef.getDownloadUrl().addOnSuccessListener(uri2 -> {
                        Picasso.get().load(uri2).into(profilePicture);

                        FirebaseDatabase database = FirebaseDatabase.getInstance("https://checkit-cd40f-default-rtdb.europe-west1.firebasedatabase.app/");
                        DatabaseReference reference = database.getReference("client_users");

                        reference.child(requireActivity().getIntent().getStringExtra("phoneNumber")).child("profileImage").setValue(uri2.toString());
                    }).addOnFailureListener(e1 -> {
                        Toast.makeText(getContext(), "Something went wrong!", Toast.LENGTH_SHORT).show();
                    });
                }).addOnFailureListener(e1 -> {
                    Toast.makeText(getContext(), "Something went wrong!", Toast.LENGTH_SHORT).show();
                });
            });
        }
    }
}