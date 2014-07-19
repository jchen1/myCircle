package f.myCircle;

import java.util.Date;

/**
 * Created by jeff on 7/19/14.
 */
public class ContactModel {
    private String firstName;
    private String lastName;
    private int ukId;   // -1 = not part of db
    private int contactId;
    private Date lastContacted;
    private Date ttk;

    private boolean isSelected;

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean isSelected) {
        this.isSelected = isSelected;
    }

    public ContactModel(String firstName, String lastName, int contactId) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.contactId = contactId;
    }

    public ContactModel(String firstName, String lastName, int ukId, int contactId, Date lastContacted, Date ttk) {
        this.firstName = firstName;
        this.lastName = lastName;

        this.ukId = ukId;
        this.contactId = contactId;
        this.lastContacted = lastContacted;

        this.ttk = ttk;
        this.isSelected = (ukId != -1);
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
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
}
