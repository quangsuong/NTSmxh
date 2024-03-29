package net.blogsv.ui.Activities;

import android.Manifest;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatRatingBar;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.android.vending.billing.IInAppBillingService;
import com.anjlab.android.iab.v3.BillingProcessor;
import com.anjlab.android.iab.v3.Constants;
import com.anjlab.android.iab.v3.TransactionDetails;
import com.google.ads.consent.ConsentForm;
import com.google.ads.consent.ConsentFormListener;
import com.google.ads.consent.ConsentInfoUpdateListener;
import com.google.ads.consent.ConsentInformation;
import com.google.ads.consent.ConsentStatus;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.messaging.FirebaseMessaging;
import com.miguelcatalan.materialsearchview.MaterialSearchView;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import net.blogsv.Adapters.LanguageAdapter;
import net.blogsv.Adapters.SelectableViewHolder;
import net.blogsv.Provider.PrefManager;
import net.blogsv.api.apiClient;
import net.blogsv.api.apiRest;
import net.blogsv.config.Global;
import net.blogsv.model.ApiResponse;
import net.blogsv.model.Language;
import net.blogsv.ntsmxh.R;
import net.blogsv.ui.fragement.CategroiesFragement;
import net.blogsv.ui.fragement.DownloadsFragement;
import net.blogsv.ui.fragement.FavoritesFragment;
import net.blogsv.ui.fragement.FollowFragment;
import net.blogsv.ui.fragement.HomeFragment;
import net.blogsv.ui.fragement.PopularFragment;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import devlight.io.library.ntb.NavigationTabBar;
import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class MainActivity extends AppCompatActivity
        implements SelectableViewHolder.OnItemSelectedListener, NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "MainActivity ----- : ";
    ConsentForm form;
    private Dialog rateDialog;
    private MaterialSearchView searchView;
    private ViewPagerAdapter adapter;
    private NavigationView navigationView;


    private final List<Language> languageList = new ArrayList<>();
    private final List<Fragment> mFragmentList = new ArrayList<>();
    private final List<String> mFragmentTitleList = new ArrayList<>();
    private AlertDialog.Builder builderLanguage;
    private PrefManager prefManager;
    private Menu menu;
    private LanguageAdapter languageAdapter;
    private ViewPager viewPager;
    private List<Fragment> fragments;// used for ViewPager Adapters

    private FloatingActionButton fab;
    private int tab_fab;
    private TextView text_view_name_nave_header;
    private CircleImageView circle_image_view_profile_nav_header;

    private Boolean FromLogin = false;


    private FollowFragment followFragment;
    private Dialog dialog;
    private Boolean DialogOpened = false;
    private TextView text_view_go_pro;

    IInAppBillingService mService;

    private static final String LOG_TAG = "iabv3";

    // put your Google merchant id here (as stated in public profile of your Payments Merchant Center)
    // if filled library will provide protection against Freedom alike Play Market simulators
    private static final String MERCHANT_ID = null;

    private BillingProcessor bp;
    private boolean readyToPurchase = false;

    ServiceConnection mServiceConn = new ServiceConnection() {
        @Override
        public void onServiceDisconnected(ComponentName name) {
            mService = null;
            // Toast.makeText(MainActivity.this, "set null", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onServiceConnected(ComponentName name,
                                       IBinder service) {
            mService = IInAppBillingService.Stub.asInterface(service);
            //Toast.makeText(MainActivity.this, "set Stub", Toast.LENGTH_SHORT).show();

        }
    };
    private String old_language;
    private MenuItem item_language;

    public void checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.READ_CONTACTS)) {
                    Intent intent_status = new Intent(getApplicationContext(), PermissionActivity.class);
                    startActivity(intent_status);
                    overridePendingTransition(R.anim.enter, R.anim.exit);
                    finish();
                } else {
                    Intent intent_status = new Intent(getApplicationContext(), PermissionActivity.class);
                    startActivity(intent_status);
                    overridePendingTransition(R.anim.enter, R.anim.exit);
                    finish();
                }
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initBuy();
        prefManager = new PrefManager(getApplicationContext());


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setTitle("Latest");
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        this.navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        checkPermission();
        initData();
        iniView();
        initAction();
        firebaseSubscribe();
        initEvent();
        initGDPR();
    }

    private void initGDPR() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            return;
        }
        ConsentInformation consentInformation =
                ConsentInformation.getInstance(MainActivity.this);
//// test
/////
        String[] publisherIds = {getResources().getString(R.string.publisher_id)};
        consentInformation.requestConsentInfoUpdate(publisherIds, new
                ConsentInfoUpdateListener() {
                    @Override
                    public void onConsentInfoUpdated(ConsentStatus consentStatus) {
// User's consent status successfully updated.
                        Log.d(TAG, "onConsentInfoUpdated");
                        switch (consentStatus) {
                            case PERSONALIZED:
                                Log.d(TAG, "PERSONALIZED");
                                ConsentInformation.getInstance(MainActivity.this)
                                        .setConsentStatus(ConsentStatus.PERSONALIZED);
                                break;
                            case NON_PERSONALIZED:
                                Log.d(TAG, "NON_PERSONALIZED");
                                ConsentInformation.getInstance(MainActivity.this)
                                        .setConsentStatus(ConsentStatus.NON_PERSONALIZED);
                                break;


                            case UNKNOWN:
                                Log.d(TAG, "UNKNOWN");
                                if
                                (ConsentInformation.getInstance(MainActivity.this).isRequestLocationInEeaOrUnknown
                                        ()) {
                                    URL privacyUrl = null;
                                    try {
// TODO: Replace with your app's privacy policy URL.
                                        privacyUrl = new URL(getResources().getString(R.string.policy_privacy_url));

                                    } catch (MalformedURLException e) {
                                        e.printStackTrace();
// Handle error.

                                    }
                                    form = new ConsentForm.Builder(MainActivity.this,
                                            privacyUrl)
                                            .withListener(new ConsentFormListener() {
                                                @Override
                                                public void onConsentFormLoaded() {
                                                    Log.d(TAG, "onConsentFormLoaded");
                                                    showform();
                                                }

                                                @Override
                                                public void onConsentFormOpened() {
                                                    Log.d(TAG, "onConsentFormOpened");
                                                }

                                                @Override
                                                public void onConsentFormClosed(ConsentStatus consentStatus, Boolean userPrefersAdFree) {
                                                    Log.d(TAG, "onConsentFormClosed");
                                                }

                                                @Override
                                                public void onConsentFormError(String errorDescription) {
                                                    Log.d(TAG, "onConsentFormError");
                                                    Log.d(TAG, errorDescription);
                                                }
                                            })
                                            .withPersonalizedAdsOption()
                                            .withNonPersonalizedAdsOption()
                                            .build();
                                    form.load();
                                } else {
                                    Log.d(TAG, "PERSONALIZED else");
                                    ConsentInformation.getInstance(MainActivity.this).setConsentStatus(ConsentStatus.PERSONALIZED);
                                }
                                break;
                            default:
                                break;
                        }
                    }

                    @Override
                    public void onFailedToUpdateConsentInfo(String errorDescription) {
// User's consent status failed to update.
                        Log.d(TAG, "onFailedToUpdateConsentInfo");
                        Log.d(TAG, errorDescription);
                    }
                });
    }

    private void showform() {
        if (form != null) {
            Log.d(TAG, "show ok");
            form.show();
        }
    }

    private void initBuy() {
        Intent serviceIntent =
                new Intent("com.android.vending.billing.InAppBillingService.BIND");
        serviceIntent.setPackage("com.android.vending");
        bindService(serviceIntent, mServiceConn, Context.BIND_AUTO_CREATE);


        if (!BillingProcessor.isIabServiceAvailable(this)) {
            //  showToast("In-app billing service is unavailable, please upgrade Android Market/Play to version >= 3.9.16");
        }

        bp = new BillingProcessor(this, Global.MERCHANT_KEY, MERCHANT_ID, new BillingProcessor.IBillingHandler() {
            @Override
            public void onProductPurchased(@NonNull String productId, @Nullable TransactionDetails details) {
                //  showToast("onProductPurchased: " + productId);
                Intent intent = new Intent(MainActivity.this, SlideActivity.class);
                startActivity(intent);
                finish();
                updateTextViews();
            }

            @Override
            public void onBillingError(int errorCode, @Nullable Throwable error) {
                // showToast("onBillingError: " + Integer.toString(errorCode));
            }

            @Override
            public void onBillingInitialized() {
                //  showToast("onBillingInitialized");
                readyToPurchase = true;
                updateTextViews();
            }

            @Override
            public void onPurchaseHistoryRestored() {
                // showToast("onPurchaseHistoryRestored");
                for (String sku : bp.listOwnedProducts())
                    Log.d(LOG_TAG, "Owned Managed Product: " + sku);
                for (String sku : bp.listOwnedSubscriptions())
                    Log.d(LOG_TAG, "Owned Subscription: " + sku);
                updateTextViews();
            }
        });
        bp.loadOwnedPurchasesFromGoogle();
    }

    private void updateTextViews() {
        PrefManager prf = new PrefManager(getApplicationContext());
        bp.loadOwnedPurchasesFromGoogle();

    }

    public Bundle getPurchases() {
        if (!bp.isInitialized()) {


            //  Toast.makeText(this, "null", Toast.LENGTH_SHORT).show();
            return null;
        }
        try {
            // Toast.makeText(this, "good", Toast.LENGTH_SHORT).show();

            return mService.getPurchases(Constants.GOOGLE_API_VERSION, getApplicationContext().getPackageName(), Constants.PRODUCT_TYPE_SUBSCRIPTION, null);
        } catch (Exception e) {
            //  Toast.makeText(this, "ex", Toast.LENGTH_SHORT).show();

            e.printStackTrace();
        }
        return null;
    }

    private void firebaseSubscribe() {
        FirebaseMessaging.getInstance().subscribeToTopic("StatusVideoApp");
    }

    private void initAction() {
        searchView = (MaterialSearchView) findViewById(R.id.search_view);
        searchView.setVoiceSearch(true);
        searchView.setCursorDrawable(R.drawable.color_cursor_white);
        searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Intent intent = new Intent(MainActivity.this, SearchActivity.class);
                intent.putExtra("query", query);
                startActivity(intent);
                overridePendingTransition(R.anim.enter, R.anim.exit);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });


        this.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PrefManager prf = new PrefManager(getApplicationContext());
                if (prf.getString("LOGGED").toString().equals("TRUE")) {


                    Intent intent_video = new Intent(getApplicationContext(), UploadVideoActivity.class);
                    startActivity(intent_video);
                    overridePendingTransition(R.anim.enter, R.anim.exit);


                } else {
                    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                    startActivity(intent);
                    FromLogin = true;

                }
            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        this.menu = menu;
        item_language = menu.findItem(R.id.action_language);
        MenuItem item = menu.findItem(R.id.action_search);
        searchView.setMenuItem(item);
        loadLang();

        return true;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (!bp.handleActivityResult(requestCode, resultCode, data))
            super.onActivityResult(requestCode, resultCode, data);
    }


    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {
            case R.id.action_language:
                Intent intent = new Intent(MainActivity.this, LanguageActivity.class);
                startActivity(intent);
                //finish();
                break;
            case R.id.action_pro:
                showDialog();
                break;
            case R.id.gplay:
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(getResources().getString(R.string.my_google_play))));
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    public void setIconItem(final MenuItem item, String url) {

        final Target mTarget = new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom loadedFrom) {
                BitmapDrawable mBitmapDrawable = new BitmapDrawable(getResources(), bitmap);
                item.setIcon(mBitmapDrawable);
            }

            @Override
            public void onBitmapFailed(Exception e, Drawable errorDrawable) {
                Log.d("DEBUG", "onBitmapFailed");
                item.setIcon(getResources().getDrawable(R.drawable.ic_global));
            }


            @Override
            public void onPrepareLoad(Drawable drawable) {
                Log.d("DEBUG", "onPrepareLoad");
            }
        };
        Picasso.get().load(url).placeholder(R.drawable.flag_placeholder).error(R.drawable.flag_placeholder).into(mTarget);

    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            viewPager.setCurrentItem(0);
        } else if (id == R.id.login) {
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
            FromLogin = true;

        } else if (R.id.whatsapp_saver == id) {
            Intent intent = new Intent(MainActivity.this, WhatsAppActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_exit) {
            final PrefManager prf = new PrefManager(getApplicationContext());
            if (prf.getString("NOT_RATE_APP").equals("TRUE")) {
                super.onBackPressed();
            } else {
                rateDialog(true);
            }
        } else if (id == R.id.nav_settings) {
            Intent intent = new Intent(getApplicationContext(), SettingsActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.enter, R.anim.exit);
        } else if (id == R.id.my_profile) {
            PrefManager prf = new PrefManager(getApplicationContext());
            if (prf.getString("LOGGED").toString().equals("TRUE")) {
                Intent intent = new Intent(getApplicationContext(), UserActivity.class);
                intent.putExtra("id", Integer.parseInt(prf.getString("ID_USER")));
                intent.putExtra("image", prf.getString("IMAGE_USER").toString());
                intent.putExtra("name", prf.getString("NAME_USER").toString());
                startActivity(intent);
                overridePendingTransition(R.anim.enter, R.anim.exit);
                overridePendingTransition(R.anim.enter, R.anim.exit);
            } else {
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
                FromLogin = true;
            }
        } else if (id == R.id.logout) {
            logout();
        } else if (id == R.id.nav_share) {
            final String appPackageName = getApplication().getPackageName();
            String shareBody = "Download " + getString(R.string.app_name) + " From :  " + "http://play.google.com/store/apps/details?id=" + appPackageName;
            Intent sharingIntent = new Intent(Intent.ACTION_SEND);
            sharingIntent.setType("text/plain");
            sharingIntent.putExtra(Intent.EXTRA_TEXT, shareBody);
            sharingIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name));
            startActivity(Intent.createChooser(sharingIntent, getResources().getString(R.string.app_name)));
        } else if (id == R.id.nav_rate) {
            rateDialog(false);
        } else if (id == R.id.nav_help) {
            Intent intent = new Intent(getApplicationContext(), SupportActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.enter, R.anim.exit);
        } else if (id == R.id.nav_policy) {
            Intent intent = new Intent(getApplicationContext(), PolicyActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.enter, R.anim.exit);
        } else if (id == R.id.buy_now) {
            showDialog();
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void iniView() {


        fab = (FloatingActionButton) findViewById(R.id.fab);
        this.followFragment = new FollowFragment();
        viewPager = (ViewPager) findViewById(R.id.vp_horizontal_ntb);
        viewPager.setOffscreenPageLimit(100);
        adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new HomeFragment());
        adapter.addFragment(new PopularFragment());
        adapter.addFragment(followFragment);
        adapter.addFragment(new CategroiesFragement());
        adapter.addFragment(new FavoritesFragment());
        adapter.addFragment(new DownloadsFragement());

        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(0);

        final String[] colors = getResources().getStringArray(R.array.default_preview);

        final NavigationTabBar navigationTabBar = (NavigationTabBar) findViewById(R.id.ntb_horizontal);
        final ArrayList<NavigationTabBar.Model> models = new ArrayList<>();
        models.add(
                new NavigationTabBar.Model.Builder(
                        getResources().getDrawable(R.drawable.ic_home),
                        Color.parseColor(colors[1]))
                        .build()
        );
        models.add(
                new NavigationTabBar.Model.Builder(
                        getResources().getDrawable(R.drawable.ic_whatshot),
                        Color.parseColor(colors[1]))
                        .build()
        );
        models.add(
                new NavigationTabBar.Model.Builder(
                        getResources().getDrawable(R.drawable.ic_subscriptions),
                        Color.parseColor(colors[1]))
                        .build()
        );
        models.add(
                new NavigationTabBar.Model.Builder(
                        getResources().getDrawable(R.drawable.ic_categories),
                        Color.parseColor(colors[1]))

                        .build()
        );
        models.add(
                new NavigationTabBar.Model.Builder(
                        getResources().getDrawable(R.drawable.ic_favorite_black),
                        Color.parseColor(colors[1]))
                        .build()
        );
        models.add(
                new NavigationTabBar.Model.Builder(
                        getResources().getDrawable(R.drawable.ic_folder_black),
                        Color.parseColor(colors[1]))
                        .build()
        );
        navigationTabBar.setModels(models);
        navigationTabBar.setViewPager(viewPager, 0);

        //IMPORTANT: ENABLE SCROLL BEHAVIOUR IN COORDINATOR LAYOUT
        navigationTabBar.setBehaviorEnabled(true);
        navigationTabBar.setOnTabBarSelectedIndexListener(new NavigationTabBar.OnTabBarSelectedIndexListener() {
            @Override
            public void onStartTabSelected(final NavigationTabBar.Model model, final int index) {
            }

            @Override
            public void onEndTabSelected(final NavigationTabBar.Model model, final int index) {
                model.hideBadge();
            }
        });
        navigationTabBar.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(final int position, final float positionOffset, final int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(final int position) {
                switch (position) {
                    case 0:
                        getSupportActionBar().setTitle("Latest");
                        break;
                    case 1:
                        getSupportActionBar().setTitle("Popular");
                        break;
                    case 2:
                        getSupportActionBar().setTitle("Follow");
                        break;
                    case 3:
                        getSupportActionBar().setTitle("Categories");
                        break;
                    case 4:
                        getSupportActionBar().setTitle("Favorites");
                        break;
                    case 5:
                        getSupportActionBar().setTitle("Downloads");
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(final int state) {
            }
        });
        View headerview = navigationView.getHeaderView(0);
        this.text_view_name_nave_header = (TextView) headerview.findViewById(R.id.text_view_name_nave_header);
        this.circle_image_view_profile_nav_header = (CircleImageView) headerview.findViewById(R.id.circle_image_view_profile_nav_header);
        this.viewPager = (ViewPager) findViewById(R.id.vp_horizontal_ntb);
        this.viewPager.setOffscreenPageLimit(100);

        // set Adapters

        viewPager.setAdapter(adapter);

        final CoordinatorLayout coordinatorLayout = (CoordinatorLayout) findViewById(R.id.parent);

    }

    private void initEvent() {
        // set listener to change the current item of view pager when click bottom nav item

    }

    private void initData() {
        fragments = new ArrayList<>(4);

        // create music fragment and add it
        HomeFragment homeFragment = new HomeFragment();


        // create backup fragment and add it
        PopularFragment popularFragment = new PopularFragment();

        // create friends fragment and add it
        FavoritesFragment favorFragment = new FavoritesFragment();

        // create friends fragment and add it
        followFragment = new FollowFragment();


        // add to fragments for Adapters
        fragments.add(homeFragment);
        fragments.add(popularFragment);
        fragments.add(followFragment);
        fragments.add(favorFragment);

    }

    public void setFromLogin() {
        this.FromLogin = true;
    }


    @Override
    public void onItemSelected(Language item) {

        List<Language> selectedItems = languageAdapter.getSelectedItems();
        //  Toast.makeText(MainActivity.this,"Selected item is "+ item.getLanguage()+ ", Totally  selectem item count is "+selectedItems.size(),Toast.LENGTH_LONG).show();
    }


    public int getDefaultLangiage() {
        return prefManager.getInt("LANGUAGE_DEFAULT");
    }

    public void loadLang() {
        Retrofit retrofit = apiClient.getClient();
        apiRest service = retrofit.create(apiRest.class);
        Call<List<Language>> call = service.languageAll();
        call.enqueue(new Callback<List<Language>>() {
            @Override
            public void onResponse(Call<List<Language>> call, final Response<List<Language>> response) {
                if (response.isSuccessful()) {
                    if (response.body().size() > 1) {
                        if (!prefManager.getString("first_lang_set").equals("true")) {
                            prefManager.setString("first_lang_set", "true");
                            Intent intent_status = new Intent(getApplicationContext(), LanguageActivity.class);
                            startActivity(intent_status);
                            overridePendingTransition(R.anim.enter, R.anim.exit);
                        }
                        item_language.setVisible(true);
                    }
                }
            }

            @Override
            public void onFailure(Call<List<Language>> call, Throwable t) {
            }
        });
    }

    public void logout() {
        PrefManager prf = new PrefManager(getApplicationContext());
        prf.remove("ID_USER");
        prf.remove("SALT_USER");
        prf.remove("TOKEN_USER");
        prf.remove("NAME_USER");
        prf.remove("TYPE_USER");
        prf.remove("USERN_USER");
        prf.remove("IMAGE_USER");
        prf.remove("LOGGED");
        if (prf.getString("LOGGED").toString().equals("TRUE")) {
            text_view_name_nave_header.setText(prf.getString("NAME_USER").toString());
            Picasso.get().load(prf.getString("IMAGE_USER").toString()).placeholder(R.drawable.profile).error(R.drawable.profile).resize(200, 200).centerCrop().into(circle_image_view_profile_nav_header);
            if (prf.getString("TYPE_USER").toString().equals("google")) {
            } else {
            }
        } else {
            Menu nav_Menu = navigationView.getMenu();
            nav_Menu.findItem(R.id.my_profile).setVisible(false);
            nav_Menu.findItem(R.id.logout).setVisible(false);
            nav_Menu.findItem(R.id.login).setVisible(true);
            text_view_name_nave_header.setText(getResources().getString(R.string.please_login));
            Picasso.get().load(R.drawable.profile).placeholder(R.drawable.profile).error(R.drawable.profile).resize(200, 200).centerCrop().into(circle_image_view_profile_nav_header);
        }
        followFragment.Resume();

        Toast.makeText(getApplicationContext(), getString(R.string.message_logout), Toast.LENGTH_LONG).show();
    }


    @Override
    protected void onResume() {
        super.onResume();
        checkPermission();

        updateTextViews();

        PrefManager prf = new PrefManager(getApplicationContext());

        Menu nav_Menu = navigationView.getMenu();


        if (prf.getString("LOGGED").toString().equals("TRUE")) {
            nav_Menu.findItem(R.id.my_profile).setVisible(true);
            nav_Menu.findItem(R.id.logout).setVisible(true);
            nav_Menu.findItem(R.id.login).setVisible(false);
            text_view_name_nave_header.setText(prf.getString("NAME_USER").toString());
            Picasso.get().load(prf.getString("IMAGE_USER").toString()).placeholder(R.drawable.profile).error(R.drawable.profile).resize(200, 200).centerCrop().into(circle_image_view_profile_nav_header);
            if (prf.getString("TYPE_USER").toString().equals("google")) {
            } else {
            }
        } else {
            nav_Menu.findItem(R.id.my_profile).setVisible(false);
            nav_Menu.findItem(R.id.logout).setVisible(false);
            nav_Menu.findItem(R.id.login).setVisible(true);

            text_view_name_nave_header.setText(getResources().getString(R.string.please_login));
            Picasso.get().load(R.drawable.profile).placeholder(R.drawable.profile).error(R.drawable.profile).resize(200, 200).centerCrop().into(circle_image_view_profile_nav_header);
        }
        if (FromLogin) {
            followFragment.Resume();
            FromLogin = false;
        }
        if (old_language == null) {
            old_language = prefManager.getString("LANGUAGE_DEFAULT");
        } else {
            if (old_language != prefManager.getString("LANGUAGE_DEFAULT")) {
                old_language = prefManager.getString("LANGUAGE_DEFAULT");
                Intent intent_save = new Intent(MainActivity.this, MainActivity.class);
                startActivity(intent_save);
                finish();
            }
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            final PrefManager prf = new PrefManager(getApplicationContext());
            if (prf.getString("NOT_RATE_APP").equals("TRUE")) {
                super.onBackPressed();
            } else {
                rateDialog(true);
                return;
            }
        }

    }

    public void showDialog() {
        this.dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(R.layout.dialog_subscribe);
        this.text_view_go_pro = (TextView) dialog.findViewById(R.id.text_view_go_pro);
        text_view_go_pro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bp.subscribe(MainActivity.this, Global.SUBSCRIPTION_ID);
            }
        });
        dialog.setOnKeyListener(new Dialog.OnKeyListener() {

            @Override
            public boolean onKey(DialogInterface arg0, int keyCode,
                                 KeyEvent event) {
                // TODO Auto-generated method stub
                if (keyCode == KeyEvent.KEYCODE_BACK) {

                    dialog.dismiss();
                }
                return true;
            }
        });
        dialog.show();
        DialogOpened = true;

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(mServiceConn);
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment) {
            mFragmentList.add(fragment);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }


    public void rateDialog(final boolean close) {
        this.rateDialog = new Dialog(this, R.style.Theme_Dialog);
        rateDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        final PrefManager prf = new PrefManager(getApplicationContext());
        rateDialog.setCancelable(false);
        rateDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        rateDialog.setContentView(R.layout.dialog_rating_app);
        final AppCompatRatingBar AppCompatRatingBar_dialog_rating_app = (AppCompatRatingBar) rateDialog.findViewById(R.id.AppCompatRatingBar_dialog_rating_app);
        final LinearLayout linear_layout_feedback = (LinearLayout) rateDialog.findViewById(R.id.linear_layout_feedback);
        final LinearLayout linear_layout_rate = (LinearLayout) rateDialog.findViewById(R.id.linear_layout_rate);
        final Button buttun_send_feedback = (Button) rateDialog.findViewById(R.id.buttun_send_feedback);
        final Button button_later = (Button) rateDialog.findViewById(R.id.button_later);
        final Button button_never = (Button) rateDialog.findViewById(R.id.button_never);
        final Button button_cancel = (Button) rateDialog.findViewById(R.id.button_cancel);
        button_never.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                prf.setString("NOT_RATE_APP", "TRUE");
                if (close)
                    finish();
            }
        });
        button_later.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rateDialog.dismiss();
                if (close)
                    finish();
            }
        });
        button_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rateDialog.dismiss();
                if (close)
                    finish();
            }
        });
        final EditText edit_text_feed_back = (EditText) rateDialog.findViewById(R.id.edit_text_feed_back);
        buttun_send_feedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                prf.setString("NOT_RATE_APP", "TRUE");
                Retrofit retrofit = apiClient.getClient();
                apiRest service = retrofit.create(apiRest.class);
                Call<ApiResponse> call = service.addSupport("Application rating feedback", AppCompatRatingBar_dialog_rating_app.getRating() + " star(s) Rating".toString(), edit_text_feed_back.getText().toString());
                call.enqueue(new Callback<ApiResponse>() {
                    @Override
                    public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                        if (response.isSuccessful()) {
                            Toasty.success(getApplicationContext(), getResources().getString(R.string.message_sended), Toast.LENGTH_SHORT).show();
                        } else {
                            Toasty.error(getApplicationContext(), getString(R.string.no_connexion), Toast.LENGTH_SHORT).show();
                        }
                        rateDialog.dismiss();

                        if (close)
                            finish();

                    }

                    @Override
                    public void onFailure(Call<ApiResponse> call, Throwable t) {
                        Toasty.error(getApplicationContext(), getString(R.string.no_connexion), Toast.LENGTH_SHORT).show();
                        rateDialog.dismiss();

                        if (close)
                            finish();
                    }
                });
            }
        });
        AppCompatRatingBar_dialog_rating_app.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                if (fromUser) {
                    if (rating > 3) {
                        final String appPackageName = getApplication().getPackageName();
                        try {
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                        } catch (android.content.ActivityNotFoundException anfe) {
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + appPackageName)));
                        }
                        prf.setString("NOT_RATE_APP", "TRUE");
                        rateDialog.dismiss();
                    } else {
                        linear_layout_feedback.setVisibility(View.VISIBLE);
                        linear_layout_rate.setVisibility(View.GONE);
                    }
                } else {

                }
            }
        });
        rateDialog.setOnKeyListener(new Dialog.OnKeyListener() {

            @Override
            public boolean onKey(DialogInterface arg0, int keyCode,
                                 KeyEvent event) {
                // TODO Auto-generated method stub
                if (keyCode == KeyEvent.KEYCODE_BACK) {

                    rateDialog.dismiss();
                    if (close)
                        finish();
                }
                return true;

            }
        });
        rateDialog.show();

    }
}
