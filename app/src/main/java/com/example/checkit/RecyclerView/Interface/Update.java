package com.example.checkit.RecyclerView.Interface;

import com.example.checkit.Models.MechanicDynamicRvModel;

import java.util.ArrayList;

public interface Update {
    void callback(int position, ArrayList<MechanicDynamicRvModel> items);
}
