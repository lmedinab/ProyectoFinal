package com.example.proyectofinal;

import android.content.ClipData;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ProViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView proNom, proDescrip, proPrecio, proCantidad,proPrecioCom;
    public ImageView proImagn;
    private ItemClickListener listener;

    public ProViewHolder(@NonNull View itemView) {
        super(itemView);

        proNom = (TextView) itemView.findViewById(R.id.pro_nombre);
        proDescrip = (TextView) itemView.findViewById(R.id.pro_descripcion);
        proPrecio = (TextView) itemView.findViewById(R.id.pro_precio);
        proPrecioCom = (TextView)itemView.findViewById(R.id.pro_preciocom);
        proCantidad = (TextView) itemView.findViewById(R.id.pro_cantidad);
        proImagn = (ImageView) itemView.findViewById(R.id.pro_imagen);
    }

    public void setItemClickListener(ItemClickListener listener){
        this.listener = listener;
    }

    @Override
    public void onClick(View view) {
        listener.onClick(view,getAdapterPosition(),false);
    }
}
