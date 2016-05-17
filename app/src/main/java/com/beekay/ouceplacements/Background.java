package com.beekay.ouceplacements;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;


/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 *
 */
public class Background extends IntentService {

    private Notification notification;
    private NotificationManager notificationManager;
    public Background() {
        super("Background");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        System.out.println("came to service");
        String message = "Service is running at "+new java.sql.Timestamp(System.currentTimeMillis());
        ArrayList<Contents> contentList = new ArrayList<>();
        try {
            Connection.Response res = Jsoup.connect("http://oucecareers.org/s_logaction.php").data("uname",intent.getExtras().getString("uname"),"upass",intent.getExtras().getString("pass"),"Submit","sign in").method(Connection.Method.POST).timeout(50000).execute();
            Document doc = Jsoup.connect("http://oucecareers.org/students/showNotice.php").followRedirects(false).cookies(res.cookies()).timeout(50000).get();
            Log.d("came","in");
            Elements table = doc.select("table");
            Contents contents;
            int i=0;
            boolean firstSkipped = false;
            for(Element tr: table.select("tr")){
                contents = new Contents();
                if(firstSkipped) {

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
                    contentList.add(contents);
                }
                firstSkipped=true;

            }
        } catch (IOException e) {
            Toast.makeText(this,"Timed out",Toast.LENGTH_LONG).show();
            Log.d("logged","out");
            e.printStackTrace();
        }
        String notificatonText = contentList.get(0).notificationContent;
        notification = new NotificationCompat.Builder(getApplicationContext()).setContentTitle("New Placements News").setContentText(notificatonText).setWhen(System.currentTimeMillis()).setDefaults(android.app.Notification.DEFAULT_SOUND).setSmallIcon(R.mipmap.ic_launcher).build();
        notificationManager.notify(1,notification);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
    }
}
