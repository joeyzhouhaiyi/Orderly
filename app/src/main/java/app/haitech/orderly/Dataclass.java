package app.haitech.orderly;

import java.util.ArrayList;

public final class Dataclass {

    private static ArrayList<String> tagList = new ArrayList<>();
    private static ArrayList<String> projectNameList = new ArrayList<>();
    private static String currentProjectName = "";




    //-------------------------------------------------------------------
    public String getCurrentProjectName()
    {
        return currentProjectName;
    }
    //-------------------------------------------------------------------
    public void setCurrentProjectName(String currentProjectName)
    {
        this.currentProjectName = currentProjectName;
    }
    //-------------------------------------------------------------------
    public int getProjectCount()
    {
        return projectNameList.size();
    }
    //-------------------------------------------------------------------
    public int getTagCount()
    {
        return tagList.size();
    }
    //-------------------------------------------------------------------
    public boolean appendProjectName(String newName)
    {
        boolean res = false;
        int size = projectNameList.size();
        projectNameList.add(newName);
        if(projectNameList.size() == ++size)
        {
            res = true;
        }
        return res;
    }
    //-------------------------------------------------------------------
    public ArrayList<String> getProjectNameList()
    {
        return projectNameList;
    }
    //-------------------------------------------------------------------
    public void setProjectNameList(ArrayList<String> projectNameList)
    {
        this.projectNameList = projectNameList;
    }
    //-------------------------------------------------------------------
    public ArrayList<String> getTagList()
    {
        return tagList;
    }
    //-------------------------------------------------------------------
    public void setTagList(ArrayList<String> tagList) {
        this.tagList = tagList;
    }
}
