package com.example.nikolajcolic.jazgobar;

import android.Manifest;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.DataAll;
import com.example.Lokacija;
import com.example.nikolajcolic.jazgobar.eventbus.MessageEventUpdateLocation;
import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.login.LoginManager;
import com.facebook.login.widget.ProfilePictureView;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONObject;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class ActivityZacetna extends AppCompatActivity {
    private static final String TAG = ActivityZacetna.class.getSimpleName();
    ApplicationMy app;
    private FloatingActionButton fab; //TODO https://github.com/Clans/FloatingActionButton
    //Spinner mySpin;
    private RecyclerView mRecyclerView;
    private AdapterLokacija mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private int index=-1;
    private String id_user="nikolaj.colic@student.um.si";
    Location mLocation;
    ProfilePictureView imageProfile;
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.my_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                // User chose the "Settings" item, show the app settings UI...
                startActivity(new Intent(this,ActivityMySettings.class));
                return true;

            case R.id.action_sort:
                //app.sortUpdate();
                app.sortChangeAndUpdate();
                mAdapter.notifyDataSetChanged();
                return true;

            default:
                return super.onOptionsItemSelected(item);

        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEventUpdateLocation event) {
        Log.i("ActivityZacetna","MessageEventUpdateLocation ");
        mLocation = event.getM();
        mAdapter.setLastLocation(mLocation);
        fab.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorAccent)));
        //podatki();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        app = (ApplicationMy)getApplication();//dodano
        setContentView(R.layout.activity_zacetna);
        mRecyclerView = (RecyclerView) findViewById(R.id.myrecycleview);
        imageProfile = (ProfilePictureView) findViewById(R.id.imageProfile);
        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        podatki();

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        // specify an adapter (see also next example)
        //app = (ApplicationMy) getApplication();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        registerForContextMenu(mRecyclerView);
        setDeleteOnSwipe(mRecyclerView);
        setUpAnimationDecoratorHelper();
        /*mySpin= (Spinner) findViewById(R.id.spinner);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);*/

        mLocation=null;
        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setBackgroundColor(getResources().getColor(R.color.colorGray));
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Lokacija l = app.getTestLocation();
                //Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                 //       .setAction("Action", null).show();
                /*Intent i = new Intent(getBaseContext(), ActivityLocation.class);
                i.putExtra(DataAll.LOKACIJA_ID, app.getNewLocation(21.2,111.2).getId());
                startActivity(i);*/
                if (mLocation==null) {
                    //                   Snackbar.make(view, getResources().getText(R.string.add_new_location_no_location), Snackbar.LENGTH_LONG).setAction("Action", null).show();
                    Snackbar.make(view, getResources().getText(R.string.add_new_location_no_location), Snackbar.LENGTH_LONG).show();
                }else {
                    Intent i = new Intent(getBaseContext(), ActivityLocation.class);
                    i.putExtra(DataAll.LOKACIJA_ID, ActivityLocation.NEW_LOCATION_ID);
                    startActivity(i);
                }
            }
        });

    }
    private void getPermissions() {
        MultiplePermissionsListener my  = new MultiplePermissionsListener() {
            @Override public void onPermissionsChecked(MultiplePermissionsReport report) {
                if (!report.areAllPermissionsGranted()) {
                    new android.app.AlertDialog.Builder(ActivityZacetna.this)
                            .setTitle(getString(R.string.permission_must_title))
                            .setMessage(getString(R.string.permission_must))
                            .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                @Override public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    ActivityZacetna.this.finish(); //end app!
                                }
                            })
                            .setIcon(R.drawable.goba)
                            .show();
                }}
            @Override public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                token.continuePermissionRequest();
            }
        };


        Dexter.withActivity(this)
                .withPermissions(
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.CAMERA
                ).withListener(my).check();
    }

    @Override
    protected void onStart() {
        super.onStart();
        getPermissions();

    }
    //http://stackoverflow.com/questions/27293960/swipe-to-dismiss-for-recyclerview
    public void setDeleteOnSwipe(final RecyclerView mRecyclerView) {

        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(final RecyclerView.ViewHolder viewHolder, int direction) {
                Lokacija tmp = app.getAll().getLocation(viewHolder.getAdapterPosition());
                if (tmp.getIdUser().equals(id_user)) {
                    DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which){
                                case DialogInterface.BUTTON_POSITIVE:
                                    app.removeLocationByPosition(viewHolder.getAdapterPosition());
                                    app.save();
                                    podatki();
                                    //mRecyclerView.getAdapter().notifyDataSetChanged();
                                    break;

                                case DialogInterface.BUTTON_NEGATIVE:
                                    mRecyclerView.getAdapter().notifyDataSetChanged();
                                    break;
                            }
                            // mRecyclerView.getAdapter().notifyDataSetChanged();
                        }
                    };

                    android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(ActivityZacetna.this);
                    builder.setTitle("Brisanje lokacije");
                    builder.setMessage("Ali res želiš izbrisati?").setPositiveButton("Da", dialogClickListener)
                            .setNegativeButton("Ne", dialogClickListener)
                    ;
                    builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface dialogInterface) {
                            mRecyclerView.getAdapter().notifyDataSetChanged();
                        }
                    });
                    builder.show();
                }else{
                Toast.makeText(ActivityZacetna.this, "To ni tvoja goba!", Toast.LENGTH_SHORT).show();
                    mRecyclerView.getAdapter().notifyDataSetChanged();
                }
            }

            boolean initiated;
            Drawable background;
            Drawable xMark;
            int xMarkMargin;
            private void init() {
                background = new ColorDrawable(Color.RED);
                xMark = ContextCompat.getDrawable(ActivityZacetna.this, R.drawable.ic_clear_24dp);
                xMark.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
                xMarkMargin = (int) ActivityZacetna.this.getResources().getDimension(R.dimen.ic_clear_margin);
                initiated = true;
            }
            @Override
            public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                View itemView = viewHolder.itemView;

                // not sure why, but this method get's called for viewholder that are already swiped away
                if (viewHolder.getAdapterPosition() == -1) {
                    // not interested in those
                    return;
                }

                if (!initiated) {
                    init();
                }

                // draw red background
                background.setBounds(itemView.getRight() + (int) dX, itemView.getTop(), itemView.getRight(), itemView.getBottom());
                background.draw(c);

                // draw x mark
                int itemHeight = itemView.getBottom() - itemView.getTop();
                int intrinsicWidth = xMark.getIntrinsicWidth();
                int intrinsicHeight = xMark.getIntrinsicWidth();

                int xMarkLeft = itemView.getRight() - xMarkMargin - intrinsicWidth;
                int xMarkRight = itemView.getRight() - xMarkMargin;
                int xMarkTop = itemView.getTop() + (itemHeight - intrinsicHeight)/2;
                int xMarkBottom = xMarkTop + intrinsicHeight;
                xMark.setBounds(xMarkLeft, xMarkTop, xMarkRight, xMarkBottom);

                xMark.draw(c);

                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
        };

        ItemTouchHelper.SimpleCallback simpleItemTouchCallback2 = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(final RecyclerView.ViewHolder viewHolder, int direction) {
                Lokacija tmp = app.getAll().getLocation(viewHolder.getAdapterPosition());
                if (tmp.getIdUser().equals(id_user)) {
                    if(!tmp.getDeli())
                    {

                        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which) {
                                    case DialogInterface.BUTTON_POSITIVE:
                                        int swipedPosition = viewHolder.getAdapterPosition();
                                        app.getAll().setLokacijaDeli(swipedPosition);
                                        app.save();
                                        mRecyclerView.getAdapter().notifyDataSetChanged();
                                        break;

                                    case DialogInterface.BUTTON_NEGATIVE:
                                        mRecyclerView.getAdapter().notifyDataSetChanged();
                                        break;
                                }
                                // mRecyclerView.getAdapter().notifyDataSetChanged();
                            }
                        };

                        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(ActivityZacetna.this);
                        builder.setTitle("Deljenje lokacije");
                        builder.setMessage("Ali res želiš deliti?").setPositiveButton("Da", dialogClickListener)
                                .setNegativeButton("Ne", dialogClickListener)
                        ;
                        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
                            @Override
                            public void onCancel(DialogInterface dialogInterface) {
                                mRecyclerView.getAdapter().notifyDataSetChanged();
                            }
                        });
                        builder.show();
                    }else{
                        Toast.makeText(ActivityZacetna.this, "To gobo si že delil!", Toast.LENGTH_SHORT).show();
                        mRecyclerView.getAdapter().notifyDataSetChanged();
                    }
                }else{
                    Toast.makeText(ActivityZacetna.this, "To ni tvoja goba!", Toast.LENGTH_SHORT).show();
                    mRecyclerView.getAdapter().notifyDataSetChanged();
                }
            }
            boolean initiated;
            Drawable background;
            Drawable xMark;
            int xMarkMargin;
            private void init() {
                background = new ColorDrawable(Color.BLUE);
                xMark = ContextCompat.getDrawable(ActivityZacetna.this, R.drawable.share_24);
                xMark.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
                xMarkMargin = (int) ActivityZacetna.this.getResources().getDimension(R.dimen.ic_clear_margin);
                initiated = true;
            }
            @Override
            public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                View itemView = viewHolder.itemView;

                // not sure why, but this method get's called for viewholder that are already swiped away
                if (viewHolder.getAdapterPosition() == -1) {
                    // not interested in those
                    return;
                }

                if (!initiated) {
                    init();
                }

                // draw blue background
                background.setBounds(itemView.getLeft(), itemView.getTop(), itemView.getLeft()+ (int) dX, itemView.getBottom());
                background.draw(c);

                // draw x mark
                int itemHeight = itemView.getBottom() - itemView.getTop();
                int intrinsicWidth = xMark.getIntrinsicWidth();
                int intrinsicHeight = xMark.getIntrinsicWidth();

                int xMarkLeft = itemView.getLeft() + xMarkMargin;
                int xMarkRight = itemView.getLeft() + xMarkMargin + intrinsicWidth;
                int xMarkTop = itemView.getTop() + (itemHeight - intrinsicHeight)/2;
                int xMarkBottom = xMarkTop + intrinsicHeight;
                xMark.setBounds(xMarkLeft, xMarkTop, xMarkRight, xMarkBottom);

                xMark.draw(c);

                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(mRecyclerView);
        ItemTouchHelper itemTouchHelper2 = new ItemTouchHelper(simpleItemTouchCallback2);
        itemTouchHelper2.attachToRecyclerView(mRecyclerView);
    }
    private void setUpAnimationDecoratorHelper() {
        mRecyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {

            // we want to cache this and not allocate anything repeatedly in the onDraw method
            Drawable background;
            boolean initiated;

            private void init() {
                background = new ColorDrawable(Color.RED);
                initiated = true;
            }

            @Override
            public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {

                if (!initiated) {
                    init();
                }

                // only if animation is in progress
                if (parent.getItemAnimator().isRunning()) {

                    // some items might be animating down and some items might be animating up to close the gap left by the removed item
                    // this is not exclusive, both movement can be happening at the same time
                    // to reproduce this leave just enough items so the first one and the last one would be just a little off screen
                    // then remove one from the middle

                    // find first child with translationY > 0
                    // and last one with translationY < 0
                    // we're after a rect that is not covered in recycler-view views at this point in time
                    View lastViewComingDown = null;
                    View firstViewComingUp = null;

                    // this is fixed
                    int left = 0;
                    int right = parent.getWidth();

                    // this we need to find out
                    int top = 0;
                    int bottom = 0;

                    // find relevant translating views
                    int childCount = parent.getLayoutManager().getChildCount();
                    for (int i = 0; i < childCount; i++) {
                        View child = parent.getLayoutManager().getChildAt(i);
                        if (child.getTranslationY() < 0) {
                            // view is coming down
                            lastViewComingDown = child;
                        } else if (child.getTranslationY() > 0) {
                            // view is coming up
                            if (firstViewComingUp == null) {
                                firstViewComingUp = child;
                            }
                        }
                    }

                    if (lastViewComingDown != null && firstViewComingUp != null) {
                        // views are coming down AND going up to fill the void
                        top = lastViewComingDown.getBottom() + (int) lastViewComingDown.getTranslationY();
                        bottom = firstViewComingUp.getTop() + (int) firstViewComingUp.getTranslationY();
                    } else if (lastViewComingDown != null) {
                        // views are going down to fill the void
                        top = lastViewComingDown.getBottom() + (int) lastViewComingDown.getTranslationY();
                        bottom = lastViewComingDown.getBottom();
                    } else if (firstViewComingUp != null) {
                        // views are coming up to fill the void
                        top = firstViewComingUp.getTop();
                        bottom = firstViewComingUp.getTop() + (int) firstViewComingUp.getTranslationY();
                    }

                    background.setBounds(left, top, right, bottom);
                    background.draw(c);

                }
                super.onDraw(c, parent, state);
            }

        });
    }
    //longclick izbira
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        int clickedItemPosition = item.getOrder();
        // do something!
        Lokacija tmp = app.getAll().getLocation(clickedItemPosition);
        if (tmp.getIdUser().equals(id_user)) {
            String izbira = item.toString();
            if (izbira.equals("Uredi")) {
                System.out.println("edit");
                System.out.println(item.toString() + " novo: " + tmp.getId());
                    Intent i = new Intent(getBaseContext(), ActivityLocation.class);
                    i.putExtra(DataAll.LOKACIJA_ID, tmp.getId());
                    startActivity(i);
            } else if (izbira.equals("Deli z vsemi")) {
                System.out.println("share");
                if (!tmp.getDeli()) {
                    Toast.makeText(this, "Uspešno si delil svojo gobo!", Toast.LENGTH_SHORT).show();
                    app.getAll().setLokacijaDeli(clickedItemPosition);
                    app.save();
                } else {
                    Toast.makeText(this, "Tvojga goba se že deli!", Toast.LENGTH_SHORT).show();
                }
            } else if (izbira.equals("Izbriši")) {
               /* app.getLokacijaAll().remove(clickedItemPosition);
                mRecyclerView.removeViewAt(clickedItemPosition);
                mAdapter.notifyItemRemoved(clickedItemPosition);
                mAdapter.notifyItemRangeChanged(clickedItemPosition, app.getLokacijaAll().size());
                mAdapter.notifyDataSetChanged();
                app.save();*/
                index = clickedItemPosition;
                createAndShowAlertDialog();
            }
        }else{
                Toast.makeText(this, "To ni tvoja goba!", Toast.LENGTH_SHORT).show();
             }
        return super.onContextItemSelected(item);
    }
    private void createAndShowAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Ali želiš res izbrisati?");
        builder.setPositiveButton("Da", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                //TODO
                app.getLokacijaAll().remove(index);
                mRecyclerView.removeViewAt(index);
                mAdapter.notifyItemRemoved(index);
                mAdapter.notifyItemRangeChanged(index, app.getLokacijaAll().size());
                mAdapter.notifyDataSetChanged();
                app.save();
                dialog.dismiss();
            }
        });
        builder.setNegativeButton("Ne", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                //TODO
                dialog.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }
    /*@Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        // Inflate Menu from xml resource
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.context_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        System.out.println("neki je kliko"+item.toString());
        String izbira = item.toString();
        if(izbira.equals("Edit")){
            System.out.println("edit");
        }else if(izbira.equals("Share")){
            System.out.println("share");
        }else if(izbira.equals("Delete")){
            System.out.println("delete");
        }
        ContextMenuRecyclerView.RecyclerContextMenuInfo info = (ContextMenuRecyclerView.RecyclerContextMenuInfo) item.getMenuInfo();
        System.out.println("novo: "+ info.position);

        return false;
    }*/
    @Override
    protected void onResume() {
        super.onResume();
        //mLocation = app.getLastLocation();
        if (mLocation==null) {

            fab.setBackgroundColor(Color.BLUE);
        }
        if (!EventBus.getDefault().isRegistered(this)) EventBus.getDefault().register(this);
        //  checkPermissions();
        app.sortUpdate();
        mAdapter.notifyDataSetChanged();

        startService(new Intent(app, GPSTracker.class));//start service

        fab.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorGray)));
        Bundle extras = getIntent().getExtras();
        imageProfile.setProfileId(extras.getString("USER_ID"));

        podatki();

    }
    public void podatki(){
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String izbranaGoba = sp.getString("lp_moje_gobe","0");
        String izbranaRazdalja = sp.getString("lp_razdalja_gob","11");
        //Toast.makeText(ActivityZacetna.this, "ALO: "+izbranaGoba, Toast.LENGTH_SHORT).show();
        if(mLocation!=null){
            mAdapter = new AdapterLokacija(app.getAllUser(id_user,Integer.parseInt(izbranaGoba),Integer.parseInt(izbranaRazdalja),mLocation.getLatitude(),mLocation.getLongitude()), this);
        }else{
            int index=Integer.parseInt(izbranaGoba);
            int razdalja=Integer.parseInt(izbranaRazdalja);

            mAdapter = new AdapterLokacija(app.getAllUser(id_user,index,razdalja,-1,-1), this);
        }
        mRecyclerView.setAdapter(mAdapter);
    }
    @Override
    protected void onPause() {
        super.onPause();

    }

    @Override
    protected void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
        // stopService(new Intent(ActivityZacetna.this, GPSTracker.class));
    }
    public Location getLocation() {
        return mLocation;
    }

    public void openMap(View v){
        Intent i = new Intent(getBaseContext(), ActivityMapa.class);
        startActivity(i);
    }
    public void onClickOdjava(View v){
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        LoginManager.getInstance().logOut();
                        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                        SharedPreferences.Editor editor =  sharedPreferences.edit();
                        editor.putString("token",null);
                        editor.putString("user_id",null);
                        editor.putLong("expires",0);
                        editor.commit();
                        Intent i = new Intent(getBaseContext(), ActivityLogin.class);
                        startActivity(i);
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        break;
                }
                // mRecyclerView.getAdapter().notifyDataSetChanged();
            }
        };
        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(ActivityZacetna.this);
        builder.setTitle("Odjava");
        builder.setMessage("Ali se res želiš odjaviti?").setPositiveButton("Da", dialogClickListener)
                .setNegativeButton("Ne", dialogClickListener)
        ;
        builder.show();
    }
    public static Intent odpriFB(Context context, SharedPreferences sp) {

        String user_id = sp.getString("user_id",null);
        try {
            context.getPackageManager()
                    .getPackageInfo("com.facebook.katana", 0); //Checks if FB is even installed.
            return new Intent(Intent.ACTION_VIEW,
                    Uri.parse("fb://profile/"+user_id)); //Trys to make intent with FB's URI
        } catch (Exception e) {
            return new Intent(Intent.ACTION_VIEW,
                    Uri.parse("http://facebook.com/profile.php?id="+user_id)); //catches and opens a url to the desired page
        }
    }
}
