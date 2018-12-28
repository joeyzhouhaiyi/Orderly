package app.haitech.orderly.DB;

import io.realm.RealmObject;

public class Tag extends RealmObject {

    private String name;
    private Project fatherProject;

    public Project getFatherProject() {
        return fatherProject;
    }

    public String getName() {
        return name;
    }

    public void setFatherProject(Project fatherProject) {
        this.fatherProject = fatherProject;
    }

    public void setName(String name) {
        this.name = name;
    }
}
