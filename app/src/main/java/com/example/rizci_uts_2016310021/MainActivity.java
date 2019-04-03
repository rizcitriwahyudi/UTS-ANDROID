package com.example.rizci_uts_2016310021;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListAdapter;

import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.example.rizci_uts_2016310021.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {
    private String TAG  = MainActivity.class.getSimpleName();
    private ListView lv;
    private ProgressDialog pDialog;

    private static String url = "https://api.banghasan.com/sholat/format/json/kota";
    ArrayList<HashMap<String, String>> JsonList;
    private static String jadwalsholat = "https://api.banghasan.com/sholat/format/json/jadwal/kota/id_kota/tanggal/yyyy-mm-dd";
    ArrayList<HashMap<String, String>> JsonJadwalSholat;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        JsonList = new ArrayList<>();
        lv = (ListView)findViewById(R.id.list);
        new Getcontacts().execute();
    }



    private class Getcontacts extends AsyncTask<Void, Void, Void> {


        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(MainActivity.this);
            pDialog.setMessage("Please wait ......");
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            HttpHandler sh = new HttpHandler();
            String jsonStr = sh.makeServiceCall(url);
            Log.e(TAG, "Response from url:" + jsonStr);

            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);

                    JSONArray contacts = jsonObj.getJSONArray("kota");

                    for (int i = 0; i < contacts.length(); i++) {
                        JSONObject c = contacts.getJSONObject(i);

                        String id = c.getString("id");
                        String nama = c.getString("nama");

                        HashMap<String, String> contact = new HashMap<>();

                        contact.put("id", id);
                        contact.put("nama", nama);

                        JsonList.add(contact);
                    }
                } catch (final JSONException e) {
                    Log.e(TAG, "Json parsing error:" + e.getMessage());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), "Json parsing error:" + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            } else {
                Log.e(TAG, "Couldn't get json from server.");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(), "Couldn't get json from server. Check LogCat for possible errors!", Toast.LENGTH_SHORT).show();

                    }
                });
            }
            return null;
        }

        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            if (pDialog.isShowing())
                pDialog.dismiss();

            ListAdapter adapter = new SimpleAdapter(MainActivity.this, JsonList, R.layout.list_item, new String[]{"id", "nama"}, new int[]{R.id.id, R.id.nama});
            lv.setAdapter(adapter);
        }
    }
}
