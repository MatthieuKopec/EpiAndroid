package com.epiandroid.epitech.epiandroid;

import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.JsonObject;

import java.util.HashMap;

public class        ProfileActivity extends MainActivity implements View.OnClickListener {
    @Override
    protected void  onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.profile_activity);
        findViewById(R.id.button_messages).setOnClickListener(this);
        this.showMenu();

        if (!this.buildRequest("infos", "POST", null, new HashMap<String, String>() {{ put("identity", "infos"); }})) {
            this.flash(getString(R.string.api_internal_error), false);
        }
    }

    @Override
    public void     onClick(View v) {
        if (v.getId() == R.id.button_messages)
            this.redirect(ProfileMessagesActivity.class);
    }

    @Override
    protected void  onApiCompleted(String identity, Pair<Integer, String> error, final JsonObject result) {
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

        switch (identity) {
            case "infos":
                if (!result.has("history") || !result.has("current")
                    || !result.has("infos")) {
                    this.flash(getString(R.string.api_invalid_data), false);
                    return;
                }

                JsonObject infos = result.getAsJsonObject("infos");
                final String login = infos.get("login").getAsString();

                ((TextView) findViewById(R.id.login_text)).append(" " + login);
                ((TextView) findViewById(R.id.name_text)).append(" " + infos.get("title").getAsString());
                ((TextView) findViewById(R.id.promotion_text)).append(" " + getString(R.string.profile_tek) + infos.get("studentyear").getAsString());
                ((TextView) findViewById(R.id.log_time_text)).append(" " + String.valueOf(Math.round(Math.ceil(result.getAsJsonObject("current").get("active_log").getAsDouble()))) + "h");

                if (!this.buildRequest("photo", "GET", new HashMap<String, String>() {{ put("login", login); }}, new HashMap<String, String>() {{ put("identity", "photo"); put("hideProgress", "true"); }})) {
                    this.flash(getString(R.string.api_internal_error), false);
                    return ;
                }
                break;

            case "photo":
                if (!result.has("url")) {
                    this.flash(getString(R.string.api_invalid_data), false);
                    return ;
                }

                ImageView image = (ImageView) findViewById(R.id.profile_picture);
                if (!buildImage(result.get("url").getAsString(), image, -1, -1, new HashMap<String, String>() {{ put("ignoreSSL", "true"); }}))
                    this.flash(getString(R.string.api_internal_error_image), false);
                break;

            default:
                break;
        }
    }

    public          ProfileActivity() {
        super();
    }
}
