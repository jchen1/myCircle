package f.Ties.fragments;

import android.app.Activity;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.f.myCircle.R;

import f.Ties.util.DatabaseManager;
import f.Ties.models.ContactModel;
import f.Ties.models.ContactModelNameComparator;
import f.Ties.util.ImageCache;
import f.Ties.util.ImageResizer;

import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Date;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ProfileFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 *
 */
public class ProfileFragment extends Fragment {
    private DatabaseManager db;
    private Activity mActivity;
    private int mImageThumbSize;
    private ImageResizer mImageResizer;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ProfileFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ProfileFragment newInstance(String param1, String param2) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }
    public ProfileFragment() {
        // Required empty public constructor
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

        //mAdapter = new ContactArrayAdapter(mActivity, getModel());

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            //mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        //mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
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
                LayoutInflater inflater = context.getLayoutInflater();
                view = inflater.inflate(R.layout.fragment_add_item, null);
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
