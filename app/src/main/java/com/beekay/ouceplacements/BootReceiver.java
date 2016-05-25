package com.beekay.ouceplacements;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

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

public class BootReceiver extends BroadcastReceiver {
    public BootReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        // an Intent broadcast.
        String path = context.getFilesDir() + "/service/";
        File file = new File(path);
        file.mkdirs();
        path += "service_option.txt";
        String cond = new String();
        File f = new File(path);
        if (!f.exists()) {
            OutputStream myOutput;
            try {
                myOutput = new BufferedOutputStream(new FileOutputStream(path, true));
                myOutput.write(new String("false").getBytes());
                myOutput.flush();
                myOutput.close();
                cond = "false";
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
                while ((text = br.readLine()) != null) {
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
        if (cond.equals("true")) {
                Intent serviceIntent = new Intent(context, AlarmReceiver.class);
                PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 5686, serviceIntent, 0);
                AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                int interval = 3*60*60000;
                manager.setInexactRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), interval, pendingIntent);
            } else {
        }


    }
}
