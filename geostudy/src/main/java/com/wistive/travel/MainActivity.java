package com.wistive.travel;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.TileOverlayOptions;
import com.amap.api.maps.model.UrlTileProvider;
import com.wistive.travel.view.DotsLayout;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {

    public static final String ACTION_PAGER_CHANGE = "ACTION.pager_change";
    private static final String TAG = MainActivity.class.getSimpleName();

    MapView mMapView = null;

    private ArrayList<Fragment> fragment_list = new ArrayList<>();//存放ViewPager下的Fragment
    private Fragment fragment1, fragment2, fragment3;
    private ViewPager viewPager;
    private MyFragmentPagerAdapter adapter;

    private MyBrocastReceiver recevier;
    private IntentFilter intentFilter;
    private DotsLayout dotsLayout;
    private AMap aMap;
    private Marker curShowWindowMarker;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initBorcastReceiver();
        initMap(savedInstanceState);
        initVideo();
    }

    private void initVideo() {
        viewPager = findViewById(R.id.vp_video);
        fragment1 = VideoFragment.newInstance("http://travel.enn.cn/group1/M00/00/0A/CiaAUlyAxreASu7hABnIV4_GV2I891.mp4");
        fragment2 = VideoFragment.newInstance("http://travel.enn.cn/group1/M00/00/0A/CiaAUlyAxbuAFwcBABOztqZJKFM900.mp4");
        fragment3 = VideoFragment.newInstance("http://jzvd.nathen.cn/c6e3dc12a1154626b3476d9bf3bd7266/6b56c5f0dc31428083757a45764763b0-5287d2089db37e62345123a1be272f8b.mp4");
        fragment_list.add(fragment1);
        fragment_list.add(fragment2);
        fragment_list.add(fragment3);
        adapter = new MyFragmentPagerAdapter(getSupportFragmentManager(), fragment_list);
        viewPager.setAdapter(adapter);
        dotsLayout = findViewById(R.id.mydots);
        dotsLayout.setDot(0, fragment_list.size());
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int index) {
                dotsLayout.setDot(index, fragment_list.size());
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
    }

    private void initBorcastReceiver() {
        recevier = new MyBrocastReceiver();
        intentFilter = new IntentFilter();
        intentFilter.addAction(ACTION_PAGER_CHANGE);
        registerReceiver(recevier, intentFilter);
    }



    private void initMap(Bundle savedInstanceState) {
        mMapView = (MapView) findViewById(R.id.map);
        mMapView.onCreate(savedInstanceState);
        aMap = mMapView.getMap();
        aMap.setOnMapClickListener(new AMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                if (curShowWindowMarker != null) {
                    curShowWindowMarker.hideInfoWindow();
                }
            }
        });
        LatLng latLng = new LatLng(28.0841180000, 116.9930360000);
        aMap.addMarker(new MarkerOptions().position(latLng).title("龙虎山").snippet("DefaultMarker"));
        LatLng latLng_t = new LatLng(28.1142180000, 116.9781360000);
        MarkerOptions options = new MarkerOptions().position(latLng_t).title("古越水街").snippet("DefaultMarker");
        aMap.addMarker(options);
        AMap.OnMarkerClickListener markerClickListener = new AMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                marker.showInfoWindow();
                curShowWindowMarker = marker;
                return true;
            }
        };
        aMap.setOnMarkerClickListener(markerClickListener);
        //添加自定义info window
        aMap.setInfoWindowAdapter(new AMap.InfoWindowAdapter() {
            View infoWindow = null;

            @Override
            public View getInfoWindow(Marker marker) {
                if (infoWindow == null) {
                    infoWindow = LayoutInflater.from(MainActivity.this).inflate(
                            R.layout.custom_info_window, null);
                }
                TextView tv = infoWindow.findViewById(R.id.title);
                tv.setText(marker.getTitle());
                TextView info = infoWindow.findViewById(R.id.info);
                info.setText(marker.getSnippet());
                return infoWindow;
            }

            @Override
            public View getInfoContents(Marker marker) {
                return null;
            }
        });
        //设置缩放等级
        aMap.moveCamera(CameraUpdateFactory.zoomTo(13));
        aMap.moveCamera(CameraUpdateFactory.changeLatLng(latLng_t));
//        useOnlineOverlay();
        useOfflineTile();
    }

    private void useOnlineOverlay() {
        TileOverlayOptions tileOverlayOptions = new TileOverlayOptions().tileProvider(new UrlTileProvider(256, 256) {
            final String url = "http://tile.opencyclemap.org/cycle/%d/%d/%d.png";

            @Override
            public URL getTileUrl(int x, int y, int zoom) {
                try {
                    return new URL(String.format(url, zoom, x, y));
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
                return null;
            }
        });
        http:
//tile.opencyclemap.org
        tileOverlayOptions.diskCacheEnabled(true)
                .memoryCacheEnabled(true)
                .memCacheSize(100000)
                .zIndex(-9999);
        aMap.addTileOverlay(tileOverlayOptions);
    }

    private void useOfflineTile() {
//        useSdcardFile();
        useAssetFile();
    }

    private void useAssetFile() {
        TileOverlayOptions tileOverlayOptions =
                new TileOverlayOptions().tileProvider(new AssetTileProvider(this.getResources().getAssets()) {
                });
        aMap.addTileOverlay(tileOverlayOptions);

    }

    private void useSdcardFile() {
        final String url = "file:///storage/emulated/0/map/%d/%d_%d.png";
//        final String url = "file:///android_asset/map/%d/%d_%d.png";

        TileOverlayOptions tileOverlayOptions =
                new TileOverlayOptions().tileProvider(new UrlTileProvider(256, 256) {

                    @Override
                    public URL getTileUrl(int x, int y, int zoom) {
                        try {
                            return new URL(String.format(url, zoom, x, y));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return null;
                    }
                });
        tileOverlayOptions.diskCacheEnabled(true)
                .diskCacheDir("/storage/emulated/0/amap/tilecache")
                .diskCacheSize(100000)
                .memoryCacheEnabled(true)
                .memCacheSize(100000)
                .zIndex(-9999);
        aMap.addTileOverlay(tileOverlayOptions);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
        unregisterReceiver(recevier);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mMapView.onSaveInstanceState(outState);
    }

    public class MyBrocastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (viewPager.getCurrentItem() != fragment_list.size() - 1) {
                viewPager.setCurrentItem(viewPager.getCurrentItem() + 1);
            } else {
                viewPager.setCurrentItem(0);
            }
        }
    }

}
