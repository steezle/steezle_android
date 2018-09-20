package com.steezle.e_com.view;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.support.v4.app.Fragment;
import android.view.View;

import com.steezle.e_com.R;
import com.steezle.e_com.fragments.CategoryFragment;
import com.steezle.e_com.fragments.FavouriteFragment;
import com.steezle.e_com.fragments.OrderListFragment;
import com.steezle.e_com.model.User;
import com.steezle.e_com.networking.AppSharedPreferences;

import java.util.ArrayList;
import java.util.Stack;

public class TabActivity extends AppCompatActivity {

    private Fragment activeFragment;
    private Stack<Fragment> fragmentStack;
    private BottomNavigationView navigationView;
    private Intent intent;
    private ArrayList<String> titleList;
    private Activity dialog;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_tab );

        fragmentStack = new Stack<Fragment>();
        titleList = new ArrayList<>();
        intent = getIntent();
        user = AppSharedPreferences.getSharePreference( getApplicationContext() ).getUser();

        navigationView = (BottomNavigationView) findViewById( R.id.navigationView );
//        navigationView.setItemTextColor( ColorStateList.valueOf( getResources().getColor( R.color.black ) ));
//        navigationView.setItemIconTintList( ColorStateList.valueOf( getResources().getColor( R.color.black ) ));
        BottomNavigationViewHelper.disableShiftMode( navigationView );

        navigationView.setOnNavigationItemSelectedListener
                ( new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.action_item1:
                                loadFragment( CategoryFragment.newInstance(), false, "1" );
                                break;

                            case R.id.action_item2:
                                try {
                                    if (user.getUser_id().equalsIgnoreCase( "" )) {
                                        openDialog( "go to My Steez" );
                                    } else {
                                        loadFragment( FavouriteFragment.newInstance(), false, "" );

                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    openDialog( "go to My Steez" );
                                }

                                break;

                            case R.id.action_item3:
                                try {
                                    if (user.getUser_id().equalsIgnoreCase( "" )) {
                                        openDialog( "Place an Order" );
                                    } else {
                                        loadFragment( OrderListFragment.newInstance(), false, "" );

                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    openDialog( "Place an Order" );
                                }
                                break;

                            case R.id.action_item4:
                                try {
                                    if (user.getUser_id().equalsIgnoreCase( "" )) {
                                        openDialog( "go to Profile Page" );

                                    } else {
                                        loadFragment( SettingFragment.newInstance(), false, "" );

                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    openDialog( "go to Profile Page" );
                                }

                                break;

                            default:
                                loadFragment( CategoryFragment.newInstance(), false, "1" );
                                break;
                        }
                        return true;
                    }
                } );

        loadFragment( CategoryFragment.newInstance(), false, "1" );
        try {
            if (user.getUser_id().equalsIgnoreCase( "" )) {
                navigationView.setSelectedItemId( R.id.action_item1 );
            } else {
                if (intent.getStringExtra( "From" ) != null && intent.getStringExtra( "From" ).equals( "user" )) {
                    navigationView.setSelectedItemId( R.id.action_item4 );
                } else if (intent.getStringExtra( "From" ) != null && intent.getStringExtra( "From" ).equals( "order" )) {
                    navigationView.setSelectedItemId( R.id.action_item3 );
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            navigationView.setSelectedItemId( R.id.action_item1 );
        }


    }

    /**
     * Load fragment with back stack flag and show/hide icon
     *
     * @param fragment
     * @param isAddToBackStack
     */
    public void loadFragment(Fragment fragment, boolean isAddToBackStack, String title) {

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();

        if (fragment != null && fragment.isVisible()) {
            fragmentTransaction.remove( fragment );
        }

/*
        if (activeFragment != null)
            fragmentTransaction.hide( activeFragment );
*/

        fragmentTransaction.replace( R.id.frame_layout, fragment, fragment.getClass().getSimpleName() );


        if (isAddToBackStack) {

            Log.d( "sdsd", "ADD TO BACK STACK" );

            if (title.equals( "1" )) {
                navigationView.setVisibility( View.VISIBLE );
            } else {
                navigationView.setVisibility( View.GONE );
            }
            fragmentStack.push( activeFragment );
            fragmentTransaction.addToBackStack( fragment.getClass().getSimpleName() );
        }
        fragmentTransaction.commit();
        activeFragment = fragment;
        setTitle( title );
        //titleList.add(title);
    }


    @Override
    public void onBackPressed() {

        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {

            fragmentHandling();

        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder( this );
            builder.setMessage( "Are you sure you want to exit?" )
                    .setCancelable( false )
                    .setPositiveButton( "Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            Intent startMain = new Intent( Intent.ACTION_MAIN );
                            startMain.addCategory( Intent.CATEGORY_HOME );
                            startMain.setFlags( Intent.FLAG_ACTIVITY_NEW_TASK );
                            startActivity( startMain );
                        }
                    } )
                    .setNegativeButton( "No", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    } );
            AlertDialog alert = builder.create();
            alert.show();
        }
    }


    private void fragmentHandling() {

        if (getSupportFragmentManager() != null) {
            getSupportFragmentManager().popBackStackImmediate();

            if (titleList.size() > 0)
                titleList.remove( titleList.size() - 1 );

            //Set ToolBar Title According To Title List
            /*if (titleList.size() > 0)
                toolbar.setTitle(titleList.get(titleList.size() - 1));*/

            /*if (getSupportFragmentManager().getBackStackEntryCount() == 0)
                enableButton(false);*/

            if (getSupportFragmentManager().getBackStackEntryCount() == 0)
                navigationView.setVisibility( View.VISIBLE );
        }
    }

    public void popBackStack(String tag, int flgs) {
        getSupportFragmentManager().popBackStack( tag, flgs );
    }

    public void popBackStack() {
        getSupportFragmentManager().popBackStackImmediate();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // super.onActivityResult(requestCode, resultCode, data);
        if (activeFragment instanceof SettingFragment) {

            activeFragment.onActivityResult( requestCode, resultCode, data );
        }
    }

    public Activity getDialog() {
        return dialog;
    }

    public void setDialog(Activity dialog) {
        this.dialog = dialog;
    }

    public void openDialog(String text) {
        AlertDialog.Builder builder = new AlertDialog.Builder( TabActivity.this, R.style.Theme_AppCompat_Light_Dialog_Alert );
        builder.setTitle( "Steezle" );
        builder.setMessage( "Sign in or Sign Up to " + text );
        builder.setCancelable( false );
        builder.setPositiveButton( "Continue", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Call Login screen
                Intent i = new Intent( getApplicationContext(), LoginActivity.class );
                i.addFlags( Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP );
                startActivity( i );

            }
        } );
        builder.setNegativeButton( "Not Now", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        } );

        builder.show();

    }

    @Override
    protected void onResume() {
        super.onResume();
        try{
            user = AppSharedPreferences.getSharePreference( getApplicationContext() ).getUser();
            if (user.getUser_id().equalsIgnoreCase( "" )){
                navigationView.setSelectedItemId( R.id.action_item1 );
            }
        }catch (Exception e)
        {
            e.printStackTrace();
            navigationView.setSelectedItemId( R.id.action_item1 );
        }
    }
}