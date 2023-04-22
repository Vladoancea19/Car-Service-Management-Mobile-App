package com.example.checkit.Dashboard.Home;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;

import androidx.activity.result.ActivityResultLauncher;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.checkit.CaptureAct;
import com.example.checkit.RecyclerView.Dynamic.RvDynamicAdapterMechanic;
import com.example.checkit.Models.CarDamageInfoModel;
import com.example.checkit.Models.CarInfoModel;
import com.example.checkit.Models.MechanicDynamicRvModel;
import com.example.checkit.Models.MechanicStaticRvModel;
import com.example.checkit.Models.RepairModel;
import com.example.checkit.R;
import com.example.checkit.RecyclerView.Static.RvStaticAdapterMechanic;
import com.example.checkit.RecyclerView.Interface.Update;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.Objects;

public class HomeFragmentMechanic extends Fragment implements Update {

    private AlertDialog dialog1, dialog2, dialog3;
    private TextInputLayout phoneNumberContainer, fullNameContainer, plateNumberContainer, carModelContainer, manufactureYearContainer, transmissionTypeContainer, fuelTypeContainer;
    private RecyclerView dynamicRecyclerView;
    private ArrayList<MechanicStaticRvModel> staticItems = new ArrayList<>();
    private RvDynamicAdapterMechanic rvDynamicAdapterMechanic;
    private static final int CAMERA_REQUEST = 1888;
    private String mechanicPhoneNumber;
    private String clientPhoneNumber;
    private CarInfoModel carInfo;
    private CarDamageInfoModel carDamageInfo;
    private ArrayList<CarDamageInfoModel> carDamageInfoList;
    private RepairModel repairs;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.home_fragment_mechanic, container, false);

        //Elements to variables
//        ImageButton addNew = view.findViewById(R.id.add_new);

        //Managing buttons actions
//        addNew.setOnClickListener(v -> {
//            carDamageInfoList = new ArrayList<>();
//            clientInfo();
//        });

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

        ArrayList<MechanicDynamicRvModel> dynamicItems = new ArrayList<>();
        dynamicRecyclerView = view.findViewById(R.id.dynamic_rv_mechanic);
        rvDynamicAdapterMechanic = new RvDynamicAdapterMechanic(dynamicItems, 0);
        dynamicRecyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));
        dynamicRecyclerView.setAdapter(rvDynamicAdapterMechanic);

        return view;
    }

    private void clientInfo() {
        AlertDialog.Builder dialogBuilder1 = new AlertDialog.Builder(getActivity());
        final View popupView = getLayoutInflater().inflate(R.layout.add_new_repair_client_info, null);

        //Elements to variables
        Button closePopupButton1 = popupView.findViewById(R.id.close_popup_button_1);
        Button scanQRButton = popupView.findViewById(R.id.scan_qr_button);
        Button nextButton1 = popupView.findViewById(R.id.next_button_1);
        fullNameContainer = popupView.findViewById(R.id.full_name_container);
        phoneNumberContainer = popupView.findViewById(R.id.phone_number_container);

        //Managing buttons actions
        scanQRButton.setOnClickListener(v -> scanQR());
        nextButton1.setOnClickListener(v -> {
            boolean validPhone = false, validName = false;

            String phoneNumberInput = Objects.requireNonNull(Objects.requireNonNull(phoneNumberContainer.getEditText()).getText()).toString();

            if(phoneNumberInput.isEmpty()) {
                phoneNumberContainer.setError("Please enter client's phone number");
            }
            else {
                phoneNumberContainer.setError(null);
                validPhone = true;
            }

            String fullNameInput = Objects.requireNonNull(Objects.requireNonNull(fullNameContainer.getEditText()).getText()).toString();

            if(fullNameInput.isEmpty()) {
                fullNameContainer.setError("Please enter client's name");
            }
            else {
                fullNameContainer.setError(null);
                validName = true;
            }

            if(validName && validPhone) {
                Intent intent = requireActivity().getIntent();
                mechanicPhoneNumber = intent.getStringExtra("phone_number");
                clientPhoneNumber = phoneNumberInput;

                dialog1.dismiss();
                carInfo();
            }
        });

        phoneNumberContainer.setOnClickListener(v -> phoneNumberContainer.setError(null));

        fullNameContainer.setOnClickListener(v -> fullNameContainer.setError(null));

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
        Button nextButton2 = popupView.findViewById(R.id.next_button_2);
        plateNumberContainer = popupView.findViewById(R.id.plate_number_container);
        carModelContainer = popupView.findViewById(R.id.car_model_container);
        manufactureYearContainer = popupView.findViewById(R.id.manufacture_year_container);
        transmissionTypeContainer = popupView.findViewById(R.id.transmission_type_container);
        fuelTypeContainer = popupView.findViewById(R.id.fuel_type_container);

        //Managing buttons actions
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
        Button uploadImageButton = popupView.findViewById(R.id.upload_image_button);
        Button submitButton = popupView.findViewById(R.id.submit_button);
        Button estimatedTimeButton = popupView.findViewById(R.id.estimated_time_button);
        MaterialButton addRepairButton = popupView.findViewById(R.id.add_repair_button);
        MaterialButton removeRepairButton = popupView.findViewById(R.id.remove_repair_button);
        TextInputEditText descriptionText = popupView.findViewById(R.id.description_text);
        TextInputEditText costText = popupView.findViewById(R.id.cost_text);
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

                switch (month) {
                    case 1:
                        formattedMonth = "JAN";
                    case 2:
                        formattedMonth = "FEB";
                    case 3:
                        formattedMonth = "MAR";
                    case 4:
                        formattedMonth = "APR";
                    case 5:
                        formattedMonth = "MAY";
                    case 6:
                        formattedMonth = "JUN";
                    case 7:
                        formattedMonth = "JUL";
                    case 8:
                        formattedMonth = "AUG";
                    case 9:
                        formattedMonth = "SEP";
                    case 10:
                        formattedMonth = "OCT";
                    case 11:
                        formattedMonth = "NOV";
                    case 12:
                        formattedMonth = "DEC";
                    default:
                        formattedMonth = "APR";
                }

                return formattedMonth;
            }
        };

        int day = Calendar.getInstance().get(Calendar.DAY_OF_MONTH), month = Calendar.getInstance().get(Calendar.MONTH), year = Calendar.getInstance().get(Calendar.YEAR), style = AlertDialog.THEME_HOLO_DARK;
        datePickerDialog = new DatePickerDialog(this.getContext(), style, dateSetListener, year, month, day);

        uploadImageButton.setOnClickListener(v -> {
            Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(cameraIntent, CAMERA_REQUEST);
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
                costContainer.setError("Please enter car's model");
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
            }
        });

        estimatedTimeButton.setText(new SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Calendar.getInstance().getTime()).toUpperCase());

        estimatedTimeButton.setOnClickListener(v -> datePickerDialog.show());

        submitButton.setOnClickListener(v -> {
            FirebaseDatabase database = FirebaseDatabase.getInstance("https://checkit-cd40f-default-rtdb.europe-west1.firebasedatabase.app/");
            DatabaseReference reference = database.getReference("reparations");

            repairs = new RepairModel(clientPhoneNumber, mechanicPhoneNumber, carInfo, carDamageInfoList, estimatedTimeButton.getText().toString(), "pending");
            DatabaseReference newReference = reference.push();
            newReference.setValue(repairs);

            dialog3.dismiss();
        });

        //Create + show popup window
        dialogBuilder3.setView(popupView);
        dialog3 = dialogBuilder3.create();
        dialog3.show();

        //Close popup window
        closePopupButton3.setOnClickListener(v -> dialog3.dismiss());
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK) {
            Bitmap photo = (Bitmap) data.getExtras().get("data");
            // TODO: deploy car damage segmentation model
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
            Objects.requireNonNull(phoneNumberContainer.getEditText()).setText(result.getContents());
        }
    });

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void callback(int position, ArrayList<MechanicDynamicRvModel> items) {
        rvDynamicAdapterMechanic = new RvDynamicAdapterMechanic(items, position);
        rvDynamicAdapterMechanic.notifyDataSetChanged();
        dynamicRecyclerView.setAdapter(rvDynamicAdapterMechanic);
    }
}