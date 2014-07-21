package f.myCircle;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.f.myCircle.R;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            if (new DatabaseManager(getApplicationContext()).getAddedContacts().size() == 0) {
                getFragmentManager().beginTransaction()
                        .add(R.id.container, new EmptyHomeFragment())
                        .commit();
            }
            else {
                getFragmentManager().beginTransaction()
                        .add(R.id.container, new HomeFragment())
                        .commit();
            }
        }

        Context context = getApplicationContext();
        Intent serviceIntent = new Intent(context, UkService.class);
        context.startService(serviceIntent);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (new DatabaseManager(getApplicationContext()).getAddedContacts().size() == 0) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new EmptyHomeFragment())
                    .commit();
        }
        else {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new HomeFragment())
                    .commit();
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
}
