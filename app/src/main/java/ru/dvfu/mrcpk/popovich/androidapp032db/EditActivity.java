package ru.dvfu.mrcpk.popovich.androidapp032db;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class EditActivity extends AppCompatActivity implements View.OnClickListener {
    TextView textView;
    EditText editFirstName;
    EditText editLastName;
    Button buttonEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) throws NullPointerException {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        editFirstName = (EditText) findViewById(R.id.editFirstName);
        editLastName = (EditText) findViewById(R.id.editLastName);
        buttonEdit = (Button) findViewById(R.id.buttonEdit);

        textView = (TextView) findViewById(R.id.textView);
        Intent intent = getIntent();
        editFirstName.setText(intent.getExtras().getString("firstname"));
        editLastName.setText(intent.getExtras().getString("lastname"));
        textView.setText(intent.getExtras().getString("desc"));
        buttonEdit.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent intent1 = new Intent();
        intent1.putExtra("firstname", editFirstName.getText().toString());
        intent1.putExtra("lastname", editLastName.getText().toString());
        setResult(RESULT_OK, intent1);
        finish();
    }
}
