package com.example.myapp;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class MainActivity extends AppCompatActivity {

    private ImageView profileImageView;
    private EditText nameEditText, emailEditText, passwordEditText, reenterPasswordEditText;
    private CheckBox agreeCheckBox;
    private RadioGroup genderRadioGroup;
    private Spinner countrySpinner;
    private Button registerButton, pickImageButton;
    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int STORAGE_PERMISSION_CODE = 102;

    // Error labels
    private TextView nameErrorTextView, emailErrorTextView, passwordErrorTextView, genderErrorTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize UI components
        profileImageView = findViewById(R.id.profile_image);
        nameEditText = findViewById(R.id.et_name);
        emailEditText = findViewById(R.id.et_email);
        passwordEditText = findViewById(R.id.et_password);
        reenterPasswordEditText = findViewById(R.id.et_reenter_password);
        agreeCheckBox = findViewById(R.id.cb_agree);
        genderRadioGroup = findViewById(R.id.rg_gender);
        countrySpinner = findViewById(R.id.spinner_country);
        registerButton = findViewById(R.id.btn_register);
        pickImageButton = findViewById(R.id.btn_pick_image);

        // Initialize error TextViews
        nameErrorTextView = findViewById(R.id.tv_name_error);
        emailErrorTextView = findViewById(R.id.tv_email_error);
        passwordErrorTextView = findViewById(R.id.tv_password_error);
        genderErrorTextView = findViewById(R.id.tv_gender_error);

        // Set up Spinner with country options
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.country_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        countrySpinner.setAdapter(adapter);

        // Handle spinner selection
        countrySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                // Handle the country selection if needed
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
            }
        });

        // Pick Image Button
        pickImageButton.setOnClickListener(view -> openImageChooser());

        // Register Button
        registerButton.setOnClickListener(view -> registerUser());
    }

    private void openImageChooser() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                // Permission is already granted, open the gallery
                openGallery();
            } else {
                // Request storage permission if not granted
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
            }
        } else {
            // For devices below Android Marshmallow, permission is automatically granted
            openGallery();
        }
    }

    // Open gallery to pick an image
    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    // Handle the result of the permission request
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == STORAGE_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, open the gallery
                openGallery();
            } else {
                // Permission denied
                Toast.makeText(this, "Storage permission denied. Cannot select image.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // Handle image result
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            Uri imageUri = data.getData();
            profileImageView.setImageURI(imageUri);
        }
    }


    // Registration Logic
    private void registerUser() {
        // Reset error messages
        nameErrorTextView.setVisibility(View.GONE);
        emailErrorTextView.setVisibility(View.GONE);
        passwordErrorTextView.setVisibility(View.GONE);
        genderErrorTextView.setVisibility(View.GONE);

        // Get input values
        String name = nameEditText.getText().toString().trim();
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();
        String reenterPassword = reenterPasswordEditText.getText().toString().trim();
        boolean isAgreed = agreeCheckBox.isChecked();
        int selectedGenderId = genderRadioGroup.getCheckedRadioButtonId();

        // Validation
        if (TextUtils.isEmpty(name)) {
            nameErrorTextView.setText("Name is required");
            nameErrorTextView.setVisibility(View.VISIBLE);
            return;
        }

        if (TextUtils.isEmpty(email) || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailErrorTextView.setText("Valid email is required");
            emailErrorTextView.setVisibility(View.VISIBLE);
            return;
        }

        if (TextUtils.isEmpty(password) || password.length() < 6) {
            passwordErrorTextView.setText("Password must be at least 6 characters");
            passwordErrorTextView.setVisibility(View.VISIBLE);
            return;
        }

        if (!password.equals(reenterPassword)) {
            passwordErrorTextView.setText("Passwords do not match");
            passwordErrorTextView.setVisibility(View.VISIBLE);
            return;
        }

        if (selectedGenderId == -1) {
            genderErrorTextView.setText("Gender is required");
            genderErrorTextView.setVisibility(View.VISIBLE);
            return;
        }

        if (!isAgreed) {
            Toast.makeText(this, "You must agree to the terms", Toast.LENGTH_SHORT).show();
            return;
        }

        // Perform registration logic here (e.g., send data to backend)
        Toast.makeText(this, "Registration successful!", Toast.LENGTH_SHORT).show();
    }
}
