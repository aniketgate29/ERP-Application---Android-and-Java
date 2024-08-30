package com.example.bottomnavigationdemo;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class ReportFragment extends Fragment {

    public ReportFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_report, container, false);

        // Initialize buttons
        Button productReportButton = rootView.findViewById(R.id.productReportButton);
        Button salesReportButton = rootView.findViewById(R.id.salesReportButton);
        Button purchaseReportButton = rootView.findViewById(R.id.purchaseReportButton);
        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) Button challanInReportButton = rootView.findViewById(R.id.challanInReportButton);
        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) Button challanOutReportButton = rootView.findViewById(R.id.challanOutReportButton);

        // Set click listeners
        productReportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle Product Report button click
                Intent intent = new Intent(getActivity(), ProductReportActivity.class);
                startActivity(intent);
            }
        });

        salesReportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle Sales Report button click
                Intent intent = new Intent(getActivity(), SaleReportActivity.class);
                startActivity(intent);
            }
        });

        purchaseReportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle Purchase Report button click
                Intent intent = new Intent(getActivity(), ParchesReport.class);
                startActivity(intent);
            }
        });

        challanInReportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle Purchase Report button click
                Intent intent = new Intent(getActivity(), ChallanInReportActivity.class);
                startActivity(intent);
            }
        });

        challanOutReportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle Purchase Report button click
                Intent intent = new Intent(getActivity(), ChallanOutReportActivity.class);
                startActivity(intent);
            }
        });

        return rootView;
    }
}

















































