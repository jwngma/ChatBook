package com.store.chattbook.Activities;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.store.chattbook.Adapters.PostViewHolder;
import com.store.chattbook.Models.Posts;
import com.store.chattbook.R;

import org.w3c.dom.Text;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private static final String TAG = "MainActivity";
    private FirebaseAuth mAuth;
    private DatabaseReference mUsersRef;
    private DatabaseReference Postsref;
    private RecyclerView postList;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private NavigationView navigationView;

    private BottomNavigationView bottomNavigationView;
    private Toolbar toolbar;
    private CircleImageView nav_profile_image;
    private TextView nav_name, nav_email;

    /*private FirebaseRecyclerOptions<Posts> options;
    private FirebaseRecyclerAdapter<Posts, PostViewHolder> adapter;*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(getWindow().FEATURE_NO_TITLE);
        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        Log.d(TAG, "onCreate: Main Activity is created");
        FirebaseApp.initializeApp(this);
        mAuth = FirebaseAuth.getInstance();
        mUsersRef = FirebaseDatabase.getInstance().getReference().child("Users");
        Postsref = FirebaseDatabase.getInstance().getReference().child("Posts");
        mUsersRef.keepSynced(true);
        Postsref.keepSynced(true);
        mUsersRef.keepSynced(true);


        initToolbar();
        initNavigationDrawer();
        initBottomNavigation();
        initRecyclerview();
        initHideBottomNav();
    }
    private void initToolbar() {
        toolbar = findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("     ChatBook");

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }
    private void initRecyclerview() {

        postList = findViewById(R.id.recyclerview);
        postList.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        postList.setLayoutManager(layoutManager);

        FirebaseRecyclerAdapter<Posts, PostViewHolder> adapter = new FirebaseRecyclerAdapter<Posts, PostViewHolder>(
                Posts.class,
                R.layout.all_post_layout,
                PostViewHolder.class,
                Postsref
        ) {
            @Override
            protected void populateViewHolder(PostViewHolder viewHolder, Posts model, int position) {

                final String postkey = getRef(position).getKey();

                viewHolder.setFullname(model.getFullname());
                viewHolder.setDate(model.getDate());
                viewHolder.setDescription(model.getDescription());
                viewHolder.setProfile_image(getApplicationContext(), model.getProfile_image());
                viewHolder.setPost_image(getApplicationContext(), model.getPost_image());
                viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.d(TAG, "onClick: " + "Click on " + postkey + " action will added later");
                        Intent intent = new Intent(MainActivity.this, ClickPostActivity.class);
                        intent.putExtra("postkey", postkey);
                        startActivity(intent);
                    }
                });
            }
        };
        postList.setAdapter(adapter);
    }
    private void initBottomNavigation() {
        bottomNavigationView = findViewById(R.id.bottom_nav);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.bottom_home:
                        Toast.makeText(MainActivity.this, "Home is clicked", Toast.LENGTH_SHORT).show();

                        break;
                    case R.id.bottom_likes:
                        Toast.makeText(MainActivity.this, menuItem.getTitle() + " has been clicked", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.bottom_notifications:
                        Toast.makeText(MainActivity.this, menuItem.getTitle() + " has been clicked", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.bottom_account:
                        Toast.makeText(MainActivity.this, menuItem.getTitle() + " has been clicked", Toast.LENGTH_SHORT).show();
                        break;
                }

                return true;
            }

        });
    }

    private void initNavigationDrawer() {
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.navigation);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);

        View navView = navigationView.inflateHeaderView(R.layout.header_layout);
        nav_profile_image = navView.findViewById(R.id.header_image);
        nav_email = navView.findViewById(R.id.header_email);
        nav_name = navView.findViewById(R.id.header_name);

    }

    private void closeDrawer() {
        drawerLayout.closeDrawer(GravityCompat.START);
    }

    private void openDrawer() {
        drawerLayout.openDrawer(GravityCompat.START);
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            closeDrawer();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.nav_home:
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                closeDrawer();
                break;
            case R.id.nav_new_post:
                Intent add_post_intent = new Intent(MainActivity.this, PostActivity.class);
                startActivity(add_post_intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                closeDrawer();
                break;
            case R.id.nav_profile:
                Intent profile_intent = new Intent(MainActivity.this, SetupActivity.class);
                startActivity(profile_intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                closeDrawer();
                break;
            case R.id.nav_friends:
                Toast.makeText(MainActivity.this, menuItem.getTitle() + " has been clicked", Toast.LENGTH_SHORT).show();
                closeDrawer();
                break;
            case R.id.nav_find_friends:
                Toast.makeText(MainActivity.this, menuItem.getTitle() + " has been clicked", Toast.LENGTH_SHORT).show();
                closeDrawer();
                break;
            case R.id.nav_messages:
                Toast.makeText(MainActivity.this, menuItem.getTitle() + " has been clicked", Toast.LENGTH_SHORT).show();
                closeDrawer();
                break;
            case R.id.nav_settings:
                Intent settings_intent = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(settings_intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                closeDrawer();
                break;
            case R.id.nav_logout:
                mAuth.signOut();
                sendToLogin();
                closeDrawer();
                break;
        }
        return true;
    }
    private void sendToLogin() {
        Intent intent1 = new Intent(MainActivity.this, LoginActivity.class);
        intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent1);
        finish();
    }
    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser firebaseUser = mAuth.getCurrentUser();
        if (firebaseUser == null) {
            sendToRegister();
        } else {
            checkUserDatabases();
        }
    }
    private void checkUserDatabases() {

        final String current_user_id = mAuth.getCurrentUser().getUid();
        mUsersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.hasChild(current_user_id)) {
                    sendToSetup();
                } else {
                    if (current_user_id != null) {
                        mUsersRef.child(current_user_id).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    if (dataSnapshot.hasChild("fullname")) {
                                        String nav_namee = dataSnapshot.child("fullname").getValue().toString();
                                        nav_name.setText(nav_namee);
                                    }
                                    if (dataSnapshot.hasChild("email")) {
                                        String nav_emaill = dataSnapshot.child("email").getValue().toString();
                                        nav_email.setText(nav_emaill);
                                    }
                                    if (dataSnapshot.hasChild("image")) {
                                        final String nav_image = dataSnapshot.child("image").getValue().toString();

                                        Picasso.get().load(nav_image).placeholder(R.drawable.profile_icon).networkPolicy(NetworkPolicy.OFFLINE).into(nav_profile_image, new Callback() {
                                            @Override
                                            public void onSuccess() {
                                            }
                                            @Override
                                            public void onError(Exception e) {
                                                Picasso.get().load(nav_image).into(nav_profile_image);
                                            }
                                        });
                                        //Glide.with(MainActivity.this).load(nav_image).into(nav_profile_image);
                                    } else {
                                        Toast.makeText(MainActivity.this, "No database found", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                            }
                        });
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }
    private void sendToSetup() {
        Intent regIntent = new Intent(MainActivity.this, SetupActivity.class);
        startActivity(regIntent);
    }
    private void sendToRegister() {
        Intent regIntent = new Intent(MainActivity.this, RegisterActivity.class);
        regIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(regIntent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }
    private void initHideBottomNav() {
        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_nav);
        CoordinatorLayout.LayoutParams layoutParams = (CoordinatorLayout.LayoutParams) bottomNavigationView.getLayoutParams();
        layoutParams.setBehavior(new BottomNavigationViewBehavior());
    }

    public class BottomNavigationViewBehavior extends CoordinatorLayout.Behavior<BottomNavigationView> {

        private int height;

        @Override
        public boolean onLayoutChild(CoordinatorLayout parent, BottomNavigationView child, int layoutDirection) {
            height = child.getHeight();
            return super.onLayoutChild(parent, child, layoutDirection);
        }

        @Override
        public boolean onStartNestedScroll(@NonNull CoordinatorLayout coordinatorLayout,
                                           BottomNavigationView child, @NonNull
                                                   View directTargetChild, @NonNull View target,
                                           int axes, int type) {
            return axes == ViewCompat.SCROLL_AXIS_VERTICAL;
        }

        @Override
        public void onNestedScroll(@NonNull CoordinatorLayout coordinatorLayout, @NonNull BottomNavigationView child,
                                   @NonNull View target, int dxConsumed, int dyConsumed,
                                   int dxUnconsumed, int dyUnconsumed,
                                   @ViewCompat.NestedScrollType int type) {
            if (dyConsumed > 0) {
                slideDown(child);
            } else if (dyConsumed < 0) {
                slideUp(child);
            }
        }

        private void slideUp(BottomNavigationView child) {
            child.clearAnimation();
            child.animate().translationY(0).setDuration(200);
        }

        private void slideDown(BottomNavigationView child) {
            child.clearAnimation();
            child.animate().translationY(height).setDuration(200);
        }
    }
}
