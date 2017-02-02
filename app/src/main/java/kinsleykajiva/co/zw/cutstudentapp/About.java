package kinsleykajiva.co.zw.cutstudentapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

public class About extends AppCompatActivity {
    private TextView toAckknowdge;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toAckknowdge = (TextView) findViewById(R.id.toAckknowdge);
        if (getIntent().hasExtra("Settinngs-licenses")) {
            getSupportActionBar().setTitle("Library Licenses");

            toAckknowdge.setText(R.string.library_licence);
        } else if (getIntent().hasExtra("Settinngs-bugs")) {
            getSupportActionBar().setTitle("Known Bugs");
            toAckknowdge.setText(R.string.known_Bugs);
        } else {
            getSupportActionBar().setTitle("About");
            toAckknowdge.setText(R.string.AboutApp);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
