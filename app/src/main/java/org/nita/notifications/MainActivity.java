package org.nita.notifications;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.listener.single.DialogOnDeniedPermissionListener;
import com.karumi.dexter.listener.single.PermissionListener;

import org.nita.notifications.fragments.MainNoticeFragment;
import org.nita.notifications.settings.SettingsActivity;


public class MainActivity extends AppCompatActivity {

    public static final String BASE_URL = "https://www.nita.ac.in";
    public static final String ACADEMIC_URL = "https://www.nita.ac.in/NITAmain/academics/academicsNotice.html";
    public static final String EVENTS_URL = "https://www.nita.ac.in/NITAmain/news--events/newseventshome.html";
    public static final String UPCOMING_URL = "https://www.nita.ac.in/NITAmain/news--events/events.html";
    public static final String CATEGORY_TAG = "CAT_TAG";
    public static final String URL_TAG = "URL_TAG";
    public static final String SAVE_KEY = "save_key";

    Toolbar toolbar;
    View root;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initInstances();

        PermissionListener dialogPermissionListener =
                DialogOnDeniedPermissionListener.Builder
                        .withContext(this)
                        .withTitle("Storage permission")
                        .withMessage("Storage permission (optional) is required to cache notice data for offline usage.")
                        .withButtonText(android.R.string.ok)
                        .withIcon(R.mipmap.ic_launcher)
                        .build();
        Dexter.withContext(this)
                .withPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .withListener(dialogPermissionListener)
                .check();
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

    private void setupViewPager(ViewPager viewPager) {
        viewPager.setOffscreenPageLimit(3);
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFrag(new MainNoticeFragment(), getString(R.string.category_latest), BASE_URL);
        adapter.addFrag(new MainNoticeFragment(), getString(R.string.category_academic), ACADEMIC_URL);
        adapter.addFrag(new MainNoticeFragment(), getString(R.string.category_events), EVENTS_URL);
        adapter.addFrag(new MainNoticeFragment(), getString(R.string.category_upcoming), UPCOMING_URL);
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
        if (id == R.id.action_settings) {
            startActivity(new Intent(this,SettingsActivity.class));
            return true;
        }

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
            Snackbar.make(root, "Subscribed to NITA push notifications", Snackbar.LENGTH_SHORT);
        }
    }
}
