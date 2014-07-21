package f.myCircle;

import android.app.Activity;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.f.myCircle.R;

import java.util.Date;
import java.util.List;

public class HomeArrayAdapter extends ArrayAdapter<ContactModel> {

    private final List<ContactModel> list;
    private final Activity context;

    public HomeArrayAdapter(Activity context, List<ContactModel> list) {
        super(context, R.layout.fragment_main_item, list);
        this.context = context;
        this.list = list;
    }

    static class ViewHolder {
        protected TextView name;
        protected TextView timeLeft;
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
            view.setTag(viewHolder);
        } else {
            view = convertView;
        }
        ViewHolder holder = (ViewHolder) view.getTag();
        holder.name.setText(list.get(position).getName());

        ContactModel cm = getItem(position);

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