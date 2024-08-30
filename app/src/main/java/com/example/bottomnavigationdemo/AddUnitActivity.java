package com.example.bottomnavigationdemo;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class AddUnitActivity extends AppCompatActivity {


    private EditText ed1, ed2;
    private Button btnAdd, btnCancel;
    private FirebaseFirestore database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_unit);

        ed1 = findViewById(R.id.ed1);
        ed2 = findViewById(R.id.ed2);
        btnAdd = findViewById(R.id.btnAdd);
        btnCancel = findViewById(R.id.btnCancel1);

        database = FirebaseFirestore.getInstance();


        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                add();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Clear input fields when the "Cancel" button is clicked
                ed1.setText("");
                ed2.setText("");
                // Finish the current activity to go back to the previous one
                finish();
            }
        });
    }


    private void add() {
        String Name = ed1.getText().toString();
        String discription = ed2.getText().toString();

        Map<String, Object> data = new HashMap<>();
        data.put("Name", Name);
        data.put("discription", discription);

        database.collection("AddUnit")
                .add(data)
                .addOnSuccessListener(documentReference -> {
                    Log.d("Firestore", "DocumentSnapshot added with ID: " + documentReference.getId());
                    // Clear EditText fields after successful addition
                   // clearEditTexts();
                })
                .addOnFailureListener(e -> Log.w("Firestore", "Error adding document", e));

        clearEditTexts();
    }

    private void clearEditTexts() {
        ed1.setText("");
        ed2.setText("");
}

    }
