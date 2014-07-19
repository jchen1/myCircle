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

        long diff = (new Date(list.get(position).getLastContacted().getTime() + list.get(position).getTtk().getTime())).getTime() - (new Date()).getTime();
        if (diff < 1000l*60) {
            holder.text2.setText("" + Math.round(diff / (1000.0)) + " seconds");
            holder.text2.setTextColor(Color.RED);
        }
        else if (diff < 1000l*60*60) {
            holder.text2.setText("" + Math.round(diff / (1000.0 * 60)) + " minutes");
            holder.text2.setTextColor(Color.RED);
        }
        else if (diff < 1000l*60*60*24) {
            holder.text2.setText("" + Math.round(diff / (1000.0 * 60 * 60)) + " hours");
            holder.text2.setTextColor(Color.RED);
        }
        else if (diff < 1000l*60*60*24*30) {
            holder.text2.setText("" + Math.round(diff / (1000.0 * 60 * 60 * 24)) + " days");
            holder.text2.setTextColor(Color.rgb(241, 196, 15));
        }
        else if (diff < 1000l*60*60*24*30*12) {
            holder.text2.setText("" + Math.round(diff / (1000.0 * 60 * 60 * 24 * 30)) + " months");
            holder.text2.setTextColor(Color.GREEN);
        }
        else {
            holder.text2.setText("" + Math.round(diff / (1000.0 * 60 * 60 * 24 * 30 * 12.5)) + " years");
            holder.text2.setTextColor(Color.GREEN);
        }
        return view;
    }
}