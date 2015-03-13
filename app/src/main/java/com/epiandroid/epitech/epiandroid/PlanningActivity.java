package com.epiandroid.epitech.epiandroid;

import  android.os.Bundle;

import  android.util.Pair;

import  android.view.View;

import  android.widget.Button;
import  android.widget.ListView;
import  android.widget.TextView;

import  com.google.gson.JsonArray;
import  com.google.gson.JsonElement;
import  com.google.gson.JsonObject;

import  java.util.ArrayList;
import  java.util.Calendar;
import  java.util.HashMap;
import  java.util.Map;
import  java.text.SimpleDateFormat;

public class PlanningActivity extends MainActivity {
    private Calendar    currentDate;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.planning_activity);
        currentDate = Calendar.getInstance();

        final Button next = (Button) findViewById(R.id.button_next);
        next.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                ListView ls = (ListView) findViewById(R.id.planninglist);
                ls.setAdapter(null);
                currentDate.add(Calendar.DATE, 1);
                makeRequest();
            }
        });
        final Button prev = (Button) findViewById(R.id.button_prev);
        prev.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                ListView ls = (ListView) findViewById(R.id.planninglist);
                ls.setAdapter(null);
                currentDate.add(Calendar.DATE, -1);
                makeRequest();
            }
        });
        makeRequest();
    }

    @Override
    protected void     onApiCompleted(String identity, Pair<Integer, String> error, JsonArray result) {
        if (!identity.equals("token")) {
            ArrayList<PlanningBean> activity = new ArrayList<>();

            for (JsonElement tmp : result) {
                final JsonObject planning = tmp.getAsJsonObject();

                PlanningBean tmpBean = new PlanningBean();
                tmpBean.setCalendarType((planning.get("calendar_type") != null && !planning.get("calendar_type").isJsonNull()) ? stripTags(planning.get("calendar_type").getAsString()) : getString(R.string.unknownm));
                if (!tmpBean.getCalendarType().equals("susie")) {
                    tmpBean.setTitle((planning.get("acti_title") != null && !planning.get("acti_title").isJsonNull()) ? stripTags(planning.get("acti_title").getAsString()) : getString(R.string.unknownm));
                    tmpBean.setModule((planning.get("titlemodule") != null && !planning.get("titlemodule").isJsonNull()) ? stripTags(planning.get("titlemodule").getAsString()) : getString(R.string.unknownm));
                } else {
                    tmpBean.setTitle((planning.get("title") != null && !planning.get("title").isJsonNull()) ? stripTags(planning.get("title").getAsString()) : getString(R.string.unknownm));
                    tmpBean.setModule(getString(R.string.susie));
                }
                tmpBean.setPast((planning.get("past") != null && !planning.get("past").isJsonNull()) ? stripTags(planning.get("past").getAsString()) : getString(R.string.unknownm));
                tmpBean.setAllowToken((planning.get("allow_token") != null && !planning.get("allow_token").isJsonNull()) ? stripTags(planning.get("allow_token").getAsString()) : getString(R.string.unknownm));
                tmpBean.setEventRegistered((planning.get("event_registered") != null && !planning.get("event_registered").isJsonNull()) ? stripTags(planning.get("event_registered").getAsString()) : getString(R.string.unknownm));
                tmpBean.setModuleRegistered((planning.get("module_registered") != null && !planning.get("module_registered").isJsonNull()) ? stripTags(planning.get("module_registered").getAsString()) : getString(R.string.unknownm));
                tmpBean.setCodeActi((planning.get("codeacti") != null && !planning.get("codeacti").isJsonNull()) ? stripTags(planning.get("codeacti").getAsString()) : getString(R.string.unknownm));
                tmpBean.setCodeEvent((planning.get("codeevent") != null && !planning.get("codeevent").isJsonNull()) ? stripTags(planning.get("codeevent").getAsString()) : getString(R.string.unknownm));
                tmpBean.setCodeModule((planning.get("codemodule") != null && !planning.get("codemodule").isJsonNull()) ? stripTags(planning.get("codemodule").getAsString()) : getString(R.string.unknownm));
                tmpBean.setCodeInstance((planning.get("codeinstance") != null && !planning.get("codeinstance").isJsonNull()) ? stripTags(planning.get("codeinstance").getAsString()) : getString(R.string.unknownm));
                tmpBean.setScolarYear((planning.get("scolaryear") != null && !planning.get("scolaryear").isJsonNull()) ? stripTags(planning.get("scolaryear").getAsString()) : getString(R.string.unknownm));

                if (tmpBean.getModuleRegistered().equals("false"))
                    continue;

                if (planning.has("room") && !planning.get("room").isJsonNull()
                        && planning.get("room").getAsJsonObject() != null) {
                    JsonObject room = planning.get("room").getAsJsonObject();
                    tmpBean.setRoom((room.get("code") != null && !room.get("code").isJsonNull()) ? stripTags(room.get("code").getAsString()) : getString(R.string.unknownm));
                } else
                    tmpBean.setRoom(getString(R.string.unknownf));

                tmpBean.setTime((planning.get("start") != null && !planning.get("start").isJsonNull()) ? stripTags(planning.get("start").getAsString()) : getString(R.string.unknownm),
                        (planning.get("end") != null && !planning.get("end").isJsonNull()) ? stripTags(planning.get("end").getAsString()) : getString(R.string.unknownm));
                activity.add(tmpBean);
            }

            ListView ls = (ListView) findViewById(R.id.planninglist);
            PlanningAdapter adapter = new PlanningAdapter(this, R.layout.planning_activity, R.layout.planning_row, activity);
            ls.setAdapter(adapter);
        }
    }

    private void    makeRequest() {
        final String date = new SimpleDateFormat("yyyy-MM-dd").format(currentDate.getTime());
        TextView curDate = (TextView) this.findViewById(R.id.text_date);
        curDate.setText(new SimpleDateFormat("dd/MM/yyyy").format(currentDate.getTime()));
        Map<String, String> params = new HashMap<String, String>() {{
            put("start", date);
            put("end", date);
        }};
        if (!this.buildRequest("planning", "GET", params, new HashMap<String, String>() {{
            put("asJsonArray", "true");
            put("identity", "planning"); }})) {
            this.flash(getString(R.string.api_internal_error), false);
            return ;
        }
        this.showMenu();
    }

    public          PlanningActivity() {
        super();
    }
}
