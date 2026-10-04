package com.example.tasks;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class TaskManagerActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private String authHeader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_manager);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        authHeader = getIntent().getStringExtra("authHeader");
        loadTasks();
    }

    private void loadTasks() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://10.0.2.2:8080/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        TaskApi taskApi = retrofit.create(TaskApi.class);
        taskApi.getTasks("Basic " + authHeader).enqueue(new Callback<List<Task>>() {
            @Override
            public void onResponse(Call<List<Task>> call, Response<List<Task>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    recyclerView.setAdapter(new TaskAdapter(response.body()));
                } else {
                    Toast.makeText(TaskManagerActivity.this, "Ошибка загрузки задач", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Task>> call, Throwable error) {
                Log.e("TaskManagerActivity", "Ошибка загрузки", error);
                Toast.makeText(TaskManagerActivity.this, "Сервер недоступен", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
