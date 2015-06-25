package materia.aswifter.com.materialexample;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

/**
 * Created by chenyc on 2015/6/25.
 */
public class EditTextFLExampleActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edittext_fl);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("EditText Floating Labels");
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

//        TextInputLayout textInputEmail =(TextInputLayout)findViewById(R.id.textInputEmail);
//        textInputEmail.setErrorEnabled(true);
//        textInputEmail.setError("Error Message");
    }
}
