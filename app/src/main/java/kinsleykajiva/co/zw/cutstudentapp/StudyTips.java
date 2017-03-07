package kinsleykajiva.co.zw.cutstudentapp;

import android.os.AsyncTask;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import Adapters.StudyTipsRecyclerAdapter.DataModel;
import Adapters.StudyTipsRecyclerAdapter.TipExplanationModel;
import Adapters.StudyTipsRecyclerAdapter.TipModel;
import Adapters.StudyTipsRecyclerAdapter.TipsAdapter;
import DBAccess.RealmDB.ExaminationDB;

public class StudyTips extends AppCompatActivity {

    private TipsAdapter mAdapter;
    private RecyclerView mRecyclerView;
    private  ArrayList<DataModel> data;
    //   ic_expand_less_blue_400_24dp

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_study_tips);
        initViews();
    }

    private void initViews() {
        getSupportActionBar().setTitle("Tips");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        mRecyclerView = (RecyclerView) findViewById(R.id.recycler);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        new AsyncTask<Void, Void, ArrayList<Pojo_Tips>>() {
            @Override
            protected ArrayList<Pojo_Tips> doInBackground(Void... voids) {
                return processJson(getTipsJson());

            }

            @Override
            protected void onPostExecute(ArrayList<Pojo_Tips> tipModels) {
                super.onPostExecute(tipModels);
                mAdapter = new TipsAdapter(StudyTips.this, makeDataItem(getData(tipModels)));
                mRecyclerView.setAdapter(mAdapter);
            }
        }.execute();

    }
    private String getTipsJson(){
        String json ;
        try {
            InputStream is = getAssets().open("study_tips.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }

    private  ArrayList<Pojo_Tips> processJson(String json){
        return (ArrayList<Pojo_Tips>) ( new Gson().fromJson(json, new TypeToken<ArrayList<Pojo_Tips>>() {}.getType()));
    }
    private ArrayList<TipModel> getData(ArrayList<Pojo_Tips> processedJson) {

        ArrayList<TipModel> Tip = new ArrayList<>();


        for(Pojo_Tips get:processedJson){
            ArrayList<TipExplanationModel> l = new ArrayList<>();

            l.add(new TipExplanationModel("explain "+get.getExplaination()));
            Tip.add(new TipModel("Title "+get.getTitle(), l));
        }


        return Tip;
    }//end of getData
    class Pojo_Tips implements Parcelable {
        public Pojo_Tips() {
        }

        public String getExplaination() {
            return explaination;
        }

        public String getTitle() {
            return title;
        }
        @SerializedName("tip_title")
        String title;
        @SerializedName("tip_explanation")
        String explaination;

        public Pojo_Tips(String explaination, String title) {
            this.explaination = explaination;
            this.title = title;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(this.title);
            dest.writeString(this.explaination);
        }

        protected Pojo_Tips(Parcel in) {
            this.title = in.readString();
            this.explaination = in.readString();
        }

        public  final Parcelable.Creator<Pojo_Tips> CREATOR = new Parcelable.Creator<Pojo_Tips>() {
            @Override
            public Pojo_Tips createFromParcel(Parcel source) {
                return new Pojo_Tips(source);
            }

            @Override
            public Pojo_Tips[] newArray(int size) {
                return new Pojo_Tips[size];
            }
        };
    }
    private ArrayList<DataModel> makeDataItem(ArrayList<TipModel> foodModels) {
        data = new ArrayList<>();
        if (foodModels != null && !foodModels.isEmpty()) {
            for (int i = 0; i < foodModels.size(); i++) {
                data.add(new DataModel(foodModels.get(i).getName(), 0));
                if (foodModels.get(i).getItemModels() != null && !foodModels.get(i).getItemModels().isEmpty()) {
                    for (int j = 0; j < foodModels.get(i).getItemModels().size(); j++) {
                        data.add(new DataModel(foodModels.get(i).getItemModels().get(j).getItemName(), 1));
                    }
                }
            }
        }
        return data;
    }

}
