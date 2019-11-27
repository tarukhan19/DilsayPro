package com.dbvertex.dilsayproject.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.dbvertex.dilsayproject.Model.ReligionDTO;
import com.dbvertex.dilsayproject.R;
import com.dbvertex.dilsayproject.UserAuth.ChooseInterestActivity;
import com.dbvertex.dilsayproject.UserAuth.LifestyleActivity;
import com.dbvertex.dilsayproject.UserAuth.ReligionActivity;
import com.dbvertex.dilsayproject.session.SessionManager;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ReligionAdapter extends RecyclerView.Adapter<ReligionAdapter.ViewHolderPollAdapter> {
    private Context mcontex;
    private List<ReligionDTO> religionDTOList;
    public  int selection;
    SessionManager sessionManager;
    Activity activity;
    LinearLayout nextLL;
    public ReligionAdapter(Context mcontex, List<ReligionDTO> religionDTOList, LinearLayout nextLL) {
        this.mcontex = mcontex;
        this.religionDTOList = religionDTOList;
        sessionManager = new SessionManager(mcontex);
        activity= (Activity) mcontex;
        this.nextLL=nextLL;

    }

    @NonNull
    @Override
    public ViewHolderPollAdapter onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        LayoutInflater mInflater = LayoutInflater.from(mcontex);
        view = mInflater.inflate(R.layout.item_info, parent, false);
        return new ViewHolderPollAdapter(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolderPollAdapter holder, final int position) {

        // //Log.e("selectAnsId",selectAnsId.size()+"ansIdlist "+ansIdlist.size()+"");


        holder.communityText.setText(religionDTOList.get(position).getRelegionName());

        if (position==selection)
        {
            holder.checked.setVisibility(View.VISIBLE);
            holder.communityText.setTextColor(mcontex.getResources().getColor(R.color.colorPrimary));
        } else
        {
            holder.checked.setVisibility(View.GONE);
            holder.communityText.setTextColor(mcontex.getResources().getColor(R.color.black));
        }

        holder.linearlayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selection = position;
                notifyDataSetChanged();
                sessionManager.setReligion(religionDTOList.get(position).getRelegionName());

            }
        });


        holder.bindDataWithViewHolder(religionDTOList.get(position), position);


        nextLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (sessionManager.getReligion().get(SessionManager.KEY_RELIGION).isEmpty())
                {
                    Toast.makeText(mcontex, "Select religion", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Intent intent = new Intent(mcontex, LifestyleActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    activity.startActivity(intent);
                    activity.overridePendingTransition(R.anim.trans_left_in,
                            R.anim.trans_left_out);

                }




            }
        });






    }

    @Override
    public int getItemCount() {
        return religionDTOList != null ? religionDTOList.size() : 0;
    }

    public class ViewHolderPollAdapter extends RecyclerView.ViewHolder {

        TextView communityText;
        ImageView checked;
        LinearLayout linearlayout;

        private ReligionDTO mmdata;


        public ViewHolderPollAdapter(View itemView) {
            super(itemView);


            communityText = (TextView) itemView.findViewById(R.id.communityText);
            checked = (ImageView) itemView.findViewById(R.id.checked);
            linearlayout=itemView.findViewById(R.id.linearlayout);
        }

        public void bindDataWithViewHolder(ReligionDTO mpollData, int position) {

            this.mmdata = mpollData;
            if (position==selection)
            {
                mpollData.setSelected(true);

            }

        }
    }

}
