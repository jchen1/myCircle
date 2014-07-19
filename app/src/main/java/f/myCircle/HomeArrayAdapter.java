package f.myCircle;


import android.app.Activity;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.fambam.myapplication.R;

import java.util.Date;
import java.util.List;

import f.myCircle.ContactModel;

public class HomeArrayAdapter extends ArrayAdapter<ContactModel> {

    private final List<ContactModel> list;
    private final Activity context;

    public HomeArrayAdapter(Activity context, List<ContactModel> list) {
        super(context, R.layout.fragment_main_item, list);
        this.context = context;
        this.list = list;
    }

    static class ViewHolder {
        protected TextView text1;
        protected TextView text2;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = null;
        if (convertView == null) {
            LayoutInflater inflator = context.getLayoutInflater();
            view = inflator.inflate(R.layout.fragment_main_item, null);
            final ViewHolder viewHolder = new ViewHolder();
            viewHolder.text1 = (TextView) view.findViewById(R.id.text1);
            viewHolder.text2 = (TextView) view.findViewById(R.id.text2);
            view.setTag(viewHolder);
        } else {
            view = convertView;
        }
        ViewHolder holder = (ViewHolder) view.getTag();
        holder.text1.setText(list.get(position).getFirstName() + " " + list.get(position).getLastName());

        ContactModel cm = getItem(position);

        if (cm.isSelected()) {
            ((TextView) view.findViewById(R.id.text1)).setTextColor(Color.WHITE);
        }
        else {
            view.setBackgroundColor(Color.WHITE);
            ((TextView) view.findViewById(R.id.text1)).setTextColor(Color.BLACK);
        }

        long diff = (new Date(list.get(position).getLastContacted().getTime() + list.get(position).getTtk().getTime())).getTime() - (new Date()).getTime();
        holder.text2.setTextColor(Color.WHITE);
        holder.text1.setTextColor(Color.WHITE);
        if (diff < 0) {
            remove(cm);
        }
        if (diff < 1000l*60) {
            if (diff < 1000l * 30) {
                if (Math.round(diff / 1000.0) == 1) {
                    holder.text2.setText("1 second");
                }
                else {
                    holder.text2.setText("" + Math.round(diff / (1000.0)) + " seconds");
                }
                view.setBackgroundColor(Color.rgb(192, 57, 43));
            }
            else if (diff < 1000l * 45) {
                holder.text2.setText("" + Math.round(diff / (1000.0)) + " seconds");
                view.setBackgroundColor(Color.rgb(241, 196, 15));
            }
            else {
                holder.text2.setText("" + Math.round(diff / (1000.0)) + " seconds");
                view.setBackgroundColor(Color.rgb(46, 204, 113));
            }
        }
        else if (diff < 1000l*60*60) {
            holder.text2.setText("" + Math.round(diff / (1000.0 * 60)) + " minutes");
            view.setBackgroundColor(Color.rgb(192, 57, 43));
        }
        else if (diff < 1000l*60*60*24) {
            holder.text2.setText("" + Math.round(diff / (1000.0 * 60 * 60)) + " hours");
            view.setBackgroundColor(Color.rgb(192, 57, 43));
        }
        else if (diff < 1000l*60*60*24*30) {
            holder.text2.setText("" + Math.round(diff / (1000.0 * 60 * 60 * 24)) + " days");
            view.setBackgroundColor(Color.rgb(241, 196, 15));
        }
        else if (diff < 1000l*60*60*24*30*12) {
            holder.text2.setText("" + Math.round(diff / (1000.0 * 60 * 60 * 24 * 30)) + " months");
            view.setBackgroundColor(Color.rgb(46, 204, 113));
        }
        else {
            holder.text2.setText("" + Math.round(diff / (1000.0 * 60 * 60 * 24 * 30 * 12.5)) + " years");
            view.setBackgroundColor(Color.rgb(46, 204, 113));
        }
        return view;
    }
}