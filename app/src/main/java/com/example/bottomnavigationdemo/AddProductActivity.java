package com.example.bottomnavigationdemo;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class AddProductActivity extends AppCompatActivity {

    private EditText productNameEditText;
    private Spinner unitSpinner;
    private Button saveButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bottomsheetlayout);

      /*  productNameEditText = findViewById(R.id.editTextProductName);
        unitSpinner = findViewById(R.id.spinnerUnit);
        saveButton = findViewById(R.id.btnSave);

        // Populate the spinner with unit options
        List<String> unitOptions = new ArrayList<>();
        unitOptions.add("Kg");
        unitOptions.add("Liter");
        unitOptions.add("Piece");

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, unitOptions);
        unitSpinner.setAdapter(adapter);

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveProduct();
            }
        });*/
    }

    public void activity_add_product(View view){
        startActivity(new Intent(AddProductActivity.this,HomeFragment.class));
    }
    private void saveProduct() {
        String productName = productNameEditText.getText().toString();
        String selectedUnit = unitSpinner.getSelectedItem().toString();

        // You can perform further validation and save the product data to a database or perform any desired action.

        // For this example, we'll just show a toast message with the entered data.
        String message = "Product Name: " + productName + "\nSelected Unit: " + selectedUnit;
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}

