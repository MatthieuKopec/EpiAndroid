package com.epiandroid.epitech.epiandroid;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SusiesAdapter extends ArrayAdapter {
    SusiesActivity mContext;
    List             mActivityList;
    LayoutInflater   inflater;

    public SusiesAdapter(SusiesActivity c, int res, int textViewResourceId, List objects) {
        super(c, res, textViewResourceId, objects);
        this.mContext = c;
        this.mActivityList = objects;
        this.inflater = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public int getCount() {
        return mActivityList.size();
    }

    public SusiesBean getItem(int position) {
        return (SusiesBean) this.mActivityList.get(position);
    }

    public long getItemId(int position) {
        return  position;
    }

    public static class ViewHolder{
        TextView    tvTime;
        TextView    tvTitle;
        TextView    tvType;
        TextView    tvName;
    }

    @Override
    public View getView(int pos, View inView, ViewGroup parent) {
        ViewHolder  holder;

        if (inView == null) {
            holder = new ViewHolder();
            inView = this.inflater.inflate(R.layout.susies_row,parent,false);
            holder.tvTime = (TextView) inView.findViewById(R.id.activity_time);
            holder.tvTitle = (TextView) inView.findViewById(R.id.activity_title);
            holder.tvType = (TextView) inView.findViewById(R.id.activity_type);
            holder.tvName = (TextView) inView.findViewById(R.id.activity_name);
            inView.setTag(holder);
        }
        else
            holder = (ViewHolder) inView.getTag();

        final SusiesBean act = (SusiesBean) this.mActivityList.get(pos);
        holder.tvTime.setText(act.getTime());
        holder.tvTitle.setText(act.getTitle());
        holder.tvType.setText(act.getType());
        holder.tvName.setText(act.getName());

        final Button button = (Button) inView.findViewById(R.id.button_register);
        button.setText(act.getRegister() == null ? R.string.button_register : R.string.button_unregister);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean register = button.getText().toString().equals(mContext.getString(R.string.button_register));

                Map<String, String> vls = new HashMap<String, String>() {{
                    put("id", act.getId());
                    put("calendar_id", act.getCalendarId());
                }};

                if (!mContext.buildRequest("susie", (register ? "POST" : "DELETE"), vls, null))
                    mContext.flash(mContext.getString(R.string.api_internal_error), false);
            }
        });

        return inView;
    }
}
