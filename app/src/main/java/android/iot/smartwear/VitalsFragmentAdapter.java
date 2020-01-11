package android.iot.smartwear;

import java.util.List;

import com.example.android.androidTest.R;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by Shantanu Sirsamkar
 */

public class VitalsFragmentAdapter extends BaseAdapter {
    Context context;
    List<VitalsFragmentHelper> deviceList;

    VitalsFragmentAdapter(Context context, List<VitalsFragmentHelper> deviceList) {
        this.context = context;
        this.deviceList = deviceList;
    }

    @Override
    public int getCount() {
        return deviceList.size();
    }

    @Override
    public Object getItem(int position) {
        return deviceList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return deviceList.indexOf(getItem(position));
    }

    private class ViewHolder {
        ImageView deviceImage;
        TextView deviceName;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

        if(convertView == null) {
            convertView = mInflater.inflate(R.layout.device_layout, null);
            holder = new ViewHolder();

            holder.deviceImage = (ImageView) convertView.findViewById(R.id.device_image);
            holder.deviceName = (TextView) convertView.findViewById(R.id.device_image);

            VitalsFragmentHelper row_pos = deviceList.get(position);

            holder.deviceImage.setImageResource(row_pos.getVitalsSensorImage());
            holder.deviceName.setText(row_pos.getVitalsSensorName());
        }
        return convertView;
    }
}
