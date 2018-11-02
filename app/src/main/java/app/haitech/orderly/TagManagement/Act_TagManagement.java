package app.haitech.orderly.TagManagement;

import android.content.Context;
import android.content.Intent;
import android.support.v4.view.GravityCompat;
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
import android.widget.Toolbar;

import org.apmem.tools.layouts.FlowLayout;

import app.haitech.orderly.DB.Item;
import app.haitech.orderly.DB.Project;
import app.haitech.orderly.Dashboard.Act_Dashboard;
import app.haitech.orderly.Dataclass;
import app.haitech.orderly.Inventory.Act_Inventory;
import app.haitech.orderly.R;
import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;

public class Act_TagManagement extends AppCompatActivity {


    android.support.v7.widget.Toolbar toolbar;
    Dataclass myData = new Dataclass();
    private Realm realm;
    private Context mContext;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sty_tag_management);
        mContext = this;
        realm = Realm.getDefaultInstance();

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
                name.setText(tag);
                realm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        String mProj = myData.getCurrentProjectName();
                        Project myP = realm.where(Project.class).equalTo("name",mProj).findFirst();
                        if(myP != null)
                        items = myP.getItems();

                    }
                });

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
        String aTag = "Tag";
        int code = myData.appendTag(aTag);
        if(code == 0)
        {
            Toast.makeText(mContext,"Error, please try again..",Toast.LENGTH_SHORT).show();
        }
        else if (code == -1)
        {
            Toast.makeText(mContext,"The tag name exists..",Toast.LENGTH_SHORT).show();
        }
        else if (code == 1)
        {
            RenderTagViews();
        }
    }
    public void OnRemoveTag(View view)
    {

    }
    public void OnEditTag(View view)
    {

    }
}
