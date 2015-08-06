package com.beekay.ouceplacements;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.PersistableBundle;
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


public class Home extends AppCompatActivity {

    DrawerLayout drawer;
    ActionBarDrawerToggle toggle;
    Toolbar tool;
    ListView list;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        drawer=(DrawerLayout)findViewById(R.id.drawer);
        list=(ListView)findViewById(R.id.drawerlist);
        tool=(Toolbar)findViewById(R.id.tool);
        setSupportActionBar(tool);
        Intent intent=getIntent();
        ArrayList<Map<String,String>> cooks=(ArrayList<Map<String,String>>)intent.getSerializableExtra("cookie");
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

    public class HomeList extends AsyncTask<ArrayList<Map<String,String>>,String,ArrayList<String>> {




        @Override
        protected ArrayList<String> doInBackground(ArrayList<Map<String,String>>... params) {
            ArrayList<String> sideList=new ArrayList<>();
            if(params[0].size()!=0)
                try {
                    Document doc = Jsoup.connect("http://oucecareers.org/students/students.php").followRedirects(false).cookies(params[0].get(0)).get();
                    //System.out.println(doc.text().toString());
                    Element welcome=doc.select("div#adminpasscontents").first();

                    String name=welcome.text().substring(7);
                    System.out.println(name+"welcome");
                    //sideList.add(name);
                    Elements lists=doc.select("div#header-tabs");
                    Iterator<Element> eachList=lists.select("ul").select("li").iterator();
                    while(eachList.hasNext()) {
                        Element subList=eachList.next();
                        if(subList.children().size()>0){
                            for(Element l : subList.child(0).select("a")){
                                sideList.add(l.text());
                            }
                        }
                        else
                            sideList.add(subList.text());

                    }
                    sideList.add(name);
                    return sideList;
                }catch (Exception e){
                    e.printStackTrace();
                }
            return null;
        }

        @Override
        protected void onPostExecute(ArrayList<String> s) {
            Collections.reverse(s);
            super.onPostExecute(s);
            ArrayAdapter<String> adapter=new ArrayAdapter<String>(getBaseContext(),android.R.layout.simple_list_item_1,android.R.id.text1, s);
            list.setAdapter(adapter);

        }
    }



}
