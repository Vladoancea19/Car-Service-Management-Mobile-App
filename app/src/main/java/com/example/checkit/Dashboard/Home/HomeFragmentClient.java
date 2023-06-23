package com.example.checkit.Dashboard.Home;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.checkit.Models.HomeDynamicRvModel;
import com.example.checkit.Models.MechanicStaticRvModel;
import com.example.checkit.R;
import com.example.checkit.RecyclerView.Dynamic.RvDynamicAdapterClient;
import com.example.checkit.RecyclerView.Interface.RvUpdate;
import com.example.checkit.RecyclerView.Static.RvStaticAdapterClient;

import java.util.ArrayList;

public class HomeFragmentClient extends Fragment implements RvUpdate {

    private RecyclerView dynamicRecyclerView;
    private ArrayList<MechanicStaticRvModel> staticItems = new ArrayList<>();
    private RvDynamicAdapterClient rvDynamicAdapterClient;
    private EditText searchBox;
    private String clientPhoneNumber;
    private View view;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.home_fragment_client, container, false);

        searchBox = view.findViewById(R.id.search_bar_client);

        view.setOnTouchListener((v, event) -> {
            searchBox.clearFocus();
            InputMethodManager imm = (InputMethodManager) requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(searchBox.getWindowToken(), 0);
            return false;
        });

        staticItems.add(new MechanicStaticRvModel(R.drawable.gear_gradient, "In progress"));
        staticItems.add(new MechanicStaticRvModel(R.drawable.check_mark, "Done"));
        staticItems.add(new MechanicStaticRvModel(R.drawable.clock, "Pending"));
        staticItems.add(new MechanicStaticRvModel(R.drawable.cross, "Rejected"));

        RecyclerView staticRecyclerView = view.findViewById(R.id.static_rv_client);
        Intent intent = requireActivity().getIntent();
        clientPhoneNumber = intent.getStringExtra("phoneNumber");
        RvStaticAdapterClient staticRvAdapter = new RvStaticAdapterClient(staticItems, clientPhoneNumber, getActivity(), this);
        staticRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        staticRecyclerView.setAdapter(staticRvAdapter);

        ArrayList<HomeDynamicRvModel> dynamicItems = new ArrayList<>();
        dynamicRecyclerView = view.findViewById(R.id.dynamic_rv_client);
        rvDynamicAdapterClient = new RvDynamicAdapterClient(this.getContext(), dynamicItems, 0);
        dynamicRecyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));
        dynamicRecyclerView.setAdapter(rvDynamicAdapterClient);

        return view;
    }

    @Override
    public void callback(int position, ArrayList<HomeDynamicRvModel> items) {
        searchBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String searchText = s.toString();

                if(!searchText.equals("")) {
                    ArrayList<HomeDynamicRvModel> filteredList = new ArrayList<>();

                    for (HomeDynamicRvModel item : items) {
                        String carModel = item.getCarModel();
                        String plateNumber = item.getPlateNumber();

                        if(carModel != null && carModel.toLowerCase().contains(searchText.toLowerCase())) {
                            filteredList.add(item);
                        }
                        else if(plateNumber != null && plateNumber.toLowerCase().contains(searchText.toLowerCase())) {
                            filteredList.add(item);
                        }
                    }

                    rvDynamicAdapterClient = new RvDynamicAdapterClient(view.getContext(), filteredList, position);
                    rvDynamicAdapterClient.notifyDataSetChanged();
                    dynamicRecyclerView.setAdapter(rvDynamicAdapterClient);

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
                else {
                    rvDynamicAdapterClient = new RvDynamicAdapterClient(view.getContext(), items, position);
                    rvDynamicAdapterClient.notifyDataSetChanged();
                    dynamicRecyclerView.setAdapter(rvDynamicAdapterClient);

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

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        rvDynamicAdapterClient = new RvDynamicAdapterClient(this.getContext(), items, position);
        rvDynamicAdapterClient.notifyDataSetChanged();
        dynamicRecyclerView.setAdapter(rvDynamicAdapterClient);

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