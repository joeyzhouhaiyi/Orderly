package app.haitech.orderly.DB;

import java.util.Date;
import java.util.UUID;
import app.haitech.orderly.DB.Tag;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

public class Item extends RealmObject {

    @PrimaryKey
    private String  id = UUID.randomUUID().toString();
    private String  name;
    private Tag  myTag;
    @Required
    private byte[]  barcode;
    @Required
    private Date    dateIn;
    private Date    dateOut;
    private Long    value;
    private Project fatherProject;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Tag getMyTag() {
        return myTag;
    }

    public void setMyTag(Tag tag) {
        this.myTag = tag;
    }

    public byte[] getBarcode() {
        return barcode;
    }

    public void setBarcode(byte[] barcode) {
        this.barcode = barcode;
    }

    public Date getDateIn() {
        return dateIn;
    }

    public void setDateIn(Date date) {
        this.dateIn = date;
    }

    public Date getDateOut() {
        return dateOut;
    }

    public void setDateOut(Date dateOut) {
        this.dateOut = dateOut;
    }

    public Long getValue() {
        return value;
    }

    public void setValue(Long value) {
        this.value = value;
    }

    public Project getFatherProject() {
        return fatherProject;
    }

    public void setFatherProject(Project fatherProject) {
        this.fatherProject = fatherProject;
    }
}
