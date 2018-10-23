package app.haitech.orderly.Dashboard;

import android.content.Context;
import android.content.Intent;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.apmem.tools.layouts.FlowLayout;

import app.haitech.orderly.Inventory.Act_Inventory;
import app.haitech.orderly.R;

public class Act_Dashboard extends AppCompatActivity {

    String TAG = "Act_Dashboard";

    //models
    private DrawerLayout mDrawerLayout;
    public static String projectName = "";
    private Context mContext;
    private int checkedNavItem=0;

    //Views
    Toolbar toolbar;
    TextView ProjectName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sty_dashboard);
        mContext=this;
        //get project name

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
            actionbar.setTitle("Dashboard");
            actionbar.setDisplayHomeAsUpEnabled(true);
            actionbar.setHomeAsUpIndicator(R.drawable.ic_menu);
        }
        //Drawer
        mDrawerLayout = findViewById(R.id.drawer_layout);

        //Navigation Listener
        NavigationView navigationView = findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);
        ProjectName = (TextView) headerView.findViewById(R.id.projectName);//TODO: UPDATE DRAWER TITLE
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
                            case R.id.nav_inventory:
                                Intent intent = new Intent(mContext, Act_Inventory.class);
                                intent.putExtra("projectName", projectName);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                Act_Dashboard.this.finish();
                                break;

                            case R.id.nav_tag:
                                createTagsManagementDialog();
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
    protected void createTagsManagementDialog()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View v = inflater.inflate(R.layout.sty_dialog_tag_management, null);
        if (v != null) {
            final AlertDialog dialog = builder.create();
            dialog.show();
            dialog.getWindow().setContentView(v);
            RenderTagViews(v);
            Button btn_close = (Button) v.findViewById(R.id.btn_close);
            Button btn_edit = (Button) v.findViewById(R.id.btn_edit);
            btn_close.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
            btn_edit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //TODO
                }
            });
        }

    }
    //--------------------------------------------------------------------------
    protected void RenderTagViews(View v)
    {
        final FlowLayout flowLayout = v.findViewById(R.id.flowlayout_tags);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if(inflater!=null) {
            //TODO: Replace whole thing by tags
            for (int i = 0; i < 30; i++) {
                View childView = inflater.inflate(R.layout.sty_btn_tag, null);

                final LinearLayout bTag = (LinearLayout) childView.findViewById(R.id.btn_tag_style);
                bTag.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                       //TODO Add on click behaviour, enable Edit button
                        // bTag.setBackgroundResource(R.drawable.btn_square_regular_pressed);
                    }
                });
                TextView num = (TextView) bTag.findViewById(R.id.tv_tag_item_number);
                TextView name = (TextView) bTag.findViewById(R.id.tv_tag_item_name);
                num.setText("" + i);
                if(i%3== 0)
                    name.setText("Tag " + i*i);
                else
                    name.setText("Tag Name ++" + i * i *15);
                flowLayout.addView(bTag,i);
            }
            //--TD--
        }

    }
}
