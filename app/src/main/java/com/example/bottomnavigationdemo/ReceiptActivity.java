package com.example.bottomnavigationdemo;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class ReceiptActivity extends AppCompatActivity {

    private String pdfFilePath;

    // Request code for external storage permission
    private static final int STORAGE_PERMISSION_REQUEST_CODE = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receipt);

        // Retrieve payment details from intent extras
        Intent intent = getIntent();
        String customerName = intent.getStringExtra("customerName");
        String transactionDate = intent.getStringExtra("transactionDate");
        String paymentMode = intent.getStringExtra("paymentMode");
        double amount = intent.getDoubleExtra("amount", 0.0);

        // Set payment details to text views
        TextView textViewCustomerName = findViewById(R.id.textViewCustomerName);
        TextView textViewTransactionDate = findViewById(R.id.textViewTransactionDate);
        TextView textViewPaymentMode = findViewById(R.id.textViewPaymentMode);
        TextView textViewAmount = findViewById(R.id.textViewAmount);

        // Populate text views with payment details
        textViewCustomerName.setText("Customer Name: " + customerName);
        textViewTransactionDate.setText("Transaction Date: " + transactionDate);
        textViewPaymentMode.setText("Payment Mode: " + paymentMode);
        textViewAmount.setText("Amount: " + amount);

        // Generate PDF file path
        pdfFilePath = getExternalFilesDir(null) + "/receipt.pdf";

        // Set up download button click listener
        Button downloadButton = findViewById(R.id.downloadButton);
        downloadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Check if permission to write to external storage is granted
                if (ContextCompat.checkSelfPermission(ReceiptActivity.this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    downloadReceipt();
                } else {
                    // Request permission
                    ActivityCompat.requestPermissions(ReceiptActivity.this,
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            STORAGE_PERMISSION_REQUEST_CODE);
                }
            }
        });
    }

    // Handle permission request result
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == STORAGE_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, download receipt
                downloadReceipt();
            } else {
                // Permission denied, show a toast message
                Toast.makeText(this, "Permission denied. Cannot download receipt.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void downloadReceipt() {
        // Check if PDF file exists
        File file = new File(pdfFilePath);
        if (!file.exists()) {
            // PDF file does not exist, show error message
            Toast.makeText(this, "Error: Receipt not found", Toast.LENGTH_SHORT).show();
            return;
        }

        // Save the file to external storage
        File destFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "receipt.pdf");
        try {
            // Copy file to destination
            FileInputStream fis = new FileInputStream(file);
            FileOutputStream fos = new FileOutputStream(destFile);
            byte[] buffer = new byte[1024];
            int length;
            while ((length = fis.read(buffer)) > 0) {
                fos.write(buffer, 0, length);
            }
            fos.flush();
            fos.close();
            fis.close();
            // Show success message
            Toast.makeText(this, "Receipt downloaded successfully", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
            // Show error message if download fails
            Toast.makeText(this, "Error downloading receipt", Toast.LENGTH_SHORT).show();
        }
    }
}
