package com.example.bottomnavigationdemo;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProdcRegActivity extends AppCompatActivity {

    private EditText editTextProductId;
    private Spinner spinnerProductType;
    private EditText editTextDefaultBarcode;
    private EditText editTextSystemBarcode;
    private EditText spinnerProductName;
    private EditText spinnerCompany;
    private Spinner spinnerUnit;
    private EditText editTextSize;
    private EditText editTextWeight;
    private EditText editTextMRP;
    private Spinner spinnerHSN;
    private Spinner spinnerGSTIN;
    private Spinner spinnerGSTOut;
    private EditText editTextPurchaseRate;
    private EditText editTextMarginRs;
    private EditText editTextMarginPercentage;
    private EditText editTextSellingRate;
    private EditText editTextDiscountPercentage;
    private EditText editTextWithoutTaxMRP;
    private EditText editTextWithTaxMRP;
    private EditText editTextWholesaleRate;
    private EditText editTextOpeningQty;
    private EditText editTextOpeningRate;
    private EditText editTextTotal, textViewProductDescription;

    private Button saveButton;
    private Button cancelButton;

    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prodc_reg);

        // Initialize views
        editTextProductId = findViewById(R.id.editTextProductId);
        editTextDefaultBarcode = findViewById(R.id.editTextDefaultBarcode);
        editTextSystemBarcode = findViewById(R.id.editTextSystemBarcode);
        editTextSize = findViewById(R.id.editTextSize);
        editTextWeight = findViewById(R.id.editTextWeight);
        editTextMRP = findViewById(R.id.editTextMRP);
        editTextPurchaseRate = findViewById(R.id.editTextPurchaseRate);
        editTextMarginRs = findViewById(R.id.editTextMarginRs);
        editTextMarginPercentage = findViewById(R.id.editTextMarginPercentage);
        editTextSellingRate = findViewById(R.id.editTextSellingRate);
        editTextDiscountPercentage = findViewById(R.id.editTextDiscountPercentage);
        editTextWithoutTaxMRP = findViewById(R.id.editTextWithoutTaxMRP);
        editTextWithTaxMRP = findViewById(R.id.editTextWithTaxMRP);
        editTextWholesaleRate = findViewById(R.id.editTextWholesaleRate);
        editTextOpeningQty = findViewById(R.id.stk);
        editTextOpeningRate = findViewById(R.id.rate1);
        editTextTotal = findViewById(R.id.Total);
        textViewProductDescription = findViewById(R.id.proddesc);

        //spinner
        spinnerProductType = findViewById(R.id.producttype);
        spinnerProductName = findViewById(R.id.productname);
        spinnerCompany = findViewById(R.id.spinnerCompany);
        spinnerUnit = findViewById(R.id.spinnerUnit);
        spinnerHSN = findViewById(R.id.hsnsp);
        spinnerGSTIN = findViewById(R.id.gstin);
        spinnerGSTOut = findViewById(R.id.gstout);

        saveButton = findViewById(R.id.buttonSave);
        cancelButton = findViewById(R.id.buttonCancel);

        firestore = FirebaseFirestore.getInstance();

        // Fetch data from Firestore and populate the Spinner
        fetchProductTypes();

        editTextMarginPercentage.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                updateMarginField();
            }
        });

        editTextOpeningQty.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                calculateTotal();
            }
        });

        editTextOpeningRate.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                calculateTotal();
            }
        });

        // Set up onClickListener for saveButton
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveProductToFirestore();
            }
        });

        // Set up onClickListener for cancelButton
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Handle cancel button click
                finish();
            }
        });

    }

    private void updateMarginField() {
        String marginPercentageStr = editTextMarginPercentage.getText().toString();

        if (!marginPercentageStr.isEmpty()) {
            double marginPercentage = Double.parseDouble(marginPercentageStr);
            double marginRs = Double.parseDouble(editTextPurchaseRate.getText().toString()) * marginPercentage / 100.0;
            editTextMarginRs.setText(String.valueOf(marginRs));

            // Update selling price
            updateSellingPrice();
        }
    }

    private void updateSellingPrice() {
        String purchaseRateStr = editTextPurchaseRate.getText().toString();
        String marginPercentageStr = editTextMarginPercentage.getText().toString();
        String marginRsStr = editTextMarginRs.getText().toString();

        if (!purchaseRateStr.isEmpty()) {
            double purchaseRate = Double.parseDouble(purchaseRateStr);
            double marginRate;
            if (!marginPercentageStr.isEmpty()) {
                double marginPercentage = Double.parseDouble(marginPercentageStr);
                marginRate = purchaseRate * (marginPercentage / 100);
            } else if (!marginRsStr.isEmpty()) {
                marginRate = Double.parseDouble(marginRsStr);
            } else {
                // Handle error condition, either margin percentage or margin Rs should be provided
                return;
            }
            double sellingRate = purchaseRate + marginRate;
            editTextSellingRate.setText(String.valueOf(sellingRate));

            // Calculate with tax MRP
            String gstinStr = spinnerGSTIN.getSelectedItem().toString();
            if (!gstinStr.isEmpty()) {
                double gstinValue = Double.parseDouble(gstinStr);
                double withTaxMRP = sellingRate;
                editTextWithTaxMRP.setText(String.valueOf(withTaxMRP));

                // Calculate without tax MRP
                double withoutTaxMRP = withTaxMRP / (1 + (gstinValue / 100));
                editTextWithoutTaxMRP.setText(String.valueOf(withoutTaxMRP));
            }

        }
    }




    private void calculateTotal() {
        String openingQtyStr = editTextOpeningQty.getText().toString();
        String openingRateStr = editTextOpeningRate.getText().toString();

        // Check if both openingQty and openingRate are not empty
        if (!openingQtyStr.isEmpty() && !openingRateStr.isEmpty()) {
            int openingQty = Integer.parseInt(openingQtyStr);
            int openingRate = Integer.parseInt(openingRateStr);
            int total = openingQty * openingRate;
            editTextTotal.setText(String.valueOf(total));
        }
    }

    private void fetchProductTypes() {
        //for product type
        firestore.collection("addType")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<String> data = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            // Assuming each document contains a field called "productType"
                            String productType = document.getString("productType");
                            data.add(productType);
                        }
                        // Populate spinner with data
                        ArrayAdapter<String> adapter = new ArrayAdapter<>(ProdcRegActivity.this,
                                android.R.layout.simple_spinner_item, data);
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinnerProductType.setAdapter(adapter);
                    } else {
                        // Handle errors
                    }
                });




        //spinnerUnit
        firestore.collection("prodRegUnit")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<String> data = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            // Assuming each document contains a field called "name"
                            String Name = document.getString("unit");
                            data.add(Name);
                        }
                        // Populate spinner with data
                        ArrayAdapter<String> adapter = new ArrayAdapter<>(ProdcRegActivity.this,
                                android.R.layout.simple_spinner_item, data);
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinnerUnit.setAdapter(adapter);
                    } else {
                        // Handle errors
                    }
                });

        //spinnerHSN
        firestore.collection("prodRegHSN")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<String> data = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            // Assuming each document contains a field called "name"
                            String Name = document.getString("HSN");
                            data.add(Name);
                        }
                        // Populate spinner with data
                        ArrayAdapter<String> adapter = new ArrayAdapter<>(ProdcRegActivity.this,
                                android.R.layout.simple_spinner_item, data);
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinnerHSN.setAdapter(adapter);
                    } else {
                        // Handle errors
                    }
                });

        //spinnerGSTIN

        firestore.collection("prodRegGSTIN")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<String> data = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            // Assuming each document contains a field called "name"
                            String Name = document.getString("GSTIN");
                            data.add(Name);
                        }
                        // Populate spinner with data
                        String[] gstNumbers = {"0", "5", "12", "18", "28"};
                        ArrayAdapter<String> gstSpinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, gstNumbers);
                        gstSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinnerGSTIN.setAdapter(gstSpinnerAdapter);

                        spinnerGSTIN.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                                // Get the selected GSTIN value
                                String selectedGSTIN = adapterView.getItemAtPosition(i).toString();

                                // Set the selected GSTIN value as the default value for spinnerGSTOut
                                int gstOutPosition = getGSTOutPosition(selectedGSTIN);
                                spinnerGSTOut.setSelection(gstOutPosition);
                            }

                            private int getGSTOutPosition(String selectedGSTIN) {
                                String[] gstNumbers = {"0", "5", "12", "18", "28"};
                            //    String[] gstOutArray = {"GSTOut1", "GSTOut2", "GSTOut3", "GSTOut4", "GSTOut5"}; // Replace with your actual GSTOut values
                                int position = 0;
                                for (int i = 0; i < gstNumbers.length; i++) {
                                    if (gstNumbers[i].equals(selectedGSTIN)) {
                                        position = i;
                                        break;
                                    }
                                }
                                return position;
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> adapterView) {
                                // Handle case when nothing is selected
                            }
                        });



                        // Handle errors
                    }
                });

        //spinnerGSTOut
        firestore.collection("prodRegGSTOut")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<String> data = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            // Assuming each document contains a field called "name"
                            String Name = document.getString("GSTOut");
                            data.add(Name);
                        }
                        // Populate spinner with data
                        String[] gstNumbers = {"0", "5", "12", "18", "28"};
                        ArrayAdapter<String> gstSpinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, gstNumbers);
                        gstSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinnerGSTOut.setAdapter(gstSpinnerAdapter);


                    } else {
                        // Handle errors
                    }
                });





        // Set up onClickListener for saveButton
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveProductToFirestore();
            }
        });

        // Set up onClickListener for cancelButton
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Handle cancel button click
                finish();
            }
        });

    }

    private void saveProductToFirestore() {
        // Retrieve values from EditText fields
        String productId = editTextProductId.getText().toString();
        String productType = spinnerProductType.getSelectedItem().toString();
        String defaultBarcode = editTextDefaultBarcode.getText().toString();
        String systemBarcode = editTextSystemBarcode.getText().toString();
        String productName = spinnerProductName.getText().toString();
        String techdesc =textViewProductDescription.getText().toString();
        String company = spinnerCompany.getText().toString();
        String unit = spinnerUnit.getSelectedItem().toString();
        String size = editTextSize.getText().toString();
        String weight = editTextWeight.getText().toString();
        String mrp = editTextMRP.getText().toString();
        String hsn = spinnerHSN.getSelectedItem().toString();
        String gstin = spinnerGSTIN.getSelectedItem().toString();
        String gstOut = spinnerGSTOut.getSelectedItem().toString();
        String purchaseRate = editTextPurchaseRate.getText().toString();
        String marginRs = editTextMarginRs.getText().toString();
        String marginPercentage = editTextMarginPercentage.getText().toString();
        String sellingRate = editTextSellingRate.getText().toString();
        String discountPercentage = editTextDiscountPercentage.getText().toString();
        String withoutTaxMRP = editTextWithoutTaxMRP.getText().toString();
        String withTaxMRP = editTextWithTaxMRP.getText().toString();
        String wholesaleRate = editTextWholesaleRate.getText().toString();
        String openingQty = editTextOpeningQty.getText().toString();
        String openingRate = editTextOpeningRate.getText().toString();



        // Calculate total based on openingQty and openingRate
        int total = Integer.parseInt(openingQty) * Integer.parseInt(openingRate);

        // Calculate purchase rate based on entered value or calculated total
        double purchaseRateValue;
        if (!purchaseRate.isEmpty()) {
            purchaseRateValue = Double.parseDouble(purchaseRate);
        } else {
            purchaseRateValue = (double) total / Integer.parseInt(openingQty);
        }

        // Calculate selling rate based on margin percentage or entered value
        double margin;
        if (!marginPercentage.isEmpty()) {
            margin = Double.parseDouble(marginPercentage) / 100.0;
        } else {
            margin = Double.parseDouble(marginRs) / purchaseRateValue;
        }
        double sellingRateValue = purchaseRateValue * (1 + margin);

        // Calculate without tax MRP based on selling rate and GSTIN
        double gstinValue = Double.parseDouble(gstin);
        double withoutTaxMRPValue = sellingRateValue / (1 + (gstinValue / 100));

        // Calculate with tax MRP based on selling rate
        double withTaxMRPValue = sellingRateValue;

        Map<String, Object> productData = new HashMap<>();
        productData.put("productId", productId);
        productData.put("productType", productType);
        productData.put("defaultBarcode", defaultBarcode);
        productData.put("systemBarcode", systemBarcode);
        productData.put("productName", productName);
        productData.put("TechDesc", techdesc);
        productData.put("company", company);
        productData.put("unit", unit);
        productData.put("size", size);
        productData.put("weight", weight);
        productData.put("mrp", mrp);
        productData.put("hsn", hsn);
        productData.put("gstin", gstin);
        productData.put("gstOut", gstOut);
        productData.put("purchaseRate", String.valueOf(purchaseRateValue));
        productData.put("marginRs", marginRs);
        productData.put("marginPercentage", marginPercentage);
        productData.put("sellingRate", String.valueOf(sellingRateValue));
        productData.put("discountPercentage", discountPercentage);
        productData.put("withoutTaxMRP", String.valueOf(withoutTaxMRPValue));
        productData.put("withTaxMRP", String.valueOf(withTaxMRPValue));
        productData.put("wholesaleRate", wholesaleRate);
        productData.put("openingQty", openingQty);
        productData.put("openingRate", openingRate);
        productData.put("total", total); // Assign total as an Integer value

        firestore.collection("ProdRegproducts")
                .add(productData)
                .addOnSuccessListener(documentReference -> {
                    Log.d("Firestore", "DocumentSnapshot added with ID: " + documentReference.getId());
                    // Optionally, clear the input fields after successful addition
                    clearEditTextFields();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(ProdcRegActivity.this, "Error adding product: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void clearEditTextFields() {
        editTextProductId.setText("");
        editTextDefaultBarcode.setText("");
        editTextSystemBarcode.setText("");
        editTextSize.setText("");
        editTextWeight.setText("");
        editTextMRP.setText("");
        editTextPurchaseRate.setText("");
        editTextMarginRs.setText("");
        editTextMarginPercentage.setText("");
        editTextSellingRate.setText("");
        editTextDiscountPercentage.setText("");
        editTextWithoutTaxMRP.setText("");
        editTextWithTaxMRP.setText("");
        editTextWholesaleRate.setText("");
        editTextOpeningQty.setText("");
        editTextOpeningRate.setText("");
        editTextTotal.setText("");
        textViewProductDescription.setText("");

    }

}
