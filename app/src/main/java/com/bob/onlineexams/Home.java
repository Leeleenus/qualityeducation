package com.bob.onlineexams;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Home extends AppCompatActivity {

    private String userUID;
    private String firstName;
    private ProgressDialog progressDialog;
    private Handler timeoutHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        Bundle b = getIntent().getExtras();
        if (b == null || b.getString("User UID") == null) {
            Toast.makeText(this, "User UID missing!", Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();
            return;
        }

        userUID = b.getString("User UID");

        // Timeout fallback after 10 seconds
        timeoutHandler.postDelayed(() -> {
            if (progressDialog.isShowing()) {
                progressDialog.dismiss();
                Toast.makeText(Home.this, "Connection timeout. Please check your internet.", Toast.LENGTH_LONG).show();
            }
        }, 10000); // 10 seconds

        // Views
        TextView name = findViewById(R.id.name);
        TextView total_questions = findViewById(R.id.total_questions);
        TextView total_points = findViewById(R.id.total_points);
        Button startQuiz = findViewById(R.id.startQuiz);
        Button createQuiz = findViewById(R.id.createQuiz);
        RelativeLayout solvedQuizzes = findViewById(R.id.solvedQuizzes);
        RelativeLayout your_quizzes = findViewById(R.id.your_quizzes);
        EditText quiz_title = findViewById(R.id.quiz_title);
        EditText start_quiz_id = findViewById(R.id.start_quiz_id);
        ImageView signout = findViewById(R.id.signout);

        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Users").child(userUID);

        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                runOnUiThread(() -> {
                    progressDialog.dismiss();
                    timeoutHandler.removeCallbacksAndMessages(null); // Cancel timeout

                    if (snapshot.exists()) {
                        firstName = snapshot.child("First Name").getValue(String.class);
                        if (firstName != null) {
                            name.setText("Welcome " + firstName + "!");
                        }

                        if (snapshot.hasChild("Total Points")) {
                            try {
                                int points = Integer.parseInt(snapshot.child("Total Points").getValue().toString());
                                total_points.setText(String.format("%03d", points));
                            } catch (NumberFormatException e) {
                                total_points.setText("000");
                            }
                        }

                        if (snapshot.hasChild("Total Questions")) {
                            try {
                                int questions = Integer.parseInt(snapshot.child("Total Questions").getValue().toString());
                                total_questions.setText(String.format("%03d", questions));
                            } catch (NumberFormatException e) {
                                total_questions.setText("000");
                            }
                        }
                    } else {
                        Toast.makeText(Home.this, "User data not found", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                runOnUiThread(() -> {
                    progressDialog.dismiss();
                    timeoutHandler.removeCallbacksAndMessages(null);
                    Toast.makeText(Home.this, "Database error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                });
            }
        });


        signout.setOnClickListener(view -> {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(Home.this, MainActivity.class));
            finish();
        });

        startQuiz.setOnClickListener(v -> {
            Intent intent = new Intent(Home.this, Exam.class);
            intent.putExtra("User UID", userUID); // optional if needed in Exam
            startActivity(intent);
        });

        createQuiz.setOnClickListener(v -> {
            Intent intent = new Intent(Home.this, ExamEditor.class);
            intent.putExtra("User UID", userUID); // optional if needed
            startActivity(intent);
        });

        solvedQuizzes.setOnClickListener(v -> {
            Intent intent = new Intent(Home.this, Result.class); // or ListQuizzes if that fits better
            intent.putExtra("User UID", userUID); // optional
            startActivity(intent);
        });

        your_quizzes.setOnClickListener(v -> {
            Intent intent = new Intent(Home.this, ListQuizzes.class);
            intent.putExtra("User UID", userUID);
            startActivity(intent);
        });

    }
}
