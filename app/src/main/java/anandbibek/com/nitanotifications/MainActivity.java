package anandbibek.com.nitanotifications;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import anandbibek.com.nitanotifications.fragments.AcademicNoticeFragment;
import anandbibek.com.nitanotifications.fragments.EventsNoticeFragment;
import anandbibek.com.nitanotifications.fragments.MainNoticeFragment;


public class MainActivity extends AppCompatActivity {

    public static final String BASE_URL = "http://www.nita.ac.in";
    public static final String ACADEMIC_URL = "http://www.nita.ac.in/NITAmain/academics/academicsNotice.html";
    public static final String EVENTS_URL = "http://www.nita.ac.in/NITAmain/news--events/newseventshome.html";
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
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFrag(new MainNoticeFragment(), "Main");
        adapter.addFrag(new AcademicNoticeFragment(), "Academic");
        adapter.addFrag(new EventsNoticeFragment(), "Events");
        adapter.addFrag(new MainNoticeFragment(), "Upcoming");
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
