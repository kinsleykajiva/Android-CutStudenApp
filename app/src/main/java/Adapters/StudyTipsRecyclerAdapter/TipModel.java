package Adapters.StudyTipsRecyclerAdapter;

import java.util.ArrayList;

/**
 * Created by Kinsley Kajiva on 3/4/2017.
 */

public class TipModel {
    private String name;
    private ArrayList<TipExplanationModel> itemModels;

    public TipModel(String name, ArrayList<TipExplanationModel> itemModels) {
        this.name = name;
        this.itemModels = itemModels;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<TipExplanationModel> getItemModels() {
        return itemModels;
    }

    public void setItemModels(ArrayList<TipExplanationModel> itemModels) {
        this.itemModels = itemModels;
    }
}
