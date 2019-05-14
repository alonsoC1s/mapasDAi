package com.example.alonsomartinez.mapasdaifinal;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.TextView;


public class MainActivity extends AppCompatActivity {


    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_dashboard:
                    MyMapFragment mpFrag =  new MyMapFragment();
                    switchToFragment(mpFrag);
                    return true;
                case R.id.navigation_notifications:
                    BlankFragment bFrag = new BlankFragment();
                    switchToFragment(bFrag);
                    return true;
            }
            return false;
        }
    };

    public void switchToFragment(Fragment destination){

        getFragmentManager().beginTransaction().replace( R.id.nav_contenido, destination ).commit();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        // Cambiando a la pantalla del mapa como default
        MyMapFragment mpFrag =  new MyMapFragment();
        switchToFragment(mpFrag);

    }

}
