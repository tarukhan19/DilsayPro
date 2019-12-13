package com.dbvertex.dilsayproject.Filter;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
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
    AlertDialog minHeightDialog,maxHeightdialog;
    AlertDialog.Builder minHeightBuilder, maxHeightBuilder;
    ArrayList<String> minHeightArrayList,maxHeightArrayList;
    ListView minHeightLV,maxHeightLV;
    ArrayAdapter<String> minHeightadapter,maxHeightadapter;
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
        minHeightBuilder = new AlertDialog.Builder(FilterActivity.this);
        minHeightLV = new ListView(this);
        minHeightArrayList = new ArrayList<>();
        minHeightadapter = new ArrayAdapter<String>(this,
                R.layout.item_spinner, R.id.text, minHeightArrayList);
        minHeightLV.setAdapter(minHeightadapter);
        minHeightBuilder.setView(minHeightLV);
        minHeightDialog = minHeightBuilder.create();

        maxHeightBuilder = new AlertDialog.Builder(FilterActivity.this);
        maxHeightLV = new ListView(this);
        maxHeightArrayList = new ArrayList<>();
        maxHeightadapter = new ArrayAdapter<String>(this,
                R.layout.item_spinner, R.id.text, maxHeightArrayList);
        maxHeightLV.setAdapter(maxHeightadapter);
        maxHeightBuilder.setView(maxHeightLV);
        maxHeightdialog = maxHeightBuilder.create();

        binding.agerangeSeekbar.setOnRangeSeekbarChangeListener(new OnRangeSeekbarChangeListener() {
            @Override
            public void valueChanged(Number minValue, Number maxValue)
            {
                binding.ageMinVal.setText(String.valueOf(minValue));
                binding.ageMaxVal.setText(String.valueOf(maxValue));
            }
        });



        binding.minheightLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                minHeightDialog.setView(minHeightLV);
                minHeightDialog.show();
            }
        });

        minHeightLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                ViewGroup vg = (ViewGroup) view;
                TextView txt = (TextView) vg.findViewById(R.id.text);
                binding.minHeightTV.setText(txt.getText().toString());
                minHeight = txt.getText().toString();
                minHeightDialog.dismiss();

            }
        });

        binding.maxheightLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               // HideKeyboard.hideKeyboard(FilterActivity.this);
                maxHeightdialog.setView(maxHeightLV);
                maxHeightdialog.show();
            }
        });

        maxHeightLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ViewGroup vg = (ViewGroup) view;
                TextView txt = (TextView) vg.findViewById(R.id.text);
                binding.maxHeightTV.setText(txt.getText().toString());
                maxHeight = txt.getText().toString();
                maxHeightdialog.dismiss();

            }
        });
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

                            minHeightArrayList.clear();
                            maxHeightArrayList.clear();
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
                                    minHeightArrayList.add(heightname);
                                    maxHeightArrayList.add(heightname);

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
