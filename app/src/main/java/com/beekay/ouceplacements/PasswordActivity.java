package com.beekay.ouceplacements;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


public class PasswordActivity extends AppCompatActivity {

    static String username;
    static String password;
    private EditText user,pass;
    private Button logButton;
    Toolbar tool;

    public ArrayList<String> getList() {
        return list;
    }

    public void setList(ArrayList<String> list) {
        this.list = list;
    }

    private ArrayList<String> list;


    public void setCookies(Map<String, String> cookies) {
        this.cookies = cookies;
    }

    public Map<String, String> getCookies() {
        return cookies;
    }

    private Map<String,String> cookies;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(username!=null && password!=null && isNetAvailable()){
            ArrayList<String> credentialList = new ArrayList<String>(2);
            credentialList.add(0, username);
            credentialList.add(1, password);

                Login log = new Login();
                log.execute(credentialList);


        }
        else {
        setContentView(R.layout.activity_password);
        System.out.println(username + password + "in static");
        tool=(Toolbar)findViewById(R.id.tool);
        setSupportActionBar(tool);


            user = (EditText) findViewById(R.id.usertext);
            pass = (EditText) findViewById(R.id.passtext);


            logButton = (Button) findViewById(R.id.logbutton);
            logButton.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    if (isNetAvailable()) {
                        ArrayList<String> credentialList = new ArrayList<String>(2);
                        credentialList.add(0, user.getText().toString());
                        credentialList.add(1, pass.getText().toString());

                        Login log = new Login();
                        log.execute(credentialList);
                    } else

                    {
                        Toast.makeText(PasswordActivity.this, "Check your Network Connection", Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_password, menu);
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




    public boolean isNetAvailable(){
        ConnectivityManager connectivityManager=(ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info=connectivityManager.getActiveNetworkInfo();
        return info!=null && info.isConnectedOrConnecting();
    }


    /**
     * Created by Krishna on 30-07-2015.
     */
    public class Login extends AsyncTask<ArrayList<String>,String,Map<String,String>> {
        ProgressDialog progressDialog;

        @Override
        protected Map<String,String> doInBackground(ArrayList<String>... params) {
            publishProgress("");
            ArrayList<String> credentials=params[0];
            Map<String,String> loginCookies=null;
            try {
                System.out.println(credentials.get(0)+credentials.get(1));
                org.jsoup.Connection.Response res= Jsoup.connect("http://oucecareers.org/s_logaction.php").data("uname",credentials.get(0),"upass",credentials.get(1),"Submit","sign in").method(Connection.Method.POST).execute();
                try {
                    System.out.println("came to try");
                    Document doc = Jsoup.connect("http://oucecareers.org/students/students.php").followRedirects(false).cookies(res.cookies()).get();
                    Element welcome=doc.select("div#adminpasscontents").first();
                    System.out.println(welcome.text().length());
                    String name=welcome.text().substring(7);
                    if(welcome.text().length()>7) {
                        setCookies(res.cookies());
                        ArrayList<String> sideList=new ArrayList<>();
                        System.out.println(name+"welcome");
                        Elements lists=doc.select("div#header-tabs");
                        Iterator<Element> eachList=lists.select("ul").select("li").iterator();
                        while(eachList.hasNext()) {
                            Element subList=eachList.next();
                            if(subList.children().size()>0){
                                for(Element l : subList.child(0).select("a")){
                                    System.out.println(l.text());
                                    sideList.add(l.text());
                                }
                            }
                            else
                                sideList.add(subList.text());

                        }
                        sideList.add(name);
                        Collections.reverse(sideList);
                        setList(sideList);
                    }
                    else
                    setCookies(null);
                }catch(Exception e)
                {
                    System.out.println("exception at cookies");
                    e.printStackTrace();
                    setCookies(null);
                }
                return getCookies();
            } catch (IOException e) {
                System.out.println("Something's wrong");
            }
            return getCookies();

        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            progressDialog=new ProgressDialog(PasswordActivity.this);
            progressDialog.setMessage("Logging in");
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setIndeterminate(true);
            progressDialog.show();
        }

        @Override
        protected void onPostExecute(Map<String, String> stringStringMap) {

            if(username==null && password==null && getCookies()!=null){
                username=user.getText().toString();
                password=pass.getText().toString();
            }
            if(getCookies()!=null) {
                Intent intent = new Intent(PasswordActivity.this, Home.class);
                ArrayList<Map<String, String>> cookielist = new ArrayList<Map<String, String>>();
                cookielist.add(stringStringMap);
                intent.putExtra("cookie", cookielist);
                intent.putExtra("list",getList());
                startActivity(intent);
            }
            else if(getCookies()==null) {
                progressDialog.dismiss();
                Toast.makeText(getBaseContext(), "Login Failed!!!", Toast.LENGTH_LONG).show();

            }
        }
    }

}
