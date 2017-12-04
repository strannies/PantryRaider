package com.example.stevetran.pantryraider.Pantry;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.stevetran.pantryraider.R;
import com.example.stevetran.pantryraider.Search.SearchFragment;
import com.example.stevetran.pantryraider.Util.BottomNavigationHelper;
import com.example.stevetran.pantryraider.Util.SharedConstants;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by rongfalu on 11/30/17.
 */

public class RecipeActivity extends AppCompatActivity {

    private static final int ACTIVITY_NUM = 2;
    private Context mContext = RecipeActivity.this;

    //private View = R.layout.activity_recipe;
    private ImageView mImage;
    private TextView mName;
    private ListView mListIngredients;
    private ListView mSteps;

    private String image_url = "https://spoonacular.com/recipeImages/615348-556x370.jpg";
    private String name = "What";

    private String rid;

    ArrayAdapter<String> adapter_ingredinets;
    ArrayAdapter<String> adapter_steps;
    ArrayList<String> ListIngredients = new ArrayList<>();
    ArrayList<String> Steps = new ArrayList<>();

    private DatabaseReference mDatabase;
    private DatabaseReference relPath;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //set up the bottom navigation view
        //setupBottomNavigationView();

        Intent myIntent = getIntent();
        rid = myIntent.getStringExtra("rid");

        makeRequest(rid);

        setContentView(R.layout.activity_recipe);
        mDatabase = FirebaseDatabase.getInstance().getReference();


        mImage = findViewById(R.id.image_detail);
        mName = findViewById(R.id.recipename_detail);
        mListIngredients = findViewById(R.id.IngredientList_detail);
        mSteps = findViewById(R.id.Steps);

        adapter_ingredinets = new ArrayAdapter<String>(
                this,
                android.R.layout.simple_list_item_1,
                ListIngredients
        );
        mListIngredients.setAdapter(adapter_ingredinets);

        adapter_steps = new ArrayAdapter<String>(
                this,
                android.R.layout.simple_list_item_1,
                Steps
        );
        mSteps.setAdapter(adapter_steps);

    }

    private void makeRequest(final String rid) {
        RequestQueue queue = Volley.newRequestQueue(this);
        String url ="http://54.175.239.59:8080/recipe_detail";


        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.
                        JSONObject json = null;
                        try {
                            json = new JSONObject(response);

                            image_url = json.getString("image_url");
                            name = json.getString("title");
                            JSONArray ing = json.getJSONArray("ingredients");
                            for(int i = 0; i < ing.length(); i++) {
                                ListIngredients.add(ing.getJSONObject(i).getString("string"));
                            }
                            JSONArray instructions = json.getJSONArray("instructions");
                            for(int i = 0; i < instructions.length(); i++) {
                                Steps.add(instructions.getString(i));
                            }
                            mName.setText(name);
                            Picasso.with(mContext)
                                    .load(image_url)
                                    .into(mImage);

                            adapter_ingredinets.notifyDataSetChanged();
                            adapter_steps.notifyDataSetChanged();
                        } catch (JSONException e) {

                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //mTextView.setText("That didn't work!");
            }

        }) {
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();
                params.put("rid",rid);

                return params;
            }

        };

        // Add the request to the RequestQueue.
        queue.add(stringRequest);

    }
    public void saveToDB(View view){
        String key = SharedConstants.FIREBASE_USER_ID;
        Toast.makeText(RecipeActivity.this, "Recipe saved!",
                Toast.LENGTH_SHORT).show();
        mDatabase.child("/Saved_Recipes/" + key + "/").child("r"+rid).setValue(name);
        //mDatabase.child("/Account/"+key+"/Ingredients/").child(ingredient).setValue(ings.get(ingredient));


    }

    /**
     * BottomNavigationView setup
     */
    private void setupBottomNavigationView(){
        BottomNavigationViewEx bottomNavigationViewEx = (BottomNavigationViewEx) findViewById(R.id.bottomNavViewBar);
        BottomNavigationHelper.setupBottomNavigationView(bottomNavigationViewEx);
        BottomNavigationHelper.enableNavigation(mContext, bottomNavigationViewEx, this);
        Menu menu = bottomNavigationViewEx.getMenu();
        MenuItem menuItem = menu.getItem(ACTIVITY_NUM);
        menuItem.setChecked(true);
    }
}