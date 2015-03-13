package com.epiandroid.epitech.epiandroid;

import android.os.Bundle;
import android.util.Pair;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.HashMap;

public class        ProfileMessagesActivity extends MainActivity {
    @Override
    protected void  onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.profile_messages_activity);
        this.showMenu();

        if (!this.buildRequest("infos", "POST", null, new HashMap<String, String>() {{ put("identity", "infos"); }})) {
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

        if (!result.has("history")) {
            this.flash(getString(R.string.api_invalid_data), false);
            return;
        }

        ArrayList<HashMap<String, String>> messages = new ArrayList<>();

        for (JsonElement tmp : result.getAsJsonArray("history")) {
            final JsonObject message = tmp.getAsJsonObject();

            messages.add(new HashMap<String, String>() {{
                put("title", stripTags(message.get("title").getAsString()));
                put("content", stripTags(message.get("content").getAsString()));
            }});
        }
        ListAdapter adapter = new SimpleAdapter(this, messages, android.R.layout.simple_list_item_2, new String[] { "title", "content" }, new int[] { android.R.id.text1, android.R.id.text2 });
        ((ListView) findViewById(R.id.profile_messages)).setAdapter(adapter);
    }

    public          ProfileMessagesActivity() {
        super();
    }
}
