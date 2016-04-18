package com.beekay.ouceplacements;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class ApplyJobActivity extends AppCompatActivity implements View.OnClickListener {

    Button apply;
    Button cancel;
    String jobId;
    ArrayList<HashMap<String,String>> cooks;
    ArrayList<String> names;
    ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_apply_job);
        Toolbar toolbar = (Toolbar) findViewById(R.id.tool);
        toolbar.setTitle("Apply");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Intent intent = getIntent();
        cooks=Cooks.getCookies();
        if(cooks==null)
            finish();
        jobId = intent.getStringExtra("position");
        GetDetails details = new GetDetails();
        details.execute(jobId);
        apply = (Button) findViewById(R.id.applyvalue);
        cancel = (Button) findViewById(R.id.cancelname);
        apply.setOnClickListener(this);
        cancel.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {

     if(view.getId()==R.id.applyvalue){
         Apply applyJob = new Apply();
         applyJob.execute(jobId);
     }
    }

    @Override
    protected void onRestart() {
        try {
            HashMap<String,String> cookies = Cooks.getCookies().get(0);
        }catch (NullPointerException ex){
            finish();
        }
        super.onRestart();
    }

    public class GetDetails extends AsyncTask<String, String, String>{

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            progressDialog = new ProgressDialog(ApplyJobActivity.this);
            progressDialog.setMessage("Loading...");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                publishProgress("");
                Document doc = Jsoup.connect("http://oucecareers.org/students/applyjob.php?jobid="+strings[0]).cookies(cooks.get(0)).followRedirects(false).timeout(50000).get();
                Elements table = doc.select("table");
                names = new ArrayList<>();
                int i = 0;
                for(Element tr : table){
                    for(Element td : tr.select("td")){
                        if(i%2!=0) {
                            names.add(td.text());
                        }
                        i++;
                    }
                }
                return "success";
            } catch (IOException e) {
                return null;
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            progressDialog.hide();
            if(s.equals(null)){
                Toast.makeText(ApplyJobActivity.this,"Timed out while connecting",Toast.LENGTH_LONG).show();
            }
            else {
                TextView comp = (TextView) findViewById(R.id.cvalue);
                comp.setText(names.get(0));
                TextView desig = (TextView) findViewById(R.id.dvalue);
                desig.setText(names.get(1));
                TextView rType = (TextView) findViewById(R.id.rvalue);
                rType.setText(names.get(2));
                TextView cut = (TextView) findViewById(R.id.cutvalue);
                cut.setText(names.get(3));
                TextView pay = (TextView) findViewById(R.id.pvalue);
                pay.setText(names.get(4));
                TextView bond = (TextView) findViewById(R.id.bvalue);
                bond.setText(names.get(5));
                TextView note = (TextView) findViewById(R.id.nvalue);
                note.setText(names.get(6));
            }
        }
    }

    public class Apply extends AsyncTask<String, String, Boolean>{
        String s = null;
        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            progressDialog = new ProgressDialog(ApplyJobActivity.this);
            progressDialog.setMessage("Loading...");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected Boolean doInBackground(String... strings) {
            try {
                Document doc = Jsoup.connect("http://oucecareers.org/students/applyjobaction.php?jobid="+strings[0]).cookies(cooks.get(0)).followRedirects(false).timeout(50000).get();
                return doc.body().text().equalsIgnoreCase("Successfully Applied");
            } catch (IOException e) {
                s="timed out";
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            progressDialog.hide();
            if (s != null) {
                if (aBoolean) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(ApplyJobActivity.this).setMessage("Applied Successfully").setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            finish();
                        }
                    });
                    builder.create().show();
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(ApplyJobActivity.this).setMessage("Applying failed.\nTry again later.").setNegativeButton("Okay", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            finish();
                        }
                    });
                    builder.create().show();
                }
            }
            else{
                Toast.makeText(ApplyJobActivity.this,"Timed out while connecting",Toast.LENGTH_LONG).show();
            }
        }
    }
}
