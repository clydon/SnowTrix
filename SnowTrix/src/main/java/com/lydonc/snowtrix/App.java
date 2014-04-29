package com.lydonc.snowtrix;
import android.app.Application;
import com.parse.Parse;
import com.parse.ParseFacebookUtils;

public class App extends Application {

    @Override public void onCreate() {
        super.onCreate();


        //Setup Parse Key
        Parse.initialize(this, "f9iFrrgP8QpmgkR62Abvg2X0Ou0Zug0ZLeteraA7", "G92LjiUAI4DgEzJ5gl7eFFkj6zj3eieprVpEhWQ7");
        ParseFacebookUtils.initialize(getString(R.string.app_id));
/*
        Parse.initialize(this, "PRAaLniiM3AEYwNNwwm16fsPRq0oHiCOyRwl8cK3", "kcLgMl4JcEJItdSij3xe3eiMV2pcARhbPoSSCPtV");
        ParseUser.enableAutomaticUser();
        ParseACL defaultACL = new ParseACL();

        defaultACL.setPublicReadAccess(true);

        ParseACL.setDefaultACL(defaultACL, true);*/
    }
}