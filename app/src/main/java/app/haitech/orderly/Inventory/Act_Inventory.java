package app.haitech.orderly.Inventory;

import android.content.Context;
import android.content.Intent;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import app.haitech.orderly.Dashboard.Act_Dashboard;
import app.haitech.orderly.R;

public class Act_Inventory extends AppCompatActivity {

    String TAG = "Act_Inventory";

    //models
    private DrawerLayout mDrawerLayout;
    public static String projectName = "";
    private Context mContext;
    private int checkedNavItem=0;

    //Views
    Toolbar toolbar;
    TextView ProjectName;
    Spinner spinner_tag_filter;
    Spinner spinner_sort;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sty_inventory);
        mContext=this;
        //get project name
        initView();
        Intent intent = getIntent();
        projectName = intent.getStringExtra("projectName");
        if(!projectName.isEmpty())
        {
            Toast.makeText(mContext, projectName+" is the new project name.", Toast.LENGTH_SHORT).show();
        }

        // Toolbar
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionbar = getSupportActionBar();
        if(actionbar!= null) {
            actionbar.setTitle("Inventory");
            actionbar.setDisplayHomeAsUpEnabled(true);
            actionbar.setHomeAsUpIndicator(R.drawable.ic_menu);
        }
        //Drawer
        mDrawerLayout = findViewById(R.id.drawer_layout);

        //Navigation Listener
        NavigationView navigationView = findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);
        ProjectName = (TextView) headerView.findViewById(R.id.projectName);
        if(ProjectName!=null)
            ProjectName.setText(projectName);

        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener()
                {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem)
                    {
                        // set item as selected to persist highlight
                        menuItem.setChecked(true);
                        // close drawer when item is tapped
                        mDrawerLayout.closeDrawers();

                        checkedNavItem = menuItem.getItemId();

                        return true;
                    }
                });

        //Drawer Listener
        mDrawerLayout.addDrawerListener(
                new DrawerLayout.DrawerListener()
                {
                    @Override
                    public void onDrawerSlide(View drawerView, float slideOffset)
                    {
                        // Respond when the drawer's position changes
                    }

                    @Override
                    public void onDrawerOpened(View drawerView)
                    {
                        // Respond when the drawer is opened
                    }

                    @Override
                    public void onDrawerClosed(View drawerView)
                    {
                        switch (checkedNavItem)
                        {
                            case R.id.nav_dashboard:
                                Intent intent = new Intent(mContext, Act_Dashboard.class);
                                intent.putExtra("projectName", projectName);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                Act_Inventory.this.finish();
                                break;

                            case R.id.nav_add:
                                //Add item
                                break;

                            case R.id.nav_delete:
                                //Remove item
                                break;
                        }
                    }

                    @Override
                    public void onDrawerStateChanged(int newState)
                    {
                        // Respond when the drawer motion state changes
                    }
                }
        );
    }
    //---------------------------------------------------------------------------
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.mn_action_bar, menu);
        return true;
    }
    //---------------------------------------------------------------------------
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    //--------------------------------------------------------------------------
    private void initView()
    {
        spinner_tag_filter = (Spinner) findViewById(R.id.spinner_tag_filter);
        spinner_sort = (Spinner)findViewById(R.id.spinner_sort);

        String[] mItems_filter = {"ALL","Tag A", "Tag B", "Tag C"};
        ArrayAdapter<String> adapter_filter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, mItems_filter);
        adapter_filter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_tag_filter.setAdapter(adapter_filter);

        String[] mItems_sort = {"ID","Time", "Value"};
        ArrayAdapter<String> adapter_sort = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, mItems_sort);
        adapter_sort.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_sort.setAdapter(adapter_sort);
    }
}
