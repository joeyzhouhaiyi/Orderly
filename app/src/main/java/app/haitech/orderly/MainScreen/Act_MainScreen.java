package app.haitech.orderly.MainScreen;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import app.haitech.orderly.DB.DBOperations;
import app.haitech.orderly.DB.Project;
import app.haitech.orderly.DB.ProjectLibrary;
import app.haitech.orderly.Dashboard.Act_Dashboard;
import app.haitech.orderly.R;
import io.realm.Realm;
import io.realm.RealmResults;

public class Act_MainScreen extends AppCompatActivity
{
    String TAG = "Act_MainScreen";

    //models
    private DrawerLayout mDrawerLayout;
    private Context mContext;
    private int checkedNavItem =0;

    //DB related
    Realm realm;
    ProjectLibrary PL;

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
        //Create DB instance
        realm = Realm.getDefaultInstance();
        PL = DBOperations.getDefaultProjectLibrary(realm);

        // Toolbar
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionbar = getSupportActionBar();
        if(actionbar!=null) {
            actionbar.setDisplayHomeAsUpEnabled(true);
            actionbar.setHomeAsUpIndicator(R.drawable.ic_menu);
        }
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
                    public void onDrawerSlide(View drawerView, float slideOffset){}

                    @Override
                    public void onDrawerOpened(View drawerView){}

                    @Override
                    public void onDrawerClosed(View drawerView)
                    {
                        if(checkedNavItem == R.id.nav_add_new_project)
                            CreateNewProjectDialog();
                    }

                    @Override
                    public void onDrawerStateChanged(int newState){}
                }
        );
        boolean hasExistingProject = CheckIfHasProject();
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
        boolean res = false;
        RealmResults<Project> projects = realm.where(Project.class).findAll();
        if(projects.size() != 0)
            res = true;
        return res;
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
            final TextView tv_hint = (TextView)v.findViewById(R.id.tv_hint_text);
            final AlertDialog dialog = builder.create();
            dialog.show();
            dialog.getWindow().setContentView(v);

            // OK button callback
            btn_ok.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final String name = et_proj_name.getText().toString();
                    if (!name.isEmpty()) {


                        //copy new project to realm
                        realm.beginTransaction();
                        Project newProject = realm.createObject(Project.class);
                        newProject.setName(name);
                        realm.copyToRealm(newProject);
                        PL.setCSP(newProject);
                        PL.getProjects().add(newProject);
                        realm.commitTransaction();
                        Intent intent = new Intent(Act_MainScreen.this, Act_Dashboard.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
                        dialog.dismiss();
                        Act_MainScreen.this.finish();
                        Toast.makeText(mContext, "new project name "+ name + ", ID: " + newProject.getID(), Toast.LENGTH_SHORT).show();
                    }else{
                        tv_hint.setText("* Name cannot be empty!");
                        tv_hint.setTextColor(getResources().getColor(R.color.red));
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
    String selectedItem;
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

            // init spinner ----

            ArrayList<String> mItems = new ArrayList<>();
            for (Project p: PL.getProjects())
                mItems.add(p.getName());
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, mItems);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner.setAdapter(adapter);
            // ----------------

            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    selectedItem = parent.getItemAtPosition(position).toString();
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });

            // Open button callback
            btn_open.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Project selectedProject = realm.where(Project.class).equalTo("name",selectedItem).findFirst();
                    realm.beginTransaction();
                    PL.setCSP(selectedProject);
                    realm.commitTransaction();
                    Intent intent = new Intent(Act_MainScreen.this, Act_Dashboard.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
                    dialog.dismiss();
                    Act_MainScreen.this.finish();
                }
            });

            // New button callback
            btn_new.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                    CreateNewProjectDialog();
                }
            });
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.close();
    }
}
