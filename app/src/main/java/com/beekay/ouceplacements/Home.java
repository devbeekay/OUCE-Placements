package com.beekay.ouceplacements;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;


public class Home extends AppCompatActivity {


    static ArrayList<Contents> recycleList;
    DrawerLayout drawer;
    ActionBarDrawerToggle toggle;
    Toolbar tool;
    ListView list;
    NetCheck netCheck;
    Document document;
    android.support.v4.app.Fragment fragment;
    ArrayList<String> linkId;
    ArrayList<HashMap<String, String>> cooks;
    PendingIntent pendingIntent;

    public Document getDocument() {
        return document;
    }

    public void setDocument(Document document) {
        this.document = document;
    }

    public ArrayList<String> getLinkId() {
        return linkId;
    }

    public void setLinkId(ArrayList<String> linkId) {
        this.linkId = linkId;
    }

    public ArrayList<Contents> getRecycleList() {
        return recycleList;
    }

    public void setRecycleList(ArrayList<Contents> recycleList) {
        Home.recycleList = recycleList;
    }

    public ArrayList<HashMap<String, String>> getCooks() {
        return cooks;
    }

    public void setCooks(ArrayList<HashMap<String, String>> cooks) {
        this.cooks = cooks;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null) {
            setContentView(R.layout.activity_home);
            netCheck = new NetCheck();
            if(Cooks.getCookies()==null)
                finish();
            drawer = (DrawerLayout) findViewById(R.id.drawer);
            list = (ListView) findViewById(R.id.drawerlist);
            tool = (Toolbar) findViewById(R.id.tool);
            setSupportActionBar(tool);
            cooks = Cooks.getCookies();
            setCooks(cooks);
            if (netCheck.isNetAvailable(this))
                new HomeList().execute("");
            else
                Toast.makeText(this, "check your network connection", Toast.LENGTH_SHORT).show();

            toggle = new ActionBarDrawerToggle(this, drawer, tool, R.string.string_open, R.string.string_close) {

                @Override
                public void onDrawerOpened(View drawerView) {
                    invalidateOptionsMenu();
                    super.onDrawerOpened(drawerView);
                }

                @Override
                public void onDrawerClosed(View drawerView) {
                    invalidateOptionsMenu();
                    super.onDrawerClosed(drawerView);
                }
            };
            drawer.setDrawerListener(toggle);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            toggle.syncState();
        } else {
            setContentView(R.layout.activity_home);
        }


    }

    @Override
    public void onPostCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onPostCreate(savedInstanceState, persistentState);
        toggle.syncState();
    }

    @Override
    protected void onRestart() {
        try{
            HashMap<String, String> cookies = Cooks.getCookies().get(0);
        }catch (NullPointerException ex){
            finish();
        }
        super.onRestart();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        String path = this.getApplicationContext().getFilesDir()+"/service/";
        File file = new File(path);
        file.mkdirs();
        path+="service_option.txt";
        String cond = new String();
        File f = new File(path);
        if(!f.exists()) {
            OutputStream myOutput;
            try {
                myOutput = new BufferedOutputStream(new FileOutputStream(path, true));
                myOutput.write(new String("false").getBytes());
                myOutput.flush();
                myOutput.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else{
            InputStream inputStream;
            try{
                inputStream = new BufferedInputStream(new FileInputStream(path));
                BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
                String text = null;
                while((text = br.readLine())!=null){
                    cond = text;
                }
                br.close();
                inputStream.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        getMenuInflater().inflate(R.menu.menu_home,menu);
        if(cond.equals("true"))
            menu.findItem(R.id.push).setChecked(true);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.isChecked()){
            stop();
            item.setChecked(false);
        }
        else{
            start();
            item.setChecked(true);
        }
        return super.onOptionsItemSelected(item);
    }

    private void start() {
        Intent alarmIntent = new Intent(Home.this,AlarmReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(Home.this,0,alarmIntent,0);
        AlarmManager manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        int interval = 1*60*60000;
        manager.setInexactRepeating(AlarmManager.RTC_WAKEUP,System.currentTimeMillis()+3*60*1000,interval,pendingIntent);
        Toast.makeText(this,"Notification Turned on with first saved user's credentials",Toast.LENGTH_LONG).show();
        String path = this.getApplicationContext().getFilesDir()+"/service/";
        File file = new File(path);
        file.mkdirs();
        path+="service_option.txt";
        File f = new File(path);
        if(f.exists() && !f.isDirectory()){
            f.delete();
        }
        OutputStream myOutput;
        try{
            myOutput = new BufferedOutputStream(new FileOutputStream(path,true));
            myOutput.write(new String("true").getBytes());
            myOutput.flush();
            myOutput.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void stop() {
        AlarmManager manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent alarmIntent = new Intent(Home.this,AlarmReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(Home.this,0,alarmIntent,0);
        manager.cancel(pendingIntent);
        Toast.makeText(this,"Notifications turned off",Toast.LENGTH_LONG).show();
        String path = this.getApplicationContext().getFilesDir()+"/service/";
        path+="service_option.txt";
        File f = new File(path);
        if(f.exists() && !f.isDirectory())
            f.delete();
    }

    public class HomeList extends AsyncTask<String, String, ArrayList<Contents>> {

        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(Home.this);
            progressDialog.setMessage("Loading...");
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setIndeterminate(true);
            progressDialog.show();

        }

        @Override
        protected ArrayList<Contents> doInBackground(String... params) {

            if (cooks.size() != 0)
                try {
                    Document notDoc = Jsoup.connect("http://oucecareers.org/students/showNotice.php").followRedirects(false).cookies(cooks.get(0)).timeout(50000).get();
                    setDocument(notDoc);
                    publishProgress("");
                    Elements table = notDoc.select("table");
                    Contents contents;


                    ArrayList<Contents> list = new ArrayList<>();
                    int i = 0;
                    for (Element tr : table.select("tr")) {
                        contents = new Contents();
                        for (Element td : tr.select("td")) {
                            if (i == 0)
                                contents.number = td.text();

                            if (i == 1) {
                                contents.notificationContent = td.text();
                            }
                            if (i == 2)
                                contents.attachments = td.text();
                            if (i == 3)
                                contents.datePosted = td.text();
                            i++;
                        }
                        i = 0;

                        list.add(contents);
                    }

                    return list;
                } catch (IOException e) {
                    ArrayList<Contents> listFail = new ArrayList<>(1);
                    listFail.add(null);
                }
            return null;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            ArrayList<String> links = new ArrayList<>();
            Document doc = getDocument();
            int len, i;
            boolean firstSkipped = false;
            Element table = doc.select("table").first();
            for (Element row : table.select("tr")) {
                i = 0;
                for (Element link : row.select("td")) {
                    if (i == 1) {
                        if (!firstSkipped)
                            firstSkipped = true;
                        else {
                            len = link.select("a").attr("href").length();
                            links.add(link.select("a").attr("href").toString().substring(len - 5, len - 1));
                        }
                    }
                    i++;
                }
            }
            setLinkId(links);
        }

        @Override
        protected void onPostExecute(ArrayList<Contents> s) {
            progressDialog.dismiss();
            if(s.get(0).equals(null)){
                Toast.makeText(Home.this,"Timed out while connecting",Toast.LENGTH_LONG).show();
                finish();
            }
            ArrayList<String> sideList = (ArrayList<String>) getIntent().getSerializableExtra("list");
            super.onPostExecute(s);
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(getBaseContext(), android.R.layout.simple_list_item_1, android.R.id.text1, sideList);
            list.setAdapter(adapter);
            list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    if (list.getItemAtPosition(position).toString().equals("Update Marks")) {
                        android.support.v4.app.Fragment fragment=null;
                        if (netCheck.isNetAvailable(Home.this)) {
                            drawer.closeDrawer(GravityCompat.START);
                            if(fragment==null) {
                                fragment = new UpdateMarks();
                                getSupportFragmentManager().beginTransaction().replace(R.id.frameContainer, fragment).addToBackStack(null).commit();
                            }else{
                                fragment=null;
                                fragment = new UpdateMarks();
                                getSupportFragmentManager().beginTransaction().replace(R.id.frameContainer, fragment).addToBackStack(null).commit();
                            }
                        } else {
                            drawer.closeDrawer(GravityCompat.START);
                            Toast.makeText(Home.this, "Check your network connection", Toast.LENGTH_SHORT).show();
                        }
                    } else if (list.getItemAtPosition(position).toString().equalsIgnoreCase("Change Password")) {
                        drawer.closeDrawer(GravityCompat.START);
                        if(netCheck.isNetAvailable(Home.this)) {
                            if (fragment == null) {
                                fragment = new UpdatePassFragment();
                                getSupportFragmentManager().beginTransaction().replace(R.id.frameContainer, fragment).addToBackStack(null).commit();
                            } else {
                                fragment = null;
                                fragment = new UpdatePassFragment();
                                getSupportFragmentManager().beginTransaction().replace(R.id.frameContainer, fragment).addToBackStack(null).commit();
                            }
                        }else{
                            Toast.makeText(Home.this,"Check your network connectivity",Toast.LENGTH_LONG).show();
                        }
                    } else if (list.getItemAtPosition(position).toString().equalsIgnoreCase("MY MBD")) {
                        drawer.closeDrawer(GravityCompat.START);
                        if(netCheck.isNetAvailable(Home.this)) {
                            Intent intent = new Intent(Home.this, Mbd.class);
                            startActivity(intent);
                        }else{
                            Toast.makeText(Home.this,"Check your network connectivity",Toast.LENGTH_LONG).show();
                        }
                    } else if (list.getItemAtPosition(position).toString().equalsIgnoreCase("Apply YBD")) {
                        if (netCheck.isNetAvailable(Home.this)) {
                            drawer.closeDrawer(GravityCompat.START);
                            android.support.v4.app.Fragment fragment_ybd = new Ybd();
                            getSupportFragmentManager().beginTransaction().replace(R.id.frameContainer, fragment_ybd).addToBackStack(null).commit();
                        } else {
                            Toast.makeText(getApplicationContext(), "Check you Network Connection", Toast.LENGTH_SHORT).show();
                        }
                    } else if (list.getItemAtPosition(position).toString().equalsIgnoreCase("Sign Out")) {
                        Cooks.setCookies(null);
                        finish();
                    } else if (list.getItemAtPosition(position).toString().equalsIgnoreCase("Feedback")) {
                        Intent intent = new Intent(Intent.ACTION_SENDTO);
                        intent.setData(Uri.parse("mailto:"));
                        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"devbeekay@gmail.com"});
                        intent.putExtra(Intent.EXTRA_SUBJECT, "Feedback" + " " + Build.MANUFACTURER + " " + Build.MODEL + " " + Build.VERSION.RELEASE);
                        startActivity(Intent.createChooser(intent, "Send Email to Developer"));
                    } else if (list.getItemAtPosition(position).toString().equalsIgnoreCase("Job Status")) {
                        drawer.closeDrawer(GravityCompat.START);
                        if (netCheck.isNetAvailable(Home.this)) {
                            if(fragment==null) {
                                fragment = new JobStatusFrag();
                                getSupportFragmentManager().beginTransaction().replace(R.id.frameContainer, fragment).addToBackStack(null).commit();
                            }else {
                                fragment=null;
                                fragment = new JobStatusFrag();
                                getSupportFragmentManager().beginTransaction().replace(R.id.frameContainer, fragment).addToBackStack(null).commit();
                            }
                        } else {
                            Toast.makeText(getApplicationContext(), "Check you Network Connection", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });
            setRecycleList(s);
            String path = Home.this.getApplicationContext().getFilesDir()+"/service/";
            File file = new File(path);
            file.mkdirs();
            path+="last_notice.txt";
            File f = new File(path);
            if(f.exists() && !f.isDirectory()){
                f.delete();
            }
            OutputStream myOutput;
            try{
                myOutput = new BufferedOutputStream(new FileOutputStream(path,true));
                myOutput.write(getRecycleList().get(1).notificationContent.getBytes());
                System.out.println("written to last_notice.txt "+getRecycleList().get(1).notificationContent);
                myOutput.flush();
                myOutput.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            fragment = new Notification();
            Bundle bundle = new Bundle();
            bundle.putSerializable("list", new ContentWrapper(new ArrayList<>(getRecycleList().subList(1, getRecycleList().size()))));
            bundle.putStringArrayList("ids", linkId);
            fragment.setArguments(bundle);
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().add(R.id.frameContainer, fragment, "showNotice").commit();
        }
    }
}
