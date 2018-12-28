package app.haitech.orderly.TagManagement;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.apmem.tools.layouts.FlowLayout;

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

        // Toolbar
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionbar = getSupportActionBar();
        if(actionbar!= null) {
            actionbar.setTitle("Tags");
            actionbar.setDisplayHomeAsUpEnabled(true);
            actionbar.setHomeAsUpIndicator(R.drawable.ic_dashboard);
        }

        RenderTagViews();
    }
    //--------------------------------------------------------------------------
    //---------------------------------------------------------------------------
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.mn_action_bar_tag, menu);
        return true;
    }
    //---------------------------------------------------------------------------
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
    //--------------------------------------------------------------------------
    RealmList<Item> items;
    protected void RenderTagViews()
    {
        final FlowLayout flowLayout = findViewById(R.id.flowlayout_tags);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if(inflater!=null) {
            int i =0;
            for (Tag t : PL.getCSP().getTags()) {
                i++;
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
                name.setText(t.getName());
                RealmResults<Item> items = realm.where(Item.class).equalTo("myTag.name",t.getName()).and().equalTo("fatherProject.name",PL.getCSP().getName()).findAll();
                num.setText(items.size() + "");
                flowLayout.addView(bTag, i);
            }
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.close();
    }
    //-----------------------------------------------------------------------------------------
    // Button Click Listeners
    //-----------------------------------------------------------------------------------------
    public void OnAddTag(View view){
        Tag aTag = new Tag();
        aTag.setFatherProject(PL.getCSP());
        aTag.setName("Tag1");
        RealmResults<Tag> tags = realm.where(Tag.class).equalTo("name",aTag.getName()).findAll();
        if(tags.size() == 0)
        {
            realm.beginTransaction();
            realm.copyToRealm(aTag);
            PL.getCSP().getTags().add(aTag);
            realm.commitTransaction();
            RenderTagViews();
            Toast.makeText(mContext,"Tag added successfully!",Toast.LENGTH_SHORT).show();
        }
        else
        {
            Toast.makeText(mContext,"The tag name exists..",Toast.LENGTH_SHORT).show();
        }
    }
    public void OnRemoveTag(View view)
    {

    }
    public void OnEditTag(View view)
    {

    }
}
