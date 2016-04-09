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
import org.jsoup.select.Elements;

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
        ArrayList<HashMap<String,String>> cooks= Cooks.getCookies();
            System.out.println(cooks.size());
            setCookies(cooks.get(0));
            new LoadMBD().execute("");

        toolbar=(Toolbar)findViewById(R.id.anim_toolbar);
        image=(ImageView)findViewById(R.id.image);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        collapsingToolbarLayout=(CollapsingToolbarLayout)findViewById(R.id.collapsing_toolbar);
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
                Connection.Response res=Jsoup.connect("http://oucecareers.org/"+Url.substring(3)).followRedirects(false).ignoreContentType(true).timeout(10000).ignoreHttpErrors(true).execute();
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
                    switch(e.attr("name").toString()){
                        case "yoc":
                            details.year=e.val();
                            break;
                        case "name":
                            setTitle(e.val());
                            break;
                        case "course":
                            details.course=e.val();
                            break;
                        case "branch":
                            details.branch=e.val();
                            break;
                        case "dob":
                            details.dob = e.val();
                            break;
                        case "pob":
                            details.placeOfBirth = e.val();
                            break;
                        case "age":
                            details.age = e.val();
                            break;
                        case "nationality":
                            details.nationality = e.val();
                            break;
                        case "height":
                            details.height = e.val();
                            break;
                        case "eyesight":
                            details.eyesight = e.val();
                            break;
                        case "weight":
                            details.weight=e.val();
                            break;
                        case "fname":
                            details.father=e.val();
                            break;
                        case "occupation":
                            details.occupation=e.val();
                            break;
                        case "annualincome":
                            details.income = e.val();
                            break;
                        case "city1":
                            details.presentCity=e.val();
                            break;
                        case "city2":
                            details.permanentCity=e.val();
                            break;
                        case "state1":
                            details.presentState=e.val();
                            break;
                        case "state2":
                            details.permanentState=e.val();
                            break;
                        case "country1":
                            details.presentCountry=e.val();
                            break;
                        case "country2":
                            details.permanentCountry=e.val();
                            break;
                        case "pin1":
                            details.presentPin = e.val();
                            break;
                        case "pin2":
                            details.permanentPin = e.val();
                            break;
                        case "phone1":
                            details.phone1=e.val();
                            break;
                        case "phone2":
                            details.phone2=e.val();
                            break;
                        case "mail1":
                            details.mail1=e.val();
                            break;
                        case "mail2":
                            details.mail2=e.val();
                            break;

                    }
                }
                Elements addresses=doc.select("textarea");
                for(Element e: addresses){
                    Details details1=new Details();
                    if(e.attr("name").toString().equals("address1")){
                        System.out.println(e.val());
                        details.present=e.val();
                    }
                    else if(e.attr("name").toString().equals("address2")){
                        details.permanent=e.val();
                    }
                    detailList.add(details);
                }


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
