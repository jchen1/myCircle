package f.myCircle;

import android.app.Activity;
import android.app.ListFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.f.myCircle.R;

import java.util.Collections;
import java.util.List;

/**
 * Created by jeff on 7/19/14.
 */
public class AddFragment extends ListFragment {
    private DatabaseManager db;
    private Activity mActivity;

    @Override
    public void onAttach(Activity activity) {

        super.onAttach(activity);

        mActivity = activity;
    }

    @Override
    public void onResume() {
        super.onResume();
        final ListView lv = getListView();
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            ContactModel cm = (ContactModel)parent.getAdapter().getItem(position);
            cm.setSelected(!cm.isSelected());
            lv.invalidateViews();
            }
        });

        db = new DatabaseManager(mActivity);

        lv.setAdapter(new ContactArrayAdapter(mActivity, getModel()));

    }

    private List<ContactModel> getModel() {
        List<ContactModel> allContacts = db.getAllContacts();

        Collections.sort(allContacts, new ContactModelNameComparator());
        return allContacts;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_add, container, false);
        return rootView;
    }

}
