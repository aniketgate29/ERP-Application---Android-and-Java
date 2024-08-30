package com.example.bottomnavigationdemo;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class MastersFragment extends Fragment {

    public MastersFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_masters, container, false);

        // Initialize buttons
        Button addUnitButton = rootView.findViewById(R.id.addUnitButton);
        Button addTypeButton = rootView.findViewById(R.id.addTypeButton);
        Button addEmployeeButton = rootView.findViewById(R.id.addEmployeeButton);
        Button createLedgerButton = rootView.findViewById(R.id.createLedgerButton);
        Button addProductButton = rootView.findViewById(R.id.addProductButton);
        Button addServiceButton = rootView.findViewById(R.id.addServiceButton);
        Button addCropButton = rootView.findViewById(R.id.addCropButton);
        Button addGodownButton = rootView.findViewById(R.id.addGodownButton);
        Button companyMasterButton = rootView.findViewById(R.id.companyMasterButton);
        Button addBatchButton = rootView.findViewById(R.id.addBatchButton);

        // Set click listeners
        addUnitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle Add Unit button click
                // For example: navigate to AddUnitFragment
                Intent intent = new Intent(getActivity(), Unit_View.class);
                startActivity(intent);
            }
        });

        addTypeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle Add Type button click
                // For example: navigate to AddTypeFragment
                Intent intent = new Intent(getActivity(), ProductViewActivity.class);
                startActivity(intent);
            }
        });

        addEmployeeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle Add Employee button click
                // For example: navigate to AddEmployeeFragment
                Intent intent = new Intent(getActivity(), Intent_Main.class);
                startActivity(intent);
            }
        });

        createLedgerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle Create Ledger button click
                // For example: navigate to CreateLedgerFragment
                Intent intent = new Intent(getActivity(), ledger_View.class);
                startActivity(intent);
            }
        });

        addProductButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle Add Product button click
                // For example: navigate to AddProductFragment
                Intent intent = new Intent(getActivity(), Product_View.class);
                startActivity(intent);
            }
        });

        addServiceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle Add Service button click
                // For example: navigate to AddServiceFragment
                Intent intent = new Intent(getActivity(), ServiceViewActivty.class);
                startActivity(intent);
            }
        });

        addCropButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle Add Crop button click
                // For example: navigate to AddCropFragment
                Intent intent = new Intent(getActivity(), CropViewActivity.class);
                startActivity(intent);
            }
        });

        addGodownButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle Add Godown button click
                // For example: navigate to AddGodownFragment
                Intent intent = new Intent(getActivity(), godown_view.class);
                startActivity(intent);
            }
        });

        companyMasterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle Company Master button click
                // For example: navigate to CompanyMasterFragment
                Intent intent = new Intent(getActivity(), CompanyViewActivity.class);
                startActivity(intent);
            }
        });

        addBatchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle Add Batch button click
                Intent intent = new Intent(getActivity(), Batch_view.class);
                startActivity(intent);
            }
        });

        return rootView;
    }
}
