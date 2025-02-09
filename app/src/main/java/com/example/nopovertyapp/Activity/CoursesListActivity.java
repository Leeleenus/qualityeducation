package com.example.nopovertyapp.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nopovertyapp.Adapter.CoursesAdapter;
import com.example.nopovertyapp.Domin.CoursesDomain;
import com.example.nopovertyapp.R;

import java.util.ArrayList;

public class CoursesListActivity extends AppCompatActivity {
private RecyclerView.Adapter adapterCourseList;
private RecyclerView recyclerViewCourse;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_courses_list);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.ConstraintLayout), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        ImageView startBtn = findViewById(R.id.backhome);

        startBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CoursesListActivity.this, HomeActivity.class);
                startActivity(intent);
            }
        });
        initRecyclerView();
    }

    private void initRecyclerView() {
        ArrayList<CoursesDomain> items=new ArrayList<>();
        items.add(new CoursesDomain("Beginner Certification Course in AI",5,"ic_1"));
        items.add(new CoursesDomain("Google Cloud Platform Architecture",2,"ic_2"));
        items.add(new CoursesDomain("Fundamental of java Programming",2,"ic_3"));
        items.add(new CoursesDomain("Introduction to UI design history",5,"ic_4"));
        items.add(new CoursesDomain("PG Program in Big Data Engineering",10,"ic_5"));

        recyclerViewCourse=findViewById(R.id.view);
        recyclerViewCourse.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));

        adapterCourseList= new CoursesAdapter(items);
        recyclerViewCourse.setAdapter(adapterCourseList);

    }
}