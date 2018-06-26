package com.example.taulant.businesscards;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.microsoft.projectoxford.vision.VisionServiceClient;
import com.microsoft.projectoxford.vision.VisionServiceRestClient;
import com.microsoft.projectoxford.vision.contract.LanguageCodes;
import com.microsoft.projectoxford.vision.contract.Line;
import com.microsoft.projectoxford.vision.contract.OCR;
import com.microsoft.projectoxford.vision.contract.Region;
import com.microsoft.projectoxford.vision.contract.Word;
import com.microsoft.projectoxford.vision.rest.VisionServiceException;

import org.apache.commons.io.output.ByteArrayOutputStream;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;

import static android.view.View.GONE;

public class CapturedCard extends AppCompatActivity {
    BusinessCardSQLHelper mydb = new BusinessCardSQLHelper(this);

    private Uri mImageUri;
    private Bitmap mBitmap;
    private VisionServiceClient client;
    private TextView mTextView;
    private boolean errorFlag = false;

    private ArrayList<String> OCRout = new ArrayList<>();

    private TextView nameBox;
    private TextView phoneBox;
    private TextView emailBox;
    private TextView jobBox;
    private TextView companyBox;

    private static final int REQUEST_TAKE_PHOTO = 0;
    private static final int REQUEST_SELECT_IMAGE_IN_ALBUM = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        errorFlag = false;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_captured_card);

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

        //Get Intent
        Intent intent = getIntent();
        String message = intent.getStringExtra(AddInstructions.EXTRA_MESSAGE);

        if (client == null) {
            client = new VisionServiceRestClient("680a9739f224460e9fbcddac344c3b7c");
        }

        mTextView = (TextView) findViewById(R.id.textView);

        nameBox = (TextView) findViewById(R.id.nameBox);
        phoneBox = (TextView) findViewById(R.id.phoneBox);
        emailBox = (TextView) findViewById(R.id.emailBox);
        jobBox = (TextView) findViewById(R.id.jobBox);
        companyBox = (TextView) findViewById(R.id.companyBox);

        switch (message) {
            case "load":
                load();
                break;
            case "capture":
                capture();
                break;
        }

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

    public void capture() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
        }
    }

    public void load() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, REQUEST_SELECT_IMAGE_IN_ALBUM);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            mBitmap = (Bitmap) extras.get("data");
        }
        if (requestCode == REQUEST_SELECT_IMAGE_IN_ALBUM && resultCode == RESULT_OK) {
            // If image is selected successfully, set the image URI and bitmap.
            mImageUri = data.getData();
            mBitmap = ImageHelper.loadSizeLimitedBitmapFromUri(
                    mImageUri, getContentResolver());
        }

        if (mBitmap != null) {
            // Show the image on screen.
            ImageView imageView = (ImageView) findViewById(R.id.imageView);
            imageView.setImageBitmap(mBitmap);

            runOCR();
        }
    }

    public void runOCR() {
        Log.d("Run OCR", "Running OCR");
        try {
            new doRequest().execute();
        } catch (Exception e) {
            Log.d("Error", "Error encountered. Exception is: " + e.toString());
            errorFlag = true;

        }
    }

    private class doRequest extends AsyncTask<String, String, String> {
        // Store error message
        private Exception e = null;

        public doRequest() {
        }

        @Override
        protected String doInBackground(String... args) {
            try {
                return process();
            } catch (Exception e) {
                this.e = e;    // Store error
                errorFlag = true;
            }

            return null;
        }

        @Override
        protected void onPostExecute(String data) {
            super.onPostExecute(data);
            // Display based on error existence
            if (e != null) {
                Log.d("Error", "Error: " + e.getMessage());
                this.e = null;
                errorFlag = true;
            } else {
                Gson gson = new Gson();
                OCR r = gson.fromJson(data, OCR.class);

                String result = "";//"Result:\n";
                for (Region reg : r.regions) {
                    for (Line line : reg.lines) {
                        for (Word word : line.words) {
                            result += word.text+" ";
                        }
                        OCRout.add(result);

                        result = "";
                        //result += "\n";
                    }
                    //result += "\n\n";
                }


//                Log.d("Result:",OCRout.toString());
                displayOCR();
            }
        }
    }

    private String process() throws VisionServiceException, IOException {
        Gson gson = new Gson();

        // Put the image into an input stream for detection.
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, output);
        ByteArrayInputStream inputStream = new ByteArrayInputStream(output.toByteArray());

        OCR ocr;
        ocr = this.client.recognizeText(inputStream, LanguageCodes.AutoDetect, true);

        String result = gson.toJson(ocr);
        Log.d("result", result);

        return result;
    }

    public void saveButton(View V) {


        ImageView image = (ImageView) findViewById(R.id.imageView);
        Drawable d = image.getDrawable();
        BitmapDrawable bitDw = ((BitmapDrawable) d);
        Bitmap bitmap = bitDw.getBitmap();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] imageInByte = stream.toByteArray();

        try {
            mydb.insertCard(new BusinessCard(nameBox.getText().toString(), phoneBox.getText().toString(), emailBox.getText().toString(), jobBox.getText().toString(), companyBox.getText().toString(), imageInByte, null));
        } catch (Exception e) {
            Log.d("Error", "Error encountered. Exception is: " + e.toString());

        }
        Intent intent = new Intent(this, CardList.class);
        startActivity(intent);
    }


    private void displayOCR() {
        String tempItem;
        boolean[] tempCount = {false, false, false, false, false}; //{name, phone,email,job,company}
        for (int i = 0; i < OCRout.size(); i++) {
            tempItem = OCRout.get(i).trim();

            //Company
            if (tempCount[3] && !tempCount[4]){// && !tempItem.matches(".*[!@#$%^&*()_+-=/.,<>?;':]+.*")) {
                companyBox.setText(tempItem);
                tempCount[4] = true;
                OCRout.remove(i);
                i = 0;
                continue;
            }

            if (tempItem.matches(".*[@]+.*")) {//Email
                if (!tempCount[2]) {
                    if (tempItem.split(" ").length>1){
                        for (String item:tempItem.split(" ")){
                            if (item.matches(".*[@]+.*")){
                                emailBox.setText(item);
                                OCRout.remove(i);
                                tempCount[2] = true;
                                i = 0;
                                continue;
                            }
                        }
                    }
                    else{
                        emailBox.setText(tempItem);
                        OCRout.remove(i);
                        tempCount[2] = true;
                        i = 0;
                        continue;
                    }

                }


            } else if (!tempCount[1]) {//Phone
                String number = "";
                for (char item : tempItem.toCharArray()) {
                    if (Character.isDigit(item) &&number.length()<11) {
                        number += item;
                    }
                }
                if (number.length() >= 8) {
                    phoneBox.setText(number);
                    tempCount[1] = true;
                    OCRout.remove(i);
                    i = 0;
                    continue;
                }
            } else if (tempItem.split(" ").length <= 2 && tempItem.matches(".*[a-zA-Z]+.*") && !tempItem.matches(".*[!@#$%^&*()_+-=/.,<>?;':]+.*")) {
                if (!tempCount[0]) {//Name
                    nameBox.setText(tempItem);
                    OCRout.remove(i);
                    tempCount[0] = true;
                    i = 0;
                    continue;
                } else if (!tempCount[3]) {//Job
                    jobBox.setText(tempItem);
                    OCRout.remove(i);
                    tempCount[3] = true;
                    i = 0;
                    continue;
                }
            }



            if (tempCount[0] && tempCount[1] && tempCount[2] && tempCount[3] && tempCount[4]) {
                break;
            }

//            if (tempCount[3] ==0 || tempCount[4] ==0 && tempCount[0] == 1 && tempItem.indexOf('@') > tempItem.length()){
//                if(tempCount[4] == 0 && !tempItem.split(" ")[1].matches("[0-9]+")){
//                    companyBox.setText(tempItem);
//                    tempCount[4] = 1; //Company has been set
//                }
//                else if (tempCount[3] == 0  && tempItem.indexOf('.')> tempItem.length()&& tempItem.split(" ").length<4){
//                    jobBox.setText(tempItem);
//                    tempCount[3] = 1; //Job has been set
//                }
//            }
//
//            if (tempItem.indexOf('@')>=0 && tempCount[2] == 0){
//                emailBox.setText(tempItem);
//                tempCount[2] = 1;
//            }
//            else if (tempItem.split(" ").length == 2) {
//                if (tempCount[0] == 0) {
//                    nameBox.setText(tempItem);
//                    tempCount[0] = 1; //Name has been set
//                }
//                else if (tempCount[1] == 0){
//                    if (tempItem.indexOf('T') >= 0 || tempItem.indexOf('M') >=0 || tempItem.indexOf('+') >=0){
//                        if(tempItem.split(" ")[1].matches("[0-9]+")){
//                            phoneBox.setText(tempItem.split(" ")[1]);
//                            tempCount[1] = 1; //Phone has been set
//                        }
//                    }
//                }
//            }
        }
    }
}
