package com.example.prax.vis;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class DataFillEmpty extends AppCompatActivity {


    Mydatabase db;
    EditText vehid;
    EditText addressid;
    EditText contactid;
    EditText nameid;

    public void savedmsg() {
        Toast.makeText(this, "Saved", Toast.LENGTH_SHORT).show();
        Intent i = new Intent(getApplicationContext(), MainActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_fill);
        showActionBar();
        vehid= (EditText)findViewById(R.id.labelvehidedittext);
        nameid= (EditText)findViewById(R.id.labelnameidedittext);
        contactid= (EditText)findViewById(R.id.labelcontactidedittext);
        addressid= (EditText)findViewById(R.id.labeladdressidedittext);
        Button savebtn = (Button)findViewById(R.id.savebutton);
        db = new Mydatabase(this);

        savebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String number = vehid.getText().toString();
                String name=nameid.getText().toString();
                String contact = contactid.getText().toString();
                String address = addressid.getText().toString();

                if(name.length()>0 && contact.length()>0 && address.length()>0){
                    for(int i=0;i<5;i++)
                        db.savenewItem(number,name,contact,address);
                    Log.i("Data","Saved");
                    savedmsg();

                }
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // todo: goto back activity from here

                Intent intent = new Intent(this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
                return true;


            default:
                return super.onOptionsItemSelected(item);
        }
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(DataFillEmpty.this, MainActivity.class));
    }

    private void showActionBar() {
        final ActionBar bar = getSupportActionBar();
        bar.setDisplayHomeAsUpEnabled(true);
        bar.setDisplayShowHomeEnabled(true);
    }
}
