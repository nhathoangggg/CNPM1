package com.example.testled;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class MainActivity extends AppCompatActivity {
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Kết nối Firebase Realtime Database
        databaseReference = FirebaseDatabase.getInstance("https://my1stproject-90f1d-default-rtdb.asia-southeast1.firebasedatabase.app").getReference();

        Button btnOn = findViewById(R.id.btnOn);
        Button btnautoOn = findViewById(R.id.autoOn);
        TextView txtHumiValue = findViewById(R.id.Humi);
        TextView txtTempValue = findViewById(R.id.Nhiet);
        TextView txtEarthValue = findViewById(R.id.Earth);
        TextView txtPumpValue = findViewById(R.id.status);

        // Bật đèn (ghi giá trị 1 vào Firebase)
        btnOn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(btnOn.getText().equals("Tắt máy bơm")){
                    databaseReference.child("led").setValue(0);
                    btnOn.setText("Bật máy bơm");
                }
                else{
                    databaseReference.child("led").setValue(1);
                    btnOn.setText("Tắt máy bơm");
                }
            }
        });

        btnautoOn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(btnautoOn.getText().equals("Auto Mode: On")){
                    databaseReference.child("Auto").setValue(0);
                    btnautoOn.setText("Auto Mode: Off");
                }
                else{
                    databaseReference.child("Auto").setValue(1);
                    btnautoOn.setText("Auto Mode: On");
                }
            }
        });

        databaseReference.child("led").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists() && snapshot.getValue() != null) {
                    Integer ledValue = snapshot.getValue(Integer.class);
                    if(ledValue==null)
                        databaseReference.child("led").setValue(0);
                    if(ledValue==1)
                        btnOn.setText("Tắt máy bơm");
                    else
                        btnOn.setText("Bật máy bơm");
                } else {
                    txtPumpValue.setText("NaN");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Xảy ra lỗi khi đọc dữ liệu
                android.util.Log.e("FirebaseError", "Lỗi khi đọc giá trị máy bơm: " + error.getMessage());
                txtPumpValue.setText("NaN");
            }
        });

        databaseReference.child("Auto").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists() && snapshot.getValue() != null) {
                    Integer autoValue = snapshot.getValue(Integer.class);
                    if(autoValue==null){
                        databaseReference.child("Auto").setValue(0);
                    }
                    if(autoValue==1)
                        btnautoOn.setText("Auto Mode: On");
                    else
                        btnautoOn.setText("Auto Mode: Off");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Xảy ra lỗi khi đọc dữ liệu
                android.util.Log.e("FirebaseError", "Lỗi khi đọc giá trị auto: " + error.getMessage());
                btnautoOn.setText("NaN");
            }
        });

        databaseReference.child("Humi").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists() && snapshot.getValue() != null) {
                    String humiValue = snapshot.getValue().toString();
                    txtHumiValue.setText(humiValue+" %");
                } else {
                    txtHumiValue.setText("NaN");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Xảy ra lỗi khi đọc dữ liệu
                android.util.Log.e("FirebaseError", "Lỗi khi đọc giá trị Humi: " + error.getMessage());
                txtHumiValue.setText("NaN");
            }
        });

        databaseReference.child("Temp").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists() && snapshot.getValue() != null) {
                    String tempValue = snapshot.getValue().toString();
                    txtTempValue.setText(tempValue+" ℃");
                } else {
                    txtTempValue.setText("NaN");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Xảy ra lỗi khi đọc dữ liệu
                android.util.Log.e("FirebaseError", "Lỗi khi đọc giá trị Temp: " + error.getMessage());
                txtTempValue.setText("NaN");
            }
        });

        databaseReference.child("Earth").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists() && snapshot.getValue() != null) {
                    String earthValue = snapshot.getValue().toString();
                    txtEarthValue.setText(earthValue+" %");
                } else {
                    txtEarthValue.setText("NaN");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Xảy ra lỗi khi đọc dữ liệu
                android.util.Log.e("FirebaseError", "Lỗi khi đọc giá trị Earth: " + error.getMessage());
                txtEarthValue.setText("NaN");
            }
        });

        databaseReference.child("led").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists() && snapshot.getValue() != null) {
                    String pumpValue = snapshot.getValue().toString();
                    if(pumpValue.equals("1")){
                        txtPumpValue.setText("On");
                    }
                    else
                        txtPumpValue.setText("Off");
                } else {
                    txtPumpValue.setText("NaN");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Xảy ra lỗi khi đọc dữ liệu
                android.util.Log.e("FirebaseError", "Lỗi khi đọc giá trị máy bơm: " + error.getMessage());
                txtPumpValue.setText("NaN");
            }
        });
    }
}