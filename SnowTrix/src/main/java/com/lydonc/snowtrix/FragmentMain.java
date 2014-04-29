package com.lydonc.snowtrix;


import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnticipateOvershootInterpolator;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.parse.FindCallback;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import kankan.wheel.widget.OnWheelChangedListener;
import kankan.wheel.widget.OnWheelScrollListener;
import kankan.wheel.widget.WheelView;
import kankan.wheel.widget.adapters.AbstractWheelTextAdapter;

public class FragmentMain extends Fragment {
    private Context globalContext = null;
    private View globalView = null;
    private List<String> airDirection /*= new ArrayList<String>(Arrays.asList(getResources().getStringArray(R.array.airdirection)))*/;
    private List<String> airGrabs /*= new ArrayList<String>(Arrays.asList(getResources().getStringArray(R.array.airgrabs)))*/;
    private List<String> airSpin /*= new ArrayList<String>(Arrays.asList(getResources().getStringArray(R.array.airspin)))*/;
    private Dialog progressDialog;

    private boolean wheelScrolled = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        globalContext = this.getActivity();
        airDirection = new ArrayList<String>(Arrays.asList(getResources().getStringArray(R.array.airdirection)));
        airGrabs = new ArrayList<String>(Arrays.asList(getResources().getStringArray(R.array.airgrabs)));
        airSpin = new ArrayList<String>(Arrays.asList(getResources().getStringArray(R.array.airspin)));
        if (savedInstanceState == null){
            // onCreate First Time

        } else {
            // onCreate Subsequent Time
    }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        globalView = rootView;

        ImageButton refresh = (ImageButton) rootView.findViewById(R.id.refreshButton);
        refresh.setImageResource(R.drawable.refresh_icon);
        refresh.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                getActivity().finish();
                startActivity(getActivity().getIntent());
            }
        });

        final ToggleButton facebookToggle = (ToggleButton) rootView.findViewById(R.id.facebookToggle);

        facebookToggle.setChecked(ParseUser.getCurrentUser() == null ? false : true);

        //initWheel(R.id.wheelStance);
        initWheel(R.id.wheelDirection);
        initWheel(R.id.wheelSpin);
        initWheel(R.id.wheelGrab);

        Button mix = (Button) rootView.findViewById(R.id.button);
        mix.setBackgroundColor(Color.TRANSPARENT);
        mix.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //mixWheel(R.id.wheelStance);
                if(!((ToggleButton) globalView.findViewById(R.id.toggleButton)).isChecked())
                    mixWheel(R.id.wheelDirection);
                if(!((ToggleButton) globalView.findViewById(R.id.toggleButton2)).isChecked())
                    mixWheel(R.id.wheelSpin);
                if(!((ToggleButton) globalView.findViewById(R.id.toggleButton3)).isChecked())
                    mixWheel(R.id.wheelGrab);
            }
        });

//        ImageButton facebook = (ImageButton) rootView.findViewById(R.id.facebookButton);
        facebookToggle.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                if (ParseUser.getCurrentUser() == null) {

                    /*progressDialog = ProgressDialog.show(
                            getActivity(), "", "Logging in...", true);
                    List<String> permissions = Arrays.asList("basic_info");
                    ParseFacebookUtils.logIn(permissions, getActivity(), new LogInCallback() {
                        @Override
                        public void done(ParseUser user, ParseException err) {
                            progressDialog.dismiss();
                            if (user == null) {
                                Toast.makeText(getActivity(), "Uh oh. The user cancelled the Facebook login.", Toast.LENGTH_LONG).show();
                            } else if (user.isNew()) {
                                Toast.makeText(getActivity(), "User signed up and logged in through Facebook!", Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(getActivity(), "User logged in through Facebook! " + ParseUser.getCurrentUser().toString(), Toast.LENGTH_LONG).show();
                            }
                            getActivity().finish();
                            startActivity(getActivity().getIntent());
                        }
                    });*/
                    getActivity().finish();
                    startActivity(getActivity().getIntent());
                    facebookToggle.setChecked(ParseUser.getCurrentUser() == null ? false : true);

                }
                else {
                    AlertDialog.Builder alertBuilder = new AlertDialog.Builder(getActivity());
                    alertBuilder.setTitle("Warning");
                    alertBuilder.setMessage("Are you sure you want to log out? Custom Trix will no longer be available.");
                    alertBuilder.setCancelable(false);
                    alertBuilder.setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            facebookToggle.setChecked(ParseUser.getCurrentUser() == null ? false : true);
                        }
                    });
                    alertBuilder.setNegativeButton("Log Out", new DialogInterface.OnClickListener(){
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            ParseUser.logOut();
                            Toast.makeText(getActivity(), "User logged out of Facebook!", Toast.LENGTH_LONG).show();
                            getActivity().finish();
                            startActivity(getActivity().getIntent());
                            facebookToggle.setChecked(ParseUser.getCurrentUser() == null ? false : true);
                        }
                    });
                    AlertDialog alertDialog = alertBuilder.create();
                    alertDialog.show();
                    facebookToggle.setChecked(ParseUser.getCurrentUser() == null ? false : true);
                }
            }
        });


        if (ParseUser.getCurrentUser() == null)
            loginPrompt();

        return rootView;
    }

    private void loginPrompt() {
        final Dialog dialog = new Dialog(globalContext);
        dialog.setTitle("Extra Features!");
        dialog.setContentView(R.layout.activity_facebook_prompt);
        final Button loginButton = (Button) dialog.findViewById(R.id.buttonLogin);
        final Button skipButton = (Button) dialog.findViewById(R.id.buttonSkipLogin);

        dialog.show();

        skipButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getActivity(), "Login Skipped... Custom Trix Disabled", Toast.LENGTH_LONG).show();
                dialog.dismiss();
            }
        });
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //progressDialog = ProgressDialog.show(
                //        getActivity(), "", "Logging in...", true);
                List<String> permissions = Arrays.asList("basic_info");
                ParseFacebookUtils.logIn(permissions, getActivity(), new LogInCallback() {
                    @Override
                    public void done(ParseUser user, ParseException err) {
                        if (user == null) {
                            Toast.makeText(getActivity(), "Login Cancelled", Toast.LENGTH_LONG).show();
                            dialog.dismiss();
                            getActivity().finish();
                            startActivity(getActivity().getIntent());
                        } else if (user.isNew()) {
                            Toast.makeText(getActivity(), "Signed up & Logged in", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                            getActivity().finish();
                            startActivity(getActivity().getIntent());

                        } else {
                            Toast.makeText(getActivity(), "Logged in" + ParseUser.getCurrentUser().toString(), Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                            getActivity().finish();
                            startActivity(getActivity().getIntent());
                        }
                    }
                });
                //dialog.dismiss();
            }
        });

        if( ParseUser.getCurrentUser() != null ){
            Toast.makeText(getActivity(), "Logged in" + ParseUser.getCurrentUser().toString(), Toast.LENGTH_SHORT).show();
            dialog.dismiss();

        }
        /*
        if(ParseUser.getCurrentUser() == null) {
            dialog.show();
        }
*/
        /*if( ParseUser.getCurrentUser() != null ){
            Toast.makeText(getActivity(), "Logged in" + ParseUser.getCurrentUser().toString(), Toast.LENGTH_SHORT).show();
            dialog.dismiss();
    }*/
}


    // Wheel scrolled listener
    OnWheelScrollListener scrolledListener = new OnWheelScrollListener() {
        public void onScrollingStarted(WheelView wheel) {
            wheelScrolled = true;
        }
        public void onScrollingFinished(WheelView wheel) {
            wheelScrolled = false;
            printTrick();
        }
    };

    // Wheel changed listener
    private OnWheelChangedListener changedListener = new OnWheelChangedListener() {
        public void onChanged(WheelView wheel, int oldValue, int newValue) {
            if (!wheelScrolled) {
                printTrick();
            }
        }
    };

    private void printTrick() {
        Switch switchStance = (Switch) getView().findViewById(R.id.switchStance);
        String switchText = "";

        String direction = airDirection.get(getWheel(R.id.wheelDirection).getCurrentItem());
        String spin = airSpin.get(getWheel(R.id.wheelSpin).getCurrentItem());
        String grab = airGrabs.get(getWheel(R.id.wheelGrab).getCurrentItem());

        if (((ToggleButton) globalView.findViewById(R.id.toggleButton4)).isChecked()){
            int rand = (int) ((Math.random() * (100 - 1)) + 1);
            if (rand % 2 == 0) switchText = "Switch";

        }
        TextView results = (TextView) getView().findViewById(R.id.textView);
        results.setText(switchText + " " + direction + " " + spin + " " + grab);
    }

    /**
     * Initializes wheel
     * @param id the wheel widget Id
     */
    private void initWheel(int id) {
        /*final List<String> airDirection = new ArrayList<String>(Arrays.asList(getResources().getStringArray(R.array.airdirection)));
        final List<String> airGrabs = new ArrayList<String>(Arrays.asList(getResources().getStringArray(R.array.airgrabs)));
        final List<String> airSpin = new ArrayList<String>(Arrays.asList(getResources().getStringArray(R.array.airspin)));
        List<String> airStance = new ArrayList<String>(Arrays.asList(getResources().getStringArray(R.array.airstance)));
*/
        final TrickAdapter mAdapter = new TrickAdapter(getActivity(), new ArrayList<Trick>());
        final ProgressDialog mProgressDialog = new ProgressDialog(getActivity());

        ParseQuery<Trick> query = ParseQuery.getQuery(Trick.class);

        final WheelView wheel = getWheel(id);
        //wheel.setViewAdapter(new NumericWheelAdapter(globalContext, 0, 9));
        if(id == R.id.wheelDirection) {
            if(ParseUser.getCurrentUser() != null) {
                mProgressDialog.setMessage("Loading...");
                mProgressDialog.setIndeterminate(false);
                mProgressDialog.show();

                query.whereEqualTo("username", ParseUser.getCurrentUser().getUsername());
                query.whereEqualTo("trickCategory", "Direction");
                query.findInBackground(new FindCallback<Trick>() {
                    @Override
                    public void done(final List<Trick> tricks, ParseException error) {
                        if (tricks != null) {
                            mProgressDialog.dismiss();
                            mAdapter.clear();
                            mAdapter.addAll(tricks);
                            wheel.setViewAdapter(new AbstractWheelTextAdapter(globalContext) {
                                @Override
                                protected CharSequence getItemText(int index) {
                                    return tricks.get(index).getTrickName();
                                }

                                @Override
                                public int getItemsCount() {
                                    return tricks.size();
                                }
                            });
                        } else {
                            mProgressDialog.dismiss();
                            Toast.makeText(getActivity(), "Something went wrong! Try again!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
            else {
                wheel.setViewAdapter(new AbstractWheelTextAdapter(globalContext) {
                    @Override
                    protected CharSequence getItemText(int index) {
                        return airDirection.get(index);
                    }

                    @Override
                    public int getItemsCount() {
                        return airDirection.size();
                    }
                });
            }
        }
        if(id == R.id.wheelGrab) {
            if(ParseUser.getCurrentUser() != null) {
                mProgressDialog.setMessage("Loading...");
                mProgressDialog.setIndeterminate(false);
                mProgressDialog.show();

                query.whereEqualTo("username", ParseUser.getCurrentUser().getUsername());
                query.whereEqualTo("trickCategory", "Grab");
                query.findInBackground(new FindCallback<Trick>() {
                    @Override
                    public void done(final List<Trick> tricks, ParseException error) {
                        if (tricks != null) {
                            mProgressDialog.dismiss();
                            mAdapter.clear();
                            mAdapter.addAll(tricks);
                            wheel.setViewAdapter(new AbstractWheelTextAdapter(globalContext) {
                                @Override
                                protected CharSequence getItemText(int index) {
                                    return tricks.get(index).getTrickName();
                                }

                                @Override
                                public int getItemsCount() {
                                    return tricks.size();
                                }
                            });
                        } else {
                            mProgressDialog.dismiss();
                            Toast.makeText(getActivity(), "Something went wrong! Try again!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
            else {
                wheel.setViewAdapter(new AbstractWheelTextAdapter(globalContext) {
                    @Override
                    protected CharSequence getItemText(int index) {
                        return airGrabs.get(index);
                    }

                    @Override
                    public int getItemsCount() {
                        return airGrabs.size();
                    }
                });
            }

        }

        if(id == R.id.wheelSpin) {
            if(ParseUser.getCurrentUser() != null) {
                mProgressDialog.setMessage("Loading...");
                mProgressDialog.setIndeterminate(false);
                mProgressDialog.show();

                query.whereEqualTo("username", ParseUser.getCurrentUser().getUsername());
                query.whereEqualTo("trickCategory", "Spin");
                query.findInBackground(new FindCallback<Trick>() {
                    @Override
                    public void done(final List<Trick> tricks, ParseException error) {
                        if (tricks != null) {
                            mProgressDialog.dismiss();
                            mAdapter.clear();
                            mAdapter.addAll(tricks);
                            wheel.setViewAdapter(new AbstractWheelTextAdapter(globalContext) {
                                @Override
                                protected CharSequence getItemText(int index) {
                                    return tricks.get(index).getTrickName();
                                }

                                @Override
                                public int getItemsCount() {
                                    return tricks.size();
                                }
                            });
                        } else {
                            mProgressDialog.dismiss();
                            Toast.makeText(getActivity(), "Something went wrong! Try again!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
            else {
                wheel.setViewAdapter(new AbstractWheelTextAdapter(globalContext) {
                    @Override
                    protected CharSequence getItemText(int index) {
                        return airSpin.get(index);
                    }

                    @Override
                    public int getItemsCount() {
                        return airSpin.size();
                    }
                });
            }
        }

        wheel.setCurrentItem((int)(Math.random() * 10));

        wheel.addChangingListener(changedListener);
        wheel.addScrollingListener(scrolledListener);
        wheel.setCyclic(true);
        wheel.setInterpolator(new AnticipateOvershootInterpolator());
    }

    /**
     * Returns wheel by Id
     * @param id the wheel Id
     * @return the wheel with passed Id
     */
    private WheelView getWheel(int id) {
        return (WheelView) globalView.findViewById(id);
    }

    /**
     * Mixes wheel
     * @param id the wheel id
     */
    private void mixWheel(int id) {
        WheelView wheel = getWheel(id);
        wheel.scroll(-25 + (int)(Math.random() * 250), 2500);
    }
}
