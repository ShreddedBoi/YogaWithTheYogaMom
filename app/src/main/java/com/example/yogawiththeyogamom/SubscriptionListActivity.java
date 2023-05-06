package com.example.yogawiththeyogamom;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.MenuItemCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.View;
import android.widget.FrameLayout;
import android.widget.SearchView;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;


public class SubscriptionListActivity extends AppCompatActivity {
    private static final String LOG_TAG = SubscriptionListActivity.class.getName();
    private FirebaseUser user;
    private FirebaseAuth mAuth;

    private RecyclerView mRecyclerView;
    private ArrayList<SubscriptionType> mSubList;
    private SubscriptionTypeAdapter mAdapter;

    private FrameLayout redCircle;
    private TextView contentTextView;

    private int gridNumber= 1;
    private boolean viewRow = true;
    private int basketItems = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subscription_list);
        mAuth = FirebaseAuth.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();
        if(user != null){
            Log.d(LOG_TAG, "Authenticated user");
        }else {
            Log.d(LOG_TAG, "Unauthenticated user");
            finish();
        }

        mRecyclerView = findViewById(R.id.recyclerView);

        mRecyclerView.setLayoutManager(new GridLayoutManager(this, gridNumber));
        mSubList = new ArrayList<>();

        mAdapter = new SubscriptionTypeAdapter(this, mSubList);
        mRecyclerView.setAdapter(mAdapter);

        initializeData();


    }

    private void initializeData() {
        String[] subsList = getResources().getStringArray(R.array.subscription_types);
        String[] subsInfo = getResources().getStringArray(R.array.subscription_types_desc);
        String[] subsPrice = getResources().getStringArray(R.array.subs_type_prices);
        TypedArray subsImageResource = getResources().obtainTypedArray(R.array.subs_type_images);
        TypedArray subsRate = getResources().obtainTypedArray(R.array.subs_type_ratings);

        mSubList.clear();

        for (int i = 0; i< subsList.length; i++){
            mSubList.add(new SubscriptionType(subsList[i], subsInfo[i],
                    subsPrice[i], subsRate.getFloat(i, 0), subsImageResource.getResourceId(i,0)));
        }
        subsImageResource.recycle();
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.subs_types_list_menu, menu);
        final MenuItem menuItem = menu.findItem(R.id.search_bar);
        final SearchView searchView = (SearchView) menuItem.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener(){

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
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
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

                if(viewRow){
                    changeSpanCount(item, R.drawable.view_compact_24,1);
                }else {
                    changeSpanCount(item, R.drawable.view_day,2);
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
    public boolean onPrepareOptionsMenu(Menu menu){
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

    public void updateAlerts(){
        basketItems = (basketItems + 1);
            if(0 < basketItems){
                contentTextView.setText(String.valueOf(basketItems));
            }else {
                contentTextView.setText(String.valueOf(basketItems));
            }
        }
    }
