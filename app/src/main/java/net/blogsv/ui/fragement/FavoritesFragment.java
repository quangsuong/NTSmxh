package net.blogsv.ui.fragement;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.peekandpop.shalskar.peekandpop.PeekAndPop;

import net.blogsv.Adapters.VideoAdapter;
import net.blogsv.Provider.FavoritesStorage;
import net.blogsv.Provider.PrefManager;
import net.blogsv.model.Video;
import net.blogsv.ntsmxh.R;

import java.util.ArrayList;
import java.util.List;


public class FavoritesFragment extends Fragment {


    private RelativeLayout activity_favorites;
    private RecyclerView recycle_view_home_favorite;
    private ImageView imageView_empty_favorite;
    private SwipeRefreshLayout swipe_refreshl_home_favorite;
    private List<Video> VideoList = new ArrayList<Video>();
    private VideoAdapter videoAdapter;

    private View view;
    private GridLayoutManager gridLayoutManager;
    private PrefManager prf;
    private PeekAndPop peekAndPop;
    private Integer item = 0;
    private Integer lines_beetween_ads = 8;
    private Boolean native_ads_enabled = false;

    public FavoritesFragment() {
        // Required empty public constructor
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        if (isVisibleToUser) {
            item = 0;
            getStatus();

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_favorites, container, false);
        this.prf = new PrefManager(getActivity().getApplicationContext());

        iniView(view);
        initAction();
        // getStatus();
        return view;
    }


    public void iniView(View view) {

        if (getResources().getString(R.string.FACEBOOK_ADS_ENABLED_NATIVE).equals("true")) {
            native_ads_enabled = true;
            lines_beetween_ads = Integer.parseInt(getResources().getString(R.string.FACEBOOK_ADS_ITEM_BETWWEN_ADS));
        }
        PrefManager prefManager = new PrefManager(getActivity().getApplicationContext());
        if (prefManager.getString("SUBSCRIBED").equals("TRUE")) {
            native_ads_enabled = false;
        }
        this.activity_favorites = (RelativeLayout) view.findViewById(R.id.activity_favorites);

        this.recycle_view_home_favorite = (RecyclerView) view.findViewById(R.id.recycle_view_home_favorite);
        this.swipe_refreshl_home_favorite = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refreshl_home_favorite);
        this.imageView_empty_favorite = (ImageView) view.findViewById(R.id.imageView_empty_favorite);


        this.gridLayoutManager = new GridLayoutManager(getActivity().getApplicationContext(), 1, GridLayoutManager.VERTICAL, false);


        this.recycle_view_home_favorite = (RecyclerView) this.view.findViewById(R.id.recycle_view_home_favorite);
        this.swipe_refreshl_home_favorite = (SwipeRefreshLayout) this.view.findViewById(R.id.swipe_refreshl_home_favorite);

        this.peekAndPop = new PeekAndPop.Builder(getActivity())
                .parentViewGroupToDisallowTouchEvents(recycle_view_home_favorite)
                .peekLayout(R.layout.dialog_view)
                .build();
        videoAdapter = new VideoAdapter(VideoList, null, getActivity(), peekAndPop, true);
        recycle_view_home_favorite.setHasFixedSize(true);
        recycle_view_home_favorite.setAdapter(videoAdapter);
        recycle_view_home_favorite.setLayoutManager(gridLayoutManager);
    }

    public void initAction() {
        this.swipe_refreshl_home_favorite.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                item = 0;
                getStatus();
            }
        });
    }

    private void getStatus() {

        swipe_refreshl_home_favorite.setRefreshing(true);
        final FavoritesStorage storageFavorites = new FavoritesStorage(getActivity().getApplicationContext());
        List<Video> statuses = storageFavorites.loadImagesFavorites();

        if (statuses == null) {
            statuses = new ArrayList<>();
        }
        if (statuses.size() != 0) {
            VideoList.clear();
            for (int i = 0; i < statuses.size(); i++) {
                Video a = new Video();
                a = statuses.get(i);
                VideoList.add(a);
                if (native_ads_enabled) {
                    item++;
                    if (item == lines_beetween_ads) {
                        item = 0;
                        VideoList.add(new Video().setViewType(3));
                    }
                }

            }
            videoAdapter.notifyDataSetChanged();
            imageView_empty_favorite.setVisibility(View.GONE);
            recycle_view_home_favorite.setVisibility(View.VISIBLE);
        } else {
            imageView_empty_favorite.setVisibility(View.VISIBLE);
            recycle_view_home_favorite.setVisibility(View.GONE);
        }

        swipe_refreshl_home_favorite.setRefreshing(false);

    }
}