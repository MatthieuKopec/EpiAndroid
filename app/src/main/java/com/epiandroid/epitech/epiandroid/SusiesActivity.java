package com.epiandroid.epitech.epiandroid;

import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class SusiesActivity extends MainActivity {
    private Calendar    currentDate;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.susies_activity);
        currentDate = Calendar.getInstance();

        final Button next = (Button) findViewById(R.id.button_next);
        next.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                ListView ls = (ListView) findViewById(R.id.susieslist);
                ls.setAdapter(null);
                currentDate.add(Calendar.DATE, 1);
                makeRequest();
            }
        });
        final Button prev = (Button) findViewById(R.id.button_prev);
        prev.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                ListView ls = (ListView) findViewById(R.id.susieslist);
                ls.setAdapter(null);
                currentDate.add(Calendar.DATE, -1);
                makeRequest();
            }
        });
        makeRequest();
    }

    @Override
    protected void     onApiCompleted(String identity, Pair<Integer, String> error, JsonArray result) {
        ArrayList<SusiesBean> activity = new ArrayList<>();

        for (JsonElement tmp : result) {
            final JsonObject susie = tmp.getAsJsonObject();

            SusiesBean tmpBean = new SusiesBean();
            tmpBean.setTitle((susie.get("title") != null && !susie.get("title").isJsonNull()) ? stripTags(susie.get("title").getAsString()) : getString(R.string.unknownf));
            tmpBean.setType((susie.get("type") != null && !susie.get("type").isJsonNull()) ? stripTags(susie.get("type").getAsString()) : getString(R.string.unknownm));
            tmpBean.setRegister((susie.get("event_registered") != null && !susie.get("event_registered").isJsonNull()) ? stripTags(susie.get("event_registered").getAsString()) : null);
            tmpBean.setId((susie.get("id") != null && !susie.get("id").isJsonNull()) ? stripTags(susie.get("id").getAsString()) : null);
            tmpBean.setCalendarId((susie.get("id_calendar") != null && !susie.get("id_calendar").isJsonNull()) ? stripTags(susie.get("id_calendar").getAsString()) : null);

            if (susie.has("maker") && !susie.get("maker").isJsonNull()
                && susie.get("maker").getAsJsonObject() != null) {
                JsonObject owner = susie.get("maker").getAsJsonObject();
                tmpBean.setName((owner.get("title") != null && !owner.get("title").isJsonNull()) ? stripTags(owner.get("title").getAsString()) : getString(R.string.unknownm));
            } else
                tmpBean.setName(getString(R.string.unknownm));

            tmpBean.setTime((susie.get("start") != null && !susie.get("start").isJsonNull()) ? stripTags(susie.get("start").getAsString()) : getString(R.string.unknownm),
                            (susie.get("end") != null && !susie.get("end").isJsonNull()) ? stripTags(susie.get("end").getAsString()) : getString(R.string.unknownm) );
            activity.add(tmpBean);
        }

        ListView ls = (ListView) findViewById(R.id.susieslist);
        SusiesAdapter adapter = new SusiesAdapter(this, R.layout.susies_activity, R.layout.susies_row, activity);
        ls.setAdapter(adapter);
    }

    private void    makeRequest() {
        final String date = new SimpleDateFormat("yyyy-MM-dd").format(currentDate.getTime());
        TextView curDate = (TextView) this.findViewById(R.id.text_date);
        curDate.setText(new SimpleDateFormat("dd-MM-yyyy").format(currentDate.getTime()));
        Map<String, String> params = new HashMap<String, String>() {{
            put("start", date);
            put("end", date);
            put("get", "all");
        }};
        if (!this.buildRequest("susies", "GET", params, new HashMap<String, String>() {{ put("asJsonArray", "true"); }})) {
            this.flash(getString(R.string.api_internal_error), false);
            return ;
        }
        this.showMenu();
    }

    public SusiesActivity() {
        super();
    }
}
