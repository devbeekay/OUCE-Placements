package com.beekay.ouceplacements;


import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 */
public class UpdateMarks extends android.support.v4.app.Fragment {

    String roll;
    String course;
    FrameLayout frame;
    String semi1,semi2;
    HashMap<String,String> cookie;
    ProgressDialog progressDialog;
    NetCheck netCheck;

    public HashMap<String, String> getCookie() {
        return cookie;
    }

    public void setCookie(HashMap<String, String> cookie) {
        this.cookie = cookie;
    }

    public String getSemi1() {
        return semi1;
    }

    public void setSemi1(String semi1) {
        this.semi1 = semi1;
    }

    public String getSemi2() {
        return semi2;
    }

    public void setSemi2(String semi2) {
        this.semi2 = semi2;
    }

    public String getCourse() {
        return course;
    }

    public void setCourse(String course) {
        this.course = course;
    }

    public String getRoll() {
        return roll;
    }

    public void setRoll(String roll) {
        this.roll = roll;
    }

    public UpdateMarks() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        System.out.println("came to updsa");
        netCheck=new NetCheck();
        progressDialog=new ProgressDialog(getActivity());
        if(netCheck.isNetAvailable(getActivity()))
        new Updating().execute("");
        View view=inflater.inflate(R.layout.fragment_update_marks, container, false);
        frame=(FrameLayout)view.findViewById(R.id.update);
        return view;
    }


    public class Updating extends AsyncTask<String,String,String>{




        @Override
        protected String doInBackground(String... params) {
            publishProgress("");
            ArrayList<Map<String,String>> cooks= (ArrayList<Map<String, String>>) getArguments().getSerializable("cookies");
            setCookie((HashMap<String,String>)cooks.get(0));
            try {
                Document notDoc= Jsoup.connect("http://oucecareers.org/students/updatemarks.php").followRedirects(false).cookies(getCookie()).get();
                System.out.println(notDoc);
                Elements buttonText=notDoc.getElementsByTag("input");
                for(Element input:buttonText){
                    if(input.attr("id").toString().equals("pgsem1")){
                        setSemi1(input.attr("value").toString());
                    }
                    else if(input.attr("id").toString().equals("pgsem2")){
                        setSemi2(input.attr("value").toString());
                    }
                    else if(input.attr("type").toString().equals("hidden")){
                        setRoll(input.attr("value").toString());
                    }
                    else if(input.attr("type").toString().equals("button")){
                        int start=input.attr("onClick").toString().indexOf(",'")+2;
                        int end=input.attr("onClick").toString().indexOf(")")-1;
                        setCourse(input.attr("onClick").toString().substring(start,end));
                        System.out.println(input.attr("onClick").toString().substring(start,end));
                    }

                }

            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setIndeterminate(true);
            progressDialog.setCancelable(false);
            progressDialog.setMessage("Getting data");
            progressDialog.show();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            progressDialog.dismiss();
            if(getCourse().equals("ME") || getCourse().equals("MTECH")){
                LinearLayout layout=new LinearLayout(getActivity().getApplicationContext());
                layout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                layout.setOrientation(LinearLayout.VERTICAL);
                final EditText sem1=new EditText(getActivity().getApplicationContext());
                sem1.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                sem1.setHint("Enter Sem one percentage");
                sem1.setHintTextColor(getResources().getColor(android.R.color.black));
                sem1.setTextColor(getResources().getColor(android.R.color.black));
                sem1.setText("" + Float.parseFloat(getSemi1()));
                sem1.setRawInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL | InputType.TYPE_CLASS_NUMBER);
                final EditText sem2=new EditText(getActivity().getApplicationContext());
                sem2.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                sem2.setHint("Enter Sem two percentage");
                sem2.setHintTextColor(getResources().getColor(android.R.color.black));
                sem2.setRawInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL | InputType.TYPE_CLASS_NUMBER);
                sem2.setTextColor(getResources().getColor(android.R.color.black));
                sem2.setText("" + Float.parseFloat(getSemi2()));
                final Button submit=new Button(getActivity().getApplicationContext());
                submit.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                submit.setText("Update");
                final TextView agg=new TextView(getActivity().getApplicationContext());
                agg.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                agg.setTextColor(getResources().getColor(android.R.color.black));
                layout.addView(sem1, 0);
                layout.addView(sem2, 1);
                layout.addView(submit, 2);
                layout.addView(agg,3);
                frame.addView(layout);
                submit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (netCheck.isNetAvailable(getActivity().getApplicationContext())) {
                            if (sem2.getText().toString().equals(null) || sem2.getText().toString().equals("0") || sem2.getText().toString().equals("0.00") || sem2.getText().toString().equals("0.0") || sem2.getText().toString().equals("0.") || sem2.getText().toString().equals("")) {
                                if (sem1.getText().toString().equals(null) || sem1.getText().toString().equals("0") || sem1.getText().toString().equals("0.00") || sem1.getText().toString().equals("0.0") ||sem1.getText().toString().equals("0.") || sem1.getText().toString().equals("")) {
                                    agg.setText("Enter your marks correctly");
                                } else {
                                    agg.setText(sem1.getText().toString());
                                    new Update().execute(sem1.getText().toString(), sem2.getText().toString(), sem1.getText().toString());
                                }
                            } else if (sem1.getText().toString().equals(null) || sem1.getText().toString().equals("0") || sem1.getText().toString().equals("0.00") || sem1.getText().toString().equals("0.0") ||sem1.getText().toString().equals("0.") || sem1.getText().toString().equals("")) {
                                agg.setText("Enter sem one marks!!!");
                            } else {
                                agg.setText("" + ((Float.parseFloat(sem1.getText().toString()) + Float.parseFloat(sem2.getText().toString())) / 2.0));
                                new Update().execute(sem1.getText().toString(), sem2.getText().toString(), agg.getText().toString());
                            }


                        }
                        else{
                            Toast.makeText(getActivity().getApplicationContext(),"Check your network connection",Toast.LENGTH_SHORT).show();
                        }
                    }

                });
            }
        }
    }

    public class Update extends AsyncTask<String,String,String>{

        @Override
        protected String doInBackground(String... params) {
            try {
                publishProgress("");
                Connection.Response response=Jsoup.connect("http://oucecareers.org/students/updatemarksaction.php?rollno="+getRoll()+"&pgsem1="+params[0]+"&pgsem2="+params[1]+"&course="+getCourse()+"&avggpa="+params[2]).cookies(getCookie()).execute();
                System.out.println(response.toString());
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setIndeterminate(true);
            progressDialog.setCancelable(false);
            progressDialog.setMessage("Updating");
            progressDialog.show();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            progressDialog.dismiss();
        }
    }

}
