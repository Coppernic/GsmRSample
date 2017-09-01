package fr.coppernic.samples.gsmr;

import android.util.Log;

import fr.coppernic.sdk.gsmr.GsmR;
import fr.coppernic.sdk.utils.core.CpcBytes;

/**
 * Created by benoist on 01/09/17.
 */

public class Utils {
    public static void sendCommand (GsmR gsmR, String command, String TAG) {
        byte[] commandBytes = (command + "\r").getBytes();
        Log.d(TAG, ">> " + CpcBytes.byteArrayToAsciiString(commandBytes));
        gsmR.send(commandBytes, commandBytes.length);
    }
}
