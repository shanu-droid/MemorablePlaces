package example.memorableplaces;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
   static ArrayList<String> arrayList;
   static ArrayList<LatLng> location;
   static  ArrayAdapter<String> arrayAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final ListView myList = findViewById(R.id.myList);
         arrayList = new ArrayList<>();
         location = new ArrayList<>();
        SharedPreferences sharedPreferences = this.getSharedPreferences("example.memorableplaces", Context.MODE_PRIVATE);
        ArrayList<String> latitude = new ArrayList<>();
        ArrayList<String> longitude = new ArrayList<>();
        latitude.clear();
        longitude.clear();
        arrayList.clear();
        location.clear();
        try {
            arrayList = (ArrayList<String>) ObjectSerializer.deserialize(sharedPreferences.getString("places",ObjectSerializer.serialize(new ArrayList<>())));
            latitude = (ArrayList<String>) ObjectSerializer.deserialize(sharedPreferences.getString("lats",ObjectSerializer.serialize(new ArrayList<>())));
            longitude = (ArrayList<String>) ObjectSerializer.deserialize(sharedPreferences.getString("lons",ObjectSerializer.serialize(new ArrayList<>())));
        } catch (Exception e) {
            e.printStackTrace();
        }
        if(arrayList.size() > 0 && latitude.size() > 0 && longitude.size() > 0){
            if(arrayList.size() == latitude.size() && arrayList.size() == longitude.size()){
                for(int i=0; i< arrayList.size() ; i++){
                    location.add(new LatLng(Double.parseDouble(latitude.get(i)),Double.parseDouble(longitude.get(i))));
                }
            }
        }
        else{
            arrayList.add("Add a new place.......");
            location.add(new LatLng(0,0));
        }
        arrayAdapter = new ArrayAdapter<>(this,android.R.layout.simple_list_item_1,arrayList);
        myList.setAdapter(arrayAdapter);
        myList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Toast.makeText(MainActivity.this, myArrayList.get(position), Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(getApplicationContext(), MapsActivity.class);
                    intent.putExtra("position",position);
                    startActivity(intent);


            }
        });



    }
}
