package com.example.prax.vis;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;


public class MainActivity extends AppCompatActivity {

    Mydatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        showActionBar();
            db  = new Mydatabase(this);
        Button gallery = (Button) findViewById(R.id.btngallery);
        Button cam = (Button) findViewById(R.id.btncam);
        Button add = (Button)findViewById(R.id.addmore);
        ImageView College = (ImageView) findViewById(R.id.nitlogo);
        College.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.addCategory(Intent.CATEGORY_BROWSABLE);
                intent.setData(Uri.parse("http://www.nitkkr.ac.in"));
                startActivity(intent);
            }
        });

        //db=new Mydatabase(this);
        if(!db.searchforserver("MM0AV8866"))
        for(int i=0;i<5;i++)
            db.addData();


        gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("*image/");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), 1);
            }
        });
        cam.setOnClickListener(new View.OnClickListener() {
                                   @Override
                                   public void onClick(View v) {
                                       Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                       startActivityForResult(intent, 0);
                                   }
        });
          //add button for adding new data
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, DataFill.class);
                startActivity(intent);
            }
        });
}
    @Override
    protected void onActivityResult(int requestcode, int resultcode, Intent data){
        super.onActivityResult(requestcode,resultcode,data);
        if (requestcode == 1) {
            Uri uri = data.getData();
            Intent intent = new Intent(MainActivity.this, ImageCaptureGal.class);
            intent.putExtra("imageURI", uri.toString());
            startActivity(intent);
            finish();
        } else {
            Bitmap bMap = (Bitmap) data.getExtras().get("data");
            ByteArrayOutputStream bStream = new ByteArrayOutputStream();
            bMap.compress(Bitmap.CompressFormat.PNG, 100, bStream);
            byte[] byteArray = bStream.toByteArray();
            Intent anotherIntent = new Intent(this, ImageCapture.class);
            anotherIntent.putExtra("image", byteArray);
            startActivity(anotherIntent);
            finish();
        }
    }

    private void showActionBar(){
        LayoutInflater inflater =(LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v =inflater.inflate(R.layout.actionbar_layout,null);
        final ActionBar bar=getSupportActionBar();
        bar.setDisplayHomeAsUpEnabled(false);
        bar.setDisplayShowHomeEnabled(false);
        bar.setDisplayShowCustomEnabled(true);
        bar.setDisplayShowTitleEnabled(false);
        bar.setCustomView(v);
    }
    boolean doubleBackToExitPressedOnce = false;
    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click back again to exit!", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 3000);
    }


}
