package com.example.taulant.businesscards;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.apache.commons.io.output.ByteArrayOutputStream;

import static android.view.View.GONE;

public class EditCardActivity extends AppCompatActivity {
    TextView nt;
    TextView pt;
    TextView et;
    TextView jt;
    TextView ct;
    ImageView it;
    Button button;
    Button delete;
    Button cancel;

    String tempName;
    String tempPhone;
    String tempEmail;
    String tempCompany;
    String tempJob;

    @Override
    protected void onCreate(Bundle savedInstanceState) {



        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_card);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setVisibility(GONE);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });

        Bundle extras = getIntent().getExtras();
        String name = extras.getString("nameMess");
        String job = extras.getString("jobMess");
        String company = extras.getString("companyMess");
        String phone = extras.getString("phoneMess");
        String email = extras.getString("emailMess");
        byte[] image = extras.getByteArray("imageResource");



        nt = (TextView) findViewById(R.id.nameBox);
        pt = (TextView) findViewById(R.id.phoneBox);
        et = (TextView) findViewById(R.id.emailBox);
        jt = (TextView) findViewById(R.id.jobBox);
        ct = (TextView) findViewById(R.id.companyBox);
        it = (ImageView) findViewById(R.id.imageView);
        button = (Button) findViewById(R.id.button4);
        delete = (Button) findViewById(R.id.button6);
        cancel = (Button) findViewById(R.id.button7);

        tempName = name;
        tempCompany = company;
        tempEmail = email;
        tempJob = job;
        tempPhone = phone;

        nt.setText(name);
        pt.setText(phone);
        et.setText(email);
        jt.setText(job);
        ct.setText(company);


        it.setImageBitmap(BitmapFactory.decodeByteArray(
                image, 0,
                image.length));
    }

    public void editCard(View v) {

        if (button.getText().equals("Edit")) {

            nt.setEnabled(true);
            pt.setEnabled(true);
            ct.setEnabled(true);
            et.setEnabled(true);
            jt.setEnabled(true);


        } else if (button.getText().equals("Save")) {


            BusinessCardSQLHelper mydb = new BusinessCardSQLHelper(this);

            ImageView image = (ImageView) findViewById(R.id.imageView);
            Drawable d = image.getDrawable();
            BitmapDrawable bitDw = ((BitmapDrawable) d);
            Bitmap bitmap = bitDw.getBitmap();
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            byte[] imageInByte = stream.toByteArray();

            Bundle extras = getIntent().getExtras();

            mydb.updateCard(extras.getString("pos"), new BusinessCard(nt.getText().toString(), pt.getText().toString(), et.getText().toString(), jt.getText().toString(), ct.getText().toString(), imageInByte, null));

            Intent intent = new Intent(this, CardList.class);
            startActivity(intent);
        }
        button.setText("Save");
        delete.setVisibility(View.VISIBLE);
        cancel.setVisibility(View.VISIBLE);
    }

    public void deleteCard(View v) {
        BusinessCardSQLHelper mydb = new BusinessCardSQLHelper(this);

        Bundle extras = getIntent().getExtras();
        mydb.deleteCard(extras.getString("pos"));

        Intent intent = new Intent(this, CardList.class);
        startActivity(intent);
    }

    public void cancelEdit(View v){
        nt.setEnabled(false);
        pt.setEnabled(false);
        ct.setEnabled(false);
        et.setEnabled(false);
        jt.setEnabled(false);


        nt.setText(tempName);
        pt.setText(tempPhone);
        et.setText(tempEmail);
        jt.setText(tempJob);
        ct.setText(tempCompany);

        button.setText("Edit");
        delete.setVisibility(GONE);
        cancel.setVisibility(GONE);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_cardList) {
            Intent intent = new Intent(this,CardList.class);
            startActivity(intent);
            //return true;
        }else if (id == R.id.action_addInstructions){
            Intent intent = new Intent(this, AddInstructions.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }
}
