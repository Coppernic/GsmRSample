package fr.coppernic.samples.gsmr;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import fr.coppernic.sdk.gsmr.GsmR;
import fr.coppernic.sdk.utils.core.CpcBytes;
import fr.coppernic.sdk.utils.io.InstanceListener;

public class PinActivity extends AppCompatActivity implements GsmR.GsmRListener {
    public static final String TAG = "PinActivity";

    private GsmR gsmR;
    private EditText etPin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pin);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        etPin = (EditText)findViewById(R.id.etPin);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendCommand("AT+CPIN=" + etPin.getText().toString() + "\r");
            }
        });

        GsmR.Builder.get()
                .withListener(this)
                .build(this, new InstanceListener<GsmR>() {
            @Override
            public void onCreated(GsmR gsmR) {
                PinActivity.this.gsmR = gsmR;

                PinActivity.this.gsmR.open();

                sendCommand("AT+CPIN?\r");
            }

            @Override
            public void onDisposed(GsmR gsmR) {

            }
        });
    }

    @Override
    public void onData(byte[] bytes) {
        Log.d(TAG, CpcBytes.byteArrayToAsciiString(bytes));

        String answer = parseAnswer(bytes);

        if (answer.equals("+CPIN: READY") || answer.equals("OK")) {
            gsmR.close();
            Log.d(TAG, "GSM-R closed");
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }
    }

    private void sendCommand(String commandString) {
        byte[] command = commandString.getBytes();
        Log.d(TAG, CpcBytes.byteArrayToAsciiString(command));
        gsmR.send(command, command.length);
    }

    private String parseAnswer(byte[] answer) {
        // 1 - Keep only answer
        int start = CpcBytes.findBytesInArray(answer, new byte[]{13,10}, 0) + 2;
        int end = CpcBytes.findBytesInArray(answer, new byte[]{13,10}, start);

        if (end - start > 0) {

            byte[] answerOnly = new byte[end - start];
            System.arraycopy(answer, start, answerOnly, 0, answerOnly.length);

            Log.d(TAG, CpcBytes.byteArrayToAsciiString(answerOnly));

            return CpcBytes.byteArrayToAsciiString(answerOnly);
        }

        return "";
    }

    @Override
    protected void onStop() {
        super.onStop();
    }
}
