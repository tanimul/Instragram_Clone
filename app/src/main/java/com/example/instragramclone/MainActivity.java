package com.example.instragramclone;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.instragramclone.Fragment.HomeFragment;
import com.example.instragramclone.Fragment.NotificationFragment;
import com.example.instragramclone.Fragment.ProfileFragment;
import com.example.instragramclone.Fragment.SearchFragment;
import com.example.instragramclone.databinding.ActivityMainBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    private ActivityMainBinding binding;
    private BottomNavigationView bottomNavigationView;
    private Fragment selectedfragment = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.bottomNavigationView.setOnNavigationItemSelectedListener(this);

        Bundle bundle=getIntent().getExtras();
        if (bundle !=null){
            String publisher=bundle.getString("publisherid");
            SharedPreferences.Editor editor=getSharedPreferences("PREFS",MODE_PRIVATE).edit();
            editor.putString("profileid",publisher);
            editor.apply();

            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ProfileFragment()).commit();

        }else {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment()).commit();

        }

          }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

        switch (menuItem.getItemId()) {

            case R.id.nav_home:
                selectedfragment = new HomeFragment();
                break;
            case R.id.nav_search:
                selectedfragment = new SearchFragment();
                break;

            case R.id.nav_add:
                selectedfragment = null;
                startActivity(new Intent(MainActivity.this, PostActivity.class));
                break;

            case R.id.nav_heart:
                selectedfragment = new NotificationFragment();
                break;

            case R.id.nav_prof:
                SharedPreferences.Editor editor = getSharedPreferences("PREFS", MODE_PRIVATE).edit();
                editor.putString("profileid", FirebaseAuth.getInstance().getCurrentUser().getUid());
                editor.apply();
                selectedfragment = new ProfileFragment();
                break;
        }
        if(selectedfragment !=null){
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,selectedfragment).commit();

        }

        return true;
    }
}
