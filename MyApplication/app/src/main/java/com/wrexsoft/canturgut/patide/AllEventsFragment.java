package com.wrexsoft.canturgut.patide;

import android.app.SearchManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.CalendarContract;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.wang.avi.AVLoadingIndicatorView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import me.everything.providers.android.calendar.CalendarProvider;


/**
 * A simple {@link Fragment} subclass.
 */
public class AllEventsFragment extends Fragment {

    View view;

    private ListView listViewEvents;
    protected ArrayList<String> listEventIds = new ArrayList<>();

    ArrayList<HashMap<String, String>> listOfEvents = new ArrayList<HashMap<String, String>>();
    SimpleAdapter adapterListEvents;

    AVLoadingIndicatorView avi;
    String fbuserId;
    DatabaseReference dref;
    SharedPreferences settings;
    HashMap<String, String> holder;

    Drawable greenDrawable;

    public AllEventsFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_all_events, container, false);

        listEventIds.clear();
        listOfEvents.clear();
        avi = (AVLoadingIndicatorView) view.findViewById(R.id.avi);

        greenDrawable = getResources().getDrawable(R.drawable.green, getActivity().getTheme());

        settings = PreferenceManager.getDefaultSharedPreferences(getActivity());
        fbuserId = settings.getString("FbUserId", "userId");

        listViewEvents = (ListView) view.findViewById(R.id.listViewAllEvents);
        listViewEvents.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String eventId = listEventIds.get(position);
                Bundle bundle = new Bundle();
                bundle.putString("eventId", eventId);
                FragmentManager fm = getFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();
                EventDetailFragment eventDetailFragment = new EventDetailFragment();
                eventDetailFragment.setArguments(bundle);
                ft.addToBackStack(null);
                ft.replace(R.id.main_frame, eventDetailFragment);
                ft.commit();
            }
        });

        (view.findViewById(R.id.sortByDate)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "Date", Toast.LENGTH_SHORT).show();
                ApplicationCalculations.sortArray();
                appySort();
            }
        });

        (view.findViewById(R.id.SortbyName)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "Name", Toast.LENGTH_SHORT).show();
                ApplicationCalculations.sortbyName();
                appySort();
            }
        });


        (view.findViewById(R.id.sortByDate)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(getContext(),"hello",Toast.LENGTH_SHORT).show();
                ApplicationCalculations.sortArray();
                appySort();
            }
        });

        (view.findViewById(R.id.SortbyName)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(getContext(),"name",Toast.LENGTH_SHORT).show();
                ApplicationCalculations.sortbyName();
                appySort();
            }
        });

        (view.findViewById(R.id.sortByPriority)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(getContext(),"pr",Toast.LENGTH_SHORT).show();
                ApplicationCalculations.sortbyPriority();
                appySort();
            }
        });

        new ApplicationCalculations(getContext());
        dref = FirebaseDatabase.getInstance().getReference();
        EventsLoader friendsLoader = new EventsLoader(this);
        friendsLoader.execute();
        return view;
    }

    List<me.everything.providers.android.calendar.Calendar> calenrdars;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        boolean isEventCal = false;

        try {

            if (settings.getBoolean("isImportCalendar", isEventCal)) {

                Utility.readCalendarEvent(getContext());

            } else {

            }

        } catch (Exception e) {

        }
    }

    public void appySort() {

        listEventIds.clear();
        listOfEvents.clear();
        for (int i = 0; i < ApplicationCalculations.getSize(); i++) {
            HashMap<String, String> holder = new HashMap<>();
            listEventIds.add(ApplicationCalculations.getListOfEventIDs()[i]);
            holder.put("Content", ApplicationCalculations.getListOfEventNames()[i]);
            holder.put("Time", ApplicationCalculations.getListOfEventTimeLeft()[i]);

            if (Integer.parseInt(ApplicationCalculations.getListOfEventPriority()[i]) == 0 || Integer.parseInt(ApplicationCalculations.getListOfEventPriority()[i]) == 1) {

                holder.put("Image", Integer.toString(R.drawable.green));

            } else if (Integer.parseInt(ApplicationCalculations.getListOfEventPriority()[i]) == 2 || Integer.parseInt(ApplicationCalculations.getListOfEventPriority()[i]) == 3) {

                holder.put("Image", Integer.toString(R.drawable.yellow));

            } else {

                holder.put("Image", Integer.toString(R.drawable.red));

            }

            listOfEvents.add(holder);
        }

        adapterListEvents.notifyDataSetChanged();
    }

    @Override
    public void onStart() {
        super.onStart();

        adapterListEvents = new SimpleAdapter(getActivity(),
                listOfEvents,
                R.layout.list_view,
                new String[]{"Content", "Time", "Image"},
                new int[]{R.id.content, R.id.time, R.id.image_prio});

        listViewEvents.setAdapter(adapterListEvents);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        inflater.inflate(R.menu.menu_search, menu);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.search));
        SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {
                //lvAllUsers.setEnabled(true);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                //lvAllUsers.setEnabled(false);
                adapterListEvents.getFilter().filter(newText);
                return false;
            }
        });
    }

    private class EventsLoader extends AsyncTask<Void, Void, Void> {

        AllEventsFragment allEventsFragment;

        private EventsLoader(AllEventsFragment allEventsFragment) {
            this.allEventsFragment = allEventsFragment;
        }

        @Override
        protected void onPreExecute() {
            startAnim();
            allEventsFragment.listViewEvents.setVisibility(View.GONE);
        }

        @Override
        protected Void doInBackground(Void... params) {

            try {

                if (checkConnection(getActivity().getApplicationContext())) {

                    Cursor myCursor = MainMenuActivity.mydb.getKuyrukData();

                    int i = 0;

                    while (myCursor.moveToNext()) {

                        String eventID = myCursor.getString(1);
                        String type = myCursor.getString(2);
                        Cursor otherDate = MainMenuActivity.mydb.getSQLiteData();

                        while (otherDate.moveToNext()) {

                            if (otherDate.getString(1).equals(eventID)) {

                                HashMap<String, Object> eventDetails = new HashMap<>();
                                eventDetails.put("eventname", otherDate.getString(5));
                                eventDetails.put("estimatedtime", otherDate.getString(4));
                                eventDetails.put("comments", otherDate.getString(2));
                                eventDetails.put("date", otherDate.getString(3));
                                eventDetails.put("priority", otherDate.getString(6));
                                if (type.equals("insert")) {
                                    dref.child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Events").push().setValue(eventDetails);
                                } else if (type.equals("update")) {
                                    dref.child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Events").child(otherDate.getString(1)).setValue(eventDetails);
                                } else if (type.equals("delete")) {
                                    dref.child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Events").child(otherDate.getString(1)).removeValue();
                                }
                                Log.d("databaseInsert", eventDetails.get("eventname") + " is added since eventID is " + eventID + " and id in database " + otherDate.getString(1));
                                MainMenuActivity.mydb.removeFromKuyruk(eventID);
                                MainMenuActivity.mydb.removeEvent(eventID);
                            }
                        }
                    }

                    dref.child("Users").child(fbuserId).child("Events").addChildEventListener(new ChildEventListener() {
                        @Override
                        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                            try {
                                listEventIds.add(dataSnapshot.getKey());
                                MainMenuActivity.mydb.insertData(dataSnapshot.getKey().toString(), dataSnapshot.child("comments").getValue().toString(), dataSnapshot.child("date").getValue().toString(), dataSnapshot.child("estimatedtime").getValue().toString(), dataSnapshot.child("eventname").getValue().toString(), dataSnapshot.child("priority").getValue().toString());
                                adapterListEvents.notifyDataSetChanged();

                            } catch (Exception e) {
                            }
                        }

                        @Override
                        public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                            adapterListEvents.notifyDataSetChanged();
                        }

                        @Override
                        public void onChildRemoved(DataSnapshot dataSnapshot) {
                            MainMenuActivity.mydb.removeEvent(dataSnapshot.getKey().toString());
                            adapterListEvents.notifyDataSetChanged();
                        }

                        @Override
                        public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                            adapterListEvents.notifyDataSetChanged();
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            adapterListEvents.notifyDataSetChanged();
                        }
                    });
                }

            } catch (Exception e) {

                Log.e("Errorr::", "e.toString()");
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void res) {
            runTimer();
        }
    }

    private void runTimer() {
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                ApplicationCalculations.fillArray();
                final Handler handler2 = new Handler();
                handler2.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        ListUpdate();
                    }
                }, 2000);
                stopAnim();
                listViewEvents.setVisibility(View.VISIBLE);
                adapterListEvents.notifyDataSetChanged();
            }
        }, 3500);

        adapterListEvents.notifyDataSetChanged();
    }

    public void ListUpdate() {
        Log.d("listviewsee", "Size: " + ApplicationCalculations.getSize());
        for (int i = 0; i < ApplicationCalculations.getSize(); i++) {
            HashMap<String, String> holder = new HashMap<>();
            listEventIds.add(ApplicationCalculations.getListOfEventIDs()[i]);
            Log.d("listviewsee", "ListUpdate: " + ApplicationCalculations.getListOfEventNames()[i] + " and i= " + i);
            Log.d("COUNTER_MEASURE", "counter: " + i);
            holder.put("Content", ApplicationCalculations.getListOfEventNames()[i]);
            holder.put("Time", ApplicationCalculations.getListOfEventTimeLeft()[i]);

            if (Integer.parseInt(ApplicationCalculations.getListOfEventPriority()[i]) == 0 || Integer.parseInt(ApplicationCalculations.getListOfEventPriority()[i]) == 1) {

                holder.put("Image", Integer.toString(R.drawable.green));

            } else if (Integer.parseInt(ApplicationCalculations.getListOfEventPriority()[i]) == 2 || Integer.parseInt(ApplicationCalculations.getListOfEventPriority()[i]) == 3) {

                holder.put("Image", Integer.toString(R.drawable.yellow));

            } else {

                holder.put("Image", Integer.toString(R.drawable.red));

            }

            listOfEvents.add(holder);
        }

        adapterListEvents.notifyDataSetChanged();
    }

    private void startAnim() {
        avi.show();
    }

    private void stopAnim() {
        avi.smoothToHide();
    }

    public static boolean checkConnection(Context context) {
        final ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetworkInfo = connMgr.getActiveNetworkInfo();

        if (activeNetworkInfo != null) {

            if (activeNetworkInfo.getType() == ConnectivityManager.TYPE_WIFI) {

                return true;
            } else if (activeNetworkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {

                return true;
            }
        }
        return false;
    }


}



