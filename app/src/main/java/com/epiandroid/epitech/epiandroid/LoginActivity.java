package com.epiandroid.epitech.epiandroid;

import  android.os.Bundle;

import  android.view.View;
import  android.view.animation.AlphaAnimation;
import  android.view.animation.Animation;
import  android.view.animation.LinearInterpolator;

import  android.widget.Button;
import  android.widget.EditText;

import  com.google.gson.JsonObject;

import  java.util.HashMap;
import  java.util.Map;
import  android.util.Pair;

public class        LoginActivity extends MainActivity implements View.OnClickListener {
    @Override
    protected void  onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (this.findGlobal("auth") != null) {
            this.redirect(ProfileActivity.class);

            return ;
        }

        setContentView(R.layout.login_activity);

        Button button = (Button) findViewById(R.id.button_login);
        Animation animation = new AlphaAnimation(1, 0);

        animation.setDuration(500);
        animation.setInterpolator(new LinearInterpolator());
        animation.setRepeatCount(Animation.INFINITE);
        animation.setRepeatMode(Animation.REVERSE);
        button.startAnimation(animation);
        button.setOnClickListener(this);
    }

    @Override
    public void     onClick(View v) {
        switch (v.getId()) {
            case R.id.button_login:
                final String login = ((EditText) findViewById(R.id.edit_login)).getText().toString();
                final String password = ((EditText) findViewById(R.id.edit_password)).getText().toString();

                if (login.isEmpty() || password.isEmpty()) {
                    this.flash(getString(R.string.error_credentials_empty), false);
                    return ;
                }

                Map<String, String> params = new HashMap<String, String>() {{
                    put("login", login);
                    put("password", password);
                }};
                if (!this.buildRequest("login", "POST", params, new HashMap<String, String>() {{ put("identity", "login"); }})) {
                    this.flash(getString(R.string.api_internal_error), false);
                    return ;
                }
                break;

            default:
                break;
        }
    }

    @Override
    protected void     onApiCompleted(String identity, Pair<Integer, String> error, JsonObject result) {
        if (error != null) {
            String msg;

            switch (error.second) {
                case "invalid login/password combinaison":
                    msg = getString(R.string.api_invalid_creds);
                    break;

                default:
                    msg = getString(R.string.api_unknown_error);
                    break;
            }

            this.flash(msg, false);
            return ;
        }

        switch (identity) {
            case "login":
                this.editGlobal("auth", this.base64(result.get("token").getAsString(), false));
                if (!this.buildRequest("infos", "POST", null, new HashMap<String, String>() {{ put("identity", "infos"); put("hideProgress", "true"); }}))
                    this.flash(getString(R.string.api_internal_error), false);
                break;

            case "infos":
                if (!result.has("infos")) {
                    this.flash(getString(R.string.api_invalid_data), false);
                    return;
                }

                this.editGlobal("currentUser", result.getAsJsonObject("infos").toString());
                this.redirect(ProfileActivity.class);
                break;

            default:
                break;
        }
    }

    public              LoginActivity() {
        super();
    }
}
