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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;


public class Home extends AppCompatActivity implements Notification.OnFragmentInteractionListener {

    Notification.OnFragmentInteractionListener fragmentInteractionListener;
    DrawerLayout drawer;
    ActionBarDrawerToggle toggle;
    Toolbar tool;
    ListView list;

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

    ArrayList<Contents> recycleList;
    ArrayList<Map<String,String>> cooks;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        drawer=(DrawerLayout)findViewById(R.id.drawer);
        list=(ListView)findViewById(R.id.drawerlist);
        tool=(Toolbar)findViewById(R.id.tool);
        setSupportActionBar(tool);
        Intent intent=getIntent();
        cooks=(ArrayList<Map<String,String>>)intent.getSerializableExtra("cookie");

            new HomeList().execute(cooks);


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

        //noinspection SimplifiableIfStatement

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onFragmentInteraction(ArrayList<Contents> contents) {

    }

    public class HomeList extends AsyncTask<ArrayList<Map<String,String>>,String,ArrayList<Contents>> {

        ProgressDialog progressDialog;


        @Override
        protected ArrayList<Contents> doInBackground(ArrayList<Map<String,String>>... params) {

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
            progressDialog=new ProgressDialog(Home.this);
            progressDialog.setMessage("Loading...");
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setIndeterminate(true);
            progressDialog.show();
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
            setRecycleList(s);
            fragment=new Notification();
            for(String item: getLinkId()){
                System.out.println(item);
            }

            if(fragment!=null){
                Bundle bundle=new Bundle();
                bundle.putSerializable("list",new ContentWrapper(new ArrayList<>(getRecycleList().subList(1,getRecycleList().size()))));
                fragment.setArguments(bundle);
                FragmentManager fragmentManager=getSupportFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.frameContainer,fragment).commit();
            }

        }
    }



}
