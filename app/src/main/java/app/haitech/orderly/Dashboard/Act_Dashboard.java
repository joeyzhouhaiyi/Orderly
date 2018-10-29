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
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.apmem.tools.layouts.FlowLayout;

import java.util.ArrayList;

import app.haitech.orderly.DB.Item;
import app.haitech.orderly.DB.Project;
import app.haitech.orderly.Dataclass;
import app.haitech.orderly.Inventory.Act_Inventory;
import app.haitech.orderly.R;
import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;

public class Act_Dashboard extends AppCompatActivity {

    String TAG = "Act_Dashboard";

    //models
    private DrawerLayout mDrawerLayout;
    private Context mContext;
    private int checkedNavItem=0;
    Dataclass myData = new Dataclass();
    private Realm realm;
    //Views
    Toolbar toolbar;
    TextView ProjectName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sty_dashboard);
        realm = Realm.getDefaultInstance();
        mContext=this;
        //get project name
        String projectName = myData.getCurrentProjectName();
        if(!projectName.isEmpty())
        {
            Toast.makeText(mContext, projectName+" is the new project name.", Toast.LENGTH_SHORT).show();
        }

        //Init Tag List
        InitTagList();

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
                            case R.id.nav_inventory:
                                Intent intent = new Intent(mContext, Act_Inventory.class);
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
    private void InitTagList()
    {
        final String cp = myData.getCurrentProjectName();
        if(cp!=null)
        {
            realm.executeTransactionAsync(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    Project myProj = realm.where(Project.class).equalTo("name",cp).findFirst();
                    if(myProj!=null)
                    {
                        RealmResults<Item> items = myProj.getItems().where().distinct("tag").findAll();
                        if(!items.isEmpty())
                        {
                            ArrayList<String> t = new ArrayList<>();
                            for(Item i : items)
                            {
                                t.add(i.getTag());
                            }
                            myData.setTagList(t);
                        }
                    }
                }
            });
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
            for (int i = 0; i < myData.getTagCount(); i++) {
                View childView = inflater.inflate(R.layout.sty_btn_tag, null);

                final LinearLayout bTag = (LinearLayout) childView.findViewById(R.id.btn_tag_style);
                bTag.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //TODO Add on click behaviour, enable Edit button
                        // bTag.setBackgroundResource(R.drawable.btn_square_regular_pressed);
                    }
                });
                final TextView num = (TextView) bTag.findViewById(R.id.tv_tag_item_number);
                final TextView name = (TextView) bTag.findViewById(R.id.tv_tag_item_name);
                final String tag = myData.getTagList().get(i);
                realm.executeTransactionAsync(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        name.setText(tag);
                        RealmResults<Item> items = realm.where(Item.class).equalTo("tag", tag).findAll();
                        num.setText(items.size());
                    }
                });
                flowLayout.addView(bTag, i);
            }
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.close();
    }
}
