package f.myCircle;

import java.util.Comparator;

import f.myCircle.ContactModel;

/**
 * Created by jeff on 7/19/14.
 */
public class ContactModelTimeComparator implements Comparator<ContactModel> {
    @Override
    public int compare(ContactModel lhs, ContactModel rhs) {
        if (lhs.getLastContacted() == null || lhs.getTtk() == null || rhs.getLastContacted() == null || rhs.getTtk() == null) {
            return 0;
        }
        long lhsTime = lhs.getLastContacted().getTime() + lhs.getTtk().getTime();
        long rhsTime = rhs.getLastContacted().getTime() + rhs.getTtk().getTime();

        return (int)(lhsTime - rhsTime);
    }
}
