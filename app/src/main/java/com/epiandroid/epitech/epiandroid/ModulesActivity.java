package com.epiandroid.epitech.epiandroid;

import  android.os.Bundle;

import  android.util.Pair;

import  android.widget.CheckBox;
import  android.widget.CompoundButton;
import  android.widget.ListAdapter;
import  android.widget.ListView;
import  android.widget.SimpleAdapter;

import  com.google.gson.JsonElement;
import  com.google.gson.JsonObject;

import  java.util.ArrayList;
import  java.util.Calendar;
import  java.util.Collections;
import  java.util.HashMap;
import  java.util.Map;
import  java.text.SimpleDateFormat;

public class ModulesActivity extends MainActivity implements CompoundButton.OnCheckedChangeListener {
    @Override
    protected void  onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.modules_activity);
        ((CheckBox) findViewById(R.id.only_registered_modules)).setOnCheckedChangeListener(this);
        this.showMenu();

        if (!this.buildRequest("modules", "GET", null, new HashMap<String, String>() {{ put("identity", "modules"); }}))
            this.flash(getString(R.string.api_internal_error), false);
    }


    @Override
    public void onCheckedChanged(CompoundButton c, boolean isChecked) {
        switch (c.getId()) {
            case R.id.only_registered_modules:
                final JsonObject user = this.findUser();
                final boolean onlyRegistered = ((CheckBox) findViewById(R.id.only_registered_modules)).isChecked();

                Map<String, String> params = new HashMap<String, String>() {{
                    put("scolaryear", new SimpleDateFormat("yyyy").format(Calendar.getInstance().getTime()));
                    put("location", user.get("location").getAsString());
                    put("course", user.get("course_code").getAsString());
                }};
                if (!this.buildRequest((!onlyRegistered ? "all" : "") + "modules", "GET", (!onlyRegistered ? params : null), new HashMap<String, String>() {{ put("identity", (!onlyRegistered ? "all" : "") + "modules"); }})) {
                    this.flash(getString(R.string.api_internal_error), false);
                    return ;
                }
                break;

            default:
                break;
        }
    }

    @Override
    protected void     onApiCompleted(String identity, Pair<Integer, String> error, final JsonObject result) {
        if (error != null) {
            String msg;

            switch (error.second) {
                default:
                    msg = getString(R.string.api_unknown_error);
                    break;
            }

            this.flash(msg, false);
            return ;
        }

        if ((identity.equals("modules") && !result.has("modules"))
            || (identity.equals("items") && !result.has("items"))) {
            this.flash(getString(R.string.api_invalid_data), false);
            return;
        }

        ArrayList<HashMap<String, String>> modules = new ArrayList<>();

        for (JsonElement tmp : result.getAsJsonArray(identity.equals("modules") ? "modules" : "items")) {
            final JsonObject module = tmp.getAsJsonObject();

            modules.add(new HashMap<String, String>() {{
                put("title", !module.get("title").isJsonNull() && !module.get("title").getAsString().isEmpty() ? stripTags(module.get("title").getAsString()) : getString(R.string.unknown_m));
                put("credits", stripTags(module.get("credits").getAsString()) + " " + getString(R.string.credits) + (module.get("credits").getAsInt() > 0 ? "s" : ""));
            }});
        }
        Collections.reverse(modules);
        ListAdapter adapter = new SimpleAdapter(this, modules, android.R.layout.simple_list_item_2, new String[] { "title", "credits" }, new int[] { android.R.id.text1, android.R.id.text2 });
        ((ListView) findViewById(R.id.modules)).setAdapter(adapter);
    }

    public ModulesActivity() {
        super();
    }
}
