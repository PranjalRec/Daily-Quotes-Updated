package com.pranjal.dailyquotes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    TabLayout tabLayout;
    ViewPager2 viewPager;
    Toolbar toolbar;
    DrawerLayout drawerLayout;
    NavigationView navigationView;

    FirebaseAuth auth = FirebaseAuth.getInstance();
    FirebaseUser user;

    FirebaseDatabase database;
    DatabaseReference reference;
    FirebaseAnalytics mFirebaseAnalytics;

    String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        tabLayout = findViewById(R.id.tabLayoutMainActivity);
        viewPager = findViewById(R.id.viewPagerMainActivity);
        toolbar = findViewById(R.id.toolbar);
        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.nav_view);

        database = FirebaseDatabase.getInstance();
        reference = database.getReference();

        setSupportActionBar(toolbar);
        ActionBarDrawerToggle toggle = new
                ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.open_drawer,R.string.close_drawer);

        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.bringToFront();
        updateUserDataHeader();

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){

                    case R.id.nav_login:
                        Intent intent = new Intent(MainActivity.this, SignInActivity.class);
                        startActivity(intent);
                        break;


                    case R.id.nav_saved_quotes:
                        Intent savedQuoteIntent = new Intent(MainActivity.this, SavedQuotesActivity.class);
                        startActivity(savedQuoteIntent);
                        break;

                    case R.id.nav_my_posts:
                        Intent intent1 = new Intent(MainActivity.this, MyPostsActivity.class);
                        startActivity(intent1);
                        break;

                    case R.id.nav_verify_Email:
                        verifyEmail();
                        break;


                    case R.id.nav_del_data:
                        Intent intent2 = new Intent(MainActivity.this, DeleteDataActivity.class);
                        startActivity(intent2);
                        break;

                    case R.id.nav_log_out:
                        auth.signOut();
                        Intent i = new Intent(MainActivity.this, SignInActivity.class);
                        startActivity(i);
                        finish();



                }

                drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            }
        });



        user = auth.getCurrentUser();
        if(user != null){
            navigationView.getMenu().findItem(R.id.nav_login).setVisible(false);
            navigationView.getMenu().findItem(R.id.nav_log_out).setVisible(true);

            if(user.isEmailVerified()){
                navigationView.getMenu().findItem(R.id.nav_verify_Email).setVisible(false);
            }
            else{
                navigationView.getMenu().findItem(R.id.nav_verify_Email).setVisible(true);
            }


        }else{
            navigationView.getMenu().findItem(R.id.nav_login).setVisible(true);
            navigationView.getMenu().findItem(R.id.nav_log_out).setVisible(false);
            navigationView.getMenu().findItem(R.id.nav_verify_Email).setVisible(false);
        }



        

        ViewPagerAdapterPost viewPagerAdapterPost = new ViewPagerAdapterPost(getSupportFragmentManager(), getLifecycle());
        viewPager.setAdapter(viewPagerAdapterPost);

        TabLayoutMediator tabLayoutMediator = new TabLayoutMediator(tabLayout, viewPager, true, true, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                switch (position){
                    case 0:
                        tab.setText("Quotes");
                        break;

                    case 1:
                        tab.setText("Post");
                        break;
                }
            }
        });

        tabLayoutMediator.attach();
    }

    void updateUserDataHeader(){
        View navigationHeader = navigationView.getHeaderView(0);
        TextView textViewName = navigationHeader.findViewById(R.id.textViewNameHeader);
        TextView textViewEmail = navigationHeader.findViewById(R.id.textViewEmailHeader);
        ProgressBar progressBar = navigationHeader.findViewById(R.id.progressBarHeader);

        user = auth.getCurrentUser();

        if(user!=null){
            uid = user.getUid();

            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    if(snapshot.child("users").child(uid).exists()){
                        progressBar.setVisibility(View.INVISIBLE);

                        String mail = snapshot.child("users").child(uid).child("email").getValue().toString();
                        String name = snapshot.child("users").child(uid).child("name").getValue().toString();
                        textViewName.setText(name);
                        textViewEmail.setText(mail);
                    }


                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    progressBar.setVisibility(View.INVISIBLE);
                    Toast.makeText(MainActivity.this, "Something went wrong.", Toast.LENGTH_SHORT).show();
                }
            });
        }
        else {
            progressBar.setVisibility(View.INVISIBLE);
            textViewName.setText("Guest User");
        }
    }

    void verifyEmail(){
        user.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(MainActivity.this, "Email sent successfully, after verification please login again",
                        Toast.LENGTH_LONG).show();
                auth.signOut();
                Intent i = new Intent(MainActivity.this, SignInActivity.class);
                startActivity(i);
                finish();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(MainActivity.this, "Something went wrong",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onStart() {
        ConnectivityManager connectivityManager = (ConnectivityManager)this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        if(networkInfo == null || !networkInfo.isConnected() || !networkInfo.isAvailable()){
            Toast.makeText(MainActivity.this, "Your internet is very slow.", Toast.LENGTH_LONG).show();
        }
        super.onStart();
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this).setIcon(android.R.drawable.ic_dialog_alert).setTitle("Exit").setMessage("Are you sure?").setPositiveButton("yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                MainActivity.super.onBackPressed();
            }
        }).setNegativeButton("No",null).show();
    }
}