package com.als.wandersnap;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;

import com.als.wandersnap.databinding.ActivityMainAdmBinding;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;

import com.als.wandersnap.databinding.ActivityMainBinding;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivityAdm extends AppCompatActivity {

    private AppBarConfiguration mAppBarAdmConfiguration;
    private ActivityMainAdmBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainAdmBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.appBarMainAdm.toolbarAdm);

        DrawerLayout drawer = binding.drawerLayoutAdm;
        NavigationView navigationView = binding.navViewAdm;
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarAdmConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_datos, R.id.nav_admEliminarU, R.id.nav_admEliminarR)
                .setOpenableLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main_adm);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarAdmConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main_adm);
        return NavigationUI.navigateUp(navController, mAppBarAdmConfiguration)
                || super.onSupportNavigateUp();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_exit) {
            // Lógica para salir de la aplicación
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(getApplicationContext(), Login.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}