package com.beekay.ouceplacements;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

public class Mbd extends AppCompatActivity {
    Toolbar toolbar;
    ImageView image;
    String title;
    CollapsingToolbarLayout collapsingToolbarLayout;
    RecyclerView recyclerView;
    ArrayList<Details> detailList;

    public String getMyTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    Bitmap bitmap;

    public HashMap<String, String> getCookies() {
        return cookies;
    }

    public void setCookies(HashMap<String, String> cookies) {
        this.cookies = cookies;
    }

    HashMap<String,String> cookies;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mbd);
        Intent intent=getIntent();
        ArrayList<HashMap<String,String>> cooks=(ArrayList<HashMap<String,String>>)intent.getSerializableExtra("cookies");
            System.out.println(cooks.size());
            setCookies(cooks.get(0));
            new LoadMBD().execute("");

        toolbar=(Toolbar)findViewById(R.id.anim_toolbar);
        image=(ImageView)findViewById(R.id.image);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        collapsingToolbarLayout=(CollapsingToolbarLayout)findViewById(R.id.collapsing_toolbar);

       // collapsingToolbarLayout.setSystemUiVisibility(View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_mbd, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public class LoadMBD extends AsyncTask<String,String,ArrayList<Details>>{
        ProgressDialog progressDialog;

        @Override
        protected ArrayList<Details> doInBackground(String... params) {
            publishProgress("");
            detailList=new ArrayList<>();
            Details details=new Details();
            details.roll=NetCheck.getUser();
            try{
                Document doc= Jsoup.connect("http://oucecareers.org/students/showMbd.php?rollno=" + NetCheck.getUser()).cookies(getCookies()).get();
                String Url=doc.select("img").attr("src").toString();
                Connection.Response res=Jsoup.connect("http://oucecareers.org/"+Url.substring(3)).followRedirects(false).ignoreContentType(true).ignoreHttpErrors(true).execute();
                if(res.statusCode()==200) {
                    BufferedInputStream in = new BufferedInputStream((new URL("http://oucecareers.org/"+Url.substring(3))).openStream());
                    setBitmap(BitmapFactory.decodeStream(in));
                    in.close();
                }
                else if(res.statusCode()==404){
                    System.out.println("not found");
                }

                details.gender=doc.select("select > option[selected=\"selected\"").text();
                details.dream=doc.select("div#companyholder").text();
                for(Element  e: doc.select("input")){
                    if(e.attr("name").toString().equals("yoc")){
                        details.year=e.val();
                    }
                    else if(e.attr("name").toString().equals("name")){
                        setTitle(e.val());
                    }
                    else if(e.attr("name").toString().equals("course")){
                        details.course=e.val();
                    }
                    else if(e.attr("name").toString().equals("branch")){
                        details.branch=e.val();
                    }
                    else if(e.attr("name").toString().equals("dob")){
                        details.dob=e.val();
                    }
                    else if(e.attr("name").toString().equals("pob")){
                        details.placeOfBirth=e.val();
                    }
                    else if(e.attr("name").toString().equals("age")){
                        details.age=e.val();
                    }
                    else if(e.attr("name").toString().equals("nationality")){
                        details.nationality=e.val();
                    }
                    else if(e.attr("name").toString().equals("height")){
                        details.height=e.val();

                    }
                    else if(e.attr("name").toString().equals("weight")){
                        details.weight=e.val();
                    }
                    else if(e.attr("name").toString().equals("eyesight")){
                        details.eyesight=e.val();
                    }
                    else if(e.attr("name").toString().equals("fname")){
                        details.father=e.val();
                    }
                    else if(e.attr("name").toString().equals("occupation")){
                        details.occupation=e.val();
                    }
                    else if(e.attr("name").toString().equals("annualincome")){
                        details.income=e.val();
                    }
                }

                detailList.add(details);
                return detailList;
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            progressDialog=new ProgressDialog(Mbd.this);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setMessage("Loading MBD");
            progressDialog.setIndeterminate(true);
            progressDialog.setCancelable(false);
            progressDialog.show();

        }

        @Override
        protected void onPostExecute(ArrayList<Details> s) {
            progressDialog.dismiss();
            if(s!=null){
                image.setImageBitmap(getBitmap());
                getSupportActionBar().setTitle(getMyTitle());
                collapsingToolbarLayout.setTitle(getMyTitle());
                recyclerView=(RecyclerView)findViewById(R.id.mbdcardlist);
                recyclerView.setHasFixedSize(false
                );
                LinearLayoutManager llm=new LinearLayoutManager(Mbd.this);
                llm.setOrientation(LinearLayoutManager.VERTICAL);
                recyclerView.setLayoutManager(llm);
                RecyclerView.Adapter adapter=new DetailAdapter(s);
                recyclerView.setAdapter(adapter);
            }
            super.onPostExecute(s);
        }
    }
}
