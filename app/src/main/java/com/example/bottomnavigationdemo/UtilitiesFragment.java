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

public class UtilitiesFragment extends Fragment {

    public UtilitiesFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_utilities, container, false);

        // Initialize buttons
        Button chalanInButton = rootView.findViewById(R.id.chalanInButton);
        Button chalanOutButton = rootView.findViewById(R.id.chalanOutButton);
        Button poInButton = rootView.findViewById(R.id.poInButton);
        Button poOutButton = rootView.findViewById(R.id.poOutButton);
        Button quotationButton = rootView.findViewById(R.id.quotationButton);

        // Set click listeners
        chalanInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle Chalan In button click
                Intent intent = new Intent(getActivity(), ChallanIn_View.class);
                startActivity(intent);
            }
        });

        chalanOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle Chalan Out button click
                Intent intent = new Intent(getActivity(), Challanout_view.class);
                startActivity(intent);
            }
        });

        poInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle P.O In button click
                Intent intent = new Intent(getActivity(), purchaseorderInView.class);
                startActivity(intent);
            }
        });

        poOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle P.O Out button click
                Intent intent = new Intent(getActivity(), purchaseorderoutView.class);
                startActivity(intent);
            }
        });

        quotationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle Quotation button click
                Intent intent = new Intent(getActivity(), Quotation_Main.class);
                startActivity(intent);
            }
        });

        return rootView;
    }
}
