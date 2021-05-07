package com.kite.testplugin;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.better_better_better_better_better_activity_main);
        String str = "";
        for (String item : CategoryManager.getCategoryNames()) {
            str += item;
        }
        ((TextView) findViewById(R.id.content)).setText(str);
    }
}