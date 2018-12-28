package com.example.q.myapplication;

import android.Manifest;
import android.content.ContentProvider;
import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.Context;
import android.content.OperationApplicationException;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.VolumeShaper;
import android.media.audiofx.Visualizer;
import android.os.Build;
import android.os.RemoteException;
import android.provider.ContactsContract;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
  EditText et;
  EditText et2;
  Button btn;
  Button plusbtn;
   TextView tv;
  StringBuffer sb = new StringBuffer();

  ArrayList<String> items;
  ArrayAdapter adapter;
  ArrayList<Map<String, String>> dataList;
  ListView listview;

  @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        items = new ArrayList<String>();
        adapter = new ArrayAdapter(this,android.R.layout.simple_list_item_single_choice,items);
        listview = (ListView) findViewById(R.id.listview);
        listview.setAdapter(adapter);

        et = (EditText)findViewById(R.id.et);
        et2 = (EditText)findViewById(R.id.et2);
        btn = (Button)findViewById(R.id.btn);
        //tv = (TextView)findViewById(R.id.tv);
        btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v){
                if (Permissioncheck() == true) {
                    insertContacts(et.getText().toString(), et2.getText().toString());
                    String temp2 = et.getText().toString()+" : "+et2.getText().toString();
                    items.add(temp2);
                    adapter.notifyDataSetChanged();
                }
            }
        });
      //  plusbtn =(Button)findViewById(R.id.plusbtn);
        if(Permissioncheck() == true){
            loadContacts();
        }
   }
    @Override
    public int checkSelfPermission(String permission) {
        return super.checkSelfPermission(permission);
    }

    private boolean Permissioncheck(){
      if(checkSelfPermission(Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED){
          return true;
      }
      else{
          ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.READ_CONTACTS},1);
          if(checkSelfPermission(Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED){
              return true;
          }
          else{
              return false;
          }
      }
    }

  /*  View.OnClickListener myClickListener = new View.OnClickListener(){
      @Override
        public void onClick(View v) {
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
          insertContacts(et.getText().toString(),et2.getText().toString());
      }
};*/
private void loadContacts(){
    ContentResolver cr = getContentResolver();
    Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI,null,null,null,null);
    //check there exists contact
    //cur : includes every contact
    if(cur.getCount()>0) {
        while(cur.moveToNext()){
            String id = cur.getString(cur.getColumnIndex(ContactsContract.Contacts._ID));
            String name = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
            //check whether there exists phone number
            if(Integer.parseInt(cur.getString(cur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)))>0){
                Cursor pCur = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,null,ContactsContract.CommonDataKinds.Phone.CONTACT_ID+"=?",new String[]{id},null);
                //if there are multiple contacts per id
                while(pCur.moveToNext()){
                    String phoneNo = pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                    String temp3 = name+": "+phoneNo;
                    items.add(temp3);
                    adapter.notifyDataSetChanged();
                }
                pCur.close();
            }
        }
    }
}
private void insertContacts(String name,String phone) {
    ContentResolver cr = getContentResolver();

    Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI,
            null, null, null, null);

    if (cur.getCount() > 0) {
        while (cur.moveToNext()) {
            String existName = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
            if (existName.contains(name)) {
                //Toast.makeText(NativeContentProvider.this,"The contact name: " + name + " already exists", Toast.LENGTH_SHORT).show();
                return;
            }
        }
    }
    if (Permissioncheck() == true) {
        ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();
        int rawContactInsertIndex = ops.size();

        ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, rawContactInsertIndex)
                .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
                .withValue(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, name)
                .build());

        ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, rawContactInsertIndex)
                .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, phone)
                .withValue(ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_HOME)
                .build());

        try {
            cr.applyBatch(ContactsContract.AUTHORITY, ops);
        } catch(Exception e){
            e.printStackTrace();
        }
    }
}
}