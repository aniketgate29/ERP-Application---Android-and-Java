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

public class TransactionFragment extends Fragment {

    public TransactionFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_transaction, container, false);

        // Initialize buttons
        Button invoiceButton = rootView.findViewById(R.id.invoiceButton);
        Button salesReturnButton = rootView.findViewById(R.id.salesReturnButton);
        Button purchaseButton = rootView.findViewById(R.id.purchaseButton);
        Button purchaseChalanButton = rootView.findViewById(R.id.purchaseChalanButton);

        Button purchaseReturnButton = rootView.findViewById(R.id.purchaseReturnButton);
        Button paymentButton = rootView.findViewById(R.id.paymentButton);
        Button receiptButton = rootView.findViewById(R.id.receiptButton);

        // Set click listeners
        invoiceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle Invoice button click
                Intent intent = new Intent(getActivity(), Invoice_View.class);
                startActivity(intent);

            }
        });

        salesReturnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle Sales Return button click
                Intent intent = new Intent(getActivity(), sales_return_view.class);
                startActivity(intent);
            }
        });

        purchaseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle Purchase button click
                Intent intent = new Intent(getActivity(), Parches_Entry_View.class);
                startActivity(intent);
            }
        });

        purchaseChalanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle Purchase Chalan button click
            }
        });



        purchaseReturnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle Purchase Return button click
                Intent intent = new Intent(getActivity(), purchasereturnView.class);
                startActivity(intent);
            }
        });

        paymentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), PaymentActivity.class);
                startActivity(intent);
            }
        });

        receiptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle Receipt button click
            }
        });

        return rootView;
    }
}
