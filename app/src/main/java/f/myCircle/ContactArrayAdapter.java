package f.myCircle;

import android.app.Activity;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.fambam.myapplication.R;

import java.util.Date;
import java.util.List;

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
        protected TextView text;
        protected CheckBox checkbox;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = null;
        if (convertView == null) {
            LayoutInflater inflator = context.getLayoutInflater();
            view = inflator.inflate(R.layout.fragment_add_item, null);
            final ViewHolder viewHolder = new ViewHolder();
            viewHolder.text = (TextView) view.findViewById(R.id.text1);
            view.setTag(viewHolder);
        } else {
            view = convertView;
        }

        ContactModel cm = getItem(position);

        if (cm.getLastContacted() != null && cm.getTtk() != null && cm.getLastContacted().getTime() + cm.getTtk().getTime() > (new Date()).getTime()) {
            remove(cm);
        }

        if (cm.isSelected()) {
            view.setBackgroundColor(Color.rgb(52, 152, 219));
            ((TextView) view.findViewById(R.id.text1)).setTextColor(Color.WHITE);
        }
        else {
            view.setBackgroundColor(Color.WHITE);
            ((TextView) view.findViewById(R.id.text1)).setTextColor(Color.BLACK);
        }

        ViewHolder holder = (ViewHolder) view.getTag();
        holder.text.setText(list.get(position).getFirstName() + " " + list.get(position).getLastName());
        return view;
    }
}