package app.haitech.orderly.MainScreen;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.nfc.Tag;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.support.design.widget.NavigationView;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import app.haitech.orderly.Dashboard.Act_Dashboard;
import app.haitech.orderly.R;

public class Act_MainScreen extends AppCompatActivity
{
    String TAG = "Act_MainScreen";
    //models
    private DrawerLayout mDrawerLayout;
    public static String projectName = "";
    private Context mContext;

    //Views
    Toolbar toolbar;
    TextView tv_no_project;
    TextView tv_create_project;
    Button btn_create_new_project;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sty_main_screen);
        mContext=this;
        InitViews();
        // Toolbar
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.ic_menu);

        //Drawer
        mDrawerLayout = findViewById(R.id.drawer_layout);

        //Navigation Listener
        NavigationView navigationView = findViewById(R.id.nav_view);
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

                        // Add code here to update the UI based on the item selected
                        // For example, swap UI fragments here

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
                        // Respond when the drawer is closed
                    }

                    @Override
                    public void onDrawerStateChanged(int newState)
                    {
                        // Respond when the drawer motion state changes
                    }
                }
        );
        boolean hasExistingProject = CheckIfHasProject();
        //hasExistingProject=true;
        if(hasExistingProject)
        {
            CreateChooseProjectDialog();
        }else
        {
            tv_no_project.setVisibility(View.VISIBLE);
            tv_create_project.setVisibility(View.VISIBLE);
            btn_create_new_project.setVisibility(View.VISIBLE);
        }
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
    private void InitViews()
    {
        tv_create_project = (TextView) findViewById(R.id.tv_createProject);
        tv_create_project.setVisibility(View.INVISIBLE);
        tv_no_project = (TextView) findViewById(R.id.tv_noProject);
        tv_no_project.setVisibility(View.INVISIBLE);
        btn_create_new_project = (Button) findViewById(R.id.btn_newProject);
        btn_create_new_project.setVisibility(View.INVISIBLE);
    }
    //---------------------------------------------------------------------------
    public boolean CheckIfHasProject()
    {
        // check in DB if project exist
        //TODO
        return false;
    }
    //---------------------------------------------------------------------------
    // New Project Button Clicked
    public void OnClickNewProject(View v)
    {
        CreateNewProjectDialog();
    }
    //---------------------------------------------------------------------------
    /**
     * Create the dialog where the user can
     * enter a name for the new project
     */
    private void CreateNewProjectDialog()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View v = inflater.inflate(R.layout.sty_dialog_new_project, null);
        if(v!=null) {
            Button btn_ok = (Button) v.findViewById(R.id.btn_ok);
            Button btn_cancel = (Button) v.findViewById(R.id.btn_cancel);
            final EditText et_proj_name = (EditText) v.findViewById(R.id.et_projectName);//enter proj name
            final AlertDialog dialog = builder.create();
            dialog.show();
            dialog.getWindow().setContentView(v);

            // OK button callback
            btn_ok.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    projectName = et_proj_name.getText().toString();
                    if (!projectName.isEmpty()) {
                        Intent intent = new Intent(Act_MainScreen.this, Act_Dashboard.class);
                        intent.putExtra("projectName", projectName);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        dialog.dismiss();
                        Act_MainScreen.this.finish();
                    }else{
                        et_proj_name.setHint("Name cannot be empty!");
                    }
                }
            });

            // Cancel button callback
            btn_cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                }
            });

            // On focus change show keyboard
            // fix the issue where keyboard wont pop up
            et_proj_name.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean b) {
                    if (b)
                        dialog.getWindow()
                                .clearFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
                }
            });
        }
    }
    //----------------------------------------------------------------------------------------

    /**
     * If the project Exist, choose a project
     */
    private void CreateChooseProjectDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View v = inflater.inflate(R.layout.sty_dialog_choose_project, null);
        if (v != null) {
            final AlertDialog dialog = builder.create();
            dialog.show();
            dialog.getWindow().setContentView(v);

            Button btn_open = (Button) v.findViewById(R.id.btn_open);
            Button btn_new = (Button) v.findViewById(R.id.btn_new);
            Spinner spinner = (Spinner) v.findViewById(R.id.spinner);
            // ini spinner ----
            String[] mItems = {"Select project...","Project A", "Project B", "Project C","Project C","Project C","Project C","Project C","Project C"};
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, mItems);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner.setAdapter(adapter);
            // ----------------


            // Open button callback
            btn_open.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                }
            });

            // New button callback
            btn_new.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                }
            });
        }
    }
}
