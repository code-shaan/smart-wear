package android.iot.smartwear;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.android.androidTest.R;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


/**
 * Created by Shantanu Sirsamkar
 */

public class VitalsFragment extends Fragment {

    public static final MediaType getVitalsMediaType = MediaType.parse("application/json; charset=utf-8");

    String getDevicesUrl = "http://1ac12d71.ngrok.io/healthbot/rest/devices";
    String getVitalsUrl = "http://1ac12d71.ngrok.io/healthbot/rest/checkvitals";

    StringBuilder getVitalsPostBody = new StringBuilder();

    Map<String, String> classNameIdMap = new LinkedHashMap<>();

    List<String> classNameIdList = new ArrayList<>();
    List<VitalsFragmentHelper> deviceList;

    String[] deviceName;
    TypedArray deviceImage;

    ProgressDialog getVitalsProgressDialog;

    ListView devicesListView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.vitals_layout, container, false);
        getVitalsProgressDialog = ProgressDialog.show(getActivity(), "Loading", "Retrieving sensor data from server", true);
        loadDevices();
        return view;
    }

    private void loadDevices() {
        final Map<String, String> vitalsDevicesMap = new LinkedHashMap<>();
        try {
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url(getDevicesUrl)
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    call.cancel();
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    final String getVitalsResponseData = response.body().string();
                    try {
                        JSONObject getVitalsResponseJsonObject = new JSONObject(getVitalsResponseData);
                        JSONArray getVitalsResponseJsonArray = getVitalsResponseJsonObject.getJSONArray("aaData");

                        for (int index = 0; index < getVitalsResponseJsonArray.length(); index++) {
                            vitalsDevicesMap.put
                                    (getVitalsResponseJsonArray.getJSONObject(index).getString("deviceId"),
                                            getVitalsResponseJsonArray.getJSONObject(index).getString("id"));
                        }

                        setMap(vitalsDevicesMap);

                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                final List<String> vitalsDeviceList = new ArrayList<>();
                                getVitalsProgressDialog.dismiss();
                                Iterator deviceNameIdMapIterator = classNameIdMap.entrySet().iterator();
                                while (deviceNameIdMapIterator.hasNext()) {
                                    Map.Entry pair = (Map.Entry) deviceNameIdMapIterator.next();
                                    vitalsDeviceList.add(pair.getKey().toString());
                                }

                                setList(vitalsDeviceList);

                                deviceList = new ArrayList<>();
                                deviceName = vitalsDeviceList.toArray(new String[0]);
                                deviceImage = getResources().obtainTypedArray(R.array.device_images);

                                for (int index = 0; index < deviceName.length; index++) {
                                    VitalsFragmentHelper item =
                                            new VitalsFragmentHelper(deviceName[index], deviceImage.getResourceId(index, -1));
                                    deviceList.add(item);
                                }

                                devicesListView = (ListView) getView().findViewById(R.id.list);
                                VitalsFragmentAdapter adapter = new VitalsFragmentAdapter(getContext(), deviceList);
                                devicesListView.setAdapter(adapter);

                                devicesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                        final String member_name = deviceList.get(position).getVitalsSensorName();
                                        final ProgressDialog sensorProgressDialog = ProgressDialog.show(getActivity(),
                                                "Measuring",
                                                "Gathering accurate live data.. \nRelax! Take a few breaths!", true);

                                        JSONObject vitalsJsonObject = null;
                                        String getDeviceVitals =
                                                getRegisterFieldsData(
                                                        getView(), getVitalsPostBody, member_name.toString(), position);
                                        try {
                                            vitalsJsonObject = new JSONObject(getDeviceVitals);
                                            final OkHttpClient client = new OkHttpClient.Builder().
                                                    connectTimeout(60, TimeUnit.SECONDS).
                                                    readTimeout(60, TimeUnit.SECONDS).build();
                                            RequestBody monitorRequestBody =
                                                    RequestBody.create(getVitalsMediaType, vitalsJsonObject.toString());
                                            Request request = new Request.Builder()
                                                    .url(getVitalsUrl)
                                                    .post(monitorRequestBody)
                                                    .build();

                                            client.newCall(request).enqueue(new Callback() {
                                                @Override
                                                public void onFailure(Call call, IOException e) {
                                                    call.cancel();
                                                }

                                                @Override
                                                public void onResponse(Call call, Response response) throws IOException {
                                                    final String responseData = response.body().string();
                                                    getActivity().runOnUiThread(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            try {
                                                                sensorProgressDialog.dismiss();
                                                                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
                                                                alertDialogBuilder.setTitle(member_name.toString());
                                                                String sensorMessage = "Live reading for this sensor - \n\n" +
                                                                        "Heart Rate: 150 bpm \n\n" +
                                                                        "Comment: Too high, Calling 911";
                                                                alertDialogBuilder
                                                                        .setMessage(sensorMessage)
                                                                        .setCancelable(false)
                                                                        .setPositiveButton("Ok",new DialogInterface.OnClickListener() {
                                                                            public void onClick(DialogInterface dialog,int id) {
                                                                                //clearFields(registerView);
                                                                                dialog.cancel();
                                                                            }
                                                                        });
                                                                AlertDialog alertDialog = alertDialogBuilder.create();
                                                                alertDialog.show();

                                                            } catch (Exception e) {
                                                                e.printStackTrace();
                                                            }
                                                        }
                                                    });
                                                }
                                            });

                                        } catch (JSONException j) {
                                            j.printStackTrace();
                                        }

                                    }
                                });

                            }
                        });
                    } catch (Exception j) {
                        j.printStackTrace();
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String getRegisterFieldsData(View view, StringBuilder monitorStringBuilder, String deviceName, int position) {
        monitorStringBuilder.setLength(0);

        monitorStringBuilder.append("{ ");

        monitorStringBuilder.append("\"" + "userId" + "\":" + "\"user12345" + "\",");

        monitorStringBuilder.append("\"" + "deviceId" + "\":" + "\"" + classNameIdMap.get(deviceName) + "\",");

        monitorStringBuilder.append("\"" + "deviceName" + "\":" + "\"" + classNameIdMap.keySet().toArray()[position].toString() + "\"");

        monitorStringBuilder.append(" }");

        return monitorStringBuilder.toString();
    }

    private void setMap(Map<String, String> incomingMap) {
        classNameIdMap = incomingMap;
    }

    private void setList(List<String> incomingList) { classNameIdList = incomingList;}

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle("Vital Signs");
    }
}
