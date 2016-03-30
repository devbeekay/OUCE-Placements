package com.beekay.ouceplacements;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.internal.view.ContextThemeWrapper;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

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
        cooks=(ArrayList<HashMap<String,String>>)intent.getSerializableExtra("cookie");
        jobId = intent.getStringExtra("position");

        System.out.println(cooks.size());
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
         System.out.println("apply clicked");
         Apply applyJob = new Apply();
         applyJob.execute(jobId);
     }
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
                Document doc = Jsoup.connect("http://oucecareers.org/students/applyjob.php?jobid="+strings[0]).cookies(cooks.get(0)).followRedirects(false).get();
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
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

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
            progressDialog.hide();
        }
    }

    public class Apply extends AsyncTask<String, String, Boolean>{

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
                Document doc = Jsoup.connect("http://oucecareers.org/students/applyjobaction.php?jobid="+strings[0]).cookies(cooks.get(0)).followRedirects(false).get();
                if(doc.body().text().equalsIgnoreCase("Successfully Applied")){
                    return true;
                }
                else
                    return false;
            } catch (IOException e) {
                e.printStackTrace();
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            progressDialog.hide();
            if(aBoolean){
                AlertDialog.Builder builder = new AlertDialog.Builder(ApplyJobActivity.this).setMessage("Applied Successfully").setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                });
                builder.create().show();
            }
            else{
                AlertDialog.Builder builder = new AlertDialog.Builder(ApplyJobActivity.this).setMessage("Applying failed.\nTry again later.").setNegativeButton("Okay", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                });
                builder.create().show();
            }
        }
    }
}
