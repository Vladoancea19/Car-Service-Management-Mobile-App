package com.example.checkit.RecyclerView.Dynamic;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.checkit.Models.CarsDynamicRvModel;
import com.example.checkit.Models.HomeDynamicRvModel;
import com.example.checkit.R;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.util.ArrayList;

public class RvDynamicAdapterClientCars extends RecyclerView.Adapter<RvDynamicAdapterClientCars.RvDynamicViewHolderClientCars> {

    public ArrayList<CarsDynamicRvModel> carsDynamicRvModels;
    public View view;

    public RvDynamicAdapterClientCars(ArrayList<CarsDynamicRvModel> carsDynamicRvModels) {
        this.carsDynamicRvModels = carsDynamicRvModels;
    }

    public class RvDynamicViewHolderClientCars extends RecyclerView.ViewHolder {
        public TextView plateNumber, carModel, manufactureYear, fuelType, transmissionType;
        public ImageView qrCode;
        MaterialButton deleteButton;

        public RvDynamicViewHolderClientCars(@NonNull View itemView) {
            super(itemView);

            plateNumber = itemView.findViewById(R.id.plate_number_container);
            carModel = itemView.findViewById(R.id.car_model_container);
            manufactureYear = itemView.findViewById(R.id.manufacture_year_container);
            fuelType = itemView.findViewById(R.id.fuel_type_container);
            transmissionType = itemView.findViewById(R.id.transmission_type_container);
            qrCode = itemView.findViewById(R.id.qr_code_image);
            deleteButton = itemView.findViewById(R.id.delete_button);
        }
    }

    @NonNull
    @Override
    public RvDynamicViewHolderClientCars onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_dynamic_item_cars, parent, false);
        return new RvDynamicAdapterClientCars.RvDynamicViewHolderClientCars(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RvDynamicViewHolderClientCars holder, int position) {
        CarsDynamicRvModel items = carsDynamicRvModels.get(position);

        holder.carModel.setText(items.getCarModel());
        holder.plateNumber.setText(items.getPlateNumber());
        holder.transmissionType.setText(items.getTransmissionType());
        holder.fuelType.setText(items.getFuelType());
        holder.manufactureYear.setText(items.getManufactureYear());

        holder.deleteButton.setOnClickListener(v -> {
            DatabaseReference clientReference = FirebaseDatabase.getInstance("https://checkit-cd40f-default-rtdb.europe-west1.firebasedatabase.app/").getReference("my_cars");

            clientReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@androidx.annotation.NonNull DataSnapshot snapshot) {
                    for (DataSnapshot myCarsSnapshot : snapshot.getChildren()) {
                        String plateNum = myCarsSnapshot.child("plateNumber").getValue(String.class);

                        if(holder.plateNumber.getText().equals(plateNum)) {
                            myCarsSnapshot.getRef().removeValue();
                        }
                    }
                }

                @Override
                public void onCancelled(@androidx.annotation.NonNull DatabaseError error) {

                }
            });
        });

        String carInfo = holder.carModel.getText().toString() +
                ";" + holder.plateNumber.getText().toString() +
                ";" + holder.transmissionType.getText().toString() +
                ";" + holder.fuelType.getText().toString() +
                ";" + holder.manufactureYear.getText().toString();

        MultiFormatWriter writer = new MultiFormatWriter();

        try {
            BitMatrix matrix = writer.encode(carInfo, BarcodeFormat.QR_CODE, 150, 150);
            BarcodeEncoder encoder = new BarcodeEncoder();
            Bitmap bitmap = encoder.createBitmap(matrix);

            holder.qrCode.setImageBitmap(bitmap);
        } catch (WriterException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return carsDynamicRvModels.size();
    }
}
