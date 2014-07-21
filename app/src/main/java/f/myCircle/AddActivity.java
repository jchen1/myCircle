package f.myCircle;

import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;

import com.f.myCircle.R;

import java.text.SimpleDateFormat;
import java.util.Date;

public class AddActivity extends Activity {

    private AddFragment addFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        addFragment = new AddFragment();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, addFragment)
                    .commit();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_confirm) {
            confirm();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void confirm() {
        SQLiteDatabase db;
        UkDbHelper helper = new UkDbHelper(this);
        db = helper.getWritableDatabase();
        ArrayAdapter<ContactModel> adapter = (ArrayAdapter<ContactModel>)addFragment.getListView().getAdapter();
        for (int i = 0; i < adapter.getCount(); i++) {
            ContactModel cm = adapter.getItem(i);
            if (cm.isSelected()) {

                Cursor curs = db.query(UkEntryContract.UkEntry.TABLE_NAME, new String[]{UkEntryContract.UkEntry.COLUMN_NAME_ENTRY_ID}, UkEntryContract.UkEntry.COLUMN_NAME_ENTRY_ID + "=?", new String[]{new Integer(cm.getContactId()).toString()}, null, null, null, null);
                ContentValues values = new ContentValues();
                if (curs.getCount() == 0) {

                    // insert new
                    values.put(UkEntryContract.UkEntry.COLUMN_NAME_ENTRY_ID, cm.getContactId());
                    values.put(UkEntryContract.UkEntry.COLUMN_NAME_NAME, cm.getName());


                    // set the format to sql date time
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    Date date = new Date();
                    values.put(UkEntryContract.UkEntry.COLUMN_NAME_LASTCONTACT, dateFormat.format(date));
                    long oneMonthMillis = 1000l * 60l * 60l * 24l * 30l;
                    Date ttkDate = new Date(oneMonthMillis);
                    values.put(UkEntryContract.UkEntry.COLUMN_NAME_TTK, dateFormat.format(ttkDate));

                    long newRowId = db.insert(UkEntryContract.UkEntry.TABLE_NAME, "don't worry about this. no seriously.", values);
                }
                curs.close();

            }
            else {
                // delete from db if in it

                Cursor cur = db.query(UkEntryContract.UkEntry.TABLE_NAME, new String[]{UkEntryContract.UkEntry.COLUMN_NAME_ENTRY_ID}, UkEntryContract.UkEntry.COLUMN_NAME_ENTRY_ID + "=?", new String[]{new Integer(cm.getContactId()).toString()}, null, null, null, null);
                if (cur.getCount() != 0) {
                    db.delete(UkEntryContract.UkEntry.TABLE_NAME, UkEntryContract.UkEntry.COLUMN_NAME_ENTRY_ID + "=?", new String[]{new Integer(cm.getContactId()).toString()});
                }
                cur.close();
            }
        }

        this.finish();
        // grab list of selected
        // add those to db if not already added
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.add, menu);
        return true;
    }

}
