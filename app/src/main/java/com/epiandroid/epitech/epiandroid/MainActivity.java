package com.epiandroid.epitech.epiandroid;

import  android.app.Activity;
import  android.app.ProgressDialog;
import  android.content.Context;
import  android.content.Intent;
import  android.content.SharedPreferences;
import  android.preference.PreferenceManager;
import  android.text.Html;
import  android.util.Base64;
import  android.util.Pair;
import  android.view.View;
import  android.widget.AdapterView;
import  android.widget.ArrayAdapter;
import  android.widget.ImageView;
import  android.widget.ListView;
import  android.widget.Toast;

import  com.google.gson.Gson;
import  com.google.gson.JsonArray;
import  com.google.gson.JsonElement;
import  com.google.gson.JsonObject;
import  com.koushikdutta.async.future.FutureCallback;
import  com.koushikdutta.async.http.AsyncSSLSocketMiddleware;
import  com.koushikdutta.ion.Ion;

import  org.apache.http.conn.ssl.SSLSocketFactory;

import  java.security.cert.CertificateException;
import  java.security.cert.X509Certificate;
import  java.util.ArrayList;
import  java.util.HashMap;
import  java.util.List;
import  java.util.Map;
import  java.util.concurrent.TimeoutException;

import  javax.net.ssl.SSLContext;
import  javax.net.ssl.TrustManager;
import  javax.net.ssl.X509TrustManager;

public class                MainActivity extends Activity {
    /**
     * Base API url.
     */
    private final String    baseUrl = "http://epitech-api.herokuapp.com";

    /**
     * Builds and executes a HTTP request on the API.
     *
     * @param url           host on which the HTTP request is sent
     * @param method        custom request: GET, DELETE, POST, PUT
     * @param vls           request parameters
     * @param parameters    trigger options, for example ignoreSSL mapped to true
     * @return              boolean indicating whether request succeeded
     */
    protected boolean       buildRequest(String url, String method, Map<String, String> vls, Map<String, String> parameters) {
        String              apiUrl = this.baseUrl;
        String              tmp;
        Map<String, List<String>> values = new HashMap<>();

        method = (method != null ? method.toUpperCase() : null);
        if (url == null || url.isEmpty() || method == null
                || (!method.equals("GET") && !method.equals("DELETE")
                && !method.equals("POST") && !method.equals("PUT")))
            return (false);

        tmp = (parameters != null ? parameters.get("fullUrl") : null);
        apiUrl += (tmp != null ? tmp : "/" + url);

        String auth = this.base64(this.findGlobal("auth"), true);
        if (!apiUrl.substring(apiUrl.length() - 5).toLowerCase().equals("login")
                && (auth == null || auth.isEmpty()))
            return (false);

        List<String> temp = new ArrayList<>();
        temp.add(auth);
        values.put("token", temp);
        if (vls != null) {
            for (Map.Entry<String, String> entry : vls.entrySet()) {
                temp = new ArrayList<>();

                temp.add(entry.getValue());
                values.put(entry.getKey(), temp);
            }
        }

        final String identity = (parameters != null ? parameters.get("identity") : null);
        final boolean asJsonArray = (parameters != null && parameters.get("asJsonArray") != null && parameters.get("asJsonArray").equals("true") ? true : false);
        final boolean useProgress = (parameters != null && parameters.get("hideProgress") != null && parameters.get("hideProgress").equals("true") ? false : true);

        ProgressDialog pg = null;
        if (useProgress) {
            pg = new ProgressDialog(this);
            pg.setIndeterminate(false);
            pg.setMessage(getString(R.string.api_loading_msg));
            pg.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            pg.show();
        }
        final ProgressDialog progressDialog = pg;

        try {
            FutureCallback<String> callback = new FutureCallback<String>() {
                @Override
                public void onCompleted(Exception e, String res) {
                    JsonElement result;
                    Pair<Integer, String> error = null;

                    if (useProgress) {
                        progressDialog.hide();
                        progressDialog.dismiss();
                    }

                    if (res == null
                        || res.equals("{}") || res.equals("[]")) {
                        flash(getString(R.string.api_empty_data), true);
                        return ;
                    }

                    try {
                        result = new Gson().fromJson(res, (asJsonArray ? JsonArray.class : JsonObject.class));
                    } catch (Exception ex) {
                        result = null;
                    }

                    if (e != null || result == null) {
                        if (e != null && e instanceof TimeoutException)
                            flash(getString(R.string.api_timeout), false);
                        else
                            flash(getString(R.string.api_request_error), false);

                        return ;
                    }

                    if (!asJsonArray) {
                        JsonObject check = result.getAsJsonObject();

                        if (check.has("error")) {
                            JsonObject err = check.getAsJsonObject("error");
                            error = new Pair<>(err.get("code").getAsInt(), err.get("message").getAsString().toLowerCase());

                            if (error.second.equals("connection token is invalid or has expired")) {
                                removeGlobal("auth");
                                redirect(LoginActivity.class);
                                flash(getString(R.string.api_expired_token), false);
                                return;
                            }
                        }
                    }

                    if (asJsonArray)
                        onApiCompleted(identity, error, result.getAsJsonArray());
                    else
                        onApiCompleted(identity, error, result.getAsJsonObject());
                }
            };

            boolean useSSL = (parameters != null && parameters.get("ignoreSSL") != null && parameters.get("ignoreSSL").equals("true"));
            if (useSSL && !this.enableSSL())
                return (false);

            if (method.equals("GET") || method.equals("DELETE")) {
                Ion.with(this)
                        .load(method, apiUrl)
                        .setHeader("Accept", "application/json")
                        .addQueries(values)
                        .asString()
                        .setCallback(callback);

            } else {
                Ion.with(this)
                        .load(method, apiUrl)
                        .setHeader("Accept", "application/json")
                        .setBodyParameters(values)
                        .asString()
                        .setCallback(callback);
            }
        } catch (Exception e) {
            return (false);
        }

        return (true);
    }

    /**
     * Loads an image from URL and set it to an ImageView.
     *
     * @param url                   URL to the picture
     * @param image                 object to set the picture to
     * @param placeholderResource   resource used as placeholder while loading
     * @param errorResource         resource used in case of loading error
     * @param parameters            trigger options; none at the moment, set it to null
     * @return                      boolean indicating success or failure
     * @see                         ImageView
     */
    @SuppressWarnings("unused")
    protected boolean       buildImage(String url, ImageView image, int placeholderResource, int errorResource, Map<String, String> parameters) {
        boolean useSSL = (parameters != null && parameters.get("ignoreSSL") != null && parameters.get("ignoreSSL").equals("true"));
        if (useSSL)
            this.enableSSL();

        Ion.with(image)
           .placeholder(placeholderResource != -1 ? placeholderResource : R.drawable.ic_launcher)
           .error(errorResource != -1 ? errorResource : R.drawable.ic_launcher)
           .load(url);

        return (true);
    }

    /**
     * Enables SSL over HTTPS requests.
     *
     * @return  boolean indicating whether enabling SSL succeeded
     */
    private boolean         enableSSL() {
        Context context = getApplicationContext();

        TrustManager tm = new X509TrustManager() {
            public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
            }

            public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
            }

            public X509Certificate[] getAcceptedIssuers() {
                return (null);
            }
        };

        TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
            public X509Certificate[] getAcceptedIssuers() {
                return (null);
            }

            public void checkClientTrusted(X509Certificate[] certs, String authType) {
            }

            public void checkServerTrusted(X509Certificate[] certs, String authType) {
            }
        }};

        SSLContext sslContext;

        try {
            sslContext = SSLContext.getInstance("SSLv3");
            sslContext.init(null, new TrustManager[] { tm }, null);
        } catch (Exception e) {
            return (false);
        }

        AsyncSSLSocketMiddleware sslMiddleWare = Ion.getDefault(context).getHttpClient().getSSLSocketMiddleware();

        sslMiddleWare.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
        sslMiddleWare.setSSLContext(sslContext);

        Ion.getDefault(context).getHttpClient().getSSLSocketMiddleware().setTrustManagers(trustAllCerts);
        Ion.getDefault(context).getHttpClient().getSSLSocketMiddleware().setSSLContext(sslContext);

        return (true);
    }

    /**
     * Handles HTTP request's JsonObject result.  Used by Ion as its completion callback.
     *
     * @param identity  identifies which request has completed
     * @param error     error code mapped to error's description in case of error
     * @param result    resulting JsonObject object
     * @see             Ion
     */
    @SuppressWarnings("unused")
    protected void          onApiCompleted(String identity, Pair<Integer, String> error, JsonObject result) {
    }

    /**
     * Handles HTTP request's JsonArray result.  Used by Ion as its completion callback.
     *
     * @param identity  identifies which request has completed
     * @param error     error code mapped to error's description in case of error
     * @param result    resulting JsonArray object
     * @see             Ion
     */
    @SuppressWarnings("unused")
    protected void          onApiCompleted(String identity, Pair<Integer, String> error, JsonArray result) {
    }

    /**
     * Retrieve current user from preferences.
     *
     * @return user informations
     * @see    JsonObject
     */
    protected JsonObject    findUser() {
        String              json = this.findGlobal("currentUser");

        if (json == null || json.isEmpty())
            return (null);

        try {
            return (new Gson().fromJson(json, JsonObject.class));
        } catch (Exception e) {
            return (null);
        }
    }

    /**
     * Handles NavigationDrawer's menu items.
     */
    public void             showMenu()
    {
        ListView menu = (ListView) findViewById(R.id.left_nav);
        String[]            menuNames = getResources().getStringArray(R.array.nav_menu);

        menu.setBackgroundResource(android.R.color.holo_blue_dark);
        menu.setSelector(android.R.color.holo_blue_light);
        menu.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, android.R.id.text1, menuNames));
        menu.setOnItemClickListener(new ListView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Class[] actions = {
                        ProfileActivity.class,
                        PlanningActivity.class,
                        SusiesActivity.class,
                        ProjectsActivity.class,
                        MarksActivity.class,
                        ModulesActivity.class,
                        TrombiActivity.class,
                        LogoutActivity.class
                };

                if (position >= 0 && position <= actions.length)
                    redirect(actions[position]);
            }
        });
    }

    /**
     * Strips HTML tags from string.
     *
     * @param str   string that may contain HTML tags
     * @return      str minus HTML tags
     */
    protected String	    stripTags(String str)
    {
        return (str != null ? Html.fromHtml(str.replaceAll("\\\\<.*?\\\\>", "")).toString() : null);
    }

    /**
     * Redirects current activity to another one.
     *
     * @param activity  target activity
     */
    protected void          redirect(Class activity) {
        Intent i = new Intent(this, activity);

        startActivity(i);
    }

    /**
     * Creates a shared preference variable.
     *
     * @param key       used to refer to the variable afterwards
     * @param value     variable's relevant value
     */
    protected void          createGlobal(String key, String value) {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = settings.edit();

        editor.putString(key, value);
        editor.apply();
    }

    /**
     * Edits shared preference variable.
     *
     * @param key       used to refer to the variable that may already exist
     * @param value     variable's relevant new value
     */
    protected void          editGlobal(String key, String value) {
        if (this.findGlobal(key) != null)
            this.removeGlobal(key);

        this.createGlobal(key, value);
    }

    /**
     * Removes shared preference variable.
     *
     * @param key   used to refer to the existing variable
     */
    protected void          removeGlobal(String key) {
        SharedPreferences   settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = settings.edit();

        if (this.findGlobal(key) == null)
            return ;

        editor.remove(key);
        editor.apply();
    }

    /**
     * Retrieves shared preference variable's value.
     *
     * @param key   used to refer to the existing variable
     * @return      variable's relevant value
     */
    protected String        findGlobal(String key) {
        SharedPreferences   settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        return (settings.getString(key, null));
    }

    /**
     * Toasts a message.
     *
     * @param message       to be displayed
     * @param longDuration  trigger long duration display
     */
    protected void          flash(String message, boolean longDuration) {
        Toast toast;

        toast = Toast.makeText(getApplicationContext(), message, (longDuration ? Toast.LENGTH_LONG : Toast.LENGTH_SHORT));
        toast.show();
    }

    /**
     * Encodes or decodes a string in BASE64.
     *
     * @param str       relevant string
     * @param decode    decode instead of encode
     * @return          string encoded in BASE64
     */
    protected String	    base64(String str, boolean decode) {
        String	    	    crypted;
        byte[]		        bts;

        if (str == null)
            return (null);

        try {
            bts = str.getBytes("UTF-8");

            if (!decode)
                crypted = Base64.encodeToString(bts, Base64.DEFAULT);
            else
                crypted = new String(Base64.decode(bts, Base64.DEFAULT), "UTF-8");
        } catch (Exception e) {
            return (null);
        }

        return (crypted);
    }

    @SuppressWarnings("unused")
    public String           getBaseUrl() {
        return (this.baseUrl);
    }

    public                  MainActivity() {
        super();
    }
}
