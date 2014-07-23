package f.myCircle.fragments;

import android.app.Activity;
import android.app.ListFragment;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.f.myCircle.R;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import f.myCircle.models.ContactModel;
import f.myCircle.models.ContactModelNameComparator;
import f.myCircle.models.DatabaseManager;
import f.myCircle.util.ImageCache;
import f.myCircle.util.ImageResizer;

/**
 * Created by jeff on 7/19/14.
 */
public class AddFragment extends ListFragment {
    private DatabaseManager db;
    private Activity mActivity;
    private ContactArrayAdapter mAdapter;
    private static final String IMAGE_CACHE_DIR = "thumbs";

    private int mImageThumbSize;
    private ImageResizer mImageResizer;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity = activity;
        db = new DatabaseManager(mActivity);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mImageThumbSize = getResources().getDimensionPixelSize(R.dimen.contact_photo_size);

        ImageCache.ImageCacheParams cacheParams = new ImageCache.ImageCacheParams(getActivity(), IMAGE_CACHE_DIR);
        cacheParams.setMemCacheSizePercent(0.25f);

        mImageResizer = new ImageResizer(getActivity(), mImageThumbSize);
        mImageResizer.setLoadingImage(R.drawable.logo);
        mImageResizer.addImageCache(getFragmentManager(), cacheParams);

        mAdapter = new ContactArrayAdapter(mActivity, getModel());
    }

    @Override
    public void onResume() {
        super.onResume();
        mImageResizer.setExitTasksEarly(false);
        mAdapter.notifyDataSetChanged();
        final ListView lv = getListView();
        lv.setAdapter(mAdapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ContactModel cm = (ContactModel)parent.getAdapter().getItem(position);
                cm.setSelected(!cm.isSelected());
                lv.invalidateViews();
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        mImageResizer.setPauseWork(false);
        mImageResizer.setExitTasksEarly(true);
        mImageResizer.flushCache();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mImageResizer.closeCache();
        db.close();
    }


    private List<ContactModel> getModel() {
        List<ContactModel> allContacts = db.getAllContacts();
        Collections.sort(allContacts, new ContactModelNameComparator());
        return allContacts;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_add, container, false);
        return rootView;
    }

    private static class ViewHolder {
        protected ImageView contactPhoto;
        protected TextView name;
    }

    private class ContactArrayAdapter extends ArrayAdapter<ContactModel> {

        private final List<ContactModel> list;
        private final Activity context;

        public ContactArrayAdapter(Activity context, List<ContactModel> list) {
            super(context, R.layout.fragment_add_item, list);
            this.context = context;
            this.list = list;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view;
            if (convertView == null) {
                LayoutInflater inflator = context.getLayoutInflater();
                view = inflator.inflate(R.layout.fragment_add_item, null);
                final ViewHolder viewHolder = new ViewHolder();
                viewHolder.name = (TextView) view.findViewById(R.id.name);
                viewHolder.contactPhoto = (ImageView) view.findViewById(R.id.contactPhoto);
                view.setTag(viewHolder);
            } else {
                view = convertView;
            }
            ContactModel cm = getItem(position);

            ViewHolder holder = (ViewHolder) view.getTag();
            mImageResizer.loadImage(cm, holder.contactPhoto);
            holder.name.setText(cm.getName());

            if (cm.getLastContacted() != null && cm.getTtk() != null &&
                    cm.getLastContacted().getTime() + cm.getTtk().getTime() < (new Date()).getTime()) {
                remove(cm);
            }

            if (cm.isSelected()) {
                view.setBackgroundColor(Color.rgb(52, 152, 219));   //dat peter river
                holder.name.setTextColor(Color.WHITE);
            }
            else {
                view.setBackgroundColor(Color.WHITE);
                holder.name.setTextColor(Color.BLACK);
            }

            return view;
        }
    }

}
