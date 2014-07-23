package f.myCircle.adapters;

import android.app.Activity;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.f.myCircle.R;

import java.util.Date;
import java.util.List;

import f.myCircle.models.ContactModel;

/**
 * Created by jeff on 7/19/14.
 */
public class ContactArrayAdapter extends ArrayAdapter<ContactModel> {

    private final List<ContactModel> list;
    private final Activity context;

    public ContactArrayAdapter(Activity context, List<ContactModel> list) {
        super(context, R.layout.fragment_add_item, list);
        this.context = context;
        this.list = list;
    }

    static class ViewHolder {
        protected ImageView contactPhoto;
        protected TextView name;
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

        ViewHolder holder = (ViewHolder) view.getTag();
        holder.name.setText(list.get(position).getName());

        ContactModel cm = getItem(position);

        if (cm.getLastContacted() != null && cm.getTtk() != null &&
            cm.getLastContacted().getTime() + cm.getTtk().getTime() < (new Date()).getTime()) {
            remove(cm);
        }

        if (cm.isSelected()) {
            view.setBackgroundColor(Color.rgb(52, 152, 219));
            holder.name.setTextColor(Color.WHITE);
        }
        else {
            view.setBackgroundColor(Color.WHITE);
            holder.name.setTextColor(Color.BLACK);
        }

//        holder.contactPhoto.setImageBitmap(cm.getContactPhoto());

        return view;
    }
}