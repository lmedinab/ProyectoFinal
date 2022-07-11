package com.example.proyectofinal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.proyectofinal.Modal.Carrito;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class CarritoActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private Button Siguiente;
    private TextView TotalPrecio, mensaje1;
    private String CurrentUserId;
    private FirebaseAuth auth;
    private double PrecioTotalID = 0.0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_carrito);

        recyclerView = findViewById(R.id.carrito_lista);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        Siguiente = (Button) findViewById(R.id.siguiente_proceso);
        TotalPrecio = (TextView) findViewById(R.id.precio_total);
        mensaje1 = (TextView) findViewById(R.id.mensaje1);
        auth = FirebaseAuth.getInstance();
        CurrentUserId = auth.getCurrentUser().getUid();

        Siguiente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CarritoActivity.this, ConfirmarOrdenActivity.class);
                intent.putExtra("Total", String.valueOf(PrecioTotalID));
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        VerificarEstadOrden();


        final DatabaseReference CartListRef = FirebaseDatabase.getInstance().getReference().child("Carrito");

        FirebaseRecyclerOptions<Carrito>options = new FirebaseRecyclerOptions.Builder<Carrito>()
                .setQuery(CartListRef.child("Usuario compra").child(CurrentUserId).child("Productos"),Carrito.class).build();

        FirebaseRecyclerAdapter<Carrito,CarritoViewHolder> adapter = new FirebaseRecyclerAdapter<Carrito, CarritoViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull CarritoViewHolder holder, int position, @NonNull Carrito model) {

             holder.carProductoNombre.setText(model.getNombre());
             holder.carProductoCantidad.setText(model.getCantidad());
             holder.carProductoPrecio.setText(model.getPrecio());

             double UnTipoPrecio = (Double.valueOf(model.getPrecio())) * Integer.valueOf(model.getCantidad());
             PrecioTotalID = PrecioTotalID + UnTipoPrecio;
             TotalPrecio.setText("Total:"+ String.valueOf(PrecioTotalID));

             holder.itemView.setOnClickListener(new View.OnClickListener() {
                 @Override
                 public void onClick(View view) {
                     CharSequence options[] = new CharSequence[]{
                          "Editar",
                          "Eliminar"
                     };
                     AlertDialog.Builder builder = new AlertDialog.Builder(CarritoActivity.this);
                     builder.setTitle("Opciones del producto");

                     builder.setItems(options, new DialogInterface.OnClickListener() {
                         @Override
                         public void onClick(DialogInterface dialog, int i) {
                             if (i==0){
                                 Intent intent = new Intent(CarritoActivity.this,ProductoDetallesActivity.class);
                                 intent.putExtra("pid",model.getPid());
                                 startActivity(intent);
                             }
                             if (i==1){
                                 CartListRef.child("Usuario compra")
                                         .child(CurrentUserId)
                                         .child("Productos")
                                         .child(model.getPid()).removeValue()
                                         .addOnCompleteListener(new OnCompleteListener<Void>() {
                                             @Override
                                             public void onComplete(@NonNull Task<Void> task) {
                                                 if (task.isSuccessful()){
                                                     Toast.makeText(CarritoActivity.this, "Producto eliminado", Toast.LENGTH_SHORT).show();
                                                     Intent intent = new Intent(CarritoActivity.this,PrincipalActivity.class);
                                                     startActivity(intent);
                                                 }
                                             }
                                         });
                             }
                         }
                     });
                     builder.show();
                 }
             });
            }
            @NonNull
            @Override
            public CarritoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.car_item_layout,parent,false);
                CarritoViewHolder holder = new CarritoViewHolder(view);
                return holder;
            }
        };
        recyclerView.setAdapter(adapter);
        adapter.startListening();

    }

    private void VerificarEstadOrden() {
        DatabaseReference ordenRef;
        ordenRef = FirebaseDatabase.getInstance().getReference().child("Ordenes").child(CurrentUserId);
        ordenRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    String estado = snapshot.child("estado").getValue().toString();
                    String nombre = snapshot.child("nombre").getValue().toString();
                    if (estado.equals("Enviado")){
                        TotalPrecio.setText("Estimado"+nombre+"Su pedido fue enviado");
                        recyclerView.setVisibility(View.GONE);
                        mensaje1.setText("Su pedido se enviara pronto");
                        mensaje1.setVisibility(View.VISIBLE);
                        Siguiente.setVisibility(View.GONE);
                    }else if (estado.equals("No Enviado")){
                        TotalPrecio.setText("Su orden esta siendo procesada");
                        recyclerView.setVisibility(View.GONE);
                        mensaje1.setVisibility(View.VISIBLE);
                        Siguiente.setVisibility(View.GONE);
                        Toast.makeText(CarritoActivity.this, "Puedes comprar mas productos cuando el anterior finalice", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}