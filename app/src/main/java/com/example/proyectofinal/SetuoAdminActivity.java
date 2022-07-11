package com.example.proyectofinal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class SetuoAdminActivity extends AppCompatActivity {

    private EditText nombre, direccion,telefono,ciudad;
    private Button guardar;
    private String phone = "";
    private CircleImageView imagen;
    private FirebaseAuth auth;
    private DatabaseReference UserRef;
    private ProgressDialog dialog;
    private String CurrentUserId;
    private static int Galery_Pick = 1;
    private StorageReference UserImagenPerfil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setuo_admin);

        auth = FirebaseAuth.getInstance();
        CurrentUserId = auth.getCurrentUser().getUid();
        UserRef = FirebaseDatabase.getInstance().getReference().child("Admin");
        dialog = new ProgressDialog(this);
        UserImagenPerfil = FirebaseStorage.getInstance().getReference().child("Perfil");
        nombre = (EditText) findViewById(R.id.asetup_nombre);
        direccion = (EditText) findViewById(R.id.asetup_direccion);
        telefono = (EditText) findViewById(R.id.asetup_telefono);
        ciudad = (EditText) findViewById(R.id.asetup_ciudad);
        guardar = (Button) findViewById(R.id.asetup_boton);
        imagen = (CircleImageView) findViewById(R.id.asetup_imagen);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null){
            phone = bundle.getString("phone");
        }
        guardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GuardarInformacion();
            }
        });

        imagen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("image/");
                startActivityForResult(intent,Galery_Pick);
            }
        });

        UserRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.exists()){

                    if (snapshot.hasChild("imagen")){
                        String imagestr = snapshot.child("imagen").getValue().toString();
                        Picasso.get().load(imagestr).placeholder(R.drawable.home).into(imagen);
                    }else {
                        Toast.makeText(SetuoAdminActivity.this, "Por favor seleccione una imagen de perfil", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

    }//oncreate

    private void GuardarInformacion() {
        String nombres =  nombre.getText().toString().toUpperCase();
        String direccions = direccion.getText().toString();
        String ciudads = ciudad.getText().toString();
        String phone = telefono.getText().toString();

        if (TextUtils.isEmpty(nombres)){
            Toast.makeText(this, "Ingrese el nombre", Toast.LENGTH_SHORT).show();
        }else  if (TextUtils.isEmpty(direccions)){
            Toast.makeText(this, "Ingrese su direccion ", Toast.LENGTH_SHORT).show();
        }else  if (TextUtils.isEmpty(ciudads)){
            Toast.makeText(this, "Ingrese su ciudad", Toast.LENGTH_SHORT).show();
        }else  if (TextUtils.isEmpty(phone)){
            Toast.makeText(this, "Ingrese su telefono", Toast.LENGTH_SHORT).show();
        }else{
            dialog.setTitle("Guardar");
            dialog.setMessage("Por favor espere");
            dialog.show();
            dialog.setCanceledOnTouchOutside(true);

            HashMap map = new HashMap();
            map.put("nombre",nombres);
            map.put("direccion",direccions);
            map.put("ciudad",ciudads);
            map.put("telefono",phone);
            map.put("uid",CurrentUserId);

            UserRef.child(CurrentUserId).updateChildren(map).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if (task.isSuccessful()){
                        EnviarAlInicio();
                        dialog.dismiss();
                    }else {
                        String mensaje = task.getException().toString();
                        Toast.makeText(SetuoAdminActivity.this, "Error"+mensaje, Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }
                }
            });
        }
    }

    private void EnviarAlInicio() {
        Intent intent = new Intent (SetuoAdminActivity.this, AdminActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}