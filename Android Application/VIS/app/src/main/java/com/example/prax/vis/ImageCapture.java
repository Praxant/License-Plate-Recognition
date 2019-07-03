package com.example.prax.vis;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.AnimationDrawable;
import android.icu.text.Normalizer2;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.wang.avi.AVLoadingIndicatorView;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
public class ImageCapture extends AppCompatActivity {

    Bitmap bmp;
    Mydatabase db;
    String number;
    AVLoadingIndicatorView av;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.image_cap_cam);
        //actionbar
        av=findViewById(R.id.avi);


        av.hide();



        final ActionBar bar=getSupportActionBar();
        bar.setDisplayHomeAsUpEnabled(true);
        bar.setDisplayShowHomeEnabled(true);
        db=new Mydatabase(this);

        final Button send=findViewById(R.id.send);
        ImageView img=findViewById(R.id.img_cap);


        //////


        byte[] byteArray = getIntent().getByteArrayExtra("image");
        bmp = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
        img.setImageBitmap(roundCornerImage(bmp,10));
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    av.show();
                    sendPhoto(bmp);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                //startActivity(new Intent(ImageCapture.this,ImageResult.class));
            }
        });


    }
    public Bitmap roundCornerImage(Bitmap raw, float round) {
        int width = raw.getWidth();
        int height = raw.getHeight();
        Bitmap result = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(result);
        canvas.drawARGB(0, 0, 0, 0);

        final Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(Color.parseColor("#000000"));

        final Rect rect = new Rect(0, 0, width, height);
        final RectF rectF = new RectF(rect);

        canvas.drawRoundRect(rectF, round, round, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(raw, rect, rect, paint);

        return result;
    }
    private void sendPhoto(Bitmap bitmap) throws Exception {
        new UploadTask().execute(bitmap);
    }

    private class UploadTask extends AsyncTask<Bitmap, Void, Void> {

        protected Void doInBackground(Bitmap... bitmaps) {
            if (bitmaps[0] == null)
                return null;


            String ImageString=encodeImage(bitmaps[0]);


            JSONObject jsonObject=new JSONObject();
            try {
                jsonObject.put("image",ImageString);
            } catch (Exception e) {
                e.printStackTrace();
            }

            DefaultHttpClient httpclient = new DefaultHttpClient();
            try {
                HttpPost httppost = new HttpPost(
                        "http://172.16.172.170:5000/hello"); // server

                StringEntity se;
                se = new StringEntity(jsonObject.toString());
                httppost.setEntity(se);

                httppost.setHeader("Content-type", "application/json");

                HttpResponse response = null;

                try {
                    response = httpclient.execute(httppost);
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                try {
                    if (response != null) {


                        String result = response.getStatusLine().toString();
                    }
                    String result="";

                            try {
                                BufferedReader rd = new BufferedReader(new InputStreamReader(
                                        response.getEntity().getContent()));
                                String line = "";
                                while ((line = rd.readLine()) != null) {
                                    result = result + line;
                                }
                                Log.i("msgr", result);
                                number=result;



                            } catch (Exception e) {
                                number = "error";
                            }
                        } finally {

                        }
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } finally {

            }
            return null;
        }




        @Override
        protected void onPostExecute(Void result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);
            after_Search(number);
        }
    }

    public byte[] getBytesFromBitmap(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 70, stream);
        return stream.toByteArray();
    }
    public String encodeImage(Bitmap imageByteArray) {
        return android.util.Base64.encodeToString(getBytesFromBitmap(imageByteArray),
                android.util.Base64.NO_WRAP);
    }

    //network
   /*private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }*/

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(ImageCapture.this,MainActivity.class));
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


    public void after_Search(final String number){

        av.hide();
        if(number.equals("error")){
            new AlertDialog.Builder(this)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setTitle("SORRY :(")
                    .setMessage("The server is Probably Down")
                    .setPositiveButton("Try Again", new DialogInterface.OnClickListener() {
                        @Override//if the user want to try again with the same image
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    })
                    .setNegativeButton("Go To Home", new DialogInterface.OnClickListener() {
                        @Override// if the user want to go to the home screen
                        public void onClick(DialogInterface dialog, int which) {
                            Intent i=new Intent(getApplicationContext(), MainActivity.class);
                            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(i);
                        }
                    })
                    .show();
        }
        else{
            Boolean resultfound= db.searchforserver(number);

            Log.i("databasesearch",resultfound.toString());

            if(resultfound==true){
                new AlertDialog.Builder(this)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle("Match Found in the Databasee ")
                        .setMessage("The vehicle number is already present in the databse")
                        .setPositiveButton("Go To Profile", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(ImageCapture.this, DataFill.class);
                                intent.putExtra("number", number);
                                startActivity(intent);
                            }
                        })
                        .setNegativeButton("Back to Image", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .show();
            }


            else
            {
                new AlertDialog.Builder(this)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle("Unable to find the Information ")
                        .setMessage("The vehicle has not been registered to the database")
                        .setPositiveButton("Register", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(ImageCapture.this, DataFill.class);
                                intent.putExtra("number", number);
                                startActivity(intent);
                            }
                        })
                        .setNegativeButton("Back to Image", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .show();

            }
        }
    }
}
