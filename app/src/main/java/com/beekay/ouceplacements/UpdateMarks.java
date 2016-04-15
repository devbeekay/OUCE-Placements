package com.beekay.ouceplacements;


import android.app.Fragment;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
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
    String bsem1, bsem2, bsem3, bsem4, bsem5, bsem6, bsem7, bsem8;
    EditText bs1, bs2, bs3, bs4, bs5, bs6, bs7, bs8;
    View view;
    boolean loaded=false;
    Button beUpdate;
    TextView bemarks;

    public UpdateMarks() {
        // Required empty public constructor
    }

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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        System.out.println("came to updsa");
        netCheck=new NetCheck();
        progressDialog=new ProgressDialog(getActivity());
        if(netCheck.isNetAvailable(getActivity()))
        new Updating().execute("");
        while(!loaded){

        }
        if(getCourse().equalsIgnoreCase("ME") || getCourse().equalsIgnoreCase("MTECH")) {
            view = inflater.inflate(R.layout.fragment_update_marks, container, false);
            frame = (FrameLayout) view.findViewById(R.id.update);
        }else if(getCourse().equalsIgnoreCase("BE")){
            view = inflater.inflate(R.layout.be_marks, container, false);
            bs1 = (EditText) view.findViewById(R.id.s1value);
            bs2 = (EditText) view.findViewById(R.id.s2value);
            bs3 = (EditText) view.findViewById(R.id.s3value);
            bs4 = (EditText) view.findViewById(R.id.s4value);
            bs5 = (EditText) view.findViewById(R.id.s5value);
            bs6 = (EditText) view.findViewById(R.id.s6value);
            bs7 = (EditText) view.findViewById(R.id.s7value);
            bs8 = (EditText) view.findViewById(R.id.s8value);
            beUpdate = (Button) view.findViewById(R.id.beupdate);
            bemarks = (TextView) view.findViewById(R.id.marksbe);
        }
        else{
            view = inflater.inflate(R.layout.mcamarks, container, false);
            bs1 = (EditText) view.findViewById(R.id.ms1value);
            bs2 = (EditText) view.findViewById(R.id.ms2value);
            bs3 = (EditText) view.findViewById(R.id.ms3value);
            bs4 = (EditText) view.findViewById(R.id.ms4value);
            bs5 = (EditText) view.findViewById(R.id.ms5value);
            bs6 = (EditText) view.findViewById(R.id.ms6value);
            beUpdate = (Button) view.findViewById(R.id.mcaupdate);
            bemarks = (TextView) view.findViewById(R.id.marksmca);
        }
        return view;
    }


    public class Updating extends AsyncTask<String,String,String>{




        @Override
        protected String doInBackground(String... params) {
            publishProgress("");
            ArrayList<HashMap<String,String>> cooks= Cooks.getCookies();
            setCookie(cooks.get(0));
            try {
                Document notDoc= Jsoup.connect("http://oucecareers.org/students/updatemarks.php").followRedirects(false).cookies(getCookie()).get();
                Elements buttonText=notDoc.getElementsByTag("input");
                Element but = buttonText.tagName("button").first();
                String butt = buttonText.attr("onClick");
                int s = butt.indexOf(",'")+2;
                int e = butt.indexOf(")")-1;
                setCourse(butt.substring(s,e));
                loaded=true;
                if(getCourse().equalsIgnoreCase("ME") || getCourse().equalsIgnoreCase("MTECH")) {
                    for (Element input : buttonText) {
                        if (input.attr("id").toString().equals("pgsem1")) {
                            setSemi1(input.attr("value").toString());
                        } else if (input.attr("id").toString().equals("pgsem2")) {
                            setSemi2(input.attr("value").toString());
                        } else if (input.attr("type").toString().equals("hidden")) {
                            setRoll(input.attr("value").toString());
                        }
                    }
                }
                else if (getCourse().equalsIgnoreCase("BE")){
                    for (Element input : buttonText) {
                        if (input.attr("id").toString().equals("sem1gpa")) {
                            bsem1 = input.attr("value").toString();
                        } else if (input.attr("id").toString().equals("sem2gpa")) {
                            bsem2 = input.attr("value").toString();
                        } else if (input.attr("id").toString().equals("sem3gpa")) {
                            bsem3 = input.attr("value").toString();
                        } else if (input.attr("id").toString().equals("sem4gpa")) {
                            bsem4 = input.attr("value").toString();
                        } else if (input.attr("id").toString().equals("sem5gpa")) {
                            bsem5 = input.attr("value").toString();
                        } else if (input.attr("id").toString().equals("sem6gpa")) {
                            bsem6 = input.attr("value").toString();
                        } else if (input.attr("id").toString().equals("sem7gpa")) {
                            bsem7 = input.attr("value").toString();
                        } else if (input.attr("id").toString().equals("sem8gpa")) {
                            bsem8 = input.attr("value").toString();
                        } else if (input.attr("type").toString().equals("hidden")) {
                            setRoll(input.attr("value").toString());
                        }
                    }
                }
                else if(getCourse().equalsIgnoreCase("MCA")){
                    for (Element input : buttonText) {
                        if (input.attr("id").toString().equals("sem1gpa")) {
                            bsem1 = input.attr("value").toString();
                        } else if (input.attr("id").toString().equals("sem2gpa")) {
                            bsem2 = input.attr("value").toString();
                        } else if (input.attr("id").toString().equals("sem3gpa")) {
                            bsem3 = input.attr("value").toString();
                        } else if (input.attr("id").toString().equals("sem4gpa")) {
                            bsem4 = input.attr("value").toString();
                        } else if (input.attr("id").toString().equals("sem5gpa")) {
                            bsem5 = input.attr("value").toString();
                        } else if (input.attr("id").toString().equals("sem6gpa")) {
                            bsem6 = input.attr("value").toString();
                        } else if (input.attr("type").toString().equals("hidden")) {
                            setRoll(input.attr("value").toString());
                        }
                    }
                }
                else{
                    return getCourse();
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
            else if(getCourse().equalsIgnoreCase("BE") || getCourse().equalsIgnoreCase("BTECH")){
                bs1.setText(bsem1);
                bs1.setFocusable(false);
                bs1.setKeyListener(null);
                bs2.setText(bsem2);
                bs2.setFocusable(false);
                bs2.setKeyListener(null);
                bs3.setText(bsem3);
                bs3.setFocusable(false);
                bs3.setKeyListener(null);
                bs4.setText(bsem4);
                bs4.setFocusable(false);
                bs4.setKeyListener(null);
                bs5.setText(bsem5);
                bs5.setFocusable(false);
                bs5.setKeyListener(null);
                bs6.setText(bsem6);
                bs7.setText(bsem7);
                bs8.setText(bsem8);

                beUpdate.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(netCheck.isNetAvailable(getActivity()))
                            new Update().execute(bsem1,bsem2,bsem3,bsem4,bsem5,bs6.getText().toString().equals("0")?"0.00":"0.00",bs7.getText().toString().equals("0")?"0.00":"0.00",bs8.getText().toString().equals("0")?"0.00":"0.00");
                        else
                            Toast.makeText(getActivity(),"Check Your network connection",Toast.LENGTH_LONG).show();
                    }
                });
            }
            else if(getCourse().equalsIgnoreCase("MCA")){
                bs1.setText(bsem1);
                bs1.setFocusable(false);
                bs1.setKeyListener(null);
                bs2.setText(bsem2);
                bs2.setFocusable(false);
                bs2.setKeyListener(null);
                bs3.setText(bsem3);
                bs3.setFocusable(false);
                bs3.setKeyListener(null);
                bs4.setText(bsem4);
                bs4.setFocusable(false);
                bs4.setKeyListener(null);
                bs5.setText(bsem5);
                bs6.setText(bsem6);

                beUpdate.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(netCheck.isNetAvailable(getActivity()))
                            new Update().execute(bsem1,bsem2,bsem3,bsem4,bs5.getText().toString().equals("0")?"0.00":"0.00",bs6.getText().toString().equals("0")?"0.00":"0.00");
                        else
                            Toast.makeText(getActivity(),"Check Your network connection",Toast.LENGTH_LONG).show();
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
                if(getCourse().equalsIgnoreCase("ME") || getCourse().equalsIgnoreCase("MTECH") ) {
                    Connection.Response response = Jsoup.connect("http://oucecareers.org/students/updatemarksaction.php?rollno=" + getRoll() + "&pgsem1=" + params[0] + "&pgsem2=" + params[1] + "&course=" + getCourse() + "&avggpa=" + params[2]).cookies(Cooks.getCookies().get(0)).timeout(50000).execute();
                    System.out.println(response.toString());
                    return ""+params[2];
                }
                else if(getCourse().equalsIgnoreCase("BE")){
                    Float dividingFactor=6.0F;
                    if(params[5]== null || params[5].equals("0.00") || params[5].length()==0){
                        dividingFactor=5.0F;
                    }
                    else if((params[6] == null || params[6].equalsIgnoreCase("0.00") || params[6].length()==0) && (params[7] == null || params[7].equalsIgnoreCase("0.00") || params[7].length()==0) ){
                        dividingFactor = 6.0F;
                    }
                    else if(!(params[6] == null || params[6].equalsIgnoreCase("0.00") || params[6].length()==0) && (params[7] == null || params[7].equalsIgnoreCase("0.00") || params[7].length()==0) ){
                        dividingFactor = 7.0F;
                    }
                    else if(!(params[6] == null || params[6].equalsIgnoreCase("0.00") || params[6].length()==0) && !(params[7] == null || params[7].equalsIgnoreCase("0.00") || params[7].length()==0) ){
                        dividingFactor = 8.0F;
                    }
                    System.out.println(dividingFactor);
                    Float avg = (Float.parseFloat(params[0])+Float.parseFloat(params[1])+Float.parseFloat(params[2])+Float.parseFloat(params[3])+Float.parseFloat(params[4])+Float.parseFloat(params[5])+Float.parseFloat(params[6])+Float.parseFloat(params[7]))/dividingFactor;
                    Connection.Response response = Jsoup.connect("http://oucecareers.org/students/updatemarksaction.php?rollno=" + getRoll() +"&sem6gpa="+params[5]+"&sem7gpa="+params[6]+"&sem8gpa="+params[7]+"&course="+getCourse()+"&sem1gpa="+params[0]+"&sem2gpa="+params[1]+"&sem3gpa="+params[2]+"&sem4gpa="+params[3]+"&sem5gpa="+params[4]+"&avggpa="+avg).cookies(Cooks.getCookies().get(0)).timeout(50000).execute();
                    System.out.println(response.toString());
                    return ""+avg;
                }
                else if(getCourse().equalsIgnoreCase("MCA")){
                    Float dividingFactor = 6.0F;
                    if((params[4] == null || params[4].equals("0.00") || params[4].length()==0) && (params[5] == null || params[5].equals("0.00") || params[5].length()==0)){
                        dividingFactor=4.0F;
                    }else if(!(params[4] == null || params[4].equals("0.00") || params[4].length()==0) && (params[5] == null || params[5].equals("0.00") || params[5].length()==0)){
                        dividingFactor=5.0F;
                    }else if(!(params[4] == null || params[4].equals("0.00") || params[4].length()==0) && !(params[5] == null || params[5].equals("0.00") || params[5].length()==0)){
                        dividingFactor=6.0F;
                    }
                    Float avg = (Float.parseFloat(params[0])+Float.parseFloat(params[1])+Float.parseFloat(params[2])+Float.parseFloat(params[3])+Float.parseFloat(params[4])+Float.parseFloat(params[5]))/dividingFactor;
                    Connection.Response response = Jsoup.connect("http://oucecareers.org/students/updatemarksaction.php?rollno=" + getRoll() +"&sem6gpa="+params[5]+"&course="+getCourse()+"&sem1gpa="+params[0]+"&sem2gpa="+params[1]+"&sem3gpa="+params[2]+"&sem4gpa="+params[3]+"&sem5gpa="+params[4]+"&avggpa="+avg).cookies(Cooks.getCookies().get(0)).timeout(50000).execute();
                    System.out.println(response.toString());
                    return ""+avg;
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
            progressDialog.setMessage("Updating");
            progressDialog.show();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            progressDialog.dismiss();
            if(getCourse().equalsIgnoreCase("BE")){
                bemarks.setText(s);
            }
            else if(getCourse().equalsIgnoreCase("MCA")){
                bemarks.setText(s);
            }
        }
    }

}
