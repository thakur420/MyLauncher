package com.example.mylauncher;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class AppList extends AppCompatActivity {
    FloatingActionButton mic;
    RecyclerView r1;
    List<Appobject> appObjects = new ArrayList<>();

    AppAdapter appAdapter;
    public final String channel_id = "Personal Notification";
    public int notification_id = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_list);

        displayNotification();

        r1 = (RecyclerView)findViewById(R.id.applist);
        appObjects = getInstalledApp();
        appAdapter = new AppAdapter(this,appObjects);
        r1.setAdapter(appAdapter);
        r1.setLayoutManager(new GridLayoutManager(this,3));

        mic = (FloatingActionButton) findViewById(R.id.mic);
        mic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent voice = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                voice.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                voice.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
                voice.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak now....");
                startActivityForResult(voice,1);
            }
        });
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            String spokenWord = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS).get(0);
            boolean flag = false;
            Intent intent  = new Intent(Intent.ACTION_MAIN,null);
            intent.addCategory(Intent.CATEGORY_LAUNCHER);
            List<ResolveInfo> allApps = getApplicationContext().getPackageManager().queryIntentActivities(intent,0);
            for (ResolveInfo app:allApps){
                String name = app.activityInfo.loadLabel(getPackageManager()).toString().toLowerCase();
                String packageName = app.activityInfo.packageName;

                if(spokenWord.toLowerCase().contains(name)){
                    Intent launchAppIntent = getPackageManager().getLaunchIntentForPackage(packageName);
                    if(launchAppIntent != null){
                        startActivity(launchAppIntent) ;
                        flag = true;
                        break;
                    }
                }
            }
            if(!flag){
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://search?q=" + spokenWord)));
                } catch (android.content.ActivityNotFoundException anfe) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/search?q=" + spokenWord)));
                }
            }

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu,menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);

        SearchView searchView = (SearchView) searchItem.getActionView();

        searchView.setImeOptions(EditorInfo.IME_ACTION_DONE);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                appAdapter.getFilter().filter(newText);
                return false;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.play_store:
                Toast.makeText(getApplicationContext(),"PlayStore",Toast.LENGTH_SHORT).show();
                break;
            case R.id.settings:
                Intent intent = new Intent(this, Settings.class);
                this.startActivity(intent);
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    private List<Appobject> getInstalledApp() {
        List<Appobject> list = new ArrayList<>();

        Intent intent  = new Intent(Intent.ACTION_MAIN,null);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        List<ResolveInfo> allApps = getApplicationContext().getPackageManager().queryIntentActivities(intent,0);

        for(ResolveInfo apps:allApps){
            String appName = apps.activityInfo.loadLabel(getPackageManager()).toString();
            String appPackageName = apps.activityInfo.packageName;
            Drawable appImage = apps.activityInfo.loadIcon(getPackageManager());
            Appobject appObj = new Appobject(appName,appPackageName,appImage);

            if(!list.contains(appObj))
                list.add(appObj);
        }
        return list;
    }

    private void displayNotification() {
        createnotificationchannel();
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this,channel_id);
        builder.setSmallIcon(R.drawable.home);
        builder.setContentTitle("Simple Notification");
        builder.setContentText("My Launcher is active now");
        builder.setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(this);
        notificationManagerCompat.notify(notification_id,builder.build());
    }

    private void createnotificationchannel() {
        CharSequence name = "Personal notification";
        String description = "Include all the personal notification";
        int importance = NotificationManager.IMPORTANCE_DEFAULT;

        NotificationChannel notificationChannel = null;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationChannel = new NotificationChannel(channel_id, name, importance);
            notificationChannel.setDescription(description);
            NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(notificationChannel);
        }
    }
}

