package f.myCircle;

import android.app.Activity;
import android.app.ListFragment;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.f.myCircle.R;

import java.util.Collections;
import java.util.List;

/**
 * Created by jeff on 7/19/14.
 */
public class HomeFragment extends ListFragment {

    DatabaseManager db;
    Activity mActivity;

    @Override
    public void onAttach(Activity activity) {

        super.onAttach(activity);
        mActivity = activity;
    }

    public void onResume() {
        super.onResume();

        final ListView lv = getListView();

        UkDbHelper helper = new UkDbHelper(mActivity);
        db = new DatabaseManager(helper.getWritableDatabase(), mActivity);

        ArrayAdapter<ContactModel> adapter = new HomeArrayAdapter(mActivity, getModel());

        lv.setAdapter(adapter);
        final Handler handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
            lv.invalidateViews();
            handler.postDelayed(this, 1000);
            }
        });
    }

    private List<ContactModel> getModel() {
        List<ContactModel> addedContacts = db.getAddedContacts();
        Collections.sort(addedContacts, new ContactModelTimeComparator());

        return addedContacts;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        return rootView;
    }
}
