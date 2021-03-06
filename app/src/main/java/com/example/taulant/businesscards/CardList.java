package com.example.taulant.businesscards;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;

import static android.view.View.GONE;

public class CardList extends AppCompatActivity {
    ArrayList<BusinessCard> cards = new ArrayList<>();
    BusinessCardSQLHelper mydb = new BusinessCardSQLHelper(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_list);
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

        cards = mydb.getAllCards();

        if(!cards.isEmpty()) {
            BusinessCardAdapter adapter = new BusinessCardAdapter(
                    this, R.layout.my_listview_item, cards);

            ListView listView = (ListView) findViewById(R.id.listView);
            listView.setAdapter(adapter);

            listView.setOnItemClickListener(
                    new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            BusinessCard card = cards.get(position);
                            Intent intent = new Intent(view.getContext(), EditCardActivity.class);
                            intent.putExtra("nameMess", card.name);
                            intent.putExtra("imageResource", card.image);
                            intent.putExtra("jobMess", card.job);
                            intent.putExtra("companyMess", card.company);
                            intent.putExtra("phoneMess", card.phone);
                            intent.putExtra("emailMess", card.email);
                            intent.putExtra("pos",card.id);
                            startActivity(intent);
                        }
                    });
        }
    }

    public void insturctionsView(View v){
        Intent intent = new Intent(this, AddInstructions.class);
        startActivity(intent);
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
//        if (id == R.id.action_cardList) {
//            Intent intent = new Intent(this,CardList.class);
//            startActivity(intent);
//            //return true;
//        }
        if (id == R.id.action_addInstructions){
            Intent intent = new Intent(this, AddInstructions.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

}
