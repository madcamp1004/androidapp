package com.example.q.myapplication;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
  EditText et;
  EditText et2;
  Button addbtn;
  Button mdfbtn;
  Button delbtn;
  TextView tv;
  StringBuffer sb = new StringBuffer();

  ArrayList<String> items;
  ArrayAdapter adapter;
  @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        items = new ArrayList<String>();
        adapter = new ArrayAdapter(this,android.R.layout.simple_list_item_single_choice,items);
        ListView listview = (ListView) findViewById(R.id.listview);
        listview.setAdapter(adapter);

        et = (EditText)findViewById(R.id.et);
        et2 = (EditText)findViewById(R.id.et2);
        addbtn = (Button)findViewById(R.id.addbtn);
        mdfbtn = (Button)findViewById(R.id.btn);
        delbtn = (Button)findViewById(R.id.delbtn);
        //tv = (TextView)findViewById(R.id.tv);

    }
    View.OnClickListener myClickListener = new View.OnClickListener(){
      @Override
        public void onClick(View v){
          String startJson ="[";
          String endJson = "]";
          if(!sb.toString().equals(""))
          {
              sb.append(",");
          }
          String temp = "{\"name\""+":"+"\""+et.getText().toString()+"\""+","
                  +"\"phone number\""+":"+"\""+et2.getText().toString()+"\""+"}";
          sb.append(temp);
          //tv.setText(startJson+sb+endJson);
          String temp2 = et.getText().toString()+" : "+et2.getText().toString();
          items.add(temp2);
          adapter.notifyDataSetChanged();
      }
    };
}