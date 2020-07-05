package com.example.instragramclone;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Region;
import android.os.Bundle;
import android.view.View;

import com.example.instragramclone.databinding.ActivityOptionsBinding;
import com.google.firebase.auth.FirebaseAuth;

public class  OptionsActivity extends AppCompatActivity {
private ActivityOptionsBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityOptionsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setTitle("Options");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        binding.toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        binding.logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(OptionsActivity.this,StartActivity.class)
                .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
            }
        });

    }
}
