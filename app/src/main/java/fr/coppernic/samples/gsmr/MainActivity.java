package fr.coppernic.samples.gsmr;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import fr.coppernic.sdk.powermgmt.PowerMgmt;
import fr.coppernic.sdk.powermgmt.PowerMgmtFactory;
import fr.coppernic.sdk.powermgmt.cone.identifiers.InterfacesCone;
import fr.coppernic.sdk.powermgmt.cone.identifiers.ManufacturersCone;
import fr.coppernic.sdk.powermgmt.cone.identifiers.ModelsCone;
import fr.coppernic.sdk.powermgmt.cone.identifiers.PeripheralTypesCone;

public class MainActivity extends AppCompatActivity {

    private FragmentManager fragmentManager = null;
    private PowerMgmt powerMgmt;

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

        powerMgmt = PowerMgmtFactory.get().setContext(this)
                .setPeripheralTypes(PeripheralTypesCone.Modem)
                .setManufacturers(ManufacturersCone.Triorail)
                .setModels(ModelsCone.Trm5_ext)
                .setInterfaces(InterfacesCone.ExpansionPort)
                .build();
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

    @Override
    protected void onStart() {
        super.onStart();
        // If modem is not powered on, start Pin activity
        // to power it on and enter pin
        if (!powerMgmt.get()) {
            Intent intent = new Intent(this, PinActivity.class);
            startActivity(intent);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        // Powers off modem
        powerMgmt.powerOff();
    }
}