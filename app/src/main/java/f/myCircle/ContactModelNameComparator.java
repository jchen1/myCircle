package f.myCircle;

/**
 * Created by jeff on 7/19/14.
 */

import java.util.Comparator;


public class ContactModelNameComparator implements Comparator<ContactModel> {
    @Override
    public int compare(ContactModel lhs, ContactModel rhs) {
        String lhsName = (lhs.getFirstName() + " " + lhs.getLastName()).toLowerCase();
        String rhsName = (rhs.getFirstName() + " " + rhs.getLastName()).toLowerCase();

        return lhsName.compareTo(rhsName);
    }
}