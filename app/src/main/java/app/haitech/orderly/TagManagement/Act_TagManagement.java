package app.haitech.orderly.TagManagement;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.apmem.tools.layouts.FlowLayout;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

import app.haitech.orderly.DB.DBOperations;
import app.haitech.orderly.DB.Item;
import app.haitech.orderly.DB.ProjectLibrary;
import app.haitech.orderly.DB.Tag;
import app.haitech.orderly.Dashboard.Act_Dashboard;
import app.haitech.orderly.Inventory.Act_Inventory;
import app.haitech.orderly.R;
import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;

public class Act_TagManagement extends AppCompatActivity {

    RecyclerView recyclerView;
    RecyclerViewAdapterForTags recyclerViewAdapterForTags;
    List<String> list = new ArrayList<>();


    android.support.v7.widget.Toolbar toolbar;

    private Context mContext;

    //DB related
    private Realm realm;
    private ProjectLibrary PL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sty_tag_management);
        mContext = this;
        realm = Realm.getDefaultInstance();
        PL = DBOperations.getDefaultProjectLibrary(realm);
        recyclerView = findViewById(R.id.recyclerview_tag);
        initTags();
        recyclerViewAdapterForTags = new RecyclerViewAdapterForTags(this,list);
        recyclerView.setAdapter(recyclerViewAdapterForTags);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new DividerItemDecoration(this,DividerItemDecoration.VERTICAL));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        // Toolbar
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionbar = getSupportActionBar();
        if(actionbar!= null) {
            actionbar.setTitle("Tags");
            actionbar.setDisplayHomeAsUpEnabled(true);
            actionbar.setHomeAsUpIndicator(R.drawable.ic_dashboard);
        }
    }
    //-----------------------------------------------------------------------------------------
    //-----------------------------------------------------------------------------------------
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.mn_action_bar_tag, menu);
        return true;
    }
    //-----------------------------------------------------------------------------------------
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId()) {
            case android.R.id.home:
                startActivity(new Intent(mContext, Act_Dashboard.class));
                overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
                Act_TagManagement.this.finish();
                break;
            case R.id.action_inventory:
                startActivity(new Intent(mContext, Act_Inventory.class));
                overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
                Act_TagManagement.this.finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
    //-----------------------------------------------------------------------------------------
    public void initTags()
    {
        list = DBOperations.getAllTagNamesFromCSP(realm,PL.getCSP());
}
    //-----------------------------------------------------------------------------------------
    @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.close();
    }
    //-----------------------------------------------------------------------------------------
    // Button Click Listeners
    //-----------------------------------------------------------------------------------------
    public void OnAddTag(View view){

        final EditText editText = new EditText(Act_TagManagement.this);
        AlertDialog.Builder inputDialog =
                new AlertDialog.Builder(Act_TagManagement.this);
        inputDialog.setTitle("Enter a new tag name").setView(editText);
        inputDialog.setPositiveButton("OK",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String nn = "Total new tag";
                        Toast.makeText(Act_TagManagement.this,
                                editText.getText().toString(),
                                Toast.LENGTH_SHORT).show();
                        nn = editText.getText().toString();

                        RealmResults<Tag> tags = realm.where(Tag.class).equalTo("name",nn).findAll();
                        realm.beginTransaction();
                        if(tags.size() == 0)
                        {
                            Tag aTag = realm.createObject(Tag.class);
                            aTag.setFatherProject(PL.getCSP());
                            aTag.setName(nn);
                            realm.copyToRealm(aTag);
                            PL.getCSP().getTags().add(aTag);
                            realm.commitTransaction();
                            recyclerViewAdapterForTags.OnAddTag(0,nn);
                            Toast.makeText(mContext,"Tag added successfully!",Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            realm.cancelTransaction();
                            Toast.makeText(mContext,"The tag name exists..",Toast.LENGTH_SHORT).show();
                        }
                    }
                }).show();
    }
    //-----------------------------------------------------------------------------------------
    public void OnRemoveTag(View view)
    {

    }
    //-----------------------------------------------------------------------------------------
    public void OnEditTag(View view)
    {

    }

    //-----------------------------------------------------------------------------------------
    /**
     * Adapter class for recycler view, IS-A adapter with type <MyViewHolder>
     */
    class RecyclerViewAdapterForTags extends RecyclerView.Adapter<RecyclerViewAdapterForTags.MyViewHolder>{

        List<String> list;
        LayoutInflater inflater;

        public RecyclerViewAdapterForTags(Context context, List<String> list)
        {
            inflater = LayoutInflater.from(context);
            this.list = list;
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            //加载ItemView
            View itemView = inflater.inflate(R.layout.recyclerview_tag,parent,false);
            //创建ViewHolder对象
            MyViewHolder holder = new MyViewHolder(itemView);
            //返回holder
            return holder;
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, final int position) {
            //获得指定的数据
            String lCurrentTagName = list.get(position);
            holder.tv_tagName.setText(lCurrentTagName);
            int numOfItemForThisTag = DBOperations.getNumberOfItemsForCurrentTagName(lCurrentTagName,realm,PL.getCSP());
            holder.tv_numItems.setText(Integer.toString(numOfItemForThisTag));
            long totalValueForThisTag = DBOperations.getTotalValueForCurrentTagName(lCurrentTagName,realm,PL.getCSP());
            holder.tv_totalValue.setText(Long.toString(totalValueForThisTag));
        }

        @Override
        public int getItemCount() {
            return list == null ? 0 : list.size();
        }
        //-----------------------------------------------------------------------------------------
        public void OnAddTag(int position,String newTagName)
        {
            list.add(position,newTagName);
            notifyItemInserted(position);
        }
        //-----------------------------------------------------------------------------------------
        public void OnRemoveTag(int position)
        {
            list.remove(position);
            notifyItemRemoved(position);
        }
        //-----------------------------------------------------------------------------------------
        public void OnChangeTag(int position)
        {
            notifyItemChanged(position);
        }
        //-----------------------------------------------------------------------------------------
        /**
         * class #MyViewHolder, customized viewholder for tag row
         */
        class MyViewHolder extends RecyclerView.ViewHolder{

            TextView tv_tagName;
            TextView tv_numItems;
            TextView tv_totalValue;

            public MyViewHolder (View v)
            {
                super(v);
                tv_tagName = v.findViewById(R.id.tv_tagName);
                tv_numItems = v.findViewById(R.id.tv_numItems);
                tv_totalValue = v.findViewById(R.id.tv_totalValue);
            }
        }
    }
}
