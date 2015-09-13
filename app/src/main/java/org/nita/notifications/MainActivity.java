package org.nita.notifications;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import org.nita.notifications.fragments.MainNoticeFragment;
import org.nita.notifications.gcm.RegistrationIntentService;


public class MainActivity extends AppCompatActivity {

    public static final String BASE_URL = "http://www.nita.ac.in";
    public static final String ACADEMIC_URL = "http://www.nita.ac.in/NITAmain/academics/academicsNotice.html";
    public static final String EVENTS_URL = "http://www.nita.ac.in/NITAmain/news--events/newseventshome.html";
    public static final String UPCOMING_URL = "http://www.nita.ac.in/NITAmain/news--events/events.html";
    public static final String CATEGORY_TAG = "CAT_TAG";
    public static final String URL_TAG = "URL_TAG";
    public static final String SAVE_KEY = "save_key";

    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initInstances();
    }


    private void initInstances() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ViewPager viewPager = (ViewPager) findViewById(R.id.viewPager);
        setupViewPager(viewPager);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        tabLayout.setupWithViewPager(viewPager);

        //TODO handle registration gracefully
        Intent intent = new Intent(this, RegistrationIntentService.class);
        startService(intent);
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
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
