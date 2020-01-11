package android.iot.smartwear;

import android.app.ProgressDialog;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.android.androidTest.R;

import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by Work on 3/29/17.
 */

public class RecommendFragment extends Fragment {

    String recommendUrl = "http://1ac12d71.ngrok.io/healthbot/rest/recommend";
    public static final MediaType recommendMediaTypeJson = MediaType.parse("application/json; charset=utf-8");
    StringBuilder recommendStringBuilder = new StringBuilder();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.recommend_layout, container, false);
        final ProgressDialog progress = ProgressDialog.show(getActivity(), "Analyzing", "Shhh! IQ Bot magic in progress", true);

        final ImageView recoImageView = (ImageView) view.findViewById(R.id.recommend_image_view);
        recoImageView.setBackgroundResource(android.R.color.transparent);

        try {
            String recommendPostBody = getRegisterFieldsData(view, recommendStringBuilder);
            JSONObject recommendJsonObject = new JSONObject(recommendPostBody);
            OkHttpClient client = new OkHttpClient();
            RequestBody recommendRequestBody = RequestBody.create(recommendMediaTypeJson, recommendJsonObject.toString());
            Request request = new Request.Builder()
                    .url(recommendUrl)
                    .post(recommendRequestBody)
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    call.cancel();
                }

                @Override
                public void onResponse(Call call, final Response response) throws IOException {
                    final String responseData = response.body().string();
                    Log.i("RESP", responseData);
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ImageView recommendImageView;
                            TextView recommendTextViewTop;
                            TextView recommendTextViewBottom;
                            try {
                                recommendImageView = (ImageView) view.findViewById(R.id.recommend_image_view);
                                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                                        RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                                params.setMargins(300,150,150,150);
                                recommendImageView.setLayoutParams(params);
                                recommendImageView.getLayoutParams().width = 500;
                                recommendImageView.getLayoutParams().height = 500;

                                recommendTextViewTop = (TextView) view.findViewById(R.id.recommend_text_view_top);
                                recommendTextViewTop.setVisibility(View.VISIBLE);
                                recommendTextViewBottom = (TextView) view.findViewById(R.id.recommend_text_view_bottom);
                                recommendTextViewBottom.setVisibility(View.VISIBLE);

                                if(responseData.contains("primary")) {
                                    recommendImageView.setBackgroundResource(R.drawable.ic_recommend_pills);
                                    recommendTextViewTop.setText("IQ Bot results based on readings from past 7 days");
                                    recommendTextViewBottom.setText("Diagnosis\n[ your blood pressure staying high ]\n" +
                                            "\nRecommendation\n[ you check daily dosage with your Primary Care Physician ]");
                                }
                                else if (responseData.contains("Heart Scan")) {
                                    recommendImageView.setBackgroundResource(R.drawable.ic_recommend_ecg);
                                    recommendTextViewTop.setText("IQ Bot results based on readings from past 7 days");
                                    recommendTextViewBottom.setText("Diagnosis\n[ your heart rate is abnormal on certain times ]\n" +
                                            "\nRecommendation\n[ you schedule a Heart Scan at the earliest ]");
                                }
                                else if (responseData.contains("Brain CT")) {
                                    recommendImageView.setBackgroundResource(R.drawable.ic_recommend_mri);
                                    recommendTextViewTop.setText("IQ Bot results based on readings from past 7 days");
                                    recommendTextViewBottom.setText("Diagnosis\n[ your neck blood pressure is staying high ]\n" +
                                            "\nRecommendation\n[ you schedule a Brain CT Scan as soon as you can ]");
                                }
                                else {
                                    recommendImageView.setBackgroundResource(R.drawable.ic_recommend_pvd);
                                    recommendTextViewTop.setText("IQ Bot results based on readings from past 7 days");
                                    recommendTextViewBottom.setText("Diagnosis\n[ your leg blood pressure staying high ]\n" +
                                            "\nRecommendation\n[ you checkup for Peripheral Vascular Disease as soon as you can ]");
                                }
                                progress.dismiss();
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

    private String getRegisterFieldsData(View view, StringBuilder recommendStringBuilder) {
        recommendStringBuilder.append("{");

        recommendStringBuilder.append("\"" + "userId" + "\":" + "\"user123" + "\"");

        //recommendStringBuilder.append("\"" + "phone" + "\":" + "0001110001" + "");

        recommendStringBuilder.append("}");

        return recommendStringBuilder.toString();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle("Health Recommendation");
    }
}
