package fr.coppernic.samples.gsmr;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import fr.coppernic.sdk.utils.core.CpcBytes;

/**
 * Created by benoist on 28/08/17.
 */

public class DataAdapter extends ArrayAdapter<Data> {
    private int resId;
    private LayoutInflater layoutInflater;
    private List<Data> dataArray;

    public DataAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull List<Data> objects) {
        super(context, resource, objects);

        resId = resource;
        dataArray = objects;
        layoutInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return dataArray.size();
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = convertView;

        if (view == null) {
            view = layoutInflater.inflate(resId, null);
        }

        Data data = dataArray.get(position);

        TextView tvDataIn = (TextView)view.findViewById(R.id.tvDataIn);
        TextView tvDataOut = (TextView)view.findViewById(R.id.tvDataOut);

        tvDataIn.setText(CpcBytes.byteArrayToAsciiString(data.getIn()));
        tvDataOut.setText(CpcBytes.byteArrayToAsciiString(data.getOut()));

        return view;
    }
}
