package com.ennova.outscreen.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.CoordinateConverter;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.TileOverlayOptions;
import com.ennova.outscreen.BaseActivity;
import com.ennova.outscreen.R;
import com.ennova.outscreen.bean.Points;
import com.ennova.outscreen.custom.AssetTileProvider;
import com.ennova.outscreen.network.HttpMethods;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Subscriber;

public class MapActivity extends BaseActivity {

    @BindView(R.id.mapView)
    MapView mMapView;
    private AMap aMap;
    private Marker curShowWindowMarker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        ButterKnife.bind(this);
        mMapView.onCreate(savedInstanceState);
        initMap();
        initPoints();
    }

    private void initPoints() {
        HttpMethods.getInstance().getPoints("-1", new Subscriber<Points>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                toast("network error");
            }

            @Override
            public void onNext(Points points) {
                if (points != null && points.getCode() == 0 && points.getData() != null) {
                    for (int i = 0; i < points.getData().size(); i++) {
                        Points.Point point = points.getData().get(i);
                        LatLng latLng = new LatLng(point.getLatitude(), point.getLongitude());
                        MarkerOptions options = new MarkerOptions().position(converterLatLng(latLng)).title(point.getShopName()).snippet("DefaultMarker");
                        Marker marker = aMap.addMarker(options);
                    }
                } else {
                    toast("network error");
                }
            }
        });
    }

    private void initMap() {
        aMap = mMapView.getMap();
        aMap.setOnMapClickListener(new AMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                if (curShowWindowMarker != null) {
                    curShowWindowMarker.hideInfoWindow();
                }
            }
        });
//        LatLng latLng = new LatLng(28.0841180000, 116.9930360000);
//        aMap.addMarker(new MarkerOptions().position(latLng).title("龙虎山").snippet("DefaultMarker"));
//        LatLng latLng_t = new LatLng(28.1142180000, 116.9781360000);
//        MarkerOptions options = new MarkerOptions().position(latLng_t).title("古越水街").snippet("DefaultMarker");
//        aMap.addMarker(options);
//        LatLng latLng_t1 = new LatLng(28.122406, 116.984864);//这是百度坐标，需要转换下
//        MarkerOptions options1 = new MarkerOptions().position(converterLatLng(latLng_t1)).title("千里香馄饨").snippet("DefaultMarker");
//        aMap.addMarker(options1);
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
        AMap.InfoWindowAdapter infoWindowAdapter = new AMap.InfoWindowAdapter() {
            View infoWindow = null;

            @Override
            public View getInfoWindow(Marker marker) {
                if (infoWindow == null) {
                    infoWindow = LayoutInflater.from(MapActivity.this).inflate(
                            R.layout.layout_custom_info_window, null);
                }
                TextView tv = infoWindow.findViewById(R.id.title);
                tv.setText(marker.getTitle());
                TextView info = infoWindow.findViewById(R.id.info);
                info.setText(marker.getSnippet());
                infoWindow.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(MapActivity.this, PointDetailActivity.class);
                        startActivity(intent);
                    }
                });
                return infoWindow;
            }

            @Override
            public View getInfoContents(Marker marker) {
                return null;
            }
        };
        aMap.setInfoWindowAdapter(infoWindowAdapter);
        //设置缩放等级
        LatLng latLng = new LatLng(28.0841180000, 116.9930360000);
        aMap.moveCamera(CameraUpdateFactory.zoomTo(16));
        aMap.moveCamera(CameraUpdateFactory.changeLatLng(latLng));
        TileOverlayOptions tileOverlayOptions =
                new TileOverlayOptions().tileProvider(new AssetTileProvider(this.getResources().getAssets()) {
                });
        aMap.addTileOverlay(tileOverlayOptions);
    }

    private LatLng converterLatLng(LatLng latLng_t1) {
        CoordinateConverter converter = new CoordinateConverter(this);
        converter.from(CoordinateConverter.CoordType.BAIDU);
        converter.coord(latLng_t1);
        return converter.convert();
    }
}
