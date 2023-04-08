package com.example.checkit;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.checkit.DynamicRVInterface.LoadMore;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MechanicOngoingFragment extends Fragment {

    private AlertDialog dialog1, dialog2, dialog3;
    private Button scanQR;
    private TextInputLayout phoneNumber, fullName, plateNumber, carModel, manufactureYear, transmissionType, fuelType;
    private ArrayList<StaticRvModel> staticItems = new ArrayList<>();
    private List<DynamicRVModel> dynamicItems = new ArrayList<>();
    private DynamicRVAdapter dynamicRVAdapter;
    private static final int CAMERA_REQUEST = 1888;
//    private ImageView imageView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_mechanic_ongoing, container, false);

        //Elements to variables
        ImageButton addNew = view.findViewById(R.id.add_new);
        RecyclerView staticRecyclerView = view.findViewById(R.id.rv1);
        RecyclerView dynamicRecyclerView = view.findViewById(R.id.rv2);

        //Managing buttons actions
        addNew.setOnClickListener(v -> clientInfo());

        //Adding values to the static recyclerview items
        staticItems.add(new StaticRvModel(R.drawable.gear_gradient, "In progress"));
        staticItems.add(new StaticRvModel(R.drawable.check_mark, "Done"));
        staticItems.add(new StaticRvModel(R.drawable.clock, "Pending"));
        staticItems.add(new StaticRvModel(R.drawable.cross, "Rejected"));

        StaticRvAdapter staticRvAdapter = new StaticRvAdapter(staticItems);
        staticRecyclerView.setLayoutManager(new LinearLayoutManager(this.getContext(), LinearLayoutManager.HORIZONTAL, false));
        staticRecyclerView.setAdapter(staticRvAdapter);

        //Adding values to the dynamic recyclerview items
        dynamicItems.add(new DynamicRVModel(100, "100"+"%", "B 23 MYE", "Skoda Octavia"));
        dynamicItems.add(new DynamicRVModel(50, "50"+"%", "IF 50 NDJ", "Dacia Logan"));
        dynamicItems.add(new DynamicRVModel(33, "33"+"%", "B 23 MYE", "Skoda Octavia"));
        dynamicItems.add(new DynamicRVModel(75, "75"+"%", "B 23 MYE", "Skoda Octavia"));
        dynamicItems.add(new DynamicRVModel(66, "66"+"%", "B 23 MYE", "Skoda Octavia"));
        dynamicItems.add(new DynamicRVModel(80, "80"+"%", "B 23 MYE", "Skoda Octavia"));

        dynamicRecyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));
        dynamicRVAdapter = new DynamicRVAdapter(dynamicRecyclerView, this.getActivity(), dynamicItems);
        dynamicRecyclerView.setAdapter(dynamicRVAdapter);

        dynamicRVAdapter.setLoadMore(new LoadMore() {
            @Override
            public void onLoadMore() {
                if(dynamicItems.size() <= 10) {
                    dynamicItems.add(null);
                    dynamicRVAdapter.notifyItemInserted(dynamicItems.size());
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            dynamicItems.remove(dynamicItems.size()-1);
                            dynamicRVAdapter.notifyItemRemoved(dynamicItems.size());

                            int index = dynamicItems.size();
                            int end = index+10;
                            for(int i = index; i < end ; i++) {
                                int progress;
                                String plateNumber, carModel, progressText;
                                progress = 25; plateNumber = "B 76 GHF";
                                carModel = "Volkswagen Passat"; progressText = "25%";

                                DynamicRVModel item = new DynamicRVModel(progress, progressText, plateNumber, carModel);
                                dynamicItems.add(item);
                            }
                            dynamicRVAdapter.notifyDataSetChanged();
                            dynamicRVAdapter.setLoaded();
                        }
                    }, 4000);
                }
            }
        });

        return view;
    }

    private void clientInfo() {
        AlertDialog.Builder dialogBuilder1 = new AlertDialog.Builder(getActivity());
        final View popupView = getLayoutInflater().inflate(R.layout.add_new_reparation_client_info, null);

        //Elements to variables
        Button closePopup1 = popupView.findViewById(R.id.close_popup);
        scanQR = popupView.findViewById(R.id.or_scan_qr_code);
        Button next1 = popupView.findViewById(R.id.next_button);
        phoneNumber = popupView.findViewById(R.id.phone_number);
        fullName = popupView.findViewById(R.id.full_name);

        //Managing buttons actions
        scanQR.setOnClickListener(v -> scanQR());
        next1.setOnClickListener(v -> {
            //Colectare date
            boolean validPhone = false, validName = false;

            String phoneNumberInput = Objects.requireNonNull(phoneNumber.getEditText().getText()).toString();

            if(phoneNumberInput.isEmpty()) {
                phoneNumber.setError("Please enter client's phone number");
            }
            else {
                phoneNumber.setError(null);
                validPhone = true;
            }

            String fullNameInput = Objects.requireNonNull(fullName.getEditText().getText()).toString();

            if(fullNameInput.isEmpty()) {
                fullName.setError("Please enter client's name");
            }
            else {
                fullName.setError(null);
                validName = true;
            }

            if(validName && validPhone) {
                dialog1.dismiss();
                carInfo();
            }
        });

        phoneNumber.setOnClickListener(v -> phoneNumber.setError(null));

        fullName.setOnClickListener(v -> fullName.setError(null));

        //Create + show popup window
        dialogBuilder1.setView(popupView);
        dialog1 = dialogBuilder1.create();
        dialog1.show();

        //Close popup window
        closePopup1.setOnClickListener(v -> dialog1.dismiss());
    }

    private void carInfo() {
        AlertDialog.Builder dialogBuilder2 = new AlertDialog.Builder(getActivity());
        final View popupView = getLayoutInflater().inflate(R.layout.add_new_reparation_car_info, null);

        //Elements to variables
        Button closePopup2 = popupView.findViewById(R.id.close_popup);
        Button next2 = popupView.findViewById(R.id.next_button);
        plateNumber = popupView.findViewById(R.id.plate_number);
        carModel = popupView.findViewById(R.id.car_model);
        manufactureYear = popupView.findViewById(R.id.manufacture_year);
        transmissionType = popupView.findViewById(R.id.transmission_type);
        fuelType = popupView.findViewById(R.id.fuel_type);

        //Managing buttons actions
        next2.setOnClickListener(v -> {
            //Colectare date
            boolean validPlateNumber = false, validCarModel = false, validManufactureYear = false, validTransmissionType = false, validFuelType = false;

            String plateNumberInput = Objects.requireNonNull(plateNumber.getEditText().getText()).toString();

            if(plateNumberInput.isEmpty()) {
                plateNumber.setError("Please enter car's plate number");
            }
            else {
                plateNumber.setError(null);
                validPlateNumber = true;
            }

            String carModelInput = Objects.requireNonNull(carModel.getEditText().getText()).toString();

            if(carModelInput.isEmpty()) {
                carModel.setError("Please enter car's model");
            }
            else {
                carModel.setError(null);
                validCarModel = true;
            }

            String manufactureYearInput = Objects.requireNonNull(manufactureYear.getEditText().getText()).toString();

            if(manufactureYearInput.isEmpty()) {
                manufactureYear.setError("Please enter car's manufacture year");
            }
            else {
                manufactureYear.setError(null);
                validManufactureYear = true;
            }

            String transmissionTypeInput = Objects.requireNonNull(transmissionType.getEditText().getText()).toString();

            if(transmissionTypeInput.isEmpty()) {
                transmissionType.setError("Please enter car's transmission type");
            }
            else {
                transmissionType.setError(null);
                validTransmissionType = true;
            }

            String fuelTypeInput = Objects.requireNonNull(fuelType.getEditText().getText()).toString();

            if(fuelTypeInput.isEmpty()) {
                fuelType.setError("Please enter car's fuel type");
            }
            else {
                fuelType.setError(null);
                validFuelType = true;
            }

            if(validCarModel && validPlateNumber && validManufactureYear && validTransmissionType && validFuelType) {
                dialog2.dismiss();
                carDamageInfo();
            }
        });

        //Create + show popup window
        dialogBuilder2.setView(popupView);
        dialog2 = dialogBuilder2.create();
        dialog2.show();

        //Close popup window
        closePopup2.setOnClickListener(v -> dialog2.dismiss());
    }

    private void carDamageInfo() {
        AlertDialog.Builder dialogBuilder3 = new AlertDialog.Builder(getActivity());
        View popupView = getLayoutInflater().inflate(R.layout.add_new_reparation_car_damage_info, null);

        //Elements to variables
        Button closePopup3 = popupView.findViewById(R.id.close_popup);
        Button uploadImage = popupView.findViewById(R.id.upload_image);
        Button submit = popupView.findViewById(R.id.submit);
//        imageView = popupView.findViewById(R.id.testImage);

        //Managing buttons actions
        submit.setOnClickListener(v -> carInfo());
        uploadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAMERA_REQUEST);
            }
        });

        //Create + show popup window
        dialogBuilder3.setView(popupView);
        dialog3 = dialogBuilder3.create();
        dialog3.show();

        //Close popup window
        closePopup3.setOnClickListener(v -> dialog3.dismiss());
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK) {
            Bitmap photo = (Bitmap) data.getExtras().get("data");
//            imageView.setImageBitmap(photo);
        }
    }

    private void scanQR() {
        ScanOptions options = new ScanOptions();
        options.setBeepEnabled(false);
        options.setOrientationLocked(true);
        options.setCaptureActivity(CaptureAct.class);
        barLauncher.launch(options);
    }

    ActivityResultLauncher<ScanOptions> barLauncher = registerForActivityResult(new ScanContract(), result -> {
        if (result.getContents() != null) {
            String resultValue = result.getContents();

            DatabaseReference clientReference = FirebaseDatabase.getInstance("https://checkit-cd40f-default-rtdb.europe-west1.firebasedatabase.app/").getReference("client_users");
            Query checkClientUser = clientReference.orderByChild("phoneNumber").equalTo(resultValue);

            checkClientUser.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.exists()) {

                        String firstNameStored = snapshot.child(resultValue).child("firstName").getValue(String.class);
                        String lastNameStored = snapshot.child(resultValue).child("lastName").getValue(String.class);

                        String fullNameStored = firstNameStored + " " + lastNameStored;

                        fullName.getEditText().setText(fullNameStored);
                        phoneNumber.getEditText().setText(result.getContents());
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    });
}