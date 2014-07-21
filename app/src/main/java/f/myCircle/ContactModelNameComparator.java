package f.myCircle;

/**
 * Created by jeff on 7/19/14.
 */

import java.util.Comparator;

public class ContactModelNameComparator implements Comparator<ContactModel> {
    @Override
    public int compare(ContactModel lhs, ContactModel rhs) {
        return lhs.getName().compareTo(rhs.getName());
    }
}