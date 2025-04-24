package com.bob.onlineexams;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Signup extends AppCompatActivity {

    private FirebaseAuth auth;
    private DatabaseReference database;

    private EditText firstNameField, lastNameField, emailField, passwordField, confirmPasswordField;
    private Button signupButton;
    private TextView loginText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance().getReference();

        // Initialize views
        firstNameField = findViewById(R.id.first_name);
        lastNameField = findViewById(R.id.last_name);
        emailField = findViewById(R.id.email);
        passwordField = findViewById(R.id.password);
        confirmPasswordField = findViewById(R.id.confirm_password);
        signupButton = findViewById(R.id.signup);
        loginText = findViewById(R.id.login);

        signupButton.setOnClickListener(view -> attemptSignup());
        loginText.setOnClickListener(view -> {
            startActivity(new Intent(Signup.this, MainActivity.class));
            finish();
        });
    }

    private void attemptSignup() {
        String firstName = firstNameField.getText().toString().trim();
        String lastName = lastNameField.getText().toString().trim();
        String email = emailField.getText().toString().trim();
        String password = passwordField.getText().toString();
        String confirmPassword = confirmPasswordField.getText().toString();

        // Basic validation
        if (firstName.isEmpty()) {
            firstNameField.setError("Enter first name");
            return;
        }
        if (lastName.isEmpty()) {
            lastNameField.setError("Enter last name");
            return;
        }
        if (email.isEmpty()) {
            emailField.setError("Enter email");
            return;
        }
        if (password.isEmpty() || password.length() < 6) {
            passwordField.setError("Password must be at least 6 characters");
            return;
        }
        if (!password.equals(confirmPassword)) {
            confirmPasswordField.setError("Passwords do not match");
            return;
        }

        ProgressDialog progressDialog = new ProgressDialog(Signup.this);
        progressDialog.setMessage("Creating account...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        // Firebase signup
        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, (OnCompleteListener<AuthResult>) task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = auth.getCurrentUser();
                        if (user != null) {
                            DatabaseReference userRef = database.child("Users").child(user.getUid());
                            userRef.child("First Name").setValue(firstName);
                            userRef.child("Last Name").setValue(lastName);

                            progressDialog.dismiss();
                            Intent intent = new Intent(Signup.this, Home.class);
                            intent.putExtra("User UID", user.getUid());
                            startActivity(intent);
                            finish();
                        }
                    } else {
                        progressDialog.dismiss();
                        Toast.makeText(Signup.this, "Signup failed: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                });


    }
}
