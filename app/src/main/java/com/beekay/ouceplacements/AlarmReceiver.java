package com.beekay.ouceplacements;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.widget.Toast;

import java.io.File;

public class AlarmReceiver extends BroadcastReceiver {

    private DataOpener opener;

    public AlarmReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        System.out.println("got broadcast");
        try {
            opener = new DataOpener(context);
            opener.openRead();
            String user = null, pass = null;
            String errorText = "No credentials to Login with... Can't turn on notifications... Try again by saving atleast one user credentials";
            Cursor cursor = opener.retrieve();
            if (cursor.getCount() > 0) {
                int i = 0;
                while (cursor.moveToFirst() && i == 0) {
                    user = cursor.getString(0);
                    pass = cursor.getString(1);
                    i++;
                }

                Intent serviceIntent = new Intent(context, Background.class);
                serviceIntent.putExtra("uname", user);
                serviceIntent.putExtra("pass", pass);
                System.out.println(user + pass);
                context.startService(serviceIntent);

            } else {
                Toast.makeText(context, errorText, Toast.LENGTH_LONG).show();
                Intent intents = new Intent(context, AlarmReceiver.class);
                AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                PendingIntent pi = PendingIntent.getBroadcast(context, 5686, intents, 0);
                manager.cancel(pi);
                String path = context.getFilesDir() + "/service/";
                path += "service_option.txt";
                File f = new File(path);
                if (f.exists() && !f.isDirectory())
                    f.delete();
            }
            cursor.close();
            opener.close();
        }catch (SQLException e){

        }
        finally {
            opener.close();
        }
    }
}
