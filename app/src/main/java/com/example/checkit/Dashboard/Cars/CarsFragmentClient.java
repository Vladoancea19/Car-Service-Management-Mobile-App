package com.example.checkit.Dashboard.Cars;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.checkit.Login.LoginActivity;
import com.example.checkit.Models.CarInfoModel;
import com.example.checkit.Models.CarsDynamicRvModel;
import com.example.checkit.Models.ClientUserModel;
import com.example.checkit.Models.MyCarInfoModel;
import com.example.checkit.R;
import com.example.checkit.RecyclerView.Dynamic.RvDynamicAdapterClientCars;
import com.example.checkit.RecyclerView.Interface.RvUpdateCars;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Objects;

public class CarsFragmentClient extends Fragment implements RvUpdateCars {

    private RecyclerView dynamicRecyclerView;
    private RvDynamicAdapterClientCars rvDynamicAdapterClientCars;
    private String clientPhoneNumber;
    private View view;
    private AlertDialog dialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.cars_fragment_client, container, false);

        // Elements to variables
        MaterialButton addNewCarButton = view.findViewById(R.id.add_new_car_button);

        Intent intent = requireActivity().getIntent();
        clientPhoneNumber = intent.getStringExtra("phoneNumber");

        // Managing buttons actions
        addNewCarButton.setOnClickListener(v -> addNewCar());

        DatabaseReference clientReference = FirebaseDatabase.getInstance("https://checkit-cd40f-default-rtdb.europe-west1.firebasedatabase.app/").getReference("my_cars");
        RvUpdateCars rvUpdate = this;

        clientReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<CarsDynamicRvModel> dynamicItems = new ArrayList<>();

                for(DataSnapshot reparationSnapshot : snapshot.getChildren()) {

                    String clientPhoneNumberDb = reparationSnapshot.child("clientPhoneNumber").getValue(String.class);

                    if(clientPhoneNumberDb.equals(clientPhoneNumber)) {
                        String plateNumber = reparationSnapshot.child("plateNumber").getValue(String.class);
                        String carModel = reparationSnapshot.child("carModel").getValue(String.class);
                        String manufactureYear = reparationSnapshot.child("manufactureYear").getValue(String.class);
                        String fuelType = reparationSnapshot.child("fuelType").getValue(String.class);
                        String transmissionType = reparationSnapshot.child("transmissionType").getValue(String.class);

                        dynamicItems.add(new CarsDynamicRvModel(plateNumber, carModel, manufactureYear, fuelType, transmissionType));
                        rvUpdate.callbackCars(dynamicItems);
                    }
                }
                rvUpdate.callbackCars(dynamicItems);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        ArrayList<CarsDynamicRvModel> dynamicItems2 = new ArrayList<>();
        dynamicRecyclerView = view.findViewById(R.id.dynamic_rv_cars);
        rvDynamicAdapterClientCars = new RvDynamicAdapterClientCars(dynamicItems2);
        dynamicRecyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));
        dynamicRecyclerView.setAdapter(rvDynamicAdapterClientCars);

        return view;
    }

    private void addNewCar() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
        final View popupView = getLayoutInflater().inflate(R.layout.add_new_car, null);

        //Elements to variables
        Button closePopupButton = popupView.findViewById(R.id.close_popup_button);
        TextInputLayout plateNumberContainer = popupView.findViewById(R.id.plate_number_container);
        TextInputLayout carModelContainer = popupView.findViewById(R.id.car_model_container);
        TextInputLayout manufactureYearContainer = popupView.findViewById(R.id.manufacture_year_container);
        TextInputLayout transmissionTypeContainer = popupView.findViewById(R.id.transmission_type_container);
        TextInputLayout fuelTypeContainer = popupView.findViewById(R.id.fuel_type_container);
        MaterialButton submitButton = popupView.findViewById(R.id.submit_button);

        //Managing buttons actions
        submitButton.setOnClickListener(v -> {
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
                //Setting database reference
                FirebaseDatabase database = FirebaseDatabase.getInstance("https://checkit-cd40f-default-rtdb.europe-west1.firebasedatabase.app/");
                DatabaseReference reference = database.getReference("my_cars");

                //Save input into database
                DatabaseReference newReference = reference.push();

                MyCarInfoModel myCarInfoModel = new MyCarInfoModel(plateNumberContainer.getEditText().getText().toString(), carModelContainer.getEditText().getText().toString(),
                        manufactureYearContainer.getEditText().getText().toString(), fuelTypeContainer.getEditText().getText().toString(),
                        transmissionTypeContainer.getEditText().getText().toString(), clientPhoneNumber);

                newReference.setValue(myCarInfoModel);

                //Pop-up after successfully adding a car
                Toast.makeText(getActivity(), "Car successfully added!", Toast.LENGTH_LONG).show();

                dialog.dismiss();
            }
        });

        //Create + show popup window
        dialogBuilder.setView(popupView);
        dialog = dialogBuilder.create();
        dialog.show();

        //Close popup window
        closePopupButton.setOnClickListener(v -> dialog.dismiss());
    }

    @Override
    public void callbackCars(ArrayList<CarsDynamicRvModel> items) {
        rvDynamicAdapterClientCars = new RvDynamicAdapterClientCars(items);
        rvDynamicAdapterClientCars.notifyDataSetChanged();
        dynamicRecyclerView.setAdapter(rvDynamicAdapterClientCars);
    }
}