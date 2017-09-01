package fr.coppernic.samples.gsmr;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity {

    private FragmentManager fragmentManager = null;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_voice:
                    replaceFragment(getVoiceFragment(), VoiceFragment.TAG);
                    return true;
                case R.id.navigation_dashboard:
                    replaceFragment(getDebugFragment(), DebugFragment.TAG);
                    return true;
                case R.id.navigation_sms:
                    replaceFragment(getSmsFragment(), SmsFragment.TAG);
                    return true;
            }
            return false;
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        fragmentManager = getSupportFragmentManager();
        if (fragmentManager.findFragmentByTag(DebugFragment.TAG) == null) {

            this.setupFragments();
        }
    }

    private void setupFragments() {
        final FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.add(R.id.content, getVoiceFragment(), VoiceFragment.TAG);
        ft.commit();
    }

    private void replaceFragment(final Fragment fragment, String tag) {

        if (fragment == null)
            return;

        final FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.replace(R.id.content, fragment, tag);
        ft.commit();
    }

    private DebugFragment getDebugFragment() {
        return DebugFragment.newInstance();
    }

    private SmsFragment getSmsFragment() {
        return SmsFragment.newInstance();
    }

    private VoiceFragment getVoiceFragment() {
        return VoiceFragment.newInstance();
    }
}