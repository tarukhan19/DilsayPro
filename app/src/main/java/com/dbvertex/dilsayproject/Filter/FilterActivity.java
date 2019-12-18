package com.dbvertex.dilsayproject.Filter;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.crystal.crystalrangeseekbar.interfaces.OnRangeSeekbarChangeListener;
import com.crystal.crystalrangeseekbar.interfaces.OnRangeSeekbarFinalValueListener;
import com.crystal.crystalrangeseekbar.widgets.CrystalRangeSeekbar;
import com.dbvertex.dilsayproject.EndPoints;
import com.dbvertex.dilsayproject.HideKeyboard;
import com.dbvertex.dilsayproject.Model.HeightDTO;
import com.dbvertex.dilsayproject.R;
import com.dbvertex.dilsayproject.UserAuth.ReligionActivity;
import com.dbvertex.dilsayproject.databinding.ActivityFilterBinding;
import com.dbvertex.dilsayproject.session.SessionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class FilterActivity extends AppCompatActivity {
    ActivityFilterBinding binding;
    SessionManager sessionManager;
    RequestQueue queue;
    ProgressDialog progressDialog;

    String minHeight,maxHeight;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_filter);
        if (Build.VERSION.SDK_INT >= 21)
        {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getResources().getColor(R.color.colorPrimary));
        }
        queue = Volley.newRequestQueue(this);
        sessionManager = new SessionManager(getApplicationContext());
        progressDialog = new ProgressDialog(this);


        binding.agerangeSeekbar.setOnRangeSeekbarChangeListener(new OnRangeSeekbarChangeListener() {
            @Override
            public void valueChanged(Number minValue, Number maxValue)
            {
                binding.ageMinVal.setText(String.valueOf(minValue));
                binding.ageMaxVal.setText(String.valueOf(maxValue));
            }
        });





        binding.religionLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(FilterActivity.this, ReligionActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.putExtra("from","filter");
                startActivity(intent);

                overridePendingTransition(R.anim.trans_left_in,
                        R.anim.trans_left_out);
            }
        });

        if (!sessionManager.getFilterReligion().get(SessionManager.KEY_FILTER_RELIGION_NAME).isEmpty())
        {
            binding.reliogionTV.setText(sessionManager.getFilterReligion().get(SessionManager.KEY_FILTER_RELIGION_NAME));
        }
        loadHeight();
    }

    private void loadHeight()
    {
        progressDialog.setMessage("Please Wait..");
        progressDialog.setCancelable(true);
        progressDialog.show();

        StringRequest postRequest = new StringRequest(Request.Method.POST, EndPoints.LOADHEIGHT,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("response", response);
                        try {

                            progressDialog.dismiss();
                            JSONObject object=new JSONObject(response);

                            String status=object.getString("status");
                            String message=object.getString("message");
                            if (status.equalsIgnoreCase("200") && message.equalsIgnoreCase("success"))
                            {
                                JSONObject data= object.getJSONObject("data");
                                JSONArray dataarray=data.getJSONArray("height");

                                for (int i=0;i<dataarray.length();i++)
                                {
                                    String heightname=dataarray.getString(i);

                                }

                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        progressDialog.dismiss();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("education", sessionManager.getEducation().get(SessionManager.KEY_EDUCATION));
                params.put("user_id", sessionManager.getUserId().get(SessionManager.KEY_USERID));
                Log.e("params", params.toString());
                return params;
            }
        };
        int socketTimeout = 30000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        postRequest.setRetryPolicy(policy);
        queue.add(postRequest);
    }

}
