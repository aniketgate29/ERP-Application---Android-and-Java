package com.example.bottomnavigationdemo;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class EditProfile extends AppCompatActivity {

    EditText editName, editEmail, editUsername, editPassword;
    Button saveButton;
    ImageButton profileImg;

    private Uri imageUri;
    String userId;

    private static final int PICK_IMAGE_REQUEST = 1;

    private FirebaseFirestore database; // Firestore instance

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        database = FirebaseFirestore.getInstance(); // Initialize Firestore

        editName = findViewById(R.id.editName);
        editEmail = findViewById(R.id.editEmail);
        editUsername = findViewById(R.id.editUsername);
        editPassword = findViewById(R.id.editPassword);
        saveButton = findViewById(R.id.saveButton);
        profileImg = findViewById(R.id.profileImg);

        showData();

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateProfile();
            }
        });

        profileImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooser();
            }
        });
    }

    private void showData() {
        Intent intent = getIntent();
        userId = intent.getStringExtra("userId");
        String nameUser = intent.getStringExtra("name");
        String emailUser = intent.getStringExtra("email");
        String usernameUser = intent.getStringExtra("username");
        String passwordUser = intent.getStringExtra("password");

        editName.setText(nameUser);
        editEmail.setText(emailUser);
        editUsername.setText(usernameUser);
        editPassword.setText(passwordUser);

    }

   /* private void updateProfile() throws IOException {
        String newName = editName.getText().toString().trim();
        String newEmail = editEmail.getText().toString().trim();
        String newPassword = editPassword.getText().toString().trim();
        String newUsername = editUsername.getText().toString().trim();

        // Check if an image is selected
        if (imageUri != null) {
            // Get reference to Firebase Storage
            StorageReference storageRef = FirebaseStorage.getInstance().getReference().child("profile_images").child(userId + ".png");

            // Convert image to byte array
            Bitmap bmp = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bmp.compress(Bitmap.CompressFormat.PNG, 100, baos);
            byte[] imageData = baos.toByteArray();

            // Upload the byte array to Firebase Storage
            storageRef.putBytes(imageData)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            // Get the download URL of the uploaded image
                            storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    String imageUrl = uri.toString();

                                    // Update user profile with the new image URL
                                    DocumentReference userRef = database.collection("users").document(userId);
                                    userRef.update("name", newName,
                                                    "email", newEmail,
                                                    "password", newPassword,
                                                    "username", newUsername,
                                                    "Image", imageUrl)
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        Toast.makeText(EditProfile.this, "Profile Updated Successfully", Toast.LENGTH_SHORT).show();
                                                    } else {
                                                        Toast.makeText(EditProfile.this, "Failed to update profile", Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });
                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // Handle failure
                            Toast.makeText(EditProfile.this, "Failed to upload image", Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            // If no image is selected, update only other profile information
            DocumentReference userRef = database.collection("users").document(userId);
            userRef.update("name", newName,
                            "email", newEmail,
                            "password", newPassword,
                            "username", newUsername)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(EditProfile.this, "Profile Updated Successfully", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(EditProfile.this, "Failed to update profile", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }*/
   private void updateProfile() {
       String newName = editName.getText().toString().trim();
       String newEmail = editEmail.getText().toString().trim();
       String newPassword = editPassword.getText().toString().trim();
       String newUsername = editUsername.getText().toString().trim();

       // Check if an image is selected
       if (imageUri != null) {
           // Upload the image to Firebase Storage
           uploadImageToFirebaseStorage(newName, newEmail, newPassword, newUsername);
       } else {
           // No image selected, update the profile without an image
           updateUserProfile(newName, newEmail, newPassword, newUsername, null);
       }
   }

    private void uploadImageToFirebaseStorage(final String newName, final String newEmail, final String newPassword, final String newUsername)
    {
        // Get a reference to the Firebase Storage
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();

        // Set the image file name in Firebase Storage (e.g., using the user's ID)
        final StorageReference imageRef = storageRef.child("profile_Image/" + userId + ".JPG");

        // Upload the image file to Firebase Storage
        UploadTask uploadTask = imageRef.putFile(imageUri);
        uploadTask.addOnSuccessListener(taskSnapshot -> {
            // Image upload successful, get the download URL of the uploaded image
            imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                // Update the user's profile with the image URL
                updateUserProfile(newName, newEmail, newPassword, newUsername, uri.toString());
            }).addOnFailureListener(e -> {
                // Failed to get image download URL
                Log.e("FirebaseStorage", "Failed to get image download URL", e);
                Toast.makeText(EditProfile.this, "Failed to get image download URL: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            });
        }).addOnFailureListener(e -> {
            // Image upload failed
            Log.e("FirebaseStorage", "Failed to upload image", e);
            Toast.makeText(EditProfile.this, "Failed to upload image: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }

    private void updateUserProfile(String name, String email, String password, String username, String imageUrl) {
        DocumentReference userRef = database.collection("users").document(userId);
        userRef.update("name", name,
                        "email", email,
                        "password", password,
                        "username", username,
                        "Image", imageUrl)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(EditProfile.this, "Profile Updated Successfully", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(EditProfile.this, "Failed to update profile", Toast.LENGTH_SHORT).show();
                    }
                });
    }/*
   private void updateProfile() {
       String newName = editName.getText().toString().trim();
       String newEmail = editEmail.getText().toString().trim();
       String newPassword = editPassword.getText().toString().trim();
       String newUsername = editUsername.getText().toString().trim();

       // Check if an image is selected
       if (imageUri != null) {
           // Upload the image to Firebase Storage
           uploadImageToFirebaseStorage(newName, newEmail, newPassword, newUsername);
       } else {
           // No image selected, update the profile without an image
           updateUserProfile(newName, newEmail, newPassword, newUsername, null);
       }
   }

    private void uploadImageToFirebaseStorage(final String newName, final String newEmail, final String newPassword, final String newUsername) {
        // Get a reference to the Firebase Storage
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();

        // Set the image file name in Firebase Storage (e.g., using the user's ID)
        final StorageReference imageRef = storageRef.child("profile_Image/" + userId + ".png");

        // Upload the image file to Firebase Storage
        UploadTask uploadTask = imageRef.putFile(imageUri);
        uploadTask.addOnSuccessListener(taskSnapshot -> {
            // Image upload successful, get the download URL of the uploaded image
            imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                // Update the user's profile with the image URL
                updateUserProfile(newName, newEmail, newPassword, newUsername, uri.toString());
            }).addOnFailureListener(e -> {
                // Failed to get image download URL
                Log.e("FirebaseStorage", "Failed to get image download URL", e);
                Toast.makeText(EditProfile.this, "Failed to get image download URL: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            });
        }).addOnFailureListener(e -> {
            // Image upload failed
            Log.e("FirebaseStorage", "Failed to upload image", e);
            Toast.makeText(EditProfile.this, "Failed to upload image: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }

    private void updateUserProfile(String name, String email, String password, String username, String imageUrl) {
        DocumentReference userRef = database.collection("users").document(userId);
        userRef.update("name", name,
                        "email", email,
                        "password", password,
                        "username", username,
                        "Image", imageUrl)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(EditProfile.this, "Profile Updated Successfully", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(EditProfile.this, "Failed to update profile", Toast.LENGTH_SHORT).show();
                    }
                });
    }
*/


    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            imageUri = data.getData();
            Log.d("Image URI", "URI: " + imageUri.toString());

            // Set the selected image to the ImageButton
            profileImg.setImageURI(imageUri);

            // Upload the selected image to Firebase Storage
        }
    }

}
