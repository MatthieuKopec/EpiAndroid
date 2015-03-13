package com.epiandroid.epitech.epiandroid;

import  android.os.Bundle;

public class        LogoutActivity extends MainActivity {
    @Override
    protected void  onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.removeGlobal("currentUser");
        this.removeGlobal("auth");
        this.redirect(LoginActivity.class);
    }

    public              LogoutActivity() {
        super();
    }
}
