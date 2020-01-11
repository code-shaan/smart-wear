package android.iot.smartwear;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.TimeUnit;

import org.json.JSONObject;

import com.example.android.androidTest.R;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

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

public class ScanFragment extends Fragment {

    public static final MediaType getScanMediaType = MediaType.parse("application/json; charset=utf-8");

    String getScanUrl = "http://1ac12d71.ngrok.io/healthbot/rest/scanPic";

    StringBuilder getScanPostBody = new StringBuilder();

    ProgressDialog getScanProgressDialog;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.scan_layout, container, false);

        final ImageView scanImageView = (ImageView) view.findViewById(R.id.scan_image_view);
        scanImageView.setBackgroundResource(android.R.color.transparent);
        getScanProgressDialog = ProgressDialog.show(getActivity(), "Loading", "Retrieving image from sensor", true);

        try {
            String getScanData = getScanFieldsData(view, getScanPostBody);
            JSONObject scanJsonObject = new JSONObject(getScanData);
            final OkHttpClient client = new OkHttpClient.Builder().
                    connectTimeout(60, TimeUnit.SECONDS).
                    readTimeout(60, TimeUnit.SECONDS).build();

            RequestBody getScanRequestBody = RequestBody.create(getScanMediaType, scanJsonObject.toString());
            Request request = new Request.Builder()
                    .url(getScanUrl)
                    .post(getScanRequestBody)
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    call.cancel();
                }

                @Override
                public void onResponse(Call call, final Response response) throws IOException {
                    getScanProgressDialog.dismiss();
                    InputStream is = (InputStream)response.body().byteStream();
                    final Bitmap bmp = BitmapFactory.decodeStream(is);

                    final String getScanResponseData = response.body().string();
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ImageView recommendImageView;
                            try {
                                recommendImageView = (ImageView) view.findViewById(R.id.scan_image_view);
                                recommendImageView.setImageBitmap(bmp);
                                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                                        RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                                params.setMargins(50,50,50,50);
                                recommendImageView.setLayoutParams(params);
                                recommendImageView.getLayoutParams().width = 1000;
                                recommendImageView.getLayoutParams().height = 1300;
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
        return view;
    }

    private String getScanFieldsData(View view, StringBuilder getScanPostBody) {
        getScanPostBody.append("{");

        getScanPostBody.append("\"" + "userId" + "\":" + "\"user12345" + "\"");

        getScanPostBody.append("}");

        return getScanPostBody.toString();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle("Body Scan");
    }
}
