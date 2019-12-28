package com.zuccessful.trueharmony.services;

import android.app.Service;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class SMSService extends Service {

    File myFile;

    StringBuffer SMS_Messages = new StringBuffer();
    DateFormat outputformat = new SimpleDateFormat("HH:mm");
    String output = null;
    public SMSService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public int onStartCommand(Intent intent, int flags, int startId) {


        String path = Environment.getExternalStorageDirectory().getAbsolutePath();
        myFile = new File(path+"/SMSlog.csv");
        try{
            myFile.createNewFile();
        }catch(Exception e){
            Log.e("FileTag","File creation:"+e);
        }

        fetchInbox();
        return START_STICKY;

    }


    public void fetchInbox()
    {
        ArrayList sms = new ArrayList();
        StringBuffer SMSmessage = new StringBuffer();
        //System.out.println("SMS");
        SMSmessage.append("SMS Details as on :");

        String curr_date = new SimpleDateFormat("HH:mm").format(Calendar.getInstance().getTime());
        //DateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSX", Locale.US);

        // output = outputformat.format(curr_date);
        output = curr_date;
        int h = Integer.parseInt(output.split(":")[0]);
        int m = Integer.parseInt(output.split(":")[1]);

        System.out.println("Time" + h+ "," +m);
        SMSmessage.append(curr_date);
        SMSmessage.append("Sent :");

        Uri uriSms = Uri.parse("content://sms/sent");
        Cursor cursor = getContentResolver().query(uriSms, new String[]{"_id", "address", "date", "body"}, null, null, null);

        if (cursor != null) {
            System.out.println("got cursor");
        }
        cursor.moveToFirst();
        //System.out.println("SMS");
        while (cursor.moveToNext()) {
            System.out.println("SMS");
            String address = cursor.getString(1);
            String body = cursor.getString(3);

            System.out.println("======&gt; Mobile number =&gt; " + address);
            System.out.println("=====&gt; SMS Text =&gt; " + body);

            sms.add("Address=&gt; " + address + "n SMS =&gt; " + body);

            SMSmessage.append(sms);
        }
        SMSmessage.append("Inbox");

        Uri uriSmsinbox = Uri.parse("content://sms/inbox");
        Cursor cursor2 = getContentResolver().query(uriSmsinbox, new String[]{"_id", "address", "date", "body"}, null, null, null);

        if (cursor2 != null) {
            System.out.println("got cursor");
        }
        cursor2.moveToFirst();
        //System.out.println("SMS");
        while (cursor2.moveToNext()) {
            System.out.println("SMS");
            String address = cursor2.getString(1);
            String body = cursor2.getString(3);

            System.out.println("======&gt; Mobile number =&gt; " + address);
            System.out.println("=====&gt; SMS Text =&gt; " + body);

            sms.add("Address=&gt; " + address + "n SMS =&gt; " + body);

            SMSmessage.append(sms);
        }

        System.out.println(SMSmessage);

        try {

            FileOutputStream fOut = new FileOutputStream(myFile);
            OutputStreamWriter myOutWriter = new OutputStreamWriter(fOut);

            myOutWriter.append(SMSmessage);
            myOutWriter.close();
            fOut.close();

        } catch (FileNotFoundException e) {
            System.out.println("exception");
            e.printStackTrace();
        }
        catch (IOException e) {e.printStackTrace();}


    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
