package com.example.bottomnavigationdemo;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CreateLedgerActivity extends AppCompatActivity {

    private EditText ledgerNameBox, selectDateEditText, addrBox, emailBox, mobileNoBox, aadharBox,
            gstBox, openingBalBox, narrationBox, payTermBox, creditLimitBox,panBox ;
    private Spinner underBox, locationBox, custTypeBox, spinnerState, cropNameBox, goDownBox ;
    private Button addBtn, CancelBtn, button;
    private List<Map<String, Object>>  CropsList;
    // Declare RadioGroup and RadioButtons
    private LinearLayout productEntriesContainer1;

    private RadioGroup typeRadioGroup;
    private RadioButton crRadioButton, drRadioButton;




    String selectedState;
    private FirebaseFirestore database;
    CollectionReference ledgerCollection ;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_ledger);
        productEntriesContainer1 = findViewById(R.id.productEntriesContainer1);


        database = FirebaseFirestore.getInstance();
        ledgerCollection = database.collection("ledger");
        // Initialize your EditText fields, Spinners, and Buttons

        selectDateEditText = findViewById(R.id.selectDate);
        panBox = findViewById(R.id.panBox);
        addrBox = findViewById(R.id.addrBox);
        emailBox = findViewById(R.id.emailBox);
        mobileNoBox = findViewById(R.id.mobileNoBox);
        aadharBox = findViewById(R.id.aadharBox);
        gstBox = findViewById(R.id.gstBox);
        openingBalBox = findViewById(R.id.openingBalBox);
        narrationBox = findViewById(R.id.narrationBox);
        payTermBox = findViewById(R.id.payTermBox);
        creditLimitBox = findViewById(R.id.creditLimitBox);
        ledgerNameBox = findViewById(R.id.ledgerNamebox);

        //spinners
        cropNameBox = findViewById(R.id.cropNameBox);
        underBox = findViewById(R.id.underBox);
        custTypeBox = findViewById(R.id.custTypeBox);
        spinnerState = findViewById(R.id.spinnerState);
        goDownBox = findViewById(R.id.goDownBox);


        //button
        addBtn = findViewById(R.id.addBtn);
        CancelBtn = findViewById(R.id.CancelBtn);
        button = findViewById(R.id.button);


        //radio
        // Initialize RadioGroup and RadioButtons
        typeRadioGroup = findViewById(R.id.typeRadioGroup);
        crRadioButton = findViewById(R.id.crRadioButton);
        drRadioButton = findViewById(R.id.drRadioButton);


        // Add listener for radio button selection
        typeRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // Check which radio button is selected
                if (checkedId == R.id.crRadioButton) {
                    // CR radio button is selected
                    // Handle CR logic here
                } else if (checkedId == R.id.drRadioButton) {
                    // DR radio button is selected
                    // Handle DR logic here
                }
            }
        });


        selectDateEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog();
            }

            private void showDatePickerDialog() {

                final Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

                // Set the default date to April 1st
                int defaultYear = 2024; // Set the desired default year
                int defaultMonth = Calendar.APRIL; // April is represented by 3 as it's zero-based
                int defaultDay = 1; // Set the desired default day of the month


                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        CreateLedgerActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                                // Set the selected date in the EditText
                                selectDateEditText.setText(dayOfMonth + "/" + (month + 1) + "/" + year);
                            }
                        },
                        defaultYear, // Pass the default year
                        defaultMonth, // Pass the default month
                        defaultDay // Pass the default day of the month
                );

                datePickerDialog.show();

            }
        });
/*
        // Add a TextWatcher to listen for changes in the ledger name field
        // Add an OnItemSelectedListener to listen for item selection changes in the spinner
        ledgerNameBox.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Get the selected ledger name
                String selectedLedgerName = parent.getItemAtPosition(position).toString();

                // Call a method to fetch suggestions based on the selected ledger name
                fetchSuggestions(selectedLedgerName);
            }

            private void fetchSuggestions(String selectedLedgerName) {
                ledgerCollection
                        .whereGreaterThanOrEqualTo("ledgerName", selectedLedgerName)
                        .whereLessThan("ledgerName", selectedLedgerName + "z")
                        .get()
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                List<String> suggestions = new ArrayList<>();
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    String ledgerName = document.getString("ledgerName");
                                    suggestions.add(ledgerName);
                                }
                                // Show suggestions to the user (you can implement this based on your UI)
                                // For example, you can populate a dropdown or a list with the suggestions
                                showSuggestions(suggestions);
                            } else {
                                // Handle errors
                                Log.e("Firestore", "Error getting documents: ", task.getException());
                            }
                        });
            }

            private void showSuggestions(List<String> suggestions) {
                // Populate a dropdown or update an autocomplete component with the suggestions
                ArrayAdapter<String> adapter = new ArrayAdapter<>(CreateLedgerActivity.this,
                        android.R.layout.simple_dropdown_item_1line, suggestions);
                ledgerNameBox.setAdapter(adapter);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Handle when nothing is selected (optional)
            }
        });
           //

*/

        //for crops
        database.collection("AddCrop")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<String> data = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            // Assuming each document contains a field called "name"
                            String cropName = document.getString("Name");
                            data.add(cropName);
                        }
                        // Populate spinner with data
                        ArrayAdapter<String> adapter = new ArrayAdapter<>(CreateLedgerActivity.this,
                                android.R.layout.simple_spinner_item, data);
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        cropNameBox.setAdapter(adapter);
                    } else {
                        // Handle errors
                    }
                });

                    //for under
        database.collection("under")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<String> data = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            // Assuming each document contains a field called "name"
                            String Name = document.getString("underName");
                            data.add(Name);
                        }
                        // Populate spinner with data
                        ArrayAdapter<String> adapter = new ArrayAdapter<>(CreateLedgerActivity.this,
                                android.R.layout.simple_spinner_item, data);
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        underBox.setAdapter(adapter);
                    } else {
                        // Handle errors
                    }
                });

        //for cust

        database.collection("customers")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<String> data = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            // Assuming each document contains a field called "name"
                            String custName = document.getString("custName");
                            data.add(custName);
                        }
                        // Populate spinner with data
                        ArrayAdapter<String> adapter = new ArrayAdapter<>(CreateLedgerActivity.this,
                                android.R.layout.simple_spinner_item, data);
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        custTypeBox.setAdapter(adapter);
                    } else {
                        // Handle errors
                    }
                });

        //goDown
        database.collection("goDown")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<String> data = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            // Assuming each document contains a field called "name"
                            String name = document.getString("name");
                            data.add(name);
                        }
                        // Populate spinner with data
                        ArrayAdapter<String> adapter = new ArrayAdapter<>(CreateLedgerActivity.this,
                                android.R.layout.simple_spinner_item, data);
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        goDownBox.setAdapter(adapter);
                    } else {
                        // Handle errors
                    }
                });
/*
        //ledgerNAme
        database.collection("ledger")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<String> data = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            // Assuming each document contains a field called "name"
                            String name = document.getString("ledgerName");
                            data.add(name);
                        }
                        // Populate spinner with data
                        ArrayAdapter<String> adapter = new ArrayAdapter<>(CreateLedgerActivity.this,
                                android.R.layout.simple_spinner_item, data);
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        ledgerNameBox.setAdapter(adapter);
                    } else {
                        // Handle errors
                    }
                });
*/
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.state_names,
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerState.setAdapter(adapter);

        spinnerState.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                selectedState = adapterView.getItemAtPosition(position).toString();
                // Do something with the selected state
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                // Do something when nothing is selected
            }
        });
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String selectedCrop = cropNameBox.getSelectedItem().toString();
                addDataCard(selectedCrop);

            }
        });



        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Call a method to handle account creation logic
                add();
            }
        });

        CancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Call a method to handle account creation logic
                cancel();
            }
        }
        );
    }

    private void addDataCard(String name){

        // Log the selected crop to check if it's retrieved correctly
        Log.d("Selected Crop", name);

        // Initialize CropsList if not already initialized
        if (CropsList == null) {
            CropsList = new ArrayList<>();
        }

        View productCardView = getLayoutInflater().inflate(R.layout.unit_card, null);

        TextView nameTextView = productCardView.findViewById(R.id.productNameTextView);

        // Log a message to verify if the card is being added
        Log.d("AddDataCard", "Adding card for crop: " + name);

        Button deleteButton = productCardView.findViewById(R.id.Delete);

        nameTextView.setText(name);

        // Now, add the view to the productEntriesContainer
        productEntriesContainer1.addView(productCardView);
        Map<String, Object> productEntry = new HashMap<>();
        productEntry.put("Crops", name);
        CropsList.add(productEntry);

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Remove the data card view from the layout
                productEntriesContainer1.removeView(productCardView);
                // Remove the item from CropsList
                CropsList.remove(productEntry);
            }
        });


    }


    private void cancel() {
        // Handle cancel button click
        finish();
    }

    private void add(){
        String ledgerName = ledgerNameBox.getText().toString();
        // Check if the ledger name is empty

        if (ledgerName.isEmpty()) {
            showToast("Please enter a ledger name");
            return;
        }

        // Check if the ledger name already exists in Firestore
        database.collection("ledger")
                .whereEqualTo("ledgerName", ledgerName)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (!task.getResult().isEmpty()) {
                            // Ledger name already exists, show error message
                            showToast("Ledger name already exists. Please choose a different name.");
                        } else {
                            // Ledger name is unique, proceed with adding data to Firestore
                            addLedgerData();
                        }
                    } else {
                        // Error occurred while checking ledger name existence
                        showToast("Error occurred while checking ledger name. Please try again.");
                    }
                });


    }

    private void addLedgerData() {
        String ledgerName = ledgerNameBox.getText().toString();
        String date = selectDateEditText.getText().toString().trim();
        String address = addrBox.getText().toString();
        String email = emailBox.getText().toString();
        String mobileNo = mobileNoBox.getText().toString();
        String aadhar = aadharBox.getText().toString();
        String pan = panBox.getText().toString();
        String gst = gstBox.getText().toString();
        String openingBalance = openingBalBox.getText().toString();
        String narration = narrationBox.getText().toString();
        String payTerm = payTermBox.getText().toString();
        String creditLimit = creditLimitBox.getText().toString();

        // Get the selected item from Spinners

        String selectedUnder = underBox.getSelectedItem().toString();
        String selectedCustomerType = custTypeBox.getSelectedItem().toString();
        String selectedState = spinnerState.getSelectedItem().toString();
        String selctedGoDown = goDownBox.getSelectedItem().toString();






        // Get the selected radio button text (CR or DR)
        String selectedType = "";
        int selectedRadioButtonId = typeRadioGroup.getCheckedRadioButtonId();
        if (selectedRadioButtonId != -1) {
            RadioButton selectedRadioButton = findViewById(selectedRadioButtonId);
            selectedType = selectedRadioButton.getText().toString();
        }

        // Check if any field is empty
        if (    address.isEmpty() ||
                email.isEmpty() ||
                mobileNo.isEmpty() ||
                aadhar.isEmpty() ||
                gst.isEmpty()||
                date.isEmpty()||
                pan.isEmpty()||
                openingBalance.isEmpty()||
                selectedUnder.isEmpty()||
                selctedGoDown.isEmpty()||
                selectedType.isEmpty())
        {
            showToast("Please fill all fields");
            return;
        }
        // Create a data object to store in Firestore
        Map<String, Object> data = new HashMap<>();
        data.put("ledgerName", ledgerName);
        data.put("date", date);
        data.put("state", selectedState);
        data.put("address", address);
        data.put("email", email);
        data.put("mobileNo", mobileNo);
        data.put("aadhar", aadhar);
        data.put("pan", pan);
        data.put("gst", gst);
        data.put("openingBalance", openingBalance);
        data.put("narration", narration);
        data.put("payTerm", payTerm);
        data.put("creditLimit", creditLimit);
        data.put("Cropslist", CropsList);
        data.put("selectedUnder", selectedUnder);
        data.put("selectedCustomerType", selectedCustomerType);
        data.put("selctedGoDown", selctedGoDown);
        data.put("selectedType", selectedType);

        // Add data to Firestore
        database.collection("ledger")
                .add(data)
                .addOnSuccessListener(documentReference -> {
                    Log.d("Firestore", "DocumentSnapshot added with ID: " + documentReference.getId());
                    // Clear EditText fields after successful addition
                    clearEditTextFields();
                })
                .addOnFailureListener(e -> Log.w("Firestore", "Error adding document", e));



    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    // Method to clear EditText fields
    private void clearEditTextFields() {
        ledgerNameBox.setText("");
        selectDateEditText.setText("");
        addrBox.setText("");
        emailBox.setText("");
        mobileNoBox.setText("");
        aadharBox.setText("");
        panBox.setText("");
        gstBox.setText("");
        openingBalBox.setText("");
        narrationBox.setText("");
        payTermBox.setText("");
        creditLimitBox.setText("");
    }



}