package com.example.proyectofinal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.proyectofinal.Modal.Productos;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class ProductoDetallesActivity extends AppCompatActivity {

    private Button agregarCarrito;
    private ImageView aumentar,disminuir,productoImagen;
    private TextView productoPrecio, productoDescripcion,productoNombre, productoContador;
    int contador = 1;
    private String productoID = "", estado = "Normal", CurrentUserID;
    private FirebaseAuth auth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_producto_detalles);

        productoID = getIntent().getStringExtra("pid");

        agregarCarrito = (Button) findViewById(R.id.boton_siguiente_detalle);
        aumentar = (ImageView) findViewById(R.id.aumentar);
        disminuir = (ImageView) findViewById(R.id.disminuir);
        productoContador = (TextView) findViewById(R.id.producto_contador_detalle);
        productoImagen = (ImageView) findViewById(R.id.producto_imagen_detalle);
        productoPrecio = (TextView) findViewById(R.id.producto_precio_detalle);
        productoDescripcion = (TextView) findViewById(R.id.producto_descripcion_detalle);
        productoNombre = (TextView) findViewById(R.id.producto_nombre_detalle);


        ObtenerDatosProducto(productoID);
        auth = FirebaseAuth.getInstance();
        CurrentUserID = auth.getCurrentUser().getUid();
        agregarCarrito.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (estado.equals("Pedido")||estado.equals("Enviado")){
                    Toast.makeText(ProductoDetallesActivity.this, "Esperando a que el pedido finalice", Toast.LENGTH_SHORT).show();
                }else {
                    aagregarAlaLista();
                }
            }
        });


        aumentar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                   contador++;
                   productoContador.setText(String.valueOf(contador));

            }
        });
        disminuir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                    contador--;
                    productoContador.setText(String.valueOf(contador));

            }
        });


     }

    @Override
    protected void onStart() {
        super.onStart();
        VerificarEstadoOrden();
    }

    private void aagregarAlaLista() {

        String CurrentTime, CurrentDate;
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat data = new SimpleDateFormat("MM-dd-yyyy");
        CurrentDate = data.format(calendar.getTime());

        SimpleDateFormat time = new SimpleDateFormat("HH:mm:ss");
        CurrentTime=time.format(calendar.getTime());

        final DatabaseReference CarttListRef = FirebaseDatabase.getInstance().getReference().child("Carrito");

        final HashMap<String , Object> map = new HashMap<>();
        map.put("pid",productoID);
        map.put("nombre",productoNombre.getText().toString());
        map.put("precio",productoPrecio.getText().toString());
        map.put("fecha",CurrentDate);
        map.put("hora",CurrentTime);
        map.put("cantidad",productoContador.getText().toString());
        map.put("descuento","");

        CarttListRef.child("Usuario Compra").child(CurrentUserID).child("Productos").child(productoID).updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    CarttListRef.child("Administracion").child(CurrentUserID).child("Productos").child(productoID).updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                           if (task.isSuccessful()){
                               Toast.makeText(ProductoDetallesActivity.this, "Agregado", Toast.LENGTH_SHORT).show();

                               Intent intent = new Intent(ProductoDetallesActivity.this, PrincipalActivity.class);
                               startActivity(intent);
                           }
                        }
                    });
                }
            }
        });
    }

    private void ObtenerDatosProducto(String productoID) {
        DatabaseReference ProductoRef = FirebaseDatabase.getInstance().getReference().child("Productos");
        ProductoRef.child(productoID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    Productos productos = snapshot.getValue(Productos.class);
                    productoNombre.setText(productos.getNombre());
                    productoDescripcion.setText(productos.getDescripcion());
                    productoPrecio.setText("Precio: S/"+productos.getPreciovent());
                    Picasso.get().load(productos.getImagen()).into(productoImagen);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void VerificarEstadoOrden() {
        DatabaseReference OrdenRef;
        OrdenRef = FirebaseDatabase.getInstance().getReference().child("Ordenes").child(CurrentUserID);
        OrdenRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                   String envioStado = snapshot.child("estado").getValue().toString();
                   if (envioStado.equals("Enviado")){
                       estado = "Enviado";
                   }else if (envioStado.equals("No Enviado")){
                       estado = "Pedido";
                   }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}