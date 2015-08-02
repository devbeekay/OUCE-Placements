package com.beekay.ouceplacements;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;


public class PasswordActivity extends AppCompatActivity {
    int count=0;
    static String username;
    static String password;
    private EditText user,pass;
    private Button logButton;

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
        setContentView(R.layout.activity_password);
        user=(EditText)findViewById(R.id.usertext);
        pass=(EditText)findViewById(R.id.passtext);

        logButton=(Button)findViewById(R.id.logbutton);
        logButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                    ArrayList<String> credentialList = new ArrayList<String>(2);
                    credentialList.add(0, user.getText().toString());
                    credentialList.add(1, pass.getText().toString());

                Login log=        new Login();
                log.execute(credentialList);
                }
        });
    }

    public void getPage(){
        System.out.println("came to get page");
        ArrayList<Map<String, String>> cook = new ArrayList<Map<String, String>>();

        cook.add(cookies);
        new HomeList().execute(cook);


    }

    public void sendLogin(ArrayList<String> credentials){

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



    public class HomeList extends AsyncTask<ArrayList<Map<String,String>>,String,String> {




        @Override
        protected String doInBackground(ArrayList<Map<String,String>>... params) {
            if(params[0].size()!=0)
            try {
                  Document doc = Jsoup.connect("http://oucecareers.org/students/showNotice.php").cookies(params[0].get(0)).get();
                Elements table = doc.getElementsByTag("table");
                ArrayList<String> ele = new ArrayList<String>();
                if (table.first() != null) {

                    Iterator<Element> ite = table.select("tr").select("a").iterator();
                    ele.add(ite.next().text());

                    while (ite.hasNext() && ele.size() <= 30) {
                        String td = ite.next().getElementsByAttribute("href").toString();
                        if (!ele.contains(td)) {
                            ele.add(td);

                        }
                    }
                    for (int i = 0; i < ele.size(); i++) {
                        System.out.println(ele.get(i));
                    }
                }
            }catch (Exception e){
                e.printStackTrace();
            }
            return null;
        }
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


        @Override
        protected Map<String,String> doInBackground(ArrayList<String>... params) {
            ArrayList<String> credentials=params[0];
            Map<String,String> loginCookies=null;
            try {
                System.out.println(credentials.get(0)+credentials.get(1));
                org.jsoup.Connection.Response res= Jsoup.connect("http://oucecareers.org/s_logaction.php").data("uname",credentials.get(0),"upass",credentials.get(1),"Submit","sign in").method(Connection.Method.POST).execute();
                loginCookies=res.cookies();

            } catch (IOException e) {
                System.out.println("Something's wrong bitch");
            }
            return loginCookies;

        }

        @Override
        protected void onPostExecute(Map<String, String> stringStringMap) {
            setCookies(stringStringMap);
            getPage();
            Intent intent=new Intent(PasswordActivity.this,Home.class);
            startActivity(intent);
        }
    }

}
