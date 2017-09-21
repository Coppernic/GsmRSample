package fr.coppernic.samples.gsmr;

import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import fr.coppernic.sdk.gsmr.GsmR;
import fr.coppernic.sdk.utils.core.CpcBytes;
import fr.coppernic.sdk.utils.io.InstanceListener;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link VoiceFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class VoiceFragment extends Fragment implements GsmR.GsmRListener {
    public static final String TAG = "VoiceFragment";

    private static final String SELECT_AUDIO_HARDWARE_SET_2 = "AT^SNFS=2";
    private static final String CALL = "ATD";
    private static final String HANG_UP = "ATH";
    private static final String ANSWER = "ATA";
    private static final String RING = "RING";
    // GSM-R
    private GsmR gsmR;
    // UI
    private EditText etPhoneNumber;

    public VoiceFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment VoiceFragment.
     */
    public static VoiceFragment newInstance() {
        return new VoiceFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_voice, container, false);
        // Instantiation of GSM-R object
        GsmR.Builder.get().withListener(this).build(getActivity(), new InstanceListener<GsmR>() {
            @Override
            public void onCreated(GsmR gsmR) {
                VoiceFragment.this.gsmR = gsmR;
                // Opens communication channel
                VoiceFragment.this.gsmR.open();
                // Switches audio hardware to channel 2
                Utils.sendCommand(gsmR, SELECT_AUDIO_HARDWARE_SET_2, TAG);
            }

            @Override
            public void onDisposed(GsmR gsmR) {

            }
        });

        etPhoneNumber = (EditText)rootView.findViewById(R.id.etPhoneNumber);

        // Call button
        rootView.findViewById(R.id.btnCall).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Gets phone number
                String phoneNumber = etPhoneNumber.getText().toString();
                // Makes a phone call to phoneNumber
                Utils.sendCommand(gsmR, CALL + phoneNumber + ";", TAG);
            }
        });

        // Hang up button
        rootView.findViewById(R.id.btnHangUp).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Hangs up
                Utils.sendCommand(gsmR, HANG_UP, TAG);
            }
        });

        // Answer button
        rootView.findViewById(R.id.btnAnswer).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Answers
                Utils.sendCommand(gsmR, ANSWER, TAG);
            }
        });

        return rootView;
    }

    @Override
    public void onStop() {
        super.onStop();

        gsmR.close();
    }

    @Override
    public void onData(byte[] bytes) {
        Log.d(TAG, CpcBytes.byteArrayToAsciiString(bytes));

        // Parsing data received
        parseAnswer(bytes);
    }

    private void parseAnswer(byte[] answer) {
        // Deal with ring
        String answerString = CpcBytes.byteArrayToAsciiString(answer);
        if (answerString.contains(RING)) {
            // Plays a notification whenever a RING is received
            new PlayNotificationTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }
    }

    // Async task playing a notification
    private class PlayNotificationTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            try {
                Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                Ringtone r = RingtoneManager.getRingtone(getActivity(), notification);
                r.play();
                SystemClock.sleep(1000);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}
