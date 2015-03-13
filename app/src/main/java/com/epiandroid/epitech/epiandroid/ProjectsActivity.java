package com.epiandroid.epitech.epiandroid;

import  android.os.Bundle;

import  android.widget.CheckBox;
import  android.widget.CompoundButton;
import  android.widget.ListAdapter;
import  android.widget.ListView;
import  android.widget.SimpleAdapter;

import  com.google.gson.JsonArray;
import  com.google.gson.JsonElement;
import  com.google.gson.JsonObject;

import  android.util.Pair;

import  java.util.ArrayList;
import  java.util.HashMap;

public class        ProjectsActivity extends MainActivity implements CompoundButton.OnCheckedChangeListener {
    @Override
    protected void  onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.projects_activity);
        ((CheckBox) findViewById(R.id.only_registered_projects)).setOnCheckedChangeListener(this);
        this.showMenu();

        if (!this.buildRequest("projects", "GET", null, new HashMap<String, String>() {{ put("asJsonArray", "true"); }}))
            this.flash(getString(R.string.api_internal_error), false);
    }

    @Override
    public void onCheckedChanged(CompoundButton c, boolean isChecked) {
        switch (c.getId()) {
            case R.id.only_registered_projects:
                if (!this.buildRequest("projects", "GET", null, new HashMap<String, String>() {{ put("asJsonArray", "true"); }})) {
                    this.flash(getString(R.string.api_internal_error), false);
                    return ;
                }
                break;

            default:
                break;
        }
    }

    @Override
     protected void  onApiCompleted(String identity, Pair<Integer, String> error, JsonArray result) {
        ArrayList<HashMap<String, String>> projects = new ArrayList<>();

        for (JsonElement tmp : result) {
            final JsonObject project = tmp.getAsJsonObject();
            boolean onlyRegistered = ((CheckBox) findViewById(R.id.only_registered_projects)).isChecked();

            if ((project.get("type_acti").getAsString().equals("Projet") || project.get("type_acti").getAsString().equals("Mini-Projets"))
                && (!onlyRegistered
                    || (onlyRegistered && project.get("registered").getAsInt() == 1))) {
                projects.add(new HashMap<String, String>() {{
                    put("title", stripTags(project.get("acti_title").getAsString()));
                    put("module", stripTags(project.get("title_module").getAsString()));
                }});
            }
        }
        ListAdapter adapter = new SimpleAdapter(this, projects, android.R.layout.simple_list_item_2, new String[] { "title", "module" }, new int[] { android.R.id.text1, android.R.id.text2 });
        ((ListView) findViewById(R.id.projects)).setAdapter(adapter);
    }

    public          ProjectsActivity() {
        super();
    }
}
