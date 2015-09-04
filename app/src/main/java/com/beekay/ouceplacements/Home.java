package com.beekay.ouceplacements;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.PersistableBundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;


public class Home extends AppCompatActivity  {


    DrawerLayout drawer;
    ActionBarDrawerToggle toggle;
    Toolbar tool;
    ListView list;
    NetCheck netCheck;
    public Document getDocument() {
        return document;
    }

    public void setDocument(Document document) {
        this.document = document;
    }

    Document document;
    android.support.v4.app.Fragment fragment;

    public ArrayList<String> getLinkId() {
        return linkId;
    }

    public void setLinkId(ArrayList<String> linkId) {
        this.linkId = linkId;
    }

    ArrayList<String> linkId;

    public ArrayList<Contents> getRecycleList() {
        return recycleList;
    }

    public void setRecycleList(ArrayList<Contents> recycleList) {
        this.recycleList = recycleList;
    }

    static ArrayList<Contents> recycleList;
    ArrayList<HashMap<String,String>> cooks;

    public ArrayList<HashMap<String, String>> getCooks() {
        return cooks;
    }

    public void setCooks(ArrayList<HashMap<String, String>> cooks) {
        this.cooks = cooks;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        netCheck=new NetCheck();

        drawer=(DrawerLayout)findViewById(R.id.drawer);
        list=(ListView)findViewById(R.id.drawerlist);
        tool=(Toolbar)findViewById(R.id.tool);
        setSupportActionBar(tool);
        Intent intent=getIntent();
        cooks=(ArrayList<HashMap<String,String>>)intent.getSerializableExtra("cookie");
        setCooks(cooks);
if(netCheck.isNetAvailable(this))
            new HomeList().execute(getCooks());
else
    Toast.makeText(this,"check your network connection",Toast.LENGTH_SHORT).show();

        toggle=new ActionBarDrawerToggle(this,drawer,tool,R.string.string_open,R.string.string_close){

            @Override
            public void onDrawerOpened(View drawerView) {
                invalidateOptionsMenu();
                super.onDrawerOpened(drawerView);
            }

            @Override
            public void onDrawerClosed(View drawerView)
            {
                invalidateOptionsMenu();
                super.onDrawerClosed(drawerView);
            }
        };
        drawer.setDrawerListener(toggle);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toggle.syncState();
    }

    @Override
    public void onPostCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onPostCreate(savedInstanceState, persistentState);
        toggle.syncState();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_home, menu);
        MenuItem item=menu.findItem(R.id.share);
        item.setVisible(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (toggle.onOptionsItemSelected(item)) {
            return true;
        }



        return super.onOptionsItemSelected(item);
    }



    public class HomeList extends AsyncTask<ArrayList<HashMap<String,String>>,String,ArrayList<Contents>> {

        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog=new ProgressDialog(Home.this);
            progressDialog.setMessage("Loading...");
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setIndeterminate(true);
            progressDialog.show();

        }

        @Override
        protected ArrayList<Contents> doInBackground(ArrayList<HashMap<String,String>>... params) {

            if(params[0].size()!=0)
                try {


                    Document notDoc=Jsoup.connect("http://oucecareers.org/students/showNotice.php").followRedirects(false).cookies(params[0].get(0)).get();
                    setDocument(notDoc);
                    publishProgress("");
                    Elements table=notDoc.select("table");
                    Contents contents;


                    ArrayList<Contents> list=new ArrayList<Contents>();
                    int i=0;
                    for(Element tr : table.select("tr")){
                        contents=new Contents();
                        for(Element td : tr.select("td")){
                            if(i==0)
                                contents.number=td.text();

                            if(i==1) {

                                contents.notificationContent = td.text();


                            }
                            if(i==2)
                                contents.attachments=td.text();
                            if(i==3)
                                contents.datePosted=td.text();
                            i++;
                        }
                        i=0;

                        list.add(contents);
                    }

                    return list;
                }catch (Exception e){
                    e.printStackTrace();
                }
            return null;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            ArrayList<String> links=new ArrayList<>();
            Document doc=getDocument();
            int len,i=0;
            boolean firstSkipped=false;
            Element table=doc.select("table").first();
            for(Element row: table.select("tr")){
                i=0;
                for(Element link: row.select("td")) {

                    if(i==1 ) {
                        if(!firstSkipped)
                            firstSkipped=true;

                        else {
                         //   System.out.print(link.select("a").attr("href").toString().length());
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
            ArrayList<String> sideList=(ArrayList<String>)getIntent().getSerializableExtra("list");
            super.onPostExecute(s);
            ArrayAdapter<String> adapter=new ArrayAdapter<String>(getBaseContext(),android.R.layout.simple_list_item_1,android.R.id.text1, sideList);
            list.setAdapter(adapter);
            list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    System.out.println("came to handle");
                    if (list.getItemAtPosition(position).toString().equals("Update Marks")) {
                        if (netCheck.isNetAvailable(Home.this)) {

//                            getSupportFragmentManager().beginTransaction().remove(getSupportFragmentManager().findFragmentByTag("showNotice")).commit();
                            drawer.closeDrawer(Gravity.LEFT);
                            Bundle bundle = new Bundle();
                            System.out.println(getCooks());
                            bundle.putSerializable("cookies", getCooks());
                            android.support.v4.app.Fragment fragment_marks = new UpdateMarks();
                            fragment_marks.setArguments(bundle);

                            getSupportFragmentManager().beginTransaction().replace(R.id.frameContainer, fragment_marks).addToBackStack(null).commit();

                        } else {
                            Toast.makeText(Home.this, "Check your network connection", Toast.LENGTH_SHORT).show();
                        }
                    } else if (list.getItemAtPosition(position).toString().equals("Home") || list.getItemAtPosition(position).toString().equals("Notice Board") || position == 0) {
                        if (fragment.isVisible())
                            drawer.closeDrawer(Gravity.LEFT);
                        else {
                            drawer.closeDrawer(Gravity.LEFT);
                            getSupportFragmentManager().beginTransaction().replace(R.id.frameContainer, fragment).commit();

                        }
                    } else if (list.getItemAtPosition(position).toString().equalsIgnoreCase("Change Password")) {
                        drawer.closeDrawer(Gravity.LEFT);
                        Bundle bundle = new Bundle();
                        System.out.println(getCooks());
                        bundle.putSerializable("cookies", getCooks());
                        android.support.v4.app.Fragment fragment_pass = new UpdatePassFragment();
                        fragment_pass.setArguments(bundle);
                        getSupportFragmentManager().beginTransaction().replace(R.id.frameContainer, fragment_pass).addToBackStack(null).commit();
                    } else if (list.getItemAtPosition(position).toString().equalsIgnoreCase("MY MBD")) {
                        drawer.closeDrawer(Gravity.LEFT);

                        Intent intent=new Intent(Home.this,Mbd.class);
                        intent.putExtra("cookies",getCooks());
                        startActivity(intent);
                    }


                }
            });
            setRecycleList(s);
            fragment=new Notification();

            if(fragment!=null){
                Bundle bundle=new Bundle();
                bundle.putSerializable("list",new ContentWrapper(new ArrayList<>(getRecycleList().subList(1,getRecycleList().size()))));
                bundle.putStringArrayList("ids",linkId);
                bundle.putSerializable("cookies",cooks);
                fragment.setArguments(bundle);
                FragmentManager fragmentManager=getSupportFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.frameContainer,fragment,"showNotice").commit();
            }

        }
    }



}
