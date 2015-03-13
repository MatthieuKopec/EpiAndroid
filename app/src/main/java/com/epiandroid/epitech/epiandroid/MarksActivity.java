package com.epiandroid.epitech.epiandroid;

import  android.os.Bundle;
import  android.util.Pair;
import  android.widget.ListAdapter;
import  android.widget.ListView;
import  android.widget.SimpleAdapter;

import  com.google.gson.JsonElement;
import  com.google.gson.JsonObject;

import  java.util.ArrayList;
import  java.util.Collections;
import  java.util.HashMap;

public class        MarksActivity extends MainActivity {
    @Override
    protected void  onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.marks_activity);
        this.showMenu();

        if (!this.buildRequest("marks", "GET", null, null)) {
            this.flash(getString(R.string.api_internal_error), false);
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

        if (!result.has("notes")) {
            this.flash(getString(R.string.api_invalid_data), false);
            return;
        }

        ArrayList<HashMap<String, String>> marks = new ArrayList<>();

        for (JsonElement tmp : result.getAsJsonArray("notes")) {
            final JsonObject mark = tmp.getAsJsonObject();

            marks.add(new HashMap<String, String>() {{
                put("title", stripTags(mark.get("title").getAsString()) + " (" + stripTags(mark.get("titlemodule").getAsString()) + ")");
                put("mark", stripTags(mark.get("final_note").getAsString()));
            }});
        }
        Collections.reverse(marks);
        ListAdapter adapter = new SimpleAdapter(this, marks, android.R.layout.simple_list_item_2, new String[] { "title", "mark" }, new int[] { android.R.id.text1, android.R.id.text2 });
        ((ListView) findViewById(R.id.marks)).setAdapter(adapter);
    }

    public          MarksActivity() {
        super();
    }
}
