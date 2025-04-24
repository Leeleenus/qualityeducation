package com.bob.onlineexams;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.google.firebase.FirebaseApp;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseApp.initializeApp(this);
        setContentView(R.layout.activity_main);

        EditText email = findViewById(R.id.email);
        EditText password = findViewById(R.id.password);
        Button login = findViewById(R.id.login);
        TextView signup = findViewById(R.id.signup);

        auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        if (user!=null) {
            Intent i = new Intent(MainActivity.this, Home.class);
            i.putExtra("User UID", user.getUid());
            startActivity(i);
            finish();
        }

        login.setOnClickListener(view -> {
            String em = email.getText().toString().trim();
            String pass = password.getText().toString();

            if (em.isEmpty()) {
                email.setError("Enter email");
                return;
            }
            if (pass.isEmpty()) {
                password.setError("Enter password");
                return;
            }

            ProgressDialog progressDialog = new ProgressDialog(MainActivity.this);
            progressDialog.setMessage("Logging in...");
            progressDialog.setCancelable(false);
            progressDialog.show();

            auth.signInWithEmailAndPassword(em, pass).addOnCompleteListener(MainActivity.this,
                    (OnCompleteListener<AuthResult>) task -> {
                        if (task.isSuccessful()) {
                            FirebaseUser user1 = auth.getCurrentUser(); // ← declared as user1
                            runOnUiThread(() -> {
                                progressDialog.dismiss();
                                Intent i = new Intent(MainActivity.this, Home.class);
                                i.putExtra("User UID", user1.getUid());
                                startActivity(i);
                                finish();
                            });
                        }
                    });

        });
        signup.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, Signup.class);
            startActivity(intent);
        });

    }
}