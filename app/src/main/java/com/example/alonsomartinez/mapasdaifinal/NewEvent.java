package com.example.alonsomartinez.mapasdaifinal;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class NewEvent extends AppCompatActivity {

    Double chosenLat;
    Double chosenLong;
    EditText eventName;
    EditText eventContent;
    Button btnCrear;
    DatabaseReference dbRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_event);

        eventName = findViewById(R.id.et_newpost_eventname);
        eventContent = findViewById(R.id.et_newpost_eventdescription);
        btnCrear = findViewById(R.id.btn_crear_evento);

        //Get the extra information passed on by place picker. i.e chosen LatLng
        Bundle latLngBundle = getIntent().getExtras();
        chosenLat = latLngBundle.getDouble("eventLatitude");
        chosenLong = latLngBundle.getDouble("eventLongitude");

        dbRef = FirebaseDatabase.getInstance().getReference();

        btnCrear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Aqui se crea el evento

                String finalName = eventName.getText().toString();
                String finalContent = eventContent.getText().toString();

                if (!finalName.isEmpty() && !finalContent.isEmpty()){
                    // Vàlido

                    String pushId = dbRef.child("Eventos").push().getKey();
                    MyEvents evento = new MyEvents(finalName, finalContent, chosenLat, chosenLong, pushId);

                    // Empujando
                    dbRef.child("Eventos").child(pushId).setValue(evento);

                    Toast.makeText(getApplicationContext(),"Se creó exitosamente tu evento!",Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);

                }
            }
        });
    }
}
