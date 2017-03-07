package Adapters.StudyTipsRecyclerAdapter;

/**
 * Created by Adil Shaikh on 4/2/16.
 */
public class DataModel {
    private String itemName;
    private int type;
    private boolean isCollapsed;

    public DataModel(String itemName, int type) {
        this.itemName = itemName;
        this.type = type;
    }

    public boolean isCollapsed() {
        return isCollapsed;
    }

    public void setIsCollapsed(boolean isCollapsed) {
        this.isCollapsed = isCollapsed;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
