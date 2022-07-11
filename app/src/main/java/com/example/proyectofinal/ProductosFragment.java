package com.example.proyectofinal;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toolbar;

import com.example.proyectofinal.Modal.Productos;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.squareup.picasso.Picasso;


public class ProductosFragment extends Fragment {


    private View fragmento;
    private DatabaseReference PrductosRef;
    private RecyclerView recyclerMenu;
    RecyclerView.LayoutManager layoutManager;

    public ProductosFragment() { }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        fragmento = inflater.inflate(R.layout.fragment_productos, container, false);


        PrductosRef = FirebaseDatabase.getInstance().getReference().child("Productos");
        recyclerMenu = (RecyclerView) fragmento.findViewById(R.id.reciclre_menu);
        recyclerMenu.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getContext());
        recyclerMenu.setLayoutManager(layoutManager);




        return fragmento;
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseRecyclerOptions<Productos> options = new FirebaseRecyclerOptions.Builder<Productos>()
                .setQuery(PrductosRef, Productos.class).build();
        FirebaseRecyclerAdapter<Productos,ProViewHolder> adapter = new FirebaseRecyclerAdapter<Productos, ProViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull ProViewHolder holder, int position, @NonNull Productos model) {

                holder.proNom.setText(model.getNombre().toUpperCase());
                holder.proCantidad.setText("Cantidad:"+model.getCantidad());
                holder.proDescrip.setText(model.getDescripcion());
                holder.proPrecioCom.setText("Precio Compra: S/"+model.getPreciocom());
                holder.proPrecio.setText("Precio Venta: S/"+model.getPreciovent());
                Picasso.get().load(model.getImagen()).into(holder.proImagn);
            }
            @NonNull
            @Override
            public ProViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.producto_item_layout,parent,false);
                ProViewHolder holder = new ProViewHolder(view);
                return holder;
            }
        };
        recyclerMenu.setAdapter(adapter);
        adapter.startListening();
    }

}

