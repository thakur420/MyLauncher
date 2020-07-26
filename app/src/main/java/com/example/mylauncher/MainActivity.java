package com.example.mylauncher;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TableLayout;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();
    }

    public void launchApp(View view) {
        //Toast.makeText(getApplicationContext(),"App bar clicked",Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this,AppList.class);
        startActivity(intent);
    }
}
