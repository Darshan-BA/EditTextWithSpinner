package com.ba.datawithtype;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.ba.edittextwithspinner.EditTextWithSpinner;
import com.ba.edittextwithspinner.FieldData;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static String TAG="BA";
    List<FieldData> phoneNumbers,emails,dates;
    private EditTextWithSpinner phone,email,date;
    private Button save;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        phone=findViewById(R.id.phoneNumbers);
        email=findViewById(R.id.emails);
        date=findViewById(R.id.events);
        save=findViewById(R.id.saveBut);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                phoneNumbers=phone.getTypeAndData();
                emails=email.getTypeAndData();
                dates=date.getTypeAndData();
                for (FieldData p:phoneNumbers){
                    Log.d(TAG,"type= "+p.getType()+" phoneNumber="+p.getData());
                }
                for (FieldData p:emails){
                    Log.d(TAG,"type= "+p.getType()+" emails="+p.getData());
                }
                for (FieldData p:dates){
                    Log.d(TAG,"type= "+p.getType()+" dates="+p.getData());
                }
            }
        });

    }
}