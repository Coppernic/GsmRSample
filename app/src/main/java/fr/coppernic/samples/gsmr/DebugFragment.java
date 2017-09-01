package fr.coppernic.samples.gsmr;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.ArrayList;

import fr.coppernic.sdk.gsmr.GsmR;
import fr.coppernic.sdk.utils.core.CpcBytes;
import fr.coppernic.sdk.utils.core.CpcResult;
import fr.coppernic.sdk.utils.io.InstanceListener;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DebugFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DebugFragment extends Fragment implements GsmR.GsmRListener{
    public static final String TAG = "DebugFragment";

    private GsmR gsmR;
    private DataAdapter dataAdapter;
    private ArrayList<Data> dataArray;
    private Data data;
    private byte[] command;

    public DebugFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment DebugFragment.
     */
    public static DebugFragment newInstance() {
        return new DebugFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Instantiation of GsmR object
        GsmR.Builder.get().withListener(this).build(getActivity(), new InstanceListener<GsmR>() {
            @Override
            public void onCreated(GsmR gsmR) {
                DebugFragment.this.gsmR = gsmR;
                // Opens communication channel
                CpcResult.RESULT res = DebugFragment.this.gsmR.open();

                if (res == CpcResult.RESULT.OK) {
                    Log.d(TAG, "open OK");
                } else {
                    Log.d(TAG, "open Fail");
                }
            }

            @Override
            public void onDisposed(GsmR gsmR) {

            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_debug, container, false);

        // AT button
        rootView.findViewById(R.id.btnAt).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendCommand("AT\r");
            }
        });

        // AT+CPIN button
        rootView.findViewById(R.id.btnAtCpinQuery).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendCommand("AT+CPIN?\r");
            }
        });

        // Data list view
        dataArray = new ArrayList<>();
        ListView lvData = (ListView)rootView.findViewById(R.id.lvData);
        dataAdapter = new DataAdapter(getActivity(), R.layout.commands_row, dataArray);
        lvData.setAdapter(dataAdapter);

        // Inflate the layout for this fragment
        return rootView;
    }

    @Override
    public void onStop() {
        super.onStop();
        gsmR.close();
    }

    @Override
    public void onData(byte[] bytes) {
        Log.d(TAG, CpcBytes.byteArrayToString(bytes));
        dataArray.add(new Data(command, bytes));
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                dataAdapter.notifyDataSetChanged();
            }
        });
    }

    private void sendCommand(String commandString) {
        command = commandString.getBytes();
        gsmR.send(command, command.length);
    }
}