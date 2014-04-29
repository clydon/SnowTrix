package com.lydonc.snowtrix;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v13.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.facebook.Session;
import com.parse.ParseFacebookUtils;
import com.parse.ParseObject;

import java.util.Locale;
import java.util.Random;


public class MainActivity extends FragmentActivity {

    SectionsPagerAdapter mSectionsPagerAdapter;
    ViewPager mViewPager;
    MenuItem mAddTrickMenuItem;
    private Shaker mShakeDetector;
    private SensorManager mSensorManager;
    private Sensor mAccelerometer;

    static final String TAG = "MyApp";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ParseObject.registerSubclass(TrickComponent.class);

        //Setup ViewPager
        mSectionsPagerAdapter = new SectionsPagerAdapter(getFragmentManager());
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        //Set Default Fragment
        mViewPager.setCurrentItem(1);

        // ShakeDetector initialization
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mShakeDetector = new Shaker(new Shaker.OnShakeListener() {
            @Override
            public void onShake() {
                rollDice();
            }
        });

        // Fetch Facebook user info if the session is active
        Session session = ParseFacebookUtils.getSession();
        if (session != null && session.isOpened()) {
            //makeMeRequest();
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ParseFacebookUtils.finishAuthentication(requestCode, resultCode, data);
    }

    private void rollDice() {
        String[] airDirection = getResources().getStringArray(R.array.airdirection);
        String[] airGrabs = getResources().getStringArray(R.array.airgrabs);
        String[] airSpin = getResources().getStringArray(R.array.airspin);
        String[] airStance = getResources().getStringArray(R.array.airstance);

        String[] jibDirection = getResources().getStringArray(R.array.jibdirection);
        String[] jibGrabs = getResources().getStringArray(R.array.jibgrabs);
        String[] jibOffSpin = getResources().getStringArray(R.array.jiboffspin);
        String[] jibOnSpin = getResources().getStringArray(R.array.jibonspin);
        String[] jibStance = getResources().getStringArray(R.array.jibstance);

        if (mViewPager.getCurrentItem() == 1){  //If MAIN Fragment
            String randStance = airStance[new Random().nextInt(airStance.length)];
            String randDir = airDirection[new Random().nextInt(airDirection.length)];
            String randSpin = airSpin[new Random().nextInt(airSpin.length)];
            String randGrab = airGrabs[new Random().nextInt(airGrabs.length)];

            TextView textView = (TextView) findViewById(R.id.textView);
            String randomTrick = randStance + " " + randDir + " " + randSpin + " " + randGrab;
            textView.setText(randomTrick);

            //Toast.makeText(MainActivity.this, "", Toast.LENGTH_LONG).show();
        }
        else if (mViewPager.getCurrentItem() == 0){
            mAddTrickMenuItem.setVisible(true);
        }
        else {
            mAddTrickMenuItem.setVisible(false);
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        mSensorManager.registerListener(mShakeDetector, mAccelerometer, SensorManager.SENSOR_DELAY_UI);

        /*ParseUser currentUser = ParseUser.getCurrentUser();
        if (currentUser != null) {
            // Check if the user is currently logged
            // and show any cached content
            updateViewsWithProfileInfo();
        } else {
            // If the user is not logged in, go to the
            // activity showing the login view.
            // startLoginActivity();
        }*/
    }

    /*private void makeMeRequest() {
        Request request = Request.newMeRequest(ParseFacebookUtils.getSession(),
                new Request.GraphUserCallback() {
                    @Override
                    public void onCompleted(GraphUser user, Response response) {
                        if (user != null) {
                            // Create a JSON object to hold the profile info
                            JSONObject userProfile = new JSONObject();
                            try {
                                // Populate the JSON object
                                userProfile.put("facebookId", user.getId());
                                userProfile.put("name", user.getName());
                                if (user.getLocation().getProperty("name") != null) {
                                    userProfile.put("location", (String) user
                                            .getLocation().getProperty("name"));
                                }
                                if (user.getProperty("gender") != null) {
                                    userProfile.put("gender",
                                            (String) user.getProperty("gender"));
                                }
                                if (user.getBirthday() != null) {
                                    userProfile.put("birthday",
                                            user.getBirthday());
                                }
                                if (user.getProperty("relationship_status") != null) {
                                    userProfile
                                            .put("relationship_status",
                                                    (String) user
                                                            .getProperty("relationship_status"));
                                }

                                // Save the user profile info in a user property
                                ParseUser currentUser = ParseUser
                                        .getCurrentUser();
                                currentUser.put("profile", userProfile);
                                currentUser.saveInBackground();

                                // Show the user info
                                //updateViewsWithProfileInfo(); //todo
                            } catch (JSONException e) {
                                Log.d(MainActivity.TAG,
                                        "Error parsing returned user data.");
                            }

                        } else if (response.getError() != null) {
                            if ((response.getError().getCategory() == FacebookRequestError.Category.AUTHENTICATION_RETRY)
                                    || (response.getError().getCategory() == FacebookRequestError.Category.AUTHENTICATION_REOPEN_SESSION)) {
                                Log.d(MainActivity.TAG,
                                        "The facebook session was invalidated.");
                                onLogoutButtonClicked();
                            } else {
                                Log.d(MainActivity.TAG,
                                        "Some other error: "
                                                + response.getError()
                                                .getErrorMessage());
                            }
                        }
                    }
                });
        request.executeAsync();

    }
*/

    @Override
    protected void onPause() {
        mSensorManager.unregisterListener(mShakeDetector);
        super.onPause();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.user_details, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();/*
        if (id == R.id.action_facebook) {
            onLoginButtonClicked();
            return true;
        }*/
        return super.onOptionsItemSelected(item);
    }

    public class SectionsPagerAdapter extends FragmentStatePagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            Fragment fragment = null;
            if(position == 0){
                fragment = new FragmentLists();
            }
            if(position == 1){
                fragment = new FragmentMain();
            }
            return fragment;
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            Locale l = Locale.getDefault();
            switch (position) {
                case 0:
                    return getString(R.string.title_section1).toUpperCase(l);
                case 1:
                    return getString(R.string.title_section2).toUpperCase(l);
                /*case 2:
                    return getString(R.string.title_section3).toUpperCase(l);*/
            }
            return null;
        }
    }
}

