package com.example.proyectofinal;

import android.content.ClipData;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ProductosViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView productoNom, productoDescrip, productoPrecio, productoCantidad;
    public ImageView productoImagn;
    private ItemClickListener listener;
    public ProductosViewHolder(@NonNull View itemView) {
        super(itemView);

        productoNom = (TextView) itemView.findViewById(R.id.producto_nombre);
        productoDescrip = (TextView) itemView.findViewById(R.id.producto_descripcion);
        productoPrecio = (TextView) itemView.findViewById(R.id.producto_precio);
        productoCantidad = (TextView) itemView.findViewById(R.id.producto_cantidad);
        productoImagn = (ImageView) itemView.findViewById(R.id.producto_imagen);
    }

    public void setItemClickListener(ItemClickListener listener){
        this.listener = listener;
    }

    @Override
    public void onClick(View view) {
        listener.onClick(view,getAdapterPosition(),false);
    }
}
