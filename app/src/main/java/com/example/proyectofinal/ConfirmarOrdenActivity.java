package com.example.proyectofinal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class ConfirmarOrdenActivity extends AppCompatActivity {

    private EditText nombre, correo, direccion, telefono;
    private Button confirmar;
    private String totalPago = "";
    private FirebaseAuth auth;
    private String CurrentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirmar_orden);

        auth = FirebaseAuth.getInstance();
        CurrentUserId = auth.getCurrentUser().getUid();

        totalPago = getIntent().getStringExtra("Total");
        Toast.makeText(this, "Total a pagar: S/" + totalPago, Toast.LENGTH_SHORT).show();

        nombre = (EditText) findViewById(R.id.final_nombre);
        correo = (EditText) findViewById(R.id.final_correo);
        direccion = (EditText) findViewById(R.id.final_direccion);
        telefono = (EditText) findViewById(R.id.final_telefono);

        confirmar = (Button) findViewById(R.id.boton_confirmar);
        confirmar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                VerificarDatos();
            }
        });

    }

    private void VerificarDatos() {
        if (TextUtils.isEmpty(nombre.getText().toString())) {
            Toast.makeText(this, "Por favor ingrese su nombre", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(direccion.getText().toString())) {
            Toast.makeText(this, "Por favor ingrese su direccion", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(telefono.getText().toString())) {
            Toast.makeText(this, "Por favor ingrese su telefono", Toast.LENGTH_SHORT).show();
        }else if (TextUtils.isEmpty(correo.getText().toString())) {
            Toast.makeText(this, "Por favor ingrese su correo", Toast.LENGTH_SHORT).show();
        }else {
            ConfirmarOrden();
        }
    }

    private void ConfirmarOrden() {

        final String CurrentTime, CurrentDate;
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy");
        CurrentDate = dateFormat.format(calendar.getTime());

        SimpleDateFormat dateFormat1 = new SimpleDateFormat("HH:mm:ss");
        CurrentTime = dateFormat1.format(calendar.getTime());

        final DatabaseReference OrdenesRef = FirebaseDatabase.getInstance().getReference().child("Ordenes").child(CurrentUserId);

        HashMap<String,Object>map = new HashMap<>();
        map.put("total",totalPago);
        map.put("nombre",nombre.getText().toString());
        map.put("direccion",direccion.getText().toString());
        map.put("telefono",telefono.getText().toString());
        map.put("correo",correo.getText().toString());
        map.put("fecha",CurrentDate);
        map.put("hora",CurrentTime);
        map.put("estado","No Enviado");

        OrdenesRef.updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
               if (task.isSuccessful()){
                   FirebaseDatabase.getInstance().getReference().child("Carrito")
                           .child("Usuario Compra").child(CurrentUserId).removeValue()
                           .addOnCompleteListener(new OnCompleteListener<Void>() {
                               @Override
                               public void onComplete(@NonNull Task<Void> task) {
                                   if (task.isSuccessful()){
                                       Toast.makeText(ConfirmarOrdenActivity.this, "Tu orden fue tomada con exito", Toast.LENGTH_SHORT).show();
                                       Intent intent = new Intent(ConfirmarOrdenActivity.this,PrincipalActivity.class);
                                       intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                       startActivity(intent);
                                       finish();
                                   }
                               }
                           });
               }
            }
        });
    }
}