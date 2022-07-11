package com.example.proyectofinal;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

public class FragmentUno extends Fragment {

    private View fragmento;
    private ImageView cerveza,pisco,wisky,rones;
    private ImageView tragos,energizante,gaseosas,agua;

    public FragmentUno() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        fragmento = inflater.inflate(R.layout.fragment_uno, container, false);


        cerveza = (ImageView) fragmento.findViewById(R.id.cerveza);
        pisco = (ImageView) fragmento.findViewById(R.id.pisco);
        wisky = (ImageView) fragmento.findViewById(R.id.wisky);
        rones = (ImageView) fragmento.findViewById(R.id.rones);
        tragos = (ImageView) fragmento.findViewById(R.id.tragos);
        energizante = (ImageView) fragmento.findViewById(R.id.energizante);
        gaseosas = (ImageView) fragmento.findViewById(R.id.gaseosas);
        agua = (ImageView) fragmento.findViewById(R.id.agua);

        cerveza.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(),AgregarproductoActivity.class);
                intent.putExtra("categoria","cerveza");
                startActivity(intent);
            }
        });
        pisco.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(),AgregarproductoActivity.class);
                intent.putExtra("categoria","pisco");
                startActivity(intent);
            }
        });
        wisky.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(),AgregarproductoActivity.class);
                intent.putExtra("categoria","wisky");
                startActivity(intent);
            }
        });
        rones.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(),AgregarproductoActivity.class);
                intent.putExtra("categoria","rones");
                startActivity(intent);
            }
        });
        tragos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(),AgregarproductoActivity.class);
                intent.putExtra("categoria","tragos");
                startActivity(intent);
            }
        });
        energizante.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(),AgregarproductoActivity.class);
                intent.putExtra("categoria","energizante");
                startActivity(intent);
            }
        });
        gaseosas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(),AgregarproductoActivity.class);
                intent.putExtra("categoria","gaseosas");
                startActivity(intent);
            }
        });
        agua.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(),AgregarproductoActivity.class);
                intent.putExtra("categoria","agua");
                startActivity(intent);
            }
        });



        return fragmento;
    }
}