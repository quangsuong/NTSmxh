package net.blogsv.ui.Activities;


import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.peekandpop.shalskar.peekandpop.PeekAndPop;

import net.blogsv.Adapters.VideoAdapter;
import net.blogsv.Provider.PrefManager;
import net.blogsv.api.apiClient;
import net.blogsv.api.apiRest;
import net.blogsv.model.Category;
import net.blogsv.model.User;
import net.blogsv.model.Video;
import net.blogsv.ntsmxh.R;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class SearchActivity extends AppCompatActivity {


    private String query;

    private Integer page = 0;
    private String language = "0";
    private Boolean loaded = false;

    private SwipeRefreshLayout swipe_refreshl_image_search;
    private RecyclerView recycler_view_image_search;
    private List<Video> VideoList = new ArrayList<>();
    private List<Category> categoryList = new ArrayList<>();
    private VideoAdapter videoAdapter;
    private LinearLayoutManager linearLayoutManager;
    private PrefManager prefManager;
    private boolean loading = true;
    private int pastVisiblesItems, visibleItemCount, totalItemCount;
    private RelativeLayout relative_layout_load_more;
    private LinearLayout linear_layout_page_error;
    private Button button_try_again;
    private ImageView imageView_empty_favorite;
    private PeekAndPop peekAndPop;

    private Integer item = 0;
    private Integer lines_beetween_ads = 8;
    private Boolean native_ads_enabled = false;
    private List<User> userList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getIntent().getExtras();
        this.query = bundle.getString("query");
        this.prefManager = new PrefManager(getApplicationContext());
        this.language = prefManager.getString("LANGUAGE_DEFAULT");

        setContentView(R.layout.activity_search);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(query);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        initView();
        loadUsers();
        showAdsBanner();
        initAction();

    }

    private void showAdsBanner() {
        if (prefManager.getString("SUBSCRIBED").equals("FALSE")) {
            final AdView mAdView = (AdView) findViewById(R.id.adView);
            AdRequest adRequest = new AdRequest.Builder()
                    .build();

            // Start loading the ad in the background.
            mAdView.loadAd(adRequest);

            mAdView.setAdListener(new AdListener() {
                @Override
                public void onAdLoaded() {
                    super.onAdLoaded();
                    mAdView.setVisibility(View.VISIBLE);
                }
            });
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.back_enter, R.anim.back_exit);
        return;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                super.onBackPressed();
                overridePendingTransition(R.anim.back_enter, R.anim.back_exit);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void initView() {
        if (getResources().getString(R.string.FACEBOOK_ADS_ENABLED_NATIVE).equals("true")) {
            native_ads_enabled = true;
            lines_beetween_ads = Integer.parseInt(getResources().getString(R.string.FACEBOOK_ADS_ITEM_BETWWEN_ADS));
        }
        PrefManager prefManager = new PrefManager(getApplicationContext());
        if (prefManager.getString("SUBSCRIBED").equals("TRUE")) {
            native_ads_enabled = false;
        }
        this.imageView_empty_favorite = (ImageView) findViewById(R.id.imageView_empty_favorite);
        this.relative_layout_load_more = (RelativeLayout) findViewById(R.id.relative_layout_load_more);
        this.button_try_again = (Button) findViewById(R.id.button_try_again);
        this.swipe_refreshl_image_search = (SwipeRefreshLayout) findViewById(R.id.swipe_refreshl_image_search);
        this.linear_layout_page_error = (LinearLayout) findViewById(R.id.linear_layout_page_error);
        this.recycler_view_image_search = (RecyclerView) findViewById(R.id.recycler_view_image_search);
        this.linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);

        this.peekAndPop = new PeekAndPop.Builder(this)
                .parentViewGroupToDisallowTouchEvents(recycler_view_image_search)
                .peekLayout(R.layout.dialog_view)
                .build();
        videoAdapter = new VideoAdapter(VideoList, null, this, peekAndPop, true, false, userList);
        recycler_view_image_search.setHasFixedSize(true);
        recycler_view_image_search.setAdapter(videoAdapter);
        recycler_view_image_search.setLayoutManager(linearLayoutManager);
        recycler_view_image_search.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0) //check for scroll down
                {

                    visibleItemCount = linearLayoutManager.getChildCount();
                    totalItemCount = linearLayoutManager.getItemCount();
                    pastVisiblesItems = linearLayoutManager.findFirstVisibleItemPosition();

                    if (loading) {
                        if ((visibleItemCount + pastVisiblesItems) >= totalItemCount) {
                            loading = false;
                            loadNextVideo();
                        }
                    }
                } else {

                }
            }
        });

    }

    public void initAction() {
        swipe_refreshl_image_search.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                VideoList.clear();
                page = 0;
                item = 0;
                loading = true;
                loadUsers();
            }
        });
        button_try_again.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadUsers();
            }
        });
    }

    public void loadNextVideo() {
        relative_layout_load_more.setVisibility(View.VISIBLE);
        Retrofit retrofit = apiClient.getClient();
        apiRest service = retrofit.create(apiRest.class);
        Call<List<Video>> call = service.searchImage(prefManager.getString("ORDER_DEFAULT_STATUS"), language, page, query);
        call.enqueue(new Callback<List<Video>>() {
            @Override
            public void onResponse(Call<List<Video>> call, Response<List<Video>> response) {
                apiClient.FormatData(SearchActivity.this, response);

                if (response.isSuccessful()) {
                    if (response.body().size() != 0) {
                        for (int i = 0; i < response.body().size(); i++) {
                            VideoList.add(response.body().get(i));
                            if (native_ads_enabled) {
                                item++;
                                if (item == lines_beetween_ads) {
                                    item = 0;
                                    VideoList.add(new Video().setViewType(3));
                                }
                            }
                        }
                        videoAdapter.notifyDataSetChanged();
                        page++;
                        loading = true;

                    } else {

                    }
                    relative_layout_load_more.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(Call<List<Video>> call, Throwable t) {
                relative_layout_load_more.setVisibility(View.GONE);
            }
        });
    }

    public void loadVideo() {
        imageView_empty_favorite.setVisibility(View.GONE);
        swipe_refreshl_image_search.setRefreshing(true);
        linear_layout_page_error.setVisibility(View.GONE);
        recycler_view_image_search.setVisibility(View.GONE);

        Retrofit retrofit = apiClient.getClient();
        apiRest service = retrofit.create(apiRest.class);
        Call<List<Video>> call = service.searchImage(prefManager.getString("ORDER_DEFAULT_STATUS"), language, page, query);
        call.enqueue(new Callback<List<Video>>() {
            @Override
            public void onResponse(Call<List<Video>> call, Response<List<Video>> response) {
                swipe_refreshl_image_search.setRefreshing(false);
                apiClient.FormatData(SearchActivity.this, response);
                if (response.isSuccessful()) {
                    if (response.body().size() != 0) {
                        for (int i = 0; i < response.body().size(); i++) {
                            VideoList.add(response.body().get(i));
                            if (native_ads_enabled) {
                                item++;
                                if (item == lines_beetween_ads) {
                                    item = 0;
                                    VideoList.add(new Video().setViewType(3));
                                }
                            }
                        }
                        videoAdapter.notifyDataSetChanged();
                        page++;
                        loaded = true;
                    }

                    if (VideoList.size() == 0) {
                        imageView_empty_favorite.setVisibility(View.VISIBLE);
                        recycler_view_image_search.setVisibility(View.GONE);
                        linear_layout_page_error.setVisibility(View.GONE);
                    } else {
                        imageView_empty_favorite.setVisibility(View.GONE);
                        recycler_view_image_search.setVisibility(View.VISIBLE);
                        linear_layout_page_error.setVisibility(View.GONE);
                    }
                    videoAdapter.notifyDataSetChanged();


                } else {
                    imageView_empty_favorite.setVisibility(View.GONE);
                    recycler_view_image_search.setVisibility(View.GONE);
                    linear_layout_page_error.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(Call<List<Video>> call, Throwable t) {
                swipe_refreshl_image_search.setRefreshing(false);
                recycler_view_image_search.setVisibility(View.GONE);
                linear_layout_page_error.setVisibility(View.VISIBLE);
                imageView_empty_favorite.setVisibility(View.GONE);

            }
        });
    }

    public void loadUsers() {
        swipe_refreshl_image_search.setRefreshing(true);
        Retrofit retrofit = apiClient.getClient();
        apiRest service = retrofit.create(apiRest.class);
        Call<List<User>> call = service.searchUsers(query);
        call.enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                VideoList.clear();
                userList.clear();
                if (response.isSuccessful()) {
                    if (response.body().size() > 0) {
                        VideoList.add(new Video().setViewType(6));
                        for (int i = 0; i < response.body().size(); i++) {
                            userList.add(response.body().get(i));
                        }
                        videoAdapter.notifyDataSetChanged();
                    }
                }
                loadVideo();
                swipe_refreshl_image_search.setRefreshing(false);
            }

            @Override
            public void onFailure(Call<List<User>> call, Throwable t) {
                loadVideo();
                swipe_refreshl_image_search.setRefreshing(false);
            }
        });

    }
}
