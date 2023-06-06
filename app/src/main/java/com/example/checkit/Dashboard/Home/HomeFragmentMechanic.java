package com.example.checkit.Dashboard.Home;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.checkit.CaptureAct;
import com.example.checkit.Models.CarDamageInfoModel;
import com.example.checkit.Models.CarInfoModel;
import com.example.checkit.Models.HomeDynamicRvModel;
import com.example.checkit.Models.MechanicStaticRvModel;
import com.example.checkit.Models.RepairModel;
import com.example.checkit.R;
import com.example.checkit.RecyclerView.Dynamic.RvDynamicAdapterMechanic;
import com.example.checkit.RecyclerView.Interface.RvUpdate;
import com.example.checkit.RecyclerView.Static.RvStaticAdapterMechanic;
import com.example.checkit.ml.CarDamageDetection;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

import org.tensorflow.lite.DataType;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.Objects;

public class HomeFragmentMechanic extends Fragment implements RvUpdate {

    private AlertDialog dialog1, dialog2, dialog3;
    private TextInputLayout phoneNumberContainer, plateNumberContainer, carModelContainer, manufactureYearContainer, transmissionTypeContainer, fuelTypeContainer;
    private RecyclerView dynamicRecyclerView;
    private ArrayList<MechanicStaticRvModel> staticItems = new ArrayList<>();
    private RvDynamicAdapterMechanic rvDynamicAdapterMechanic;
    private static final int CAMERA_REQUEST = 1888, GALLERY_REQUEST = 1889;
    private String mechanicPhoneNumber;
    private String clientPhoneNumber, clientFullName;
    private CarInfoModel carInfo;
    private CarDamageInfoModel carDamageInfo;
    private ArrayList<CarDamageInfoModel> carDamageInfoList;
    private RepairModel repairs;
    private TextInputEditText descriptionText, costText;
    private View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.home_fragment_mechanic, container, false);

        //Elements to variables
        MaterialButton addNewRepairButton = view.findViewById(R.id.add_new_repair_button);

        //Managing buttons actions
        addNewRepairButton.setOnClickListener(v -> {
            carDamageInfoList = new ArrayList<>();
            clientInfo();
        });

        staticItems.add(new MechanicStaticRvModel(R.drawable.gear_gradient, "In progress"));
        staticItems.add(new MechanicStaticRvModel(R.drawable.check_mark, "Done"));
        staticItems.add(new MechanicStaticRvModel(R.drawable.clock, "Pending"));
        staticItems.add(new MechanicStaticRvModel(R.drawable.cross, "Rejected"));

        RecyclerView staticRecyclerView = view.findViewById(R.id.static_rv_mechanic);
        Intent intent = requireActivity().getIntent();
        mechanicPhoneNumber = intent.getStringExtra("phoneNumber");
        RvStaticAdapterMechanic rvStaticAdapterMechanic = new RvStaticAdapterMechanic(staticItems, mechanicPhoneNumber, getActivity(), this);
        staticRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        staticRecyclerView.setAdapter(rvStaticAdapterMechanic);

        ArrayList<HomeDynamicRvModel> dynamicItems = new ArrayList<>();
        dynamicRecyclerView = view.findViewById(R.id.dynamic_rv_mechanic);
        rvDynamicAdapterMechanic = new RvDynamicAdapterMechanic(getContext(), dynamicItems, 0);
        dynamicRecyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));
        dynamicRecyclerView.setAdapter(rvDynamicAdapterMechanic);

        return view;
    }

    @SuppressLint("ClickableViewAccessibility")
    private void clientInfo() {
        AlertDialog.Builder dialogBuilder1 = new AlertDialog.Builder(getActivity());
        final View popupView = getLayoutInflater().inflate(R.layout.add_new_repair_client_info, null);

        //Elements to variables
        Button closePopupButton1 = popupView.findViewById(R.id.close_popup_button_1);
        Button nextButton1 = popupView.findViewById(R.id.next_button_1);
        phoneNumberContainer = popupView.findViewById(R.id.phone_number_container);

        //Managing buttons actions
        nextButton1.setOnClickListener(v -> {
            String phoneNumberInput = Objects.requireNonNull(Objects.requireNonNull(phoneNumberContainer.getEditText()).getText()).toString();

            if(phoneNumberInput.isEmpty()) {
                phoneNumberContainer.setError("Please enter client's phone number");
            }
            else {
                DatabaseReference clientReference = FirebaseDatabase.getInstance("https://checkit-cd40f-default-rtdb.europe-west1.firebasedatabase.app/").getReference("client_users");
                Query checkClientUser = clientReference.orderByChild("phoneNumber").equalTo(phoneNumberInput);

                checkClientUser.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()) {
                            phoneNumberContainer.setError(null);
                            phoneNumberContainer.setErrorEnabled(false);

                            String firstNameStored = snapshot.child(phoneNumberInput).child("firstName").getValue(String.class);
                            String lastNameStored = snapshot.child(phoneNumberInput).child("lastName").getValue(String.class);
                            String fullNameStored = firstNameStored + " " + lastNameStored;

                            Intent intent = requireActivity().getIntent();
                            mechanicPhoneNumber = intent.getStringExtra("phoneNumber");
                            clientPhoneNumber = phoneNumberInput;
                            clientFullName = fullNameStored;

                            dialog1.dismiss();
                            carInfo();
                        }
                        else {
                            phoneNumberContainer.setError("Incorrect phone number");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });

        phoneNumberContainer.setOnClickListener(v -> phoneNumberContainer.setError(null));

        //Create + show popup window
        dialogBuilder1.setView(popupView);
        dialog1 = dialogBuilder1.create();
        dialog1.show();

        //Close popup window
        closePopupButton1.setOnClickListener(v -> dialog1.dismiss());
    }

    private void carInfo() {
        AlertDialog.Builder dialogBuilder2 = new AlertDialog.Builder(getActivity());
        final View popupView = getLayoutInflater().inflate(R.layout.add_new_repair_car_info, null);

        //Elements to variables
        Button closePopupButton2 = popupView.findViewById(R.id.close_popup_button_2);
        Button scanQRButton = popupView.findViewById(R.id.scan_qr_button);
        Button nextButton2 = popupView.findViewById(R.id.next_button_2);
        plateNumberContainer = popupView.findViewById(R.id.plate_number_container);
        carModelContainer = popupView.findViewById(R.id.car_model_container);
        manufactureYearContainer = popupView.findViewById(R.id.manufacture_year_container);
        transmissionTypeContainer = popupView.findViewById(R.id.transmission_type_container);
        fuelTypeContainer = popupView.findViewById(R.id.fuel_type_container);

        //Managing buttons actions
        scanQRButton.setOnClickListener(v -> scanQR());
        nextButton2.setOnClickListener(v -> {
            boolean validPlateNumber = false, validCarModel = false, validManufactureYear = false, validTransmissionType = false, validFuelType = false;

            String plateNumberInput = Objects.requireNonNull(Objects.requireNonNull(plateNumberContainer.getEditText()).getText()).toString();

            if(plateNumberInput.isEmpty()) {
                plateNumberContainer.setError("Please enter car's plate number");
            }
            else {
                plateNumberContainer.setError(null);
                validPlateNumber = true;
            }

            String carModelInput = Objects.requireNonNull(Objects.requireNonNull(carModelContainer.getEditText()).getText()).toString();

            if(carModelInput.isEmpty()) {
                carModelContainer.setError("Please enter car's model");
            }
            else {
                carModelContainer.setError(null);
                validCarModel = true;
            }

            String manufactureYearInput = Objects.requireNonNull(Objects.requireNonNull(manufactureYearContainer.getEditText()).getText()).toString();

            if(manufactureYearInput.isEmpty()) {
                manufactureYearContainer.setError("Please enter car's manufacture year");
            }
            else {
                manufactureYearContainer.setError(null);
                validManufactureYear = true;
            }

            String transmissionTypeInput = Objects.requireNonNull(Objects.requireNonNull(transmissionTypeContainer.getEditText()).getText()).toString();

            if(transmissionTypeInput.isEmpty()) {
                transmissionTypeContainer.setError("Please enter car's transmission type");
            }
            else {
                transmissionTypeContainer.setError(null);
                validTransmissionType = true;
            }

            String fuelTypeInput = Objects.requireNonNull(Objects.requireNonNull(fuelTypeContainer.getEditText()).getText()).toString();

            if(fuelTypeInput.isEmpty()) {
                fuelTypeContainer.setError("Please enter car's fuel type");
            }
            else {
                fuelTypeContainer.setError(null);
                validFuelType = true;
            }

            if(validCarModel && validPlateNumber && validManufactureYear && validTransmissionType && validFuelType) {
                carInfo = new CarInfoModel(plateNumberInput, carModelInput, manufactureYearInput, transmissionTypeInput, fuelTypeInput);

                dialog2.dismiss();
                carDamageInfo();
            }
        });

        //Create + show popup window
        dialogBuilder2.setView(popupView);
        dialog2 = dialogBuilder2.create();
        dialog2.show();

        //Close popup window
        closePopupButton2.setOnClickListener(v -> dialog2.dismiss());
    }

    private void carDamageInfo() {
        AlertDialog.Builder dialogBuilder3 = new AlertDialog.Builder(getActivity());
        View popupView = getLayoutInflater().inflate(R.layout.add_new_repair_car_damage_info, null);

        //Elements to variables
        Button closePopupButton3 = popupView.findViewById(R.id.close_popup_button_3);
        Button captureImageButton = popupView.findViewById(R.id.capture_image_button);
        Button uploadImageButton = popupView.findViewById(R.id.upload_image_button);
        Button submitButton = popupView.findViewById(R.id.submit_button);
        Button estimatedTimeButton = popupView.findViewById(R.id.estimated_time_button);
        MaterialButton addRepairButton = popupView.findViewById(R.id.add_repair_button);
        MaterialButton removeRepairButton = popupView.findViewById(R.id.remove_repair_button);
        descriptionText = popupView.findViewById(R.id.description_text);
        costText = popupView.findViewById(R.id.cost_text);
        TextInputLayout descriptionContainer = popupView.findViewById(R.id.description_container);
        TextInputLayout costContainer = popupView.findViewById(R.id.cost_container);

        //Managing buttons actions
        DatePickerDialog datePickerDialog;
        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                month += 1;
                String date = dayOfMonth + " " + getFormattedMonth(month) + " " + year;
                estimatedTimeButton.setText(date.toUpperCase());
            }

            private String getFormattedMonth(int month) {
                String formattedMonth;

                if(month ==1)
                    formattedMonth = "JAN";
                else if(month == 2)
                    formattedMonth = "FEB";
                else if(month == 3)
                    formattedMonth = "MAR";
                else if(month == 4)
                    formattedMonth = "APR";
                else if(month == 5)
                    formattedMonth = "MAY";
                else if(month == 6)
                    formattedMonth = "JUN";
                else if(month == 7)
                    formattedMonth = "JUL";
                else if(month == 8)
                    formattedMonth = "AUG";
                else if(month == 9)
                    formattedMonth = "SEP";
                else if(month == 10)
                    formattedMonth = "OCT";
                else if(month == 11)
                    formattedMonth = "NOV";
                else
                    formattedMonth = "DEC";

                return formattedMonth;
            }
        };

        int day = Calendar.getInstance().get(Calendar.DAY_OF_MONTH), month = Calendar.getInstance().get(Calendar.MONTH), year = Calendar.getInstance().get(Calendar.YEAR), style = AlertDialog.THEME_HOLO_DARK;
        datePickerDialog = new DatePickerDialog(this.getContext(), style, dateSetListener, year, month, day);

        captureImageButton.setOnClickListener(v -> {
            if(ContextCompat.checkSelfPermission(this.getContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAMERA_REQUEST);
            }
            else {
                requestPermissions(new String[]{Manifest.permission.CAMERA}, 100);
            }
        });

        uploadImageButton.setOnClickListener(v -> {
            if(ContextCompat.checkSelfPermission(this.getContext(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                Intent cameraIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(cameraIntent, GALLERY_REQUEST);
            }
            else {
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 100);
            }
        });

        descriptionText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(before == 0) {
                    addRepairButton.setVisibility(popupView.VISIBLE);
                    removeRepairButton.setVisibility(popupView.VISIBLE);
                }
                else if(s.toString().isEmpty() && Objects.requireNonNull(costText.getText()).toString().isEmpty()) {
                    addRepairButton.setVisibility(popupView.GONE);
                    removeRepairButton.setVisibility(popupView.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        costText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(before == 0) {
                    addRepairButton.setVisibility(popupView.VISIBLE);
                    removeRepairButton.setVisibility(popupView.VISIBLE);
                }
                else if(s.toString().isEmpty() && Objects.requireNonNull(descriptionText.getText()).toString().isEmpty()) {
                    addRepairButton.setVisibility(popupView.GONE);
                    removeRepairButton.setVisibility(popupView.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        removeRepairButton.setOnClickListener(v -> {
            Objects.requireNonNull(descriptionText.getText()).clear();
            Objects.requireNonNull(costText.getText()).clear();
        });

        addRepairButton.setOnClickListener(v -> {
            boolean validDescription = false, validCost = false;

            if(Objects.requireNonNull(descriptionText.getText()).toString().isEmpty()) {
                descriptionContainer.setError("Please enter repair description");
            }
            else {
                descriptionContainer.setError(null);
                validDescription = true;
            }

            if(Objects.requireNonNull(costText.getText()).toString().isEmpty()) {
                costContainer.setError("Please enter repair cost");
            }
            else {
                costContainer.setError(null);
                validCost = true;
            }

            if(validCost && validDescription) {
                carDamageInfo = new CarDamageInfoModel(descriptionText.getText().toString(), Integer.parseInt(costText.getText().toString()), "notfinished");

                carDamageInfoList.add(carDamageInfo);

                descriptionText.getText().clear();
                costText.getText().clear();

                Toast.makeText(getActivity(), "You successfully added the repair!", Toast.LENGTH_LONG).show();
            }
        });

        estimatedTimeButton.setText(new SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Calendar.getInstance().getTime()).toUpperCase());

        estimatedTimeButton.setOnClickListener(v -> datePickerDialog.show());

        submitButton.setOnClickListener(v -> {
            FirebaseDatabase database = FirebaseDatabase.getInstance("https://checkit-cd40f-default-rtdb.europe-west1.firebasedatabase.app/");
            DatabaseReference reference = database.getReference("reparations");

            if(!carDamageInfoList.isEmpty()) {
                DatabaseReference newReference = reference.push();
                String uniqueID = newReference.getKey();
                repairs = new RepairModel(clientPhoneNumber, mechanicPhoneNumber, carInfo, carDamageInfoList, estimatedTimeButton.getText().toString(), "pending", uniqueID);
                newReference.setValue(repairs);

                dialog3.dismiss();
            }
            else {
                Toast.makeText(getActivity(), "You need to add at least one repair!", Toast.LENGTH_LONG).show();
            }
        });

        //Create + show popup window
        dialogBuilder3.setView(popupView);
        dialog3 = dialogBuilder3.create();
        dialog3.show();

        //Close popup window
        closePopupButton3.setOnClickListener(v -> dialog3.dismiss());
    }

    public void classifyImage(Bitmap image) {
        try {
            CarDamageDetection cddModel = CarDamageDetection.newInstance(getActivity().getApplicationContext());

            // Creates inputs for reference.
            TensorBuffer inputFeature0 = TensorBuffer.createFixedSize(new int[]{1, 224, 224, 3}, DataType.FLOAT32);
            ByteBuffer byteBuffer = ByteBuffer.allocateDirect(4 * 224 * 224 * 3);
            byteBuffer.order(ByteOrder.nativeOrder());

            int[] intValues = new int[224 * 224];
            image.getPixels(intValues, 0, image.getWidth(), 0, 0, image.getWidth(), image.getHeight());
            int pixel = 0;

            for (int i = 0; i < 224; i++) {
                for (int j = 0; j < 224; j++) {
                    int val = intValues[pixel++];

                    byteBuffer.putFloat(((val >> 16) & 0xFF) * (1.f / 255));
                    byteBuffer.putFloat(((val >> 8) & 0xFF) * (1.f / 255));
                    byteBuffer.putFloat((val & 0xFF) * (1.f / 255));
                }
            }

            inputFeature0.loadBuffer(byteBuffer);

            // Runs model inference and gets result.
            CarDamageDetection.Outputs outputs = cddModel.process(inputFeature0);
            TensorBuffer outputFeature0 = outputs.getOutputFeature0AsTensorBuffer();

            float[] probabilityArray = outputFeature0.getFloatArray();
            float maxProbability = 0;
            int maxProbabilityPosition = 0;

            for (int i = 0; i < probabilityArray.length; i++) {
                if (probabilityArray[i] > maxProbability) {
                    maxProbability = probabilityArray[i];
                    maxProbabilityPosition = i;
                }
            }

            String[] classes = {"bumper dent", "bumper scratch", "door dent", "door scratch", "broken glass", "broken head lamp", "broken tail lamp"};
            String[] costs = {"100", "200", "300", "400", "500", "600", "700"};
            descriptionText.setText(classes[maxProbabilityPosition]);
            costText.setText(costs[maxProbabilityPosition]);

            // Releases model resources if no longer used.
            cddModel.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == CAMERA_REQUEST) {
                Bitmap image = (Bitmap) data.getExtras().get("data");

                image = Bitmap.createScaledBitmap(image, 224, 224, false);
                classifyImage(image);
            }
            else {
                Uri uri = data.getData();
                Bitmap image = null;
                try {
                    image = MediaStore.Images.Media.getBitmap(this.getActivity().getContentResolver(), uri);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                image = Bitmap.createScaledBitmap(image, 224, 224, false);
                classifyImage(image);
            }
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
            String[] qrResult = result.getContents().split(";");

            carModelContainer.getEditText().setText(qrResult[0]);
            plateNumberContainer.getEditText().setText(qrResult[1]);
            transmissionTypeContainer.getEditText().setText(qrResult[2]);
            fuelTypeContainer.getEditText().setText(qrResult[3]);
            manufactureYearContainer.getEditText().setText(qrResult[4]);
        }
    });

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void callback(int position, ArrayList<HomeDynamicRvModel> items) {
        rvDynamicAdapterMechanic = new RvDynamicAdapterMechanic(getContext(), items, position);
        rvDynamicAdapterMechanic.notifyDataSetChanged();
        dynamicRecyclerView.setAdapter(rvDynamicAdapterMechanic);

        ImageView thumbsUpImage = view.findViewById(R.id.thumbs_up_image);
        TextView upToDateText = view.findViewById(R.id.up_to_date_text);

        if(items.isEmpty()) {
            thumbsUpImage.setVisibility(View.VISIBLE);
            upToDateText.setVisibility(View.VISIBLE);
        }
        else {
            thumbsUpImage.setVisibility(View.GONE);
            upToDateText.setVisibility(View.GONE);
        }
    }
}