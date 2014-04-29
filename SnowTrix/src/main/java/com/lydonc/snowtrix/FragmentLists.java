package com.lydonc.snowtrix;


import android.app.AlertDialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.lydonc.snowtrix.androidbootstrap.BootstrapButton;
import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;

public class FragmentLists extends Fragment implements View.OnClickListener{


    private Context globalContext = null;
    TrickAdapter mAdapter;
    ProgressDialog mProgressDialog;
    ListView mListView;
    private BootstrapButton btnStance, btnDirection, btnSpin, btnGrab;
    String selectedTrickCat;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        globalContext = this.getActivity();


        if (savedInstanceState == null){
            // onCreate First Time

        } else {
            // onCreate Subsequent Time

        }


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_lists, container, false);
        setHasOptionsMenu(true);

        ParseObject.registerSubclass(Trick.class);

        mListView = (ListView) rootView.findViewById(R.id.trickListView);

        mAdapter = new TrickAdapter(getActivity(), new ArrayList<Trick>());
        mListView.setAdapter(mAdapter);

        mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            public boolean onItemLongClick(AdapterView<?> arg0, View view, int position, long id) {
                final Trick trick = mAdapter.getItem(position);

                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Warning");
                builder.setMessage("Are you sure you want to delete this?");

                builder.setNegativeButton("YES", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        trick.deleteInBackground(new DeleteCallback() {
                            public void done(ParseException e) {
                                if (e == null) {
                                    mAdapter.remove(trick);
                                }
                                else
                                    Toast.makeText(getActivity(), "Something went wrong!", Toast.LENGTH_SHORT).show();
                            }
                        });
                        dialog.dismiss();
                    }
                });
                builder.setPositiveButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) { // Do nothing, User Cancelled Delete
                        dialog.dismiss();
                    }
                });
                AlertDialog alert = builder.create();
                alert.show();


                return true;
            }
        });

        btnDirection = (BootstrapButton) rootView.findViewById(R.id.buttonDirection);
        btnSpin = (BootstrapButton) rootView.findViewById(R.id.buttonSpin);
        btnGrab = (BootstrapButton) rootView.findViewById(R.id.buttonGrab);

        btnDirection.setOnClickListener(this);
        btnSpin.setOnClickListener(this);
        btnGrab.setOnClickListener(this);

        BootstrapButton newTrick = (BootstrapButton) rootView.findViewById(R.id.buttonAddTrick);
        newTrick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                if(selectedTrickCat != null) {
                    newTrick();
                }
                else {
                    new AlertDialog.Builder(getActivity())
                            .setTitle("Adding Tricks:")
                            .setMessage("To add tricks or view lists, first click on one of the colored button above.")
                            .setNegativeButton("Continue", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    // do nothing
                                }
                            })
                            .setIcon(android.R.drawable.ic_dialog_info)
                            .show();
                }
            }
        });

        return rootView;
    }

    private void newTrick() {
        if(ParseUser.getCurrentUser() == null){
            new AlertDialog.Builder(getActivity())
                    .setTitle("Custom Trix")
                    .setMessage("You must login to use this feature.")
                    .setNegativeButton("Continue", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // do nothing
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_info)
                    .show();
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("New " + selectedTrickCat);
        final EditText input = new EditText(getActivity());
        input.setHint("Enter new " + selectedTrickCat + " name.");
        builder.setView(input);

        builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (selectedTrickCat == null) return;
                final Trick trick = new Trick();
                trick.setTrickName(input.getText().toString());
                trick.setUsername(ParseUser.getCurrentUser().getUsername());
                trick.setTrickCategory(selectedTrickCat);
                trick.setTrickList("default");
                trick.saveInBackground(new SaveCallback() {
                    public void done(ParseException e) {
                        if (e == null) {
                            mAdapter.insert(trick, 0);
                            updateData();
                        } else {
                            Toast.makeText(getActivity(), "Something went wrong!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
        builder.show();
    }

    public void updateData(){
        if (ParseUser.getCurrentUser() == null){
            Toast.makeText(getActivity(), "Must login to Facebook to use CustomTrix", Toast.LENGTH_SHORT).show();
            return;
        }

        mProgressDialog = new ProgressDialog(getActivity());
        mProgressDialog.setTitle("Custom Trix");
        mProgressDialog.setMessage("Loading...");
        mProgressDialog.setIndeterminate(false);
        mProgressDialog.show();

        ParseQuery<Trick> query = ParseQuery.getQuery(Trick.class);

        if (selectedTrickCat == "Direction") {
            query.whereEqualTo("username", ParseUser.getCurrentUser().getUsername());
            query.whereEqualTo("trickCategory", selectedTrickCat);
        }
        else if (selectedTrickCat == "Spin") {
            query.whereEqualTo("username", ParseUser.getCurrentUser().getUsername());
            query.whereEqualTo("trickCategory", selectedTrickCat);
        }
        else if (selectedTrickCat == "Grab") {
            query.whereEqualTo("username", ParseUser.getCurrentUser().getUsername());
            query.whereEqualTo("trickCategory", selectedTrickCat);
        }
        else
            return;

        query.findInBackground(new FindCallback<Trick>() {
            @Override
            public void done(List<Trick> tricks, ParseException error) {
                if(tricks != null){
                    mProgressDialog.dismiss();
                    mAdapter.clear();
                    mAdapter.addAll(tricks);

                }
                else{
                    mProgressDialog.dismiss();
                    Toast.makeText(getActivity(),"Something went wrong! Try again!",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.main, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        /*int id = item.getItemId();
        if (id == R.id.action_addTrick && selectedTrickCat != 0) {
            AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());


            String trickCatName;
            switch (selectedTrickCat){
                case 1:
                    trickCatName = "Stance";
                    break;
                case 2:
                    trickCatName = "Direction";
                    break;
                case 3:
                    trickCatName = "Spin";
                    break;
                case 4:
                    trickCatName = "Grab";
                    break;
                default:
                    trickCatName = "";
                    break;
            }

            alert.setTitle("Add New " + trickCatName);
            //alert.setMessage("Enter the name of the trick you would like to add to the <blank> List.");

            // Set an EditText view to get user input
            final EditText input = new EditText(getActivity());
            alert.setView(input);

            alert.setPositiveButton("Add Trick", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    String value = input.getText().toString();
                    // Do something with value!
                    Toast.makeText(getActivity(), value, Toast.LENGTH_SHORT).show();

                    switch (selectedTrickCat){
                        case 1:
//                            trickCatName = "Stance";
                            break;
                        case 2:
//                            trickCatName = "Direction";
                            break;
                        case 3:
//                            trickCatName = "Spin";
                            break;
                        case 4:
//                            trickCatName = "Grab";
                            break;
                        default:
//                            trickCatName = "";
                            break;
                    }
                }
            });

            alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    // Canceled.
                }
            });

            alert.show();

            return true;
        }*/
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {
        int height = (int) (60 * getView().getContext().getResources().getDisplayMetrics().density);
        LinearLayout.LayoutParams active = new LinearLayout.LayoutParams(0, height, 4);
        LinearLayout.LayoutParams inactive = new LinearLayout.LayoutParams(0, height, 3);

        switch(view.getId()) {
            case R.id.buttonDirection:
                //btnStance.setLayoutParams(inactive);
                btnDirection.setLayoutParams(active);
                btnSpin.setLayoutParams(inactive);
                btnGrab.setLayoutParams(inactive);
                selectedTrickCat = "Direction";
                updateData();
                break;
            case R.id.buttonSpin:
                //btnStance.setLayoutParams(inactive);
                btnDirection.setLayoutParams(inactive);
                btnSpin.setLayoutParams(active);
                btnGrab.setLayoutParams(inactive);
                selectedTrickCat = "Spin";
                updateData();
                break;
            case R.id.buttonGrab:
                //btnStance.setLayoutParams(inactive);
                btnDirection.setLayoutParams(inactive);
                btnSpin.setLayoutParams(inactive);
                btnGrab.setLayoutParams(active);
                selectedTrickCat = "Grab";
                updateData();
                break;
        }
    }
}
