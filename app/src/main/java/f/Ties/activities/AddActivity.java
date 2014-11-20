package f.Ties.activities;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;

import com.f.myCircle.R;

import f.Ties.fragments.AddFragment;
import f.Ties.util.DatabaseManager;
import f.Ties.models.ContactModel;

public class AddActivity extends ActionBarActivity {

    private AddFragment addFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        Toolbar toolbar = (Toolbar) findViewById(R.id.activity_toolbar);
        setSupportActionBar(toolbar);

        addFragment = new AddFragment();
    }

    @Override
    public void onResume() {
        super.onResume();
        getFragmentManager().beginTransaction()
                .replace(R.id.container, addFragment)
                .commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_confirm) {
            confirm();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void confirm() {
        DatabaseManager db = new DatabaseManager(this);
        ArrayAdapter<ContactModel> adapter = (ArrayAdapter<ContactModel>)addFragment.getListView().getAdapter();
        for (int i = 0; i < adapter.getCount(); i++) {
            ContactModel cm = adapter.getItem(i);
            if (cm.isSelected()) {
                db.addContact(cm);
            }
            else {
                db.removeContact(cm);
            }
        }

        this.finish();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.add, menu);
        return true;
    }

}
