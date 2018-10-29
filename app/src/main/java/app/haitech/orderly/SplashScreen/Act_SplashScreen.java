package app.haitech.orderly.SplashScreen;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.util.ArrayList;

import app.haitech.orderly.DB.Item;
import app.haitech.orderly.DB.Project;
import app.haitech.orderly.Dataclass;
import app.haitech.orderly.MainScreen.Act_MainScreen;
import app.haitech.orderly.R;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;

public class Act_SplashScreen extends AppCompatActivity {

    private static final int SPLASH_DELAY = 2000;

    private final Handler mHandler   = new Handler();
    private final Launcher mLauncher = new Launcher();

    Dataclass myData = new Dataclass();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sty_splash_screen);
        Realm realm = Realm.getDefaultInstance();
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                RealmResults<Project> myProjects = realm.where(Project.class).findAll();
                if(myProjects.size()!=0)
                {
                    ArrayList<String> myProjectNames = new ArrayList<>();
                    for(Project p : myProjects)
                    {
                        myProjectNames.add(p.getName());
                    }
                    myData.setProjectNameList(myProjectNames);
                }
            }
        });
        realm.close();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mHandler.postDelayed(mLauncher, SPLASH_DELAY);
    }

    @Override
    protected void onStop() {
        mHandler.removeCallbacks(mLauncher);
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void launch() {
        if (!isFinishing()) {
            startActivity(new Intent(this, Act_MainScreen.class));
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            finish();
        }
    }

    private class Launcher implements Runnable {
        @Override
        public void run() {
            launch();
        }
    }
}
