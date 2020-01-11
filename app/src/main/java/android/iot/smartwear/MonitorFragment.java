package android.iot.smartwear;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.android.androidTest.R;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.Toast;

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

public class MonitorFragment extends Fragment {

    public static final MediaType monitorDevicesMediaType = MediaType.parse("application/json; charset=utf-8");

    String getDevicesUrl = "http://1ac12d71.ngrok.io/healthbot/rest/devices";
    ProgressDialog getDevicesProgressDialog;

    String enableDevicesUrl = "http://1ac12d71.ngrok.io/healthbot/rest/monitor";
    StringBuilder enableDevicesPostBody = new StringBuilder();

    Map<String, String> classNameIdMap = new LinkedHashMap<>();
    List<String> classNameIdList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
            Bundle savedInstanceState) {
        View monitorView = inflater.inflate(R.layout.monitor_layout, container, false);
        EditText userName = (EditText) monitorView.findViewById(R.id.monitor_layout_user_name);
        userName.setEnabled(false);
        getDevicesProgressDialog = ProgressDialog.show(getActivity(), "Loading", "Retrieving data from server", true);
        loadDevices();
        return monitorView;
    }

    private void loadDevices() {
        final Map<String, String> methodNameIdMap = new LinkedHashMap<>();
        try {
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder().url(getDevicesUrl).build();
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) { call.cancel(); }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    final String getDevicesResponse = response.body().string();
                    try {
                        JSONObject getDevicesResponseObject = new JSONObject(getDevicesResponse);
                        JSONArray getDevicesResponseArray = getDevicesResponseObject.getJSONArray("aaData");

                        for (int index = 0; index < getDevicesResponseArray.length(); index++) {
                            methodNameIdMap.put(getDevicesResponseArray.getJSONObject(index).getString("deviceId"),
                                    getDevicesResponseArray.getJSONObject(index).getString("id"));
                        }

                        setMap(methodNameIdMap);

                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                final List<String> monitorDeviceList = new ArrayList<>();
                                getDevicesProgressDialog.dismiss();
                                Iterator it = classNameIdMap.entrySet().iterator();
                                while (it.hasNext()) {
                                    Map.Entry pair = (Map.Entry) it.next();
                                    monitorDeviceList.add(pair.getKey().toString());
                                }

                                final Switch switch1 =
                                        (Switch) getView().findViewById(R.id.simpleSwitch1);
                                switch1.setText(monitorDeviceList.get(0));
                                switch1.setVisibility(View.VISIBLE);

                                final Switch switch2 =
                                        (Switch) getView().findViewById(R.id.simpleSwitch2);
                                switch2.setText(monitorDeviceList.get(1));
                                switch2.setVisibility(View.VISIBLE);

                                final Switch switch3 =
                                        (Switch) getView().findViewById(R.id.simpleSwitch3);
                                switch3.setText(monitorDeviceList.get(2));
                                switch3.setVisibility(View.VISIBLE);

                                final Switch switch4 =
                                        (Switch) getView().findViewById(R.id.simpleSwitch4);
                                switch4.setText(monitorDeviceList.get(3));
                                switch4.setVisibility(View.VISIBLE);

                                final Switch switch5 =
                                        (Switch) getView().findViewById(R.id.simpleSwitch5);
                                switch5.setText(monitorDeviceList.get(4));
                                switch5.setVisibility(View.VISIBLE);

                                final Switch switch6 =
                                        (Switch) getView().findViewById(R.id.simpleSwitch6);
                                switch6.setText(monitorDeviceList.get(5));
                                switch6.setVisibility(View.VISIBLE);

                                switch1.setOnCheckedChangeListener(
                                        new CompoundButton.OnCheckedChangeListener() {
                                            @Override
                                            public void onCheckedChanged(CompoundButton buttonView,
                                                                         boolean isChecked) {
                                                if (isChecked) {
                                                    AlertDialog.Builder alertDialog =
                                                            new AlertDialog.Builder(getActivity());
                                                    String setFrequencyString =
                                                            "\nSet monitoring for every: \n" +
                                                                    "PT30S =  30 sec \n" +
                                                                    "PT30M  = 30 min \n";
                                                    alertDialog.setTitle("Monitoring Frequency");
                                                    alertDialog.setMessage(setFrequencyString);

                                                    final EditText input =
                                                            new EditText(getActivity());
                                                    LinearLayout.LayoutParams lp =
                                                            new LinearLayout.LayoutParams(
                                                                    LinearLayout.LayoutParams.MATCH_PARENT,
                                                                    LinearLayout.LayoutParams.MATCH_PARENT);
                                                    input.setLayoutParams(lp);
                                                    input.setHint("Hint: PT30S");
                                                    alertDialog.setView(input);

                                                    alertDialog.setPositiveButton("OK",
                                                            new DialogInterface.OnClickListener() {
                                                                public void onClick(
                                                                        DialogInterface dialog,
                                                                        int which) {

                                                                    JSONObject monitorJsonObject =
                                                                            null;
                                                                    String monitorPostBody =
                                                                            getMonitorFieldsData(
                                                                                    getView(),
                                                                                    enableDevicesPostBody);
                                                                    try {
                                                                        monitorJsonObject =
                                                                                new JSONObject(
                                                                                        monitorPostBody);
                                                                        OkHttpClient client =
                                                                                new OkHttpClient();
                                                                        RequestBody monitorRequestBody =
                                                                                RequestBody.create(
                                                                                        monitorDevicesMediaType,
                                                                                        monitorJsonObject
                                                                                                .toString());
                                                                        Request request =
                                                                                new Request.Builder()
                                                                                        .url(enableDevicesUrl)
                                                                                        .post(monitorRequestBody)
                                                                                        .build();

                                                                        client.newCall(request)
                                                                                .enqueue(
                                                                                        new Callback() {
                                                                                            @Override
                                                                                            public void onFailure(
                                                                                                    Call call,
                                                                                                    IOException e) {
                                                                                                call.cancel();
                                                                                            }

                                                                                            @Override
                                                                                            public void onResponse(
                                                                                                    Call call,
                                                                                                    Response response)
                                                                                                    throws IOException {
                                                                                                final String responseData =
                                                                                                        response.body()
                                                                                                                .string();

                                                                                                getActivity()
                                                                                                        .runOnUiThread(
                                                                                                                new Runnable() {
                                                                                                                    @Override
                                                                                                                    public void run() {
                                                                                                                        try {
                                                                                                                            AlertDialog.Builder alertDialogBuilder =
                                                                                                                                    new AlertDialog.Builder(
                                                                                                                                            getActivity());
                                                                                                                            alertDialogBuilder
                                                                                                                                    .setTitle(
                                                                                                                                            "Congratulations");
                                                                                                                            alertDialogBuilder
                                                                                                                                    .setMessage(
                                                                                                                                            "Your device is setup for monitoring"
                                                                                                                                                    +
                                                                                                                                                    "\n\n"
                                                                                                                                                    + "Click Ok to finish!")
                                                                                                                                    .setCancelable(
                                                                                                                                            false)
                                                                                                                                    .setPositiveButton(
                                                                                                                                            "Ok",
                                                                                                                                            new DialogInterface.OnClickListener() {
                                                                                                                                                public void onClick(
                                                                                                                                                        DialogInterface dialog,
                                                                                                                                                        int id) {
                                                                                                                                                    // clearFields(registerView);
                                                                                                                                                    dialog.cancel();
                                                                                                                                                }
                                                                                                                                            });
                                                                                                                            AlertDialog alertDialog =
                                                                                                                                    alertDialogBuilder
                                                                                                                                            .create();
                                                                                                                            alertDialog
                                                                                                                                    .show();

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

                                                    alertDialog.setNegativeButton("CANCEL",
                                                            new DialogInterface.OnClickListener() {
                                                                public void onClick(
                                                                        DialogInterface dialog,
                                                                        int which) {
                                                                    switch1.setChecked(false);
                                                                    dialog.cancel();
                                                                }
                                                            });
                                                    alertDialog.show();
                                                } else {
                                                    Toast.makeText(getActivity(),
                                                            "Monitoring Disabled",
                                                            Toast.LENGTH_LONG).show();
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

    private void setMap(Map<String, String> incomingMap) {
        classNameIdMap = incomingMap;
    }

    private void List(List<String> incomingList) {
        classNameIdList = incomingList;
    }

    private String getMonitorFieldsData(View view, StringBuilder monitorStringBuilder) {
        monitorStringBuilder.append("{ ");

        monitorStringBuilder.append("\"" + "userId" + "\":" + "\"user12345" + "\",");

        monitorStringBuilder.append("\"" + "deviceId" + "\":" + "\""
                + classNameIdMap.get("Body Temperature Sensor") + "\",");

        monitorStringBuilder.append("\"" + "deviceName" + "\":" + "\""
                + classNameIdMap.keySet().toArray()[0].toString() + "\",");

        EditText monitorEditTextPCPEmail =
                (EditText) view.findViewById(R.id.monitor_edit_text_pcp_email);
        monitorStringBuilder.append("\"" + "pcpemail" + "\":" + "\""
                + monitorEditTextPCPEmail.getText().toString() + "\",");

        monitorStringBuilder.append("\"" + "timeDuration" + "\":" + "\"PT30S" + "\"");
        monitorStringBuilder.append(" }");

        Log.i("RESP", monitorStringBuilder.toString());
        return monitorStringBuilder.toString();
    }



    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle("Monitoring Setup");
    }
}
