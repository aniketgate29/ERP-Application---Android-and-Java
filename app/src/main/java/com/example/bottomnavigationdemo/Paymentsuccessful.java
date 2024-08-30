package com.example.bottomnavigationdemo;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;

import java.io.IOException;

public class Paymentsuccessful extends AppCompatActivity {

    private Button buttonGenerateReceipt;

    // Constants for Intent extras
    public static final String CUSTOMER_NAME_EXTRA = "customerName";
    public static final String TRANSACTION_DATE_EXTRA = "transactionDate";
    public static final String PAYMENT_MODE_EXTRA = "paymentMode";
    public static final String AMOUNT_EXTRA = "amount";

    // Request code for external storage permission
    private static final int STORAGE_PERMISSION_REQUEST_CODE = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paymentsuccessful);

        // Initialize views
        TextView textViewPaymentSuccessful = findViewById(R.id.textViewPaymentSuccessful);
        buttonGenerateReceipt = findViewById(R.id.buttonGenerateReceipt);

        // Set click listener for Generate Receipt button
        buttonGenerateReceipt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Check if permission to write to external storage is granted
                if (ContextCompat.checkSelfPermission(Paymentsuccessful.this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    generateReceipt();
                } else {
                    // Request permission
                    ActivityCompat.requestPermissions(Paymentsuccessful.this,
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            STORAGE_PERMISSION_REQUEST_CODE);
                }
            }
        });
    }

    // Handle permission request result
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult (requestCode, permissions, grantResults);
        if (requestCode == STORAGE_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, generate receipt
                generateReceipt ();
            } else {
                // Permission denied, show a toast message
                Toast.makeText (this, "Permission denied. Cannot generate receipt.", Toast.LENGTH_SHORT).show ();
            }
        }
    }

    private void generateReceipt() {
        // Retrieve payment details passed from the previous activity
        String customerName = getIntent().getStringExtra(CUSTOMER_NAME_EXTRA);
        String transactionDate = getIntent().getStringExtra(TRANSACTION_DATE_EXTRA);
        String paymentMode = getIntent().getStringExtra(PAYMENT_MODE_EXTRA);
        double amount = getIntent().getDoubleExtra(AMOUNT_EXTRA, 0.0);

        try {
            // Create a new PDF document
            String pdfFilePath = getExternalFilesDir(null) + "/receipt.pdf";
            Log.d("/storage/emulated/0/Download ", "pdf" + pdfFilePath); // Log file path for debugging
            PdfWriter writer = new PdfWriter(pdfFilePath);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);

            // Add content to the PDF document
            document.add(new Paragraph("Payment Receipt"));
            document.add(new Paragraph("Customer Name: " + customerName));
            document.add(new Paragraph("Transaction Date: " + transactionDate));
            document.add(new Paragraph("Payment Mode: " + paymentMode));
            document.add(new Paragraph("Amount: $" + amount));

            // Close the document
            document.close();

            // Display a toast message indicating successful generation of receipt
            Toast.makeText(Paymentsuccessful.this, "Receipt generated successfully", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            // Handle any errors that occur during PDF generation
            e.printStackTrace();
            Toast.makeText(Paymentsuccessful.this, "Error generating receipt", Toast.LENGTH_SHORT).show();
        }
    }
}
