package com.example.bottomnavigationdemo;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class LibraryFragment extends Fragment implements View.OnClickListener {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_library, container, false);

        Button mastersButton = rootView.findViewById(R.id.mastersButton);
        Button utilitiesButton = rootView.findViewById(R.id.utilitiesButton);
        Button transactionButton = rootView.findViewById(R.id.transactionButton);
        Button reportButton = rootView.findViewById(R.id.reportButton);

        mastersButton.setOnClickListener(this);
        utilitiesButton.setOnClickListener(this);
        transactionButton.setOnClickListener(this);
        reportButton.setOnClickListener(this);

        return rootView;
    }

    @Override
    public void onClick(View v) {
        Fragment fragment;
        switch (v.getId()) {
            case R.id.mastersButton:
                fragment = new MastersFragment();
                break;
            case R.id.utilitiesButton:
                fragment = new UtilitiesFragment();
                break;
            case R.id.transactionButton:
                fragment = new TransactionFragment();
                break;
            case R.id.reportButton:
                fragment = new ReportFragment();
                break;
            default:
                return;
        }
        getParentFragmentManager().beginTransaction()
                .replace(R.id.main_layout, fragment)
                .addToBackStack(null)
                .commit();
    }
}
