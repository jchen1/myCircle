package f.Ties.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.f.myCircle.R;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import f.Ties.background.UkService;
import f.Ties.fragments.ProfileFragment;
import f.Ties.models.ContactModel;
import f.Ties.models.ContactModelTimeComparator;
import f.Ties.util.DatabaseManager;
import f.Ties.util.ImageCache;
import f.Ties.util.ImageResizer;

public class ProfileActivity extends ActionBarActivity {
    private ProfileFragment profileFragment;
    DatabaseManager db;
    Activity mActivity;
    private int mImageThumbSize;
    private ImageResizer mImageResizer;
    List<ContactModel> contacts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        mImageThumbSize = getResources().getDimensionPixelSize(R.dimen.contact_photo_size);
        db = new DatabaseManager(this);
        ImageCache.ImageCacheParams cacheParams = new ImageCache.ImageCacheParams();
        cacheParams.setMemCacheSizePercent(0.25f);
        mImageResizer = new ImageResizer(this, mImageThumbSize);
        mImageResizer.setLoadingImage(R.drawable.logo);
        mImageResizer.addImageCache(getFragmentManager(), cacheParams);
        contacts = getModel();
        ContactModel selected;
        TextView name = (TextView)findViewById(R.id.name);
        TextView lastContact = (TextView)findViewById(R.id.lastContact);
        TextView timeLeft = (TextView)findViewById(R.id.timeLeft);
        ListView history = (ListView) findViewById(R.id.history);
        Bundle bundle = getIntent().getExtras();
        int idString = Integer.parseInt(bundle.getString("selectedContact"));
        selected = contacts.get(0);
        for(ContactModel contact: contacts) {
            if (contact.getContactId()==idString) {
                selected = contact;
            }
        }
        mImageResizer.loadImage(selected, (ImageView) findViewById(R.id.contactPhoto));
        if (bundle != null) {
            name.setText(selected.getName());
            lastContact.setText("Last Contact: "+selected.getLastContacted());
            timeLeft.setText("Time Left: "+selected.getTtk());
            //ArrayAdapter<String> mHistory = new ArrayAdapter<String>(this, android.R.layout., array);
            //history.setAdapter(mHistory);
            getActionBar().setTitle(selected.getName());
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
