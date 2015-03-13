package com.epiandroid.epitech.epiandroid;

import android.app.AlertDialog;
import  android.content.Context;

import android.content.DialogInterface;
import android.content.res.Resources;
import android.text.InputType;
import  android.view.LayoutInflater;
import  android.view.View;
import  android.view.ViewGroup;

import  android.widget.ArrayAdapter;
import  android.widget.Button;
import android.widget.EditText;
import  android.widget.TextView;

import java.util.HashMap;
import  java.util.List;
import java.util.Map;

public class PlanningAdapter extends ArrayAdapter {

    PlanningActivity    mContext;
    List                mActivityList;
    LayoutInflater      inflater;

    public PlanningAdapter(Context c, int res, int textViewResourceId, List objects) {
        super(c, res, textViewResourceId, objects);
        this.mContext = (PlanningActivity) c;
        this.mActivityList = objects;
        this.inflater = (LayoutInflater)c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public int getCount() {
        return mActivityList.size();
    }

    public PlanningBean getItem(int position) {
        return (PlanningBean) this.mActivityList.get(position);
    }

    public long getItemId(int position) {
        return  position;
    }

    public static class ViewHolder{
        TextView    tvTime;
        TextView    tvTitle;
        TextView    tvModule;
        TextView    tvRoom;
    }

    @Override
    public View getView(final int pos, View inView, ViewGroup parent) {
        ViewHolder  holder;

        if (inView == null) {
            holder = new ViewHolder();
            inView = this.inflater.inflate(R.layout.planning_row,parent,false);
            holder.tvTime = (TextView) inView.findViewById(R.id.activity_time);
            holder.tvTitle = (TextView) inView.findViewById(R.id.activity_title);
            holder.tvModule = (TextView) inView.findViewById(R.id.activity_module);
            holder.tvRoom = (TextView) inView.findViewById(R.id.activity_room);
            inView.setTag(holder);
        }
        else
            holder = (ViewHolder) inView.getTag();

        final PlanningBean act = (PlanningBean) this.mActivityList.get(pos);
        holder.tvTime.setText(act.getTime());
        holder.tvTitle.setText(act.getTitle());
        holder.tvModule.setText(act.getModule());
        holder.tvRoom.setText(act.getRoom());

        Button button = (Button) inView.findViewById(R.id.button_token);
        if (!act.getAllowToken().equals("true") || !act.getPast().equals("true")
            || !act.getEventRegistered().equals("registered"))
            button.setVisibility(View.GONE);
        else
            button.setVisibility(View.VISIBLE);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showInput(view, pos);
            }
        });

        return inView;
    }

    public Map<String, String>  getParam(int pos) {
        final PlanningBean act = (PlanningBean) this.mActivityList.get(pos);

        Map<String, String> params = new HashMap<String, String>() {{
            put("scolaryear", act.getScolarYear());
            put("codemodule", act.getCodeModule());
            put("codeinstance", act.getCodeInstance());
            put("codeacti", act.getCodeActi());
            put("codeevent", act.getCodeEvent());
        }};

        return params;
    }

    public void showInput( final View inView, final int pos) {

        Resources res = inView.getResources();
        AlertDialog.Builder builder = new AlertDialog.Builder(inView.getContext());
        builder.setTitle(res.getString(R.string.token_title));

        final EditText input = new EditText(inView.getContext());
        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        builder.setView(input);
        builder.setPositiveButton(res.getString(R.string.button_ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                final String mText = input.getText().toString();
                Map<String, String> params = getParam(pos);
                params.put("tokenvalidationcode", mText);

                if (!mContext.buildRequest("token", "POST", params, new HashMap<String, String>() {{
                    put("identity", "token");
                    put("asJsonArray", "true");
                }})) {
                    mContext.flash(mContext.getString(R.string.api_internal_error), false);
                    return ;
                }
                mContext.showMenu();
            }
        });
        builder.setNegativeButton(res.getString(R.string.button_cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

}
