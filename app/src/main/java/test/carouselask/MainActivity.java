package test.carouselask;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.daimajia.slider.library.Animations.DescriptionAnimation;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.TextSliderView;
import com.daimajia.slider.library.Tricks.ViewPagerEx;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;

import test.carouselask.VolleyClasses.ResponseListener;
import test.carouselask.adapters.MainRecyclerAdapter;


public class MainActivity extends ActionBarActivity implements BaseSliderView.OnSliderClickListener, ViewPagerEx.OnPageChangeListener{

    public static final String LOGTAG ="carousels";
    private SliderLayout mDemoSlider;
    int numOfRows = 11;
    int numOfColumns = 9;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private LinearLayoutManager mLayoutManager;
    @Inject
    MockCategoryApi categoriesApi;
    private static final String TAG = "First Activity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mDemoSlider = (SliderLayout)findViewById(R.id.slider);

        HashMap<String,String> url_maps = new HashMap<String, String>();
        url_maps.put("Hannibal", "http://static2.hypable.com/wp-content/uploads/2013/12/hannibal-season-2-release-date.jpg");
        url_maps.put("Big Bang Theory", "http://tvfiles.alphacoders.com/100/hdclearart-10.png");
        url_maps.put("House of Cards", "http://cdn3.nflximg.net/images/3093/2043093.jpg");
        url_maps.put("Game of Thrones", "http://images.boomsbeat.com/data/images/full/19640/game-of-thrones-season-4-jpg.jpg");

        HashMap<String,Integer> file_maps = new HashMap<String, Integer>();
        file_maps.put("Hannibal",R.drawable.hannibal);
        file_maps.put("Big Bang Theory",R.drawable.bigbang);
        file_maps.put("House of Cards",R.drawable.house);
        file_maps.put("Game of Thrones", R.drawable.game_of_thrones);
        //loadCategories();
        for(String name : file_maps.keySet()){
            TextSliderView textSliderView = new TextSliderView(this);
            // initialize a SliderLayout
            textSliderView
                    .description(name)
                    .image(file_maps.get(name))
                    .setScaleType(BaseSliderView.ScaleType.Fit)
                    .setOnSliderClickListener(this);

            //add your extra information
            textSliderView.bundle(new Bundle());
            textSliderView.getBundle()
                    .putString("extra",name);

            mDemoSlider.addSlider(textSliderView);
        }
        mDemoSlider.setPresetTransformer(SliderLayout.Transformer.Accordion);
        mDemoSlider.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom);
        mDemoSlider.setCustomAnimation(new DescriptionAnimation());
        mDemoSlider.setDuration(4000);
        mDemoSlider.addOnPageChangeListener(this);

        mRecyclerView = (RecyclerView)findViewById(R.id.recyclerview_rootview);
        mLayoutManager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setHasFixedSize(true);

        List<List<Item>> listOfListOfItems = new ArrayList<List<Item>>();

        for(int i = 0 ; i<numOfRows ; i++){
            List<Item> listOfItems = new ArrayList<Item>();
            for(int j = 0 ; j<numOfColumns ; j++){
                int drawableResourceId = this.getResources().getIdentifier("img"+String.valueOf(1+j + (10*i)), "drawable", this.getPackageName());
                Item item = new Item("img"+String.valueOf(j+1),String.valueOf(1+j + (10*i)),drawableResourceId);
                listOfItems.add(item);
            }
            listOfListOfItems.add(listOfItems);
        }

        MainRecyclerAdapter mainRecyclerAdapter = new MainRecyclerAdapter(listOfListOfItems,this);
        mRecyclerView.setAdapter(mainRecyclerAdapter);

        mRecyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged (RecyclerView recyclerView, int newState){

                switch(newState){

                    case RecyclerView.SCROLL_STATE_IDLE:

                        Log.i(LOGTAG,"X = " + (mRecyclerView.getX() + mRecyclerView.getWidth() )+ " and Y = " + (mRecyclerView.getY()+ mRecyclerView.getHeight()));
                        float targetBottomPosition1 = mRecyclerView.getY();
                        float targetBottomPosition2 = mRecyclerView.getY() + mRecyclerView.getHeight();

                        Log.i(LOGTAG,"targetBottomPosition1 = " + targetBottomPosition1);
                        Log.i(LOGTAG,"targetBottomPosition2 = " + targetBottomPosition2);

                        View v1 = mRecyclerView.findChildViewUnder(500, targetBottomPosition1);
                        View v2 = mRecyclerView.findChildViewUnder(500, targetBottomPosition2);

                        float y1 = targetBottomPosition1;
                        if(v1!=null){
                            y1 =v1.getY();
                        }

                        float y2 = targetBottomPosition2;
                        if(v2!=null){
                            y2 =v2.getY();
                        }

                        Log.i(LOGTAG,"y1 = " + y1);
                        Log.i(LOGTAG,"y2 = " + y2);

                        float dy1 = Math.abs(y1-mRecyclerView.getY() );
                        float dy2 = Math.abs(y2-(mRecyclerView.getY()+ mRecyclerView.getHeight()));

                        Log.i(LOGTAG,"dy1 = " + dy1);
                        Log.i(LOGTAG,"dy2 = " + dy2);

                        float visiblePortionOfItem1 = 0;
                        float visiblePortionOfItem2 = 0;

                        if(y1<0 && v1 != null){
                            visiblePortionOfItem1 = v1.getHeight() - dy1;
                        }

                        if(v2 != null){
                            visiblePortionOfItem2 = v2.getHeight() - dy2;
                        }


                        int position = 0;
                        if(visiblePortionOfItem1<=visiblePortionOfItem2){
                            position = mRecyclerView.getChildPosition(mRecyclerView.findChildViewUnder(500, targetBottomPosition1));
                        }else{

                            position = mRecyclerView.getChildPosition(mRecyclerView.findChildViewUnder(500, targetBottomPosition2));
                        }
                        mRecyclerView.scrollToPosition(position);

                        break;

                    case RecyclerView.SCROLL_STATE_DRAGGING:

                        break;

                    case RecyclerView.SCROLL_STATE_SETTLING:

                        break;

                }
            }

            @Override
            public void onScrolled (RecyclerView recyclerView, int dx, int dy){

//				Log.i(LOGTAG,"X = " + dx + " and Y = " + dy);
            }
        });
    }

    @Override
    protected void onStop() {
        // To prevent a memory leak on rotation, make sure to call stopAutoCycle() on the slider before activity or fragment is destroyed
        mDemoSlider.stopAutoCycle();
        super.onStop();
    }

    @Override
    public void onSliderClick(BaseSliderView slider) {
        Toast.makeText(this,slider.getBundle().get("extra") + "",Toast.LENGTH_SHORT).show();
    }


    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

    @Override
    public void onPageSelected(int position) {
        Log.d("Slider Demo", "Page Changed: " + position);
    }

    @Override
    public void onPageScrollStateChanged(int state) {}

    private void loadCategories() {

        ResponseListener<JsonObject> responseListener = new ResponseListener<JsonObject>() {
            @Override
            public void onResponse(JsonObject result) {
                if(result != null){
                    if(result.has("template")) {

                    }
                }else{
                    //do something
                }
            }

            @Override
            public void onErrorResponse(VolleyError e) {
                Log.e(TAG, "loadCategories " + e.getMessage());
                if (e instanceof TimeoutError || e instanceof NoConnectionError || e instanceof NetworkError) {
                    //do something
                } else {
                    //do something
                }
            }
        };

        categoriesApi.loadCategories(responseListener);
    }
}