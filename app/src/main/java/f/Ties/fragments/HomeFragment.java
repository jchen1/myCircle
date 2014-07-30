package f.Ties.fragments;

import android.app.Activity;
import android.app.ListFragment;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.f.myCircle.R;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import f.Ties.util.DatabaseManager;
import f.Ties.models.ContactModel;
import f.Ties.models.ContactModelTimeComparator;
import f.Ties.util.ImageCache;
import f.Ties.util.ImageResizer;

/**
 * Created by jeff on 7/19/14.
 */
public class HomeFragment extends ListFragment {
    DatabaseManager db;
    Activity mActivity;

    private ContactArrayAdapter mAdapter;

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

        ImageCache.ImageCacheParams cacheParams = new ImageCache.ImageCacheParams();
        cacheParams.setMemCacheSizePercent(0.25f);

        mImageResizer = new ImageResizer(getActivity(), mImageThumbSize);
        mImageResizer.setLoadingImage(R.drawable.logo);
        mImageResizer.addImageCache(getFragmentManager(), cacheParams);

        mAdapter = new ContactArrayAdapter(mActivity, getModel());
    }

    public void onResume() {
        super.onResume();
        mImageResizer.setExitTasksEarly(false);
        final ListView lv = getListView();
        lv.setAdapter(mAdapter);
        mAdapter.clear();
        mAdapter.addAll(getModel());
        lv.invalidateViews();
        final Handler handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
            lv.invalidateViews();
            handler.postDelayed(this, 1000);
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

    private static class ViewHolder {
        protected ImageView contactPhoto;
        protected TextView name;
        protected TextView timeLeft;
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
            View view = null;
            if (convertView == null) {
                LayoutInflater inflator = context.getLayoutInflater();
                view = inflator.inflate(R.layout.fragment_main_item, null);
                final ViewHolder viewHolder = new ViewHolder();
                viewHolder.name = (TextView) view.findViewById(R.id.name);
                viewHolder.timeLeft = (TextView) view.findViewById(R.id.timeLeft);
                viewHolder.contactPhoto = (ImageView) view.findViewById(R.id.contactPhoto);
                view.setTag(viewHolder);
            } else {
                view = convertView;
            }
            ViewHolder holder = (ViewHolder) view.getTag();
            holder.name.setText(list.get(position).getName());

            ContactModel cm = getItem(position);
            mImageResizer.loadImage(cm, holder.contactPhoto);


            if (cm.isSelected()) {
                ((TextView) view.findViewById(R.id.name)).setTextColor(Color.WHITE);
            }
            else {
                view.setBackgroundColor(Color.WHITE);
                ((TextView) view.findViewById(R.id.name)).setTextColor(Color.BLACK);
            }

            long diff = (new Date(list.get(position).getLastContacted().getTime() + list.get(position).getTtk().getTime())).getTime() - (new Date()).getTime();
            holder.timeLeft.setTextColor(Color.WHITE);
            holder.name.setTextColor(Color.WHITE);
            if (diff < 0) {
                remove(cm);
            }
            else if (diff < 1000l*60) {
                holder.timeLeft.setText("" + Math.round(diff / (1000.0)) + " seconds");
                view.setBackgroundColor(Color.rgb(192, 57, 43));
            }
            else if (diff < 1000l*60*60) {
                holder.timeLeft.setText("" + Math.round(diff / (1000.0 * 60)) + " minutes");
                view.setBackgroundColor(Color.rgb(192, 57, 43));
            }
            else if (diff < 1000l*60*60*24) {
                holder.timeLeft.setText("" + Math.round(diff / (1000.0 * 60 * 60)) + " hours");
                view.setBackgroundColor(Color.rgb(192, 57, 43));
            }
            else if (diff < 1000l*60*60*24*30) {
                holder.timeLeft.setText("" + Math.round(diff / (1000.0 * 60 * 60 * 24)) + " days");
                view.setBackgroundColor(Color.rgb(241, 196, 15));
            }
            else if (diff < 1000l*60*60*24*30*12) {
                holder.timeLeft.setText("" + Math.round(diff / (1000.0 * 60 * 60 * 24 * 30)) + " months");
                view.setBackgroundColor(Color.rgb(46, 204, 113));
            }
            else {
                holder.timeLeft.setText("" + Math.round(diff / (1000.0 * 60 * 60 * 24 * 30 * 12.5)) + " years");
                view.setBackgroundColor(Color.rgb(46, 204, 113));
            }


            return view;
        }
    }
}
