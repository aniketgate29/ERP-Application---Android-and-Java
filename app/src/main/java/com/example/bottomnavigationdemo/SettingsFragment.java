package com.example.bottomnavigationdemo;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Switch;
import android.widget.Toast;
import androidx.fragment.app.Fragment;

public class SettingsFragment extends Fragment {

    private Switch notificationSwitch;
    private Switch darkModeSwitch;
    private Button saveButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        // Find views by their IDs
        notificationSwitch = view.findViewById(R.id.notification_switch);
        darkModeSwitch = view.findViewById(R.id.dark_mode_switch);
        saveButton = view.findViewById(R.id.save_button);

        // Set onClickListener for saveButton
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveSettings();
            }
        });

        return view;
    }

    private void saveSettings() {
        boolean notificationsEnabled = notificationSwitch.isChecked();
        boolean darkModeEnabled = darkModeSwitch.isChecked();

        // Here you can implement logic to save settings to SharedPreferences or any other storage mechanism
        // For demonstration purposes, just showing a Toast message
        String message = "Settings Saved:\nNotifications: " + (notificationsEnabled ? "Enabled" : "Disabled")
                + "\nDark Mode: " + (darkModeEnabled ? "Enabled" : "Disabled");
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }
}
