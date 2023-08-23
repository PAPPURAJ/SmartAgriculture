package bd.ac.duet.myapplication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.widget.CompoundButton;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private TextView moisture,water;
    private Switch fertilizer;
    private ImageView imageView1;


    private GridView gridView;
    private ImageAdapter imageAdapter;
    private List<String> imageUrlList = new ArrayList<>();

    DatabaseReference database = FirebaseDatabase.getInstance().getReference();

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        moisture=findViewById(R.id.moistureTv);
        water=findViewById(R.id.waterTv);
        fertilizer=findViewById(R.id.fertPumpSw);
      //  imageView1=findViewById(R.id.image1);

        startService(new Intent(this,MyService.class));


        database.child("AnalogOutput").child("Moisture").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                moisture.setText("Moisture level: "+snapshot.getValue(String.class));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        database.child("AnalogOutput").child("Pump").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                water.setText("Water pump: "+(snapshot.getValue(String.class).equals("1")?"On":"Off"));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        fertilizer.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                database.child("Load").child("Fertilizer Pump").setValue(b?"1":"0");
            }
        });
        loadGrid();

    }


    void loadGrid(){
        gridView = findViewById(R.id.gridView);
        imageAdapter = new ImageAdapter(this, imageUrlList);
        gridView.setAdapter(imageAdapter);

        // Initialize Firebase Realtime Database reference
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        // Replace "images" with your reference path in the database

        database.getReference("Image").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                Log.e("========",snapshot.getValue(String.class));
                imageUrlList.add(snapshot.getValue(String.class));

                imageAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Log.e("========",snapshot.getValue(String.class));
                imageUrlList.add(snapshot.getValue(String.class));



                imageAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                imageUrlList.clear();
                imageAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }
}