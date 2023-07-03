package org.nita.notifications;

import android.Manifest;
import android.Manifest.permission;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.listener.single.DialogOnDeniedPermissionListener.Builder;
import com.karumi.dexter.listener.single.PermissionListener;

import com.permissionx.guolindev.PermissionMediator;
import com.permissionx.guolindev.PermissionX;
import java.util.Collections;
import org.nita.notifications.fragments.MainNoticeFragment;


public class MainActivity extends AppCompatActivity {

    public static final String HOME_URL = "https://nita.ac.in/UserPanel/Default.aspx";
    public static final String NOTICE_URL = "https://nita.ac.in/UserPanel/ViewAllNewsAndEvents.aspx?nModuleID=gi";
    public static final String EVENTS_URL = "https://nita.ac.in/UserPanel/ViewAllNewsAndEvents.aspx?nModuleID=ea";
    public static final String STUDENTS_URL = "https://nita.ac.in/UserPanel/StudentNotification.aspx";
    public static final String DOWNLOAD_CORNER_URL = "https://nita.ac.in/UserPanel/DownloadCorner.aspx";
    public static final String ORDER_URL = "https://nita.ac.in/UserPanel/Minutes_Others.aspx?file=Order_Circulars";
    public static final String CATEGORY_TAG = "CAT_TAG";
    public static final String URL_TAG = "URL_TAG";
    public static final String SAVE_KEY = "save_key";

    Toolbar toolbar;
    View root;
    PermissionMediator permissionMediator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        permissionMediator = PermissionX.init(this);
        initInstances();
        createNotificationChannel();
    }

    @Override
    protected void onResume() {
        super.onResume();
        String msg = getIntent().getStringExtra("msg");
        if(msg!=null && !msg.equals("")){
            new AlertDialog.Builder(this)
                    .setTitle("New Messages : ")
                    .setMessage(msg)
                    .setPositiveButton("Dismiss",null)
                    .setCancelable(false)
                    .show();
        }
    }

    private void initInstances() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        root = findViewById(R.id.rootLayout);
        setSupportActionBar(toolbar);
        final ViewPager viewPager = (ViewPager) findViewById(R.id.viewPager);
        setupViewPager(viewPager);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        tabLayout.setupWithViewPager(viewPager);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fabBtn);
        fab.setOnClickListener(v -> setupViewPager(viewPager));

        // manual invocation not required in FCM
        /*Intent intent = new Intent(this, RegistrationIntentService.class);
        startService(intent);*/
    }

    private void createNotificationChannel() {
        if (VERSION.SDK_INT >= VERSION_CODES.TIRAMISU) {
            permissionMediator.permissions(permission.POST_NOTIFICATIONS)
                .explainReasonBeforeRequest()
                .onExplainRequestReason((scope, deniedList) -> {
                    scope.showRequestReasonDialog(Collections.singletonList(permission.POST_NOTIFICATIONS),
                        "Required for sending push notifications on website updates.", "OK",
                        "Cancel");
                }).request((allGranted, grantedList, deniedList) -> {
                    if (!allGranted) {
                        Toast.makeText(this, "Notification permission denied", Toast.LENGTH_LONG).show();
                    }
                });
        }
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(getString(R.string.channel_id), name, importance);
            channel.setDescription(description);

            NotificationChannel channel2 = new NotificationChannel(getString(R.string.channel_id_2), "Website updates", NotificationManager.IMPORTANCE_LOW);
            channel.setDescription("Website updates without new notices");
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
            notificationManager.createNotificationChannel(channel2);
        }
    }

    private void setupViewPager(ViewPager viewPager) {
        viewPager.setOffscreenPageLimit(3);
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFrag(new MainNoticeFragment(), getString(R.string.category_latest), HOME_URL);
        adapter.addFrag(new MainNoticeFragment(), getString(R.string.category_notice), NOTICE_URL);
        adapter.addFrag(new MainNoticeFragment(), getString(R.string.category_events), EVENTS_URL);
        adapter.addFrag(new MainNoticeFragment(), getString(R.string.category_students),
            STUDENTS_URL);
        adapter.addFrag(new MainNoticeFragment(), getString(R.string.category_download),
            DOWNLOAD_CORNER_URL);
        adapter.addFrag(new MainNoticeFragment(), getString(R.string.category_order),ORDER_URL);
        viewPager.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        /*if (id == R.id.action_settings) {
            startActivity(new Intent(this,SettingsActivity.class));
            return true;
        }*/

        if (id == R.id.action_about) {
            startActivity(new Intent(this, AboutActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    // Handle token refresh callback
    class RegistrationListener extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            context.unregisterReceiver(this);
            Snackbar.make(root, "Subscribed to NITA push notifications", Snackbar.LENGTH_SHORT).show();
        }
    }
}
