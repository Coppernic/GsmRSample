package fr.coppernic.samples.gsmr;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
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
 * Use the {@link SmsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SmsFragment extends Fragment implements GsmR.GsmRListener {
    public final static String TAG = "SmsFragment";

    private static final String QUERY_SMS_MESSAGE_FORMAT = "AT+CMGF?";
    private static final String SET_SMS_MESSAGE_FORMAT_TO_TEXT = "AT+CMGF=1";
    private static final String SEND_SMS = "AT+CMGS=";
    private static final String SMS_IN_PDU_MODE = "+CMGF: 0";
    private static final String SMS_IN_TEXT_MODE = "+CMGF: 1";

    private GsmR gsmR;
    private EditText etMessage;

    public SmsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment SmsFragment.
     */
    public static SmsFragment newInstance() {
        SmsFragment fragment = new SmsFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Instantiation of GSM-R object
        GsmR.Builder.get().withListener(this).build(getActivity(), new InstanceListener<GsmR>() {
            @Override
            public void onCreated(GsmR gsmR) {
                SmsFragment.this.gsmR = gsmR;
                // Opens communication channel
                SmsFragment.this.gsmR.open();
                // Checks if modem is in text mode for SMS
                Utils.sendCommand(gsmR, QUERY_SMS_MESSAGE_FORMAT, TAG);
            }

            @Override
            public void onDisposed(GsmR gsmR) {

            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_sms, container, false);

        final EditText etPhoneNumber = (EditText)rootView.findViewById(R.id.etPhoneNumber);
        etMessage = (EditText)rootView.findViewById(R.id.etMessage);

        // Floating Action button for sending SMS
        FloatingActionButton fab = (FloatingActionButton)rootView.findViewById(R.id.fabSendSms);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Gets phone number
                String phoneNumber = etPhoneNumber.getText().toString();
                // Enters in SMS mode
                String command = SEND_SMS + "\"" + phoneNumber + "\"";
                Utils.sendCommand(gsmR, command, TAG);
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
        // Gets answer and logs it into logcat
        String answer = CpcBytes.byteArrayToAsciiString(bytes);
        Log.d(TAG, "<< " + answer);
        // The modem is sending a SMS, enters text
        if (answer.contains(">")) {
            String message = etMessage.getText().toString();
            Utils.sendCommand(gsmR, message + "\u001a", TAG);
        } else if (answer.contains(SMS_IN_TEXT_MODE)) {
            // OK, Modem is in text SMS mode
        } else if (answer.contains(SMS_IN_PDU_MODE)) {
            // Modem is in PDU SMS mode, it needs to be changed
            // Sets SMS message format to text
            Utils.sendCommand(gsmR, SET_SMS_MESSAGE_FORMAT_TO_TEXT, TAG);
        }
    }
}
