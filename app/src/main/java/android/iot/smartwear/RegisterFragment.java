package android.iot.smartwear;


import java.io.IOException;

import org.json.JSONObject;

import com.example.android.androidTest.R;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

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


public class RegisterFragment extends Fragment {

    public static final MediaType registerDevicesMediaType = MediaType.parse("application/json; charset=utf-8");

    String registerDevicesUrl = "http://1ac12d71.ngrok.io/healthbot/rest/register";

    StringBuilder registerDevicesPostBody = new StringBuilder();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        final View registerView = inflater.inflate(R.layout.register_layout, container, false);

        Button registerButton = (Button) registerView.findViewById(R.id.register_button);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    OkHttpClient client = new OkHttpClient();
                    String registerDevicesPostBody =
                            getRegisterDevicesData(registerView, RegisterFragment.this.registerDevicesPostBody);
                    JSONObject registerDevicesRequestObject = new JSONObject(registerDevicesPostBody);
                    RequestBody registerDevicesRequestBody =
                            RequestBody.create(registerDevicesMediaType, registerDevicesRequestObject.toString());
                    Request request = new Request.Builder()
                            .url(registerDevicesUrl)
                            .post(registerDevicesRequestBody)
                            .build();

                    client.newCall(request).enqueue(new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            call.cancel();
                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            final String registerDevicesResponse = response.body().string();
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        AlertDialog.Builder successfullRegistrationDialog =
                                                new AlertDialog.Builder(getActivity());
                                        successfullRegistrationDialog.setTitle("Congratulations");
                                        successfullRegistrationDialog
                                                .setMessage("Your device registration is successful." +
                                                        "\n\n" + "Click Ok to finish!")
                                                .setCancelable(false)
                                                .setPositiveButton("Ok",new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog,int id) {
                                                        clearRegisterData(registerView);
                                                        dialog.cancel();
                                                    }
                                                });
                                        AlertDialog alertDialog = successfullRegistrationDialog.create();
                                        alertDialog.show();

                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        return registerView;
    }

    private String getRegisterDevicesData(View view, StringBuilder registerDevicesData) {
        registerDevicesData.append("{ ");

        EditText registerUserName = (EditText) view.findViewById(R.id.register_name_value);
        registerDevicesData.append("\"" + "userId" + "\":" + "\"" + registerUserName.getText().toString() + "\",");

        EditText registerFullName = (EditText) view.findViewById(R.id.register_user_name_value);
        registerDevicesData.append("\"" + "name" + "\":" + "\"" + registerFullName.getText().toString() + "\",");

        EditText registerEmail = (EditText) view.findViewById(R.id.register_email_value);
        registerDevicesData.append("\"" + "email" + "\":" + "\"" + registerEmail.getText().toString() + "\",");

        EditText registerPhone = (EditText) view.findViewById(R.id.register_phone_value);
        registerDevicesData.append("\"" + "phone" + "\":" + "\"" + registerPhone.getText().toString() + "\",");

        EditText registerStreet = (EditText) view.findViewById(R.id.register_street_value);
        registerDevicesData.append("\"" + "address1" + "\":" + "\"" + registerStreet.getText().toString() + "\",");

        EditText registerCity = (EditText) view.findViewById(R.id.register_city_value);
        registerDevicesData.append("\"" + "city" + "\":" + "\"" + registerCity.getText().toString() + "\",");

        EditText registerState = (EditText) view.findViewById(R.id.register_state_value);
        registerDevicesData.append("\"" + "state" + "\":" + "\"" + registerState.getText().toString() + "\",");

        EditText registerZip = (EditText) view.findViewById(R.id.register_zip_value);
        registerDevicesData.append("\"" + "zip" + "\":" + "\"" + registerZip.getText().toString() + "\",");

        EditText registerModel = (EditText) view.findViewById(R.id.register_model_value);
        registerDevicesData.append("\"" + "modelNo" + "\":" + "\"" + registerModel.getText().toString() + "\",");

        registerDevicesData.append(" }");

        return registerDevicesData.toString();
    }

    private void clearRegisterData(View view) {
        EditText registerUserName = (EditText) view.findViewById(R.id.register_name_value);
        registerUserName.setText("");

        EditText registerEmail = (EditText) view.findViewById(R.id.register_email_value);
        registerEmail.setText("");

        EditText registerPhone = (EditText) view.findViewById(R.id.register_phone_value);
        registerPhone.setText("");

        EditText registerStreet = (EditText) view.findViewById(R.id.register_street_value);
        registerStreet.setText("");

        EditText registerCity = (EditText) view.findViewById(R.id.register_city_value);
        registerCity.setText("");

        EditText registerState = (EditText) view.findViewById(R.id.register_state_value);
        registerState.setText("");

        EditText registerZip = (EditText) view.findViewById(R.id.register_zip_value);
        registerZip.setText("");

        EditText registerModel = (EditText) view.findViewById(R.id.register_model_value);
        registerModel.setText("");

        EditText registerFullName = (EditText) view.findViewById(R.id.register_user_name_value);
        registerFullName.setText("");
        registerFullName.requestFocus();
    }
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle("Device Registration");
    }
}