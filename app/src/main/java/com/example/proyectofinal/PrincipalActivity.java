package com.example.proyectofinal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.example.proyectofinal.Modal.Productos;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class PrincipalActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private FirebaseAuth auth;
    private String CurrentUserId;
    private DatabaseReference UserRef, PrductosRef;
    private String Telefono = "";
    private FloatingActionButton botonFlotante;
    private RecyclerView recyclerMenu;
    RecyclerView.LayoutManager layoutManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null){
            Telefono = bundle.getString("phone");
        }

        auth =  FirebaseAuth.getInstance();
        CurrentUserId = auth.getCurrentUser().getUid();
        UserRef = FirebaseDatabase.getInstance().getReference().child("Usuarios");
        PrductosRef = FirebaseDatabase.getInstance().getReference().child("Productos");
        recyclerMenu=findViewById(R.id.reciclre_menu);
        recyclerMenu.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerMenu.setLayoutManager(layoutManager);
        botonFlotante = (FloatingActionButton)findViewById(R.id.fab);
        botonFlotante.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PrincipalActivity.this,CarritoActivity.class);
                startActivity(intent);
            }
        });
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("The Corcho");
        setActionBar(toolbar);

        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this,drawerLayout,R.string.navigation_drawer_open,R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View headerView = navigationView.getHeaderView(0);

        TextView nombreHeader = (TextView) headerView.findViewById(R.id.usuario_nombre_perfil);
        CircleImageView imagenHeader = (CircleImageView) headerView.findViewById(R.id.usuario_imagen_perfil);

        UserRef.child(CurrentUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists() && snapshot.hasChild("imagen")){
                    String imagen = snapshot.child("imagen").getValue().toString();
                    nombreHeader.setText(snapshot.child("nombre").getValue().toString());
                    Picasso.get().load(imagen).error(R.drawable.home).into(imagenHeader);
                }else if (snapshot.exists()){
                    nombreHeader.setText(snapshot.child("nombre").getValue().toString());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser firebaseUser = auth.getCurrentUser();
        if (firebaseUser == null){
            EnviarAlLogin();
        }else {
            VerificarUsuarioExistente();
        }

        FirebaseRecyclerOptions<Productos> options = new FirebaseRecyclerOptions.Builder<Productos>()
                .setQuery(PrductosRef, Productos.class).build();

        FirebaseRecyclerAdapter<Productos,ProductosViewHolder>adapter = new FirebaseRecyclerAdapter<Productos, ProductosViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull ProductosViewHolder holder, int position, @NonNull Productos model) {

                holder.productoNom.setText(model.getNombre().toUpperCase());
                holder.productoCantidad.setText("Cantidad:"+model.getCantidad());
                holder.productoDescrip.setText(model.getDescripcion());
                holder.productoPrecio.setText("S/:"+model.getPreciovent());
                Picasso.get().load(model.getImagen()).into(holder.productoImagn);

                holder.productoImagn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(PrincipalActivity.this,ProductoDetallesActivity.class);
                        intent.putExtra("pid",model.getPid());
                        startActivity(intent);
                    }
                });

            }

            @NonNull
            @Override
            public ProductosViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.productos_item_layout,parent,false);
                ProductosViewHolder holder = new ProductosViewHolder(view);
                return holder;
            }
        };


        recyclerMenu.setAdapter(adapter);
        adapter.startListening();


    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START);
        }else {
            super.onBackPressed();
        }
    }

    private void VerificarUsuarioExistente() {
        final String CurrentUserId = auth.getCurrentUser().getUid();
        UserRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.hasChild(CurrentUserId)){
                    EnviarAlSetup();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(@NonNull Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.activity_principal_drawer,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
       int id = item.getItemId();

        if (id== R.id.nav_carrito){
            ActivityCarrito();
        }
        else if (id== R.id.nav_categorias){
            ActivityCategoria();
        }
        else if (id== R.id.nav_buscar){
            ActivityBuscar();
        }
       else if (id== R.id.nav_perfil){
            ActivityPerfil();
        }
       else if (id== R.id.nav_salir){
            auth.signOut();
            EnviarAlLogin();
        }

       DrawerLayout drawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
       drawerLayout.closeDrawer(GravityCompat.START);
       return true;
    }

    private void ActivityPerfil(){
        Toast.makeText(this, "Perfil", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(PrincipalActivity.this, PerfilActivity.class);
        startActivity(intent);
    }

    private void ActivityBuscar() {
        Toast.makeText(this, "Buscar", Toast.LENGTH_SHORT).show();
    }

    private void ActivityCategoria() {
        Toast.makeText(this, "Categorias", Toast.LENGTH_SHORT).show();
    }

    private void ActivityCarrito() {
        Toast.makeText(this, "Carrito", Toast.LENGTH_SHORT).show();
    }

    private void EnviarAlSetup() {
        Intent intent = new Intent(PrincipalActivity.this,SetupActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.putExtra("phone",Telefono);
        startActivity(intent);
        finish();
    }

    private void EnviarAlLogin(){
        Intent intent = new Intent(PrincipalActivity.this,LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }


}