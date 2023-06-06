package com.example.checkit.RecyclerView.Interface;

import com.example.checkit.Models.HomeDynamicRvModel;

import java.util.ArrayList;

public interface RvUpdate {
    void callback(int position, ArrayList<HomeDynamicRvModel> items);
}
