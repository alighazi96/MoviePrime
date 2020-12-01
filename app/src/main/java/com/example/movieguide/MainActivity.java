package com.example.movieguide;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.movieguide.adapter.BannerMoviesPagerAdapter;
import com.example.movieguide.adapter.MainRecyclerAdapter;
import com.example.movieguide.model.AllCategory;
import com.example.movieguide.model.BannerMovies;
import com.example.movieguide.model.CategoryItemList;
import com.example.movieguide.retrofit.RetrofitClient;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    BannerMoviesPagerAdapter PagerAdapter;
    TabLayout indicatorTab,categoryTab;
    ViewPager viewPager;
    List<BannerMovies> homeBannerList;
    List<BannerMovies> tvShowBannerList;
    List<BannerMovies> movieBannerList;
    List<BannerMovies> kidBannerList;

    MainRecyclerAdapter mainRecyclerAdapter;
    RecyclerView mainRecycler;
    NestedScrollView nestedScrollView;
    AppBarLayout appBarLayout;

    List<AllCategory> allCategoryList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        categoryTab=findViewById(R.id.tabLayout);
        indicatorTab = findViewById(R.id.tab_indicator);
        nestedScrollView = findViewById(R.id.nested_scroll);
        appBarLayout = findViewById(R.id.appbar);

        homeBannerList = new ArrayList<>();

        tvShowBannerList = new ArrayList<>();
        movieBannerList = new ArrayList<>();
        kidBannerList = new ArrayList<>();
        getAllMoviesData(1);


        categoryTab.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getPosition()) {
                    case 1:
                        SetScrollDefaultState();
                        setBannerMoviesPagerAdapter(tvShowBannerList);
                        getAllMoviesData(2);


                        return;
                    case 2:
                        SetScrollDefaultState();
                        setBannerMoviesPagerAdapter(movieBannerList);
                        getAllMoviesData(3);

                        return;
                    case 3:
                        SetScrollDefaultState();
                        setBannerMoviesPagerAdapter(kidBannerList);
                        getAllMoviesData(4);

                        return;
                    default:
                        SetScrollDefaultState();
                        setBannerMoviesPagerAdapter(homeBannerList);
                        getAllMoviesData(1);


                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        allCategoryList=new ArrayList<>();
        getBannerData();

    }

    private void setBannerMoviesPagerAdapter(List<BannerMovies> bannerMoviesList) {
        viewPager = findViewById(R.id.banner_viewPager);
        PagerAdapter = new BannerMoviesPagerAdapter(this, bannerMoviesList);
        viewPager.setAdapter(PagerAdapter);
        indicatorTab.setupWithViewPager(viewPager);
        Timer sliderTimer = new Timer();
        sliderTimer.scheduleAtFixedRate(new AutoSlider(), 4000, 6000);
        indicatorTab.setupWithViewPager(viewPager, true);


    }

    //automatic slider with timer

    class AutoSlider extends TimerTask {

        @Override
        public void run() {
            MainActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (viewPager.getCurrentItem() < homeBannerList.size() - 1) {
                        viewPager.setCurrentItem(viewPager.getCurrentItem() + 1);

                    } else {
                        viewPager.setCurrentItem(0);

                    }

                }


            });
        }
    }

    public void setMainRecycler(List<AllCategory> allCategoryList){
        mainRecycler=findViewById(R.id.main_recycler);
        RecyclerView.LayoutManager layoutManager=new LinearLayoutManager(this,RecyclerView.VERTICAL,false);
        mainRecycler.setLayoutManager(layoutManager);
        mainRecyclerAdapter=new MainRecyclerAdapter(this,allCategoryList);
        mainRecycler.setAdapter(mainRecyclerAdapter);

    }

    //auto scroll up when we click on tabs

    private void SetScrollDefaultState(){
        nestedScrollView.fullScroll(View.FOCUS_UP);
        nestedScrollView.scrollTo(0,0);
        appBarLayout.setExpanded(true);
    }

    private void getBannerData(){

        CompositeDisposable compositeDisposable=new CompositeDisposable();
        compositeDisposable.add(RetrofitClient.getRetrofitClient().getAllBanners()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableObserver<List<BannerMovies>>() {
                    @Override
                    public void onNext(@NonNull List<BannerMovies> bannerMovies) {
                        for(int i=0;i<bannerMovies.size();i++) {
                            if (bannerMovies.get(i).getBannerCategoryId().toString().equals("1")) {
                                homeBannerList.add(bannerMovies.get(i));

                            } else if (bannerMovies.get(i).getBannerCategoryId().toString().equals("2")) {
                                tvShowBannerList.add(bannerMovies.get(i));


                            } else if (bannerMovies.get(i).getBannerCategoryId().toString().equals("3")) {
                                movieBannerList.add(bannerMovies.get(i));



                            } else if (bannerMovies.get(i).getBannerCategoryId().toString().equals("4")) {
                                kidBannerList.add(bannerMovies.get(i));


                            }
                            else{
                            }
                        }

                        }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        Log.d("banner",""+e);


                    }

                    @Override
                    public void onComplete() {
                        setBannerMoviesPagerAdapter(homeBannerList);

                    }
                }
    ));

    }

    private void getAllMoviesData(int categoryId){

        CompositeDisposable compositeDisposable=new CompositeDisposable();
        compositeDisposable.add(RetrofitClient.getRetrofitClient().getAllCategoryMovies(categoryId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableObserver<List<AllCategory>>() {
                                   @Override
                                   public void onNext(@NonNull List<AllCategory> allCategoryList) {
                                       setMainRecycler(allCategoryList);



                                   }

                                   @Override
                                   public void onError(@NonNull Throwable e) {
                                       Log.d("banner",""+e);


                                   }

                                   @Override
                                   public void onComplete() {
                                       setBannerMoviesPagerAdapter(homeBannerList);

                                   }
                               }
                ));

    }


}



