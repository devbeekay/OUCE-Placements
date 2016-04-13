package com.beekay.ouceplacements;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.net.SocketTimeoutException;
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
    NetCheck netCheck;
    DataOpener opener;

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
        netCheck=new NetCheck();
        if(username!=null && password!=null && netCheck.isNetAvailable(this)){
            ArrayList<String> credentialList = new ArrayList<>(2);
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
            opener = new DataOpener(this);
            opener.openRead();
            Cursor cursor = opener.retrieve();
            if(cursor.getCount()>0){
                int i = 0;
                while(cursor.moveToFirst() && i==0){
                        user.setText(cursor.getString(0));
                        pass.setText(cursor.getString(1));
                        i++;
                }
            }
            cursor.close();
            opener.close();
            user.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean b) {
                    System.out.println("focus changed");
                    opener = new DataOpener(PasswordActivity.this);
                    opener.openRead();
                    Editable u = user.getText();
                    System.out.println(u.toString());
                    if (u!=null) {
                        Cursor cursor = opener.retrieve(u.toString());
                        if (cursor.getCount() > 0) {
                            int i = 0;
                            while (cursor.moveToFirst() && i == 0) {
                                System.out.println(cursor.getString(1));
                                pass.setText(cursor.getString(1));
                                i++;
                            }
                        }
                        cursor.close();
                        opener.close();
                    }
                }
            });
            logButton = (Button) findViewById(R.id.logbutton);
            logButton.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    if (netCheck.isNetAvailable(PasswordActivity.this)) {
                        ArrayList<String> credentialList = new ArrayList<>(2);
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
        if (id == R.id.about) {
            Toast.makeText(this,"PDS 2014-16 with ♥",Toast.LENGTH_LONG).show();
        }

        return super.onOptionsItemSelected(item);
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
            try {
                System.out.println(credentials.get(0) + credentials.get(1));
                org.jsoup.Connection.Response res= Jsoup.connect("http://oucecareers.org/s_logaction.php").data("uname",credentials.get(0),"upass",credentials.get(1),"Submit","sign in").method(Connection.Method.POST).timeout(50000).execute();

                try {
                    System.out.println("came to try");
                    Document doc = Jsoup.connect("http://oucecareers.org/students/students.php").followRedirects(false).cookies(res.cookies()).timeout(50000).get();
                    Element welcome=doc.select("div#adminpasscontents").first();
                    System.out.println(welcome.text().length());
                    String name=welcome.text().substring(7);
                    if(welcome.text().length()>7) {
                        ArrayList<HashMap<String, String>> cook = new ArrayList<>();
                        cook.add((HashMap<String, String>) res.cookies());
                        Cooks.setCookies(cook);
                        ArrayList<String> sideList=new ArrayList<>();
                        System.out.println(name+"welcome");
                        Elements lists=doc.select("div#header-tabs");
                        Iterator<Element> eachList=lists.select("ul").select("li").iterator();
                        while(eachList.hasNext()) {
                            Element subList=eachList.next();
                            if(subList.children().size()>0){
                                for(Element l : subList.select("a")){
                                    if(l.attr("href").toString().equals("#") || l.text().equalsIgnoreCase("Change Photo") || l.text().equalsIgnoreCase("Home") || l.text().equalsIgnoreCase("Notice Board")){

                                    }
                                    else
                                    sideList.add(l.text());
                                }
                            }
                            else
                                sideList.add(subList.text());

                        }
                        sideList.add(name.trim());
                        Collections.reverse(sideList);
                        setList(sideList);
                    }
                    else
                    Cooks.setCookies(null);
                }catch(SocketTimeoutException e)
                {
                    System.out.println("exception at cookies");
                    e.printStackTrace();
                    setCookies(null);
                    ArrayList<String> list=new ArrayList<>();
                    list.add("timedout");
                    setList(list);
                }

                return getCookies();
            } catch (Exception e) {
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
            progressDialog.setCancelable(false);
            progressDialog.setIndeterminate(true);
            progressDialog.show();
        }

        @Override
        protected void onPostExecute(Map<String, String> stringStringMap) {

            if(username==null && password==null && Cooks.getCookies()!=null ){
                username=user.getText().toString();
                password=pass.getText().toString();
            }
            if(Cooks.getCookies()!=null && !getList().get(0).equals("timedout")) {
                CheckBox box = (CheckBox) findViewById(R.id.remember);
                if(box.isChecked()){
                    opener = new DataOpener(PasswordActivity.this);
                    opener.open();
                    Cursor cursor = opener.retrieve(username);
                    if(cursor.getCount()!=0 ){
                        int i = 0;
                        String pas = null;
                        while(cursor.moveToFirst() && i==0){
                            pas = cursor.getString(1);
                            i++;
                        }
                        if(!pas.equalsIgnoreCase(password)){
                            //TODO update code
                        }
                        cursor.close();
                        opener.close();
                    }
                    else{
                        opener.insertData(username,password);
                        cursor.close();
                        opener.close();
                        Toast.makeText(PasswordActivity.this,"User remembered",Toast.LENGTH_LONG).show();
                    }
                }
                Intent intent = new Intent(PasswordActivity.this, Home.class);
                intent.putExtra("list", getList());
                progressDialog.dismiss();
                NetCheck.setUser(username);
                NetCheck.setPass(password);
                startActivity(intent);
            }
            else if(getCookies()==null) {
                progressDialog.dismiss();
                Toast.makeText(getBaseContext(), "Login Failed!!!", Toast.LENGTH_LONG).show();

            }
            else if(getList().get(0).equals("timedout")){
                progressDialog.dismiss();
                Toast.makeText(getBaseContext(), "Time out while connecting", Toast.LENGTH_LONG).show();
            }
        }
    }

}
