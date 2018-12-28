package app.haitech.orderly.DB;

import io.realm.RealmList;
import io.realm.RealmObject;

public class ProjectLibrary extends RealmObject {

    private Project CSP;
    private RealmList<Project> projects;

    public Project getCSP() {
        return CSP;
    }

    public void setCSP(Project CSP) {
        this.CSP = CSP;
    }

    public RealmList<Project> getProjects() {
        return projects;
    }

    public void setProjects(RealmList<Project> projects) {
        this.projects = projects;
    }
}
