package com.example.proyectofinal;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.proyectofinal.Clases.Ordenes;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class FragmentCuatro extends Fragment {

 private View Vista;
 private RecyclerView recicler;
 private DatabaseReference OrdenRef;

    public FragmentCuatro() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Vista = inflater.inflate(R.layout.fragment_cuatro, container, false);

        OrdenRef = FirebaseDatabase.getInstance().getReference().child("Ordenes");
        recicler = (RecyclerView) Vista.findViewById(R.id.reciclar_ordenes);
        recicler.setLayoutManager(new LinearLayoutManager(getContext()));

        return Vista;
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseRecyclerOptions options = new FirebaseRecyclerOptions.Builder<Ordenes>().setQuery(OrdenRef,Ordenes.class).build();
        FirebaseRecyclerAdapter<Ordenes, OrdenesViewHolder> adapter = new FirebaseRecyclerAdapter<Ordenes, OrdenesViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull OrdenesViewHolder holder, int position, @NonNull Ordenes model) {
                holder.nombre.setText("Cliente: "+model.getNombre());
                holder.numero.setText(model.getTelefono());
                holder.precio.setText("Total: S/"+model.getTotal());
                holder.correo.setText("Correo: "+model.getCorreo()+"\n Dir: "+model.getDireccion());
                holder.fecha.setText("Fecha:"+model.getFecha()+" Hora: "+model.getHora());
                holder.boton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Toast.makeText(getContext(), "CORRECTO", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @NonNull
            @Override
            public OrdenesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.ordenes_layout,parent,false);
                OrdenesViewHolder viewHolder = new OrdenesViewHolder(view);
                return viewHolder;
            }
        };
        recicler.setAdapter(adapter);
        adapter.startListening();

    }
    public static class OrdenesViewHolder extends RecyclerView.ViewHolder{
        TextView nombre,numero,precio,correo,fecha;
        Button boton;
        public OrdenesViewHolder(@NonNull View itemView) {
            super(itemView);
            nombre=itemView.findViewById(R.id.ordenuser);
            numero=itemView.findViewById(R.id.ordenphone);
            precio=itemView.findViewById(R.id.ordenprecio);
            correo=itemView.findViewById(R.id.ordencorreo);
            fecha=itemView.findViewById(R.id.ordenfecha);
            boton=itemView.findViewById(R.id.ver_productos_orden);

        }
    }
}