package com.ennova.outscreen.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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
import com.ennova.outscreen.bean.ShopDetail;
import com.ennova.outscreen.custom.AssetTileProvider;
import com.ennova.outscreen.network.HttpMethods;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Subscriber;

public class MapActivity extends BaseActivity {

    private final String TAG = this.getClass().getSimpleName();

    @BindView(R.id.mapView)
    MapView mMapView;
    private AMap aMap;
    private Marker curShowWindowMarker;
    private int snack_count;
    private int special_local_product_count;
    private int cultural_product_count;
    private int shop_count;
    private ShopDetail.ShopDetailBean shopDetailBean;
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
                e.printStackTrace();
            }

            @Override
            public void onNext(Points points) {
                snack_count = 0;
                special_local_product_count = 0;
                cultural_product_count = 0;
                shop_count = 0;
                if (points != null && points.getCode() == 0 && points.getData() != null) {
                    for (int i = 0; i < points.getData().size(); i++) {
                        Points.Point point = points.getData().get(i);
                        LatLng latLng = new LatLng(point.getLatitude(), point.getLongitude());
                        MarkerOptions options = new MarkerOptions().position(converterLatLng(latLng)).title(point.getShopName()).snippet("DefaultMarker");
                        Marker marker = aMap.addMarker(options);
                        marker.setObject(point.getId());
                        switch (point.getShopType()) {
                            case 0:
                                snack_count++;
                                break;
                            case 1:
                                special_local_product_count++;
                                break;
                            case 2:
                                cultural_product_count++;
                                break;
                            case 3:
                                shop_count++;
                                break;
                        }
                    }
                    Log.i(TAG, "snack_count: " + snack_count);
                    Log.i(TAG, "special_local_product_count: " + special_local_product_count);
                    Log.i(TAG, "cultural_product_count: " + cultural_product_count);
                    Log.i(TAG, "shop_count: " + shop_count);
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

        AMap.OnMarkerClickListener markerClickListener = new AMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                int shopId = (int) marker.getObject();
                HttpMethods.getInstance().getShopDetail(shopId, new Subscriber<ShopDetail>() {

                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        toast("network error");
                        e.printStackTrace();
                    }

                    @Override
                    public void onNext(ShopDetail shopDetail) {
                        if (shopDetail != null && shopDetail.getCode() == 0 && shopDetail.getData() != null) {
                            shopDetailBean = shopDetail.getData();
                            marker.showInfoWindow();
                            curShowWindowMarker = marker;
                        } else {
                            toast("network error");
                        }

                    }
                });
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
                tv.setText(shopDetailBean.getShopName());
                TextView info = infoWindow.findViewById(R.id.info);
                info.setText(shopDetailBean.getRemark());
                infoWindow.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(MapActivity.this, PointDetailActivity.class);
                        int id = (int) marker.getObject();
                        intent.putExtra("id", id);
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
        aMap.moveCamera(CameraUpdateFactory.zoomTo(16));
        LatLng latLng = new LatLng(28.1157263127, 116.9781303406);
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
