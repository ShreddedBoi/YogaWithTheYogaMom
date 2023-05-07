package com.example.yogawiththeyogamom;

import static android.view.View.GONE;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;


public class SubscriptionListActivity extends AppCompatActivity {
    private static final String LOG_TAG = SubscriptionListActivity.class.getName();
    private final int gridNumber = 1;
    private FirebaseUser user;
    private FirebaseAuth mAuth;
    private FirebaseFirestore mFirestore;
    private NotificationHandling mNotificationHandling;
    private RecyclerView mRecyclerView;
    private ArrayList<SubscriptionType> mSubList;
    private SubscriptionTypeAdapter mAdapter;
    private FrameLayout redCircle;
    private TextView contentTextView;
    private boolean viewRow = true;
    private int basketItems = 0;
    private int queryLimit;


    private CollectionReference mItems;
    BroadcastReceiver powerReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (action == null) {
                return;
            }
            switch (action) {
                case Intent.ACTION_POWER_CONNECTED:
                    queryLimit = 7;
                    break;
                case Intent.ACTION_POWER_DISCONNECTED:
                    queryLimit = 5;
                    break;
            }
            queryData();
        }
    };
    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subscription_list);
        mAuth = FirebaseAuth.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            Log.d(LOG_TAG, "Authenticated user");
        } else {
            Log.d(LOG_TAG, "Unauthenticated user");
            finish();
        }

        mRecyclerView = findViewById(R.id.recyclerView);

        mRecyclerView.setLayoutManager(new GridLayoutManager(this, gridNumber));
        mSubList = new ArrayList<>();

        mAdapter = new SubscriptionTypeAdapter(this, mSubList);
        mRecyclerView.setAdapter(mAdapter);

        mFirestore = FirebaseFirestore.getInstance();
        mItems = mFirestore.collection("Items");

        queryData();

        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_POWER_CONNECTED);
        filter.addAction(Intent.ACTION_POWER_DISCONNECTED);
        this.registerReceiver(powerReceiver, filter);


        mNotificationHandling = new NotificationHandling(this);
    }

    private void queryData() {
        mSubList.clear();
        mItems.orderBy("basketedCounter", Query.Direction.DESCENDING).limit(7).get().addOnSuccessListener(queryDocumentSnapshots -> {
            for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                SubscriptionType type = document.toObject(SubscriptionType.class);
                type.setId(document.getId());
                mSubList.add(type);
            }

            if (mSubList.size() == 0) {
                initializeData();
                queryData();
            }
            mAdapter.notifyDataSetChanged();
        });
    }

    void deleteItem(SubscriptionType type) {
        DocumentReference ref = mItems.document(type._getId());
        ref.delete().addOnSuccessListener(success -> {
                    Log.d(LOG_TAG, "Item is deleted" + type._getId());
                })
                .addOnFailureListener(failure -> {
                    Toast.makeText(this, "Item " + type._getId() + " cannot be deleted", Toast.LENGTH_LONG).show();
                });

        queryData();
    }

    private void initializeData() {
        String[] subsList = getResources().getStringArray(R.array.subscription_types);
        String[] subsInfo = getResources().getStringArray(R.array.subscription_types_desc);
        String[] subsPrice = getResources().getStringArray(R.array.subs_type_prices);
        TypedArray subsImageResource = getResources().obtainTypedArray(R.array.subs_type_images);
        TypedArray subsRate = getResources().obtainTypedArray(R.array.subs_type_ratings);


        for (int i = 0; i < subsList.length; i++) {
            mItems.add(new SubscriptionType(subsList[i], subsInfo[i], subsPrice[i],
                    subsRate.getFloat(i, 0), subsImageResource.getResourceId(i, 0), 0));
        }
        subsImageResource.recycle();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.subs_types_list_menu, menu);
        final MenuItem menuItem = menu.findItem(R.id.search_bar);
        final SearchView searchView = (SearchView) menuItem.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                Log.d(LOG_TAG, s);
                mAdapter.getFilter().filter(s);
                return false;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.log_out_but:
                Log.d(LOG_TAG, "Clicked on Logout");
                FirebaseAuth.getInstance().signOut();
                finish();
                return true;
            case R.id.settingbutton:
                Log.d(LOG_TAG, "Clicked on settings");
                return true;
            case R.id.cart:
                Log.d(LOG_TAG, "Clicked on basket");
                return true;
            case R.id.view_selector:
                Log.d(LOG_TAG, "Clicked on view selection");

                if (viewRow) {
                    changeSpanCount(item, R.drawable.view_compact_24, 1);
                } else {
                    changeSpanCount(item, R.drawable.view_day, 2);
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void changeSpanCount(MenuItem item, int drawableId, int i) {
        viewRow = !viewRow;
        item.setIcon(drawableId);
        GridLayoutManager layoutManager = (GridLayoutManager) mRecyclerView.getLayoutManager();
        layoutManager.setSpanCount(i);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        final MenuItem alertMenuItem = menu.findItem(R.id.cart);
        FrameLayout rootView = (FrameLayout) alertMenuItem.getActionView();

        redCircle = (FrameLayout) rootView.findViewById(R.id.view_alert_red_circle);
        contentTextView = (TextView) rootView.findViewById(R.id.view_alert_count_textview);

        rootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onOptionsItemSelected(alertMenuItem);
            }
        });
        return super.onPrepareOptionsMenu(menu);
    }

    public void updateAlerts(SubscriptionType type) {
        basketItems = (basketItems + 1);
        if (0 < basketItems) {
            contentTextView.setText(String.valueOf(basketItems));
        } else {
            contentTextView.setText(String.valueOf(basketItems));
        }
        redCircle.setVisibility((basketItems > 0) ? View.VISIBLE : GONE);

        mItems.document(type._getId()).update("basketedCounter", type.getBasketedCounter() + 1)
                .addOnFailureListener(failure -> {
                    Toast.makeText(this, "Item " + type._getId() + " cannot be modified", Toast.LENGTH_LONG).show();

                });

        mNotificationHandling.send(type.getName());
        queryData();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(powerReceiver);
    }
}
