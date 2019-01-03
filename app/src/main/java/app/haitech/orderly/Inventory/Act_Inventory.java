package app.haitech.orderly.Inventory;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import app.haitech.orderly.DB.DBOperations;
import app.haitech.orderly.DB.Item;
import app.haitech.orderly.DB.Project;
import app.haitech.orderly.DB.ProjectLibrary;
import app.haitech.orderly.DB.Tag;
import app.haitech.orderly.Dashboard.Act_Dashboard;
import app.haitech.orderly.R;
import app.haitech.orderly.TagManagement.Act_TagManagement;
import io.realm.Realm;
import io.realm.RealmResults;

public class Act_Inventory extends AppCompatActivity {

    String TAG = "Act_Inventory";

    //---------------------------------------------------------------------------
    // [Variables]
    //---------------------------------------------------------------------------

    RecyclerView recyclerView;
    RecyclerViewAdapterForItems recyclerViewAdapterForItems;
    List<String> list = new ArrayList<>();
    List<String> Taglist = new ArrayList<>();
    //models
    private DrawerLayout mDrawerLayout;
    private Context mContext;
    private int checkedNavItem=0;

    //DB related
    ProjectLibrary PL;
    Realm realm;
    //Views
    Toolbar toolbar;
    TextView ProjectName;
    Spinner spinner_tag_filter;
    Spinner spinner_sort;

    //---------------------------------------------------------------------------
    // [Methods]
    //---------------------------------------------------------------------------
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sty_inventory);
        mContext=this;
        //get project name
        realm = Realm.getDefaultInstance();
        PL = DBOperations.getDefaultProjectLibrary(realm);
        initTags();
        initFilter();
        initBarcodes();
        String projectName = PL.getCSP().getName();
        if(!projectName.isEmpty())
        {
            Toast.makeText(mContext, projectName+" is the project name.", Toast.LENGTH_SHORT).show();
        }
        recyclerView = findViewById(R.id.recyclerview_item);
        recyclerViewAdapterForItems = new RecyclerViewAdapterForItems(this,list);
        recyclerView.setAdapter(recyclerViewAdapterForItems);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new DividerItemDecoration(this,DividerItemDecoration.VERTICAL));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
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
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                Act_Inventory.this.finish();
                                break;

                            case R.id.nav_add:
                                //Add item
                                break;
                            case R.id.nav_add_debug:
                                createDebugAddDialog();
                                break;
                            case R.id.nav_delete:
                                //Remove item
                                break;
                            //case R.id.nav_remove_debug:
                            //    recyclerViewAdapterForItems.OnRemoveItem(0);
                             //   break;
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
    String selectedItem;
    public void createDebugAddDialog()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View v = inflater.inflate(R.layout.sty_dialog_debug_add, null);
        if(v!=null) {
            Button btn_ok = v.findViewById(R.id.btn_ok);
            final EditText barcode = v.findViewById(R.id.et_barcode);//enter barcode
            final Spinner sp_choose_tag = v.findViewById(R.id.sp_choose_tag);
            final AlertDialog dialog = builder.create();
            dialog.show();
            dialog.getWindow().setContentView(v);


            // init spinner ----

            ArrayList<String> mItems = new ArrayList<>();
            Project CSP = PL.getCSP();
            RealmResults<Tag> tags = realm.where(Tag.class).equalTo("fatherProject.name",CSP.getName()).findAll();
            for (Tag t:tags)
                mItems.add(t.getName());
            final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, mItems);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            sp_choose_tag.setAdapter(adapter);
            // ----------------

            sp_choose_tag.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    selectedItem = parent.getItemAtPosition(position).toString();
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
            // OK button callback
            btn_ok.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    realm.beginTransaction();
                    Item i = realm.createObject(Item.class,UUID.randomUUID().toString());
                    i.setBarcode(barcode.getText().toString().getBytes());
                    i.setFatherProject(PL.getCSP());
                    i.setName("default");
                    i.setValue(Long.valueOf(20));
                    Tag tag = realm.where(Tag.class).equalTo("name",selectedItem).findFirst();
                    i.setMyTag(tag);
                    Date curDate =  new Date(System.currentTimeMillis());
                    i.setDateIn(curDate);
                    realm.copyToRealm(i);
                    realm.commitTransaction();
                    recyclerViewAdapterForItems.OnAddItem(0,barcode.getText().toString());
                    dialog.dismiss();
                }
            });

            barcode.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean b) {
                    if (b)
                        dialog.getWindow()
                                .clearFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
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
    //---------------------------------------------------------------------------
    public void initTags()
    {
        Taglist = DBOperations.getAllTagNamesFromCSP(realm,PL.getCSP());
    }
    public void initBarcodes() {
        list = DBOperations.getAllBarcodeFromCSP(realm,PL.getCSP());
    }
    //---------------------------------------------------------------------------
    private void initFilter()
    {
        spinner_tag_filter = (Spinner) findViewById(R.id.spinner_tag_filter);
        spinner_sort = (Spinner)findViewById(R.id.spinner_sort);

        Taglist.add(0,"ALL");
        ArrayAdapter<String> adapter_filter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, Taglist);
        //adapter_filter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_tag_filter.setAdapter(adapter_filter);

        String[] mItems_sort = {"ID","Time", "Value"};
        ArrayAdapter<String> adapter_sort = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, mItems_sort);
        //adapter_sort.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_sort.setAdapter(adapter_sort);
    }
    //---------------------------------------------------------------------------
    @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.close();
    }

    /**
     * adapter class for recycler view for items
     */
    class RecyclerViewAdapterForItems extends RecyclerView.Adapter<RecyclerViewAdapterForItems.MyViewHolder>{

        List<String> list;
        LayoutInflater inflater;

        public RecyclerViewAdapterForItems(Context context, List<String> list)
        {
            inflater = LayoutInflater.from(context);
            this.list = list;
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            //加载ItemView
            View itemView = inflater.inflate(R.layout.recyclerview_item,parent,false);
            //创建ViewHolder对象
            MyViewHolder holder = new MyViewHolder(itemView);
            //返回holder
            return holder;
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, final int position) {
            //获得指定的数据
            String lCurrentBarcode = list.get(position);
            holder.tv_barcode.setText(lCurrentBarcode);
            String lCurrentTagName = DBOperations.getTagNameFromBarcode(realm,PL.getCSP(),lCurrentBarcode);
            holder.tv_tagName.setText(lCurrentTagName);
            String lCurrentDateIn = DBOperations.getDateInFromBarcode(realm,PL.getCSP(),lCurrentBarcode);
            holder.tv_dateIn.setText(lCurrentDateIn);
        }

        @Override
        public int getItemCount() {
            return list == null ? 0 : list.size();
        }
        //-----------------------------------------------------------------------------------------
        public void OnAddItem(int position,String newItemBarcode)
        {
            list.add(position,newItemBarcode);
            notifyItemInserted(position);
        }
        //-----------------------------------------------------------------------------------------
        public void OnRemoveItem(int position)
        {
            list.remove(position);
            notifyItemRemoved(position);
        }
        /**
         * class #MyViewHolder, customized viewholder for tag row
         */
        class MyViewHolder extends RecyclerView.ViewHolder{

            TextView tv_barcode;
            TextView tv_tagName;
            TextView tv_dateIn;

            public MyViewHolder (View v)
            {
                super(v);
                tv_barcode = v.findViewById(R.id.tv_item_barcode);
                tv_tagName = v.findViewById(R.id.tv_item_tag_name);
                tv_dateIn = v.findViewById(R.id.tv_item_date_in);
            }
        }
    }
}
