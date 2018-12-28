package app.haitech.orderly.DB;

import java.util.UUID;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.Required;

public class Project extends RealmObject {

    @Required
    private String ID = UUID.randomUUID().toString();
    private String name;
    private RealmList<Item> items;
    private RealmList<Tag> tags;

    public RealmList<Tag> getTags() {
        return tags;
    }

    public void setTags(RealmList<Tag> tags) {
        this.tags = tags;
    }

    public String getID(){return ID;}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public RealmList<Item> getItems() {
        return items;
    }

    public void setItems(RealmList<Item> items) {
        this.items = items;
    }

}
