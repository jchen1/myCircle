package f.Ties.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.f.myCircle.R;

import java.util.Collections;
import java.util.List;

import f.Ties.background.UkService;
import f.Ties.fragments.ProfileFragment;
import f.Ties.models.ContactModel;
import f.Ties.models.ContactModelTimeComparator;
import f.Ties.util.DatabaseManager;

public class ProfileActivity extends Activity {
    private ProfileFragment profileFragment;
    DatabaseManager db;
    Activity mActivity;
    List<ContactModel> contacts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        db = new DatabaseManager(this);
        contacts = getModel();
        ContactModel selected;
        TextView name = (TextView)findViewById(R.id.name);
        Bundle bundle = getIntent().getExtras();
        int idString = Integer.parseInt(bundle.getString("selectedContact"));
        selected = contacts.get(0);
        for(ContactModel contact: contacts) {
            if (contact.getContactId()==idString) {
                selected = contact;
            }
        }
        View view = getWindow().getDecorView();
        if (bundle != null) {
            name.setText(selected.getName());
        }
    }
    private List<ContactModel> getModel() {
        List<ContactModel> addedContacts = db.getAddedContacts();
        Collections.sort(addedContacts, new ContactModelTimeComparator());
        return addedContacts;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.profile, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
