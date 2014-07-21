package f.myCircle;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by jeff on 7/19/14.
 */
public class ContactModel {
    private String name;
    private int ukId;   // -1 = not part of db
    private int contactId;
    private Date lastContacted;
    private Date ttk;
    private List<Date> contactHistory;

    private boolean isSelected;

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean isSelected) {
        this.isSelected = isSelected;
    }

    public ContactModel() {
        this.isSelected = false;
        contactHistory = new ArrayList<Date>();
    }

    public ContactModel(String name, int contactId) {
        this.name = name;

        this.contactId = contactId;
        this.isSelected = false;
        contactHistory = new ArrayList<Date>();
    }

    public ContactModel(String name, int ukId, int contactId, Date lastContacted, Date ttk, List<Date> contactHistory) {
        this.name = name;

        this.ukId = ukId;
        this.contactId = contactId;
        this.lastContacted = lastContacted;

        this.ttk = ttk;
        this.isSelected = (ukId != -1);
        this.contactHistory = contactHistory;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getUkId() {
        return ukId;
    }

    public void setUkId(int ukId) {
        this.ukId = ukId;
    }

    public int getContactId() {
        return contactId;
    }

    public void setContactId(int contactId) {
        this.contactId = contactId;
    }

    public Date getLastContacted() {
        return lastContacted;
    }

    public void setLastContacted(Date lastContacted) {
        this.lastContacted = lastContacted;
    }

    public Date getTtk() {
        return ttk;
    }

    public void setTtk(Date ttk) {
        this.ttk = ttk;
    }

    public List<Date> getContactHistory() {
        return contactHistory;
    }

    public void setContactHistory(List<Date> contactHistory) {
        this.contactHistory = contactHistory;
    }

    public int getLongestStreak() {
        int longest = 1, current = 1;
        for (int i = 0; i < contactHistory.size(); i++) {
            if (i != 0 && onSameDay(contactHistory.get(i - 1), contactHistory.get(i))) {
                current++;
            }
            else if (i != 0) {
                if (longest < current) {
                    longest = current;
                }
                current = 1;
            }
        }
        return longest;
    }

    private boolean onSameDay(Date lhs, Date rhs) {
        SimpleDateFormat fmt = new SimpleDateFormat("yyyyMMdd");
        return fmt.format(lhs).equals(fmt.format(rhs));
    }
}

