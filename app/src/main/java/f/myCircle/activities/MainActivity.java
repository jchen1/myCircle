package f.myCircle.activities;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.f.myCircle.R;

import f.myCircle.models.DatabaseManager;
import f.myCircle.background.UkService;
import f.myCircle.fragments.EmptyHomeFragment;
import f.myCircle.fragments.HomeFragment;

public class MainActivity extends Activity {
    private EmptyHomeFragment emptyHomeFragment;
    private HomeFragment homeFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (isMyServiceRunning(UkService.class) == false) {
            Context context = getApplicationContext();
            Intent serviceIntent = new Intent(context, UkService.class);
            context.startService(serviceIntent);
        }

        emptyHomeFragment = new EmptyHomeFragment();
        homeFragment = new HomeFragment();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (new DatabaseManager(getApplicationContext()).getAddedContacts().size() == 0) {
            getFragmentManager().beginTransaction().replace(R.id.container, emptyHomeFragment).commit();
        }
        else {
            getFragmentManager().beginTransaction().replace(R.id.container, homeFragment).commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_add) {
            openAdd();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void openAdd() {
        Intent intent = new Intent(this, AddActivity.class);
        startActivity(intent);
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
}
