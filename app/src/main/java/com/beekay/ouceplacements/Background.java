package com.beekay.ouceplacements;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import org.jsoup.Connection;
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
        String message = "Service is running at " + new java.sql.Timestamp(System.currentTimeMillis());
        ArrayList<Contents> contentList = new ArrayList<>();
        NetCheck netCheck = new NetCheck();
        if (netCheck.isNetAvailable(this)) {
            try {
                Connection.Response res = Jsoup.connect("http://oucecareers.org/s_logaction.php").data("uname", intent.getExtras().getString("uname"), "upass", intent.getExtras().getString("pass"), "Submit", "sign in").method(Connection.Method.POST).timeout(50000).execute();
                Document doc = Jsoup.connect("http://oucecareers.org/students/showNotice.php").followRedirects(false).cookies(res.cookies()).timeout(50000).get();
                Log.d("came", "in");
                Elements table = doc.select("table");
                Contents contents;
                int i = 0;
                boolean firstSkipped = false;
                for (Element tr : table.select("tr")) {
                    contents = new Contents();
                    if (firstSkipped) {

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
                    firstSkipped = true;

                }
            } catch (IOException e) {
//                Toast.makeText(this, "Timed out", Toast.LENGTH_LONG).show();
            }
            String path = this.getApplicationContext().getFilesDir() + "/service/";
            File file = new File(path);
            file.mkdirs();
            path += "last_notice.txt";
            File f = new File(path);
            if (!f.exists()) {
                OutputStream myOutput;
                try {
                    myOutput = new BufferedOutputStream(new FileOutputStream(path, true));
                    myOutput.write(contentList.get(0).notificationContent.getBytes());
                    myOutput.flush();
                    myOutput.close();
                    pushNotification(contentList, 0);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                InputStream inputStream;
                try {
                    inputStream = new BufferedInputStream(new FileInputStream(path));
                    BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
                    String text = null;
                    String notText = null;
                    while ((text = br.readLine()) != null) {
                        notText=text;
                    }
                    br.close();
                    inputStream.close();
                    if (contentList.size()!=0 && !notText.equals(contentList.get(0).notificationContent)) {
                        int i = 0;
                        while (!notText.equals(contentList.get(i).notificationContent))
                            i++;
                        pushNotification(contentList, i-1);
                    }else{
//                        Toast.makeText(this,"Up to date",Toast.LENGTH_LONG).show();
                        pushNotification(null,-1);
                    }
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            }
            else{
//                Toast.makeText(this, "Internet Connection not available unable to connect to server", Toast.LENGTH_LONG).show();
            }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
    }

    private void pushNotification(ArrayList<Contents> notificationContent, int i) {
        System.out.println(i);
        Intent notIntent = new Intent(this, PasswordActivity.class);
        PendingIntent notPendingIntent = PendingIntent.getActivity(this, (int) System.currentTimeMillis(), notIntent, 0);
        String notificatonText = i==0?notificationContent.get(0).notificationContent:"New Placements News";
        if(i==0) {
            notification = new NotificationCompat.Builder(getApplicationContext())
                    .setContentTitle("New Placements News")
                    .setContentText(notificatonText)
                    .setWhen(System.currentTimeMillis())
                    .setContentIntent(notPendingIntent)
                    .setAutoCancel(true)
                    .setDefaults(android.app.Notification.DEFAULT_SOUND)
                    .setSmallIcon(R.mipmap.ic_launcher).build();
            notificationManager.notify(1, notification);
        }else if(i!=-1){
            if(i<3){
                notification = new NotificationCompat.Builder(getApplicationContext())
                        .setContentTitle("New Placements News")
                        .setContentText(notificatonText)
                        .setWhen(System.currentTimeMillis())
                        .setContentIntent(notPendingIntent)
                        .setAutoCancel(true)
                        .setDefaults(android.app.Notification.DEFAULT_SOUND)
                        .setStyle(new NotificationCompat.InboxStyle()
                        .addLine(notificationContent.get(0).notificationContent)
                        .addLine(notificationContent.get(1).notificationContent)
                        .addLine(notificationContent.get(2).notificationContent))
                        .setSmallIcon(R.mipmap.ic_launcher).build();
                notificationManager.notify(1, notification);
            }else{
                notification = new NotificationCompat.Builder(getApplicationContext())
                        .setContentTitle("New Placements News")
                        .setContentText(notificatonText)
                        .setWhen(System.currentTimeMillis())
                        .setContentIntent(notPendingIntent)
                        .setAutoCancel(true)
                        .setDefaults(android.app.Notification.DEFAULT_SOUND)
                        .setStyle(new NotificationCompat.InboxStyle()
                                .addLine(notificationContent.get(0).notificationContent)
                                .addLine(notificationContent.get(1).notificationContent)
                                .addLine(notificationContent.get(2).notificationContent)
                        .setSummaryText("+"+(i-3)+" notifications"))
                        .setSmallIcon(R.mipmap.ic_launcher).build();
                notificationManager.notify(1, notification);
            }

        }else if(i==-1){
//            Toast.makeText(this,"Placement news up to date",Toast.LENGTH_LONG).show();
        }


    }

}

