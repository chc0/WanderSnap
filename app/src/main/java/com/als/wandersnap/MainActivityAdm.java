package com.als.wandersnap;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;

import com.als.wandersnap.databinding.ActivityMainAdmBinding;
import com.bumptech.glide.Glide;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;

import androidx.annotation.NonNull;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;

import com.als.wandersnap.databinding.ActivityMainBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

public class MainActivityAdm extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private @NonNull ActivityMainAdmBinding binding;

    private DrawerLayout drawer;
    private NavigationView navigationView;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseUser usuarioActual = FirebaseAuth.getInstance().getCurrentUser();
    private String email="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ///*
        binding = ActivityMainAdmBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        //setContentView(R.layout.activity_main);

        setSupportActionBar(binding.appBarMain.toolbar);


            /*
        drawer = binding.drawerLayout;
        navigationView = binding.navView;
            */

        if (usuarioActual != null) {
            // El usuario está autenticado, establece la información en los elementos de la interfaz de usuario
            email = usuarioActual.getEmail();

            Query userQuery = db.collection("Usuarios").whereEqualTo("email", email);

            userQuery.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    QuerySnapshot querySnapshot = task.getResult();
                    if (querySnapshot != null && !querySnapshot.isEmpty()) {
                        // Supongo que el correo electrónico es único en tu colección
                        DocumentSnapshot document = querySnapshot.getDocuments().get(0);

                        // Obtén el nombre del usuario
                        String rol = document.getString("rol");
                        String usuario = document.getString("usuario");
                        //Log.e("OBTROL", "ROL= " + rol);
                        //Log.e("OBTROL", "USUARIO= " + usuario);


                        if (rol.equals("administrador")){
                            Log.e("OBTROL", "ROL= " + rol);
                            Log.e("OBTROL", "USUARIO= " + usuario);

                            ///*
                            drawer = binding.drawerLayoutAdm;
                            navigationView = binding.navViewAdm;
                            mAppBarConfiguration = new AppBarConfiguration.Builder(
                                    R.id.nav_home, R.id.nav_datos, R.id.nav_resenia, R.id.nav_admEliminarU, R.id.nav_admEliminarR)
                                    .setOpenableLayout(drawer)
                                    .build();
                            NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
                            NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
                            NavigationUI.setupWithNavController(navigationView, navController);
                            //*/
                        } else if (rol.equals("normal")) {
                            Log.e("OBTROL", "ROL= " + rol);
                            Log.e("OBTROL", "USUARIO= " + usuario);
                            drawer = binding.drawerLayoutAdm;
                            navigationView = binding.navViewAdm;
                            mAppBarConfiguration = new AppBarConfiguration.Builder(
                                    R.id.nav_home, R.id.nav_datos, R.id.nav_resenia, R.id.nav_admEliminarU, R.id.nav_admEliminarR)
                                    .setOpenableLayout(drawer)
                                    .build();
                            NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
                            NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
                            NavigationUI.setupWithNavController(navigationView, navController);

                        }
                    }
                } else {
                    // Manejar el error al obtener datos de Firestore
                    CustomToastUtil.showErrorToast(getApplicationContext(), "Error al cargar los datos ROL= " + email);
                }
            });
        }

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
            /*
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_datos, R.id.nav_resenia, R.id.nav_admEliminarU, R.id.nav_admEliminarR)
                .setOpenableLayout(drawer)
                .build();
            */
            /*
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
            */
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
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