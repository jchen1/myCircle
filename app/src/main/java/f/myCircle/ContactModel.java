package f.myCircle;

import java.util.Date;

/**
 * Created by jeff on 7/19/14.
 */
public class ContactModel {
    private String name;
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

    public ContactModel(String name, int contactId) {
        this.name = name;

        this.contactId = contactId;
        this.isSelected = false;
    }

    public ContactModel(String name, int ukId, int contactId, Date lastContacted, Date ttk) {
        this.name = name;

        this.ukId = ukId;
        this.contactId = contactId;
        this.lastContacted = lastContacted;

        this.ttk = ttk;
        this.isSelected = (ukId != -1);
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
}

