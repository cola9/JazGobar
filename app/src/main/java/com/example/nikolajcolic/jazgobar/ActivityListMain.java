package com.example.nikolajcolic.jazgobar;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.example.DataAll;
import com.example.Lokacija;

public class ActivityListMain extends AppCompatActivity {
    /*private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    ApplicationMy app;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_main);
        mRecyclerView = (RecyclerView) findViewById(R.id.myrecycleview);
        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        // specify an adapter (see also next example)
        app = (ApplicationMy) getApplication();
        mAdapter = new AdapterLokacija(app.getAll(), this);
        mRecyclerView.setAdapter(mAdapter);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Lokacija l = app.getTestLocation();
                Intent i = new Intent(getBaseContext(), ActivityLocation.class);
                i.putExtra(DataAll.LOKACIJA_ID, ActivityLocation.NEW_LOCATION_ID);
                startActivity(i);
            }
        });
    }
    @Override
    protected void onResume() {
        super.onResume();
        mAdapter.notifyDataSetChanged();
    }
    public void openZacetna(View v){
        Intent i = new Intent(getBaseContext(), ActivityZacetna.class);
        startActivity(i);
    }*/
}
