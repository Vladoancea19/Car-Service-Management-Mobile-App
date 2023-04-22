package com.example.checkit.Dashboard.Home;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.checkit.RecyclerView.Dynamic.RvDynamicAdapterClient;
import com.example.checkit.RecyclerView.Static.RvStaticAdapterClient;
import com.example.checkit.Models.MechanicDynamicRvModel;
import com.example.checkit.R;
import com.example.checkit.Models.MechanicStaticRvModel;
import com.example.checkit.RecyclerView.Interface.Update;

import java.util.ArrayList;

public class HomeFragmentClient extends Fragment implements Update {

    private RecyclerView dynamicRecyclerView;
    private ArrayList<MechanicStaticRvModel> staticItems = new ArrayList<>();
    private RvDynamicAdapterClient dynamicRVAdapter;
    private String clientPhoneNumber;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.home_fragment_client, container, false);

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

        ArrayList<MechanicDynamicRvModel> dynamicItems = new ArrayList<>();
        dynamicRecyclerView = view.findViewById(R.id.dynamic_rv_client);
        dynamicRVAdapter = new RvDynamicAdapterClient(dynamicItems, 0);
        dynamicRecyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));
        dynamicRecyclerView.setAdapter(dynamicRVAdapter);

        return view;
    }

    @Override
    public void callback(int position, ArrayList<MechanicDynamicRvModel> items) {
        dynamicRVAdapter = new RvDynamicAdapterClient(items, position);
        dynamicRVAdapter.notifyDataSetChanged();
        dynamicRecyclerView.setAdapter(dynamicRVAdapter);
    }
}