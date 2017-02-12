package com.moduse.nakk;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapView;

public class gps extends Activity implements MapView.MapViewEventListener, MapView.POIItemEventListener
{

    AppInfo appInfo;   // 데이터

    private final int GPS_START = 0;
    private final int GPS_RESET = 1;

    private LocationManager locationManager;
    LocationListener locationListener;
    boolean isGPSEnabled;

    MapView mapView ;   // 다음 맵
    RelativeLayout DaumLaout;

    ProgressDialog loading;   // 프로그레스 설정

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        loading = new ProgressDialog(gps.this);  // 프로그래스

        appInfo = new AppInfo();  // 데이터

        mapView = new MapView(this);
        mapView.setDaumMapApiKey(appInfo.Get_DaumKey());   //  다음 키설정
        DaumLaout = (RelativeLayout) findViewById(R.id.map_view);  // 레이아웃 설정

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);  // LocationManager  객체 얻어오기
        //isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);   // 사용 체크 GPS1
        isGPSEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);   // 사용 체크 GPS2

        InitShow();
        My_locationinit(GPS_START);  // 위치 가져오기

    }

    public void My_locationinit( int type)  // MY GPS 받아오기
    {
        StartShow();

        switch (type) {
            case GPS_START: {
                if (isGPSEnabled) {
                    locationListener = new LocationListener() {
                        @Override
                        public void onLocationChanged(Location location) {
                            //status.setText("위도: "+ location.getLatitude() + "\n경도: " + location.getLongitude() + "\n고도: " + location.getAltitude());
                            appInfo.Set_Latitude(location.getLatitude());
                            appInfo.Set_Longitude(location.getLongitude());

                            // 위치 정보를 가져올 수 있는 메소드입니다.
                            // 위치 이동이나 시간 경과 등으로 인해 호출됩니다.
                            // 최신 위치는 location 파라메터가 가지고 있습니다.
                            //최신 위치를 가져오려면, location 파라메터를 이용하시면 됩니다.

                            DaumMap_Start();  // 다음맵 시작
                        }

                        @Override
                        public void onStatusChanged(String s, int i, Bundle bundle) {
                        } // 위치 공급자의 상태가 바뀔 때 호출 됩니다.

                        @Override
                        public void onProviderEnabled(String s) {
                        } // 위치 공급자가 사용 가능해질(enabled) 때 호출 됩니다.

                        @Override
                        public void onProviderDisabled(String s) {
                        } // 위치 공급자가 사용 불가능해질(disabled) 때 호출 됩니다.
                    };

                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, // 등록할 오차범위 있음
                            100, // 통지사이의 최소 시간간격 (miliSecond)
                            100, locationListener); // 통지사이의 최소 변경거리 (m)

                    //locationManager.removeUpdates(locationListener);  // GPS 닫기
                } else if (!isGPSEnabled) {
                    Toast.makeText(this.getApplicationContext(), "위치를 찾을 수 없습니다. GPS설정을 확인해주세요.", Toast.LENGTH_SHORT).show();

                    DaumMap_Start();  // 다음맵 시작
                }
                break;
            }
            case GPS_RESET:
            {
                if (isGPSEnabled) {
                    locationListener = new LocationListener() {
                        @Override
                        public void onLocationChanged(Location location) {
                            appInfo.Set_Latitude(location.getLatitude());
                            appInfo.Set_Longitude(location.getLongitude());

                            onMapViewInitialized(mapView);
                        }

                        @Override
                        public void onStatusChanged(String s, int i, Bundle bundle) { } // 위치 공급자의 상태가 바뀔 때 호출 됩니다.

                        @Override
                        public void onProviderEnabled(String s) {} // 위치 공급자가 사용 가능해질(enabled) 때 호출 됩니다.

                        @Override
                        public void onProviderDisabled(String s) { } // 위치 공급자가 사용 불가능해질(disabled) 때 호출 됩니다.
                    };

                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, // 등록할 오차범위 있음
                            100, // 통지사이의 최소 시간간격 (miliSecond)
                            100, locationListener); // 통지사이의 최소 변경거리 (m)

                    //locationManager.removeUpdates(locationListener);  // GPS 닫기
                } else if (!isGPSEnabled) {
                    Toast.makeText(this.getApplicationContext(), "위치를 찾을 수 없습니다. GPS설정을 확인해주세요.", Toast.LENGTH_SHORT).show();
                }
                break;
            }
        }
    }


    // 지도 맵 시작
    public void DaumMap_Start()
    {
        mapView.setMapType(MapView.MapType.Hybrid);  // 맵 타입 하이드리브
        DaumLaout.addView(mapView);


        // 기존 이벤트 마커 불러오기  리스너 사용 OK
        Thread t = new Thread(new Runnable() {
            Handler handler = new Handler();
            @Override
            public void run() {
                // TODO Auto-generated method stub
                try{
                    handler.post(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            mapView.setMapViewEventListener(gps.this);
                            mapView.setPOIItemEventListener(gps.this);
                        }
                    });
                } catch(Exception e){
                    Log.e("error", e.toString());
                }
            }
        });
        t.start();
    }


   // 내위치 버튼 리스너
    public void map_mylocation(View v)
    {
        if(isGPSEnabled)
        {
            My_locationinit(GPS_RESET);  // 위치 가져오기
        }
    }


    // 뒤로가기 (종료 메인이라서)
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        switch (keyCode) {
            //하드웨어 뒤로가기 버튼에 따른 이벤트 설정
            case KeyEvent.KEYCODE_BACK:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);

                builder.setTitle("[앱 종료]") // 제목 설정
                        .setMessage("낚중일기를 종료 하시겠습니까?")
                        .setCancelable(false)  //뒤로 버튼 클릭시 취소 설정
                        .setPositiveButton("예", new DialogInterface.OnClickListener() {
                            // 예 버튼 클릭시 설정
                            public void onClick(DialogInterface dialogInterface, int i)
                            {
                                finish();
                            }
                        })
                        .setNegativeButton("아니요", null).show();

                AlertDialog alert = builder.create();  //알림 객체 생성
        }
        return super.onKeyDown(keyCode, event);
    }


    // 다음 맵 이벤트 리스너

    @Override
    public void onMapViewInitialized(MapView mapView)  // 맵 초기 설정
    {

        if(isGPSEnabled)
        {
            double Latitude = appInfo.Get_Latitude();
            double Longitude = appInfo.Get_Longitude();

            mapView.setMapCenterPointAndZoomLevel(MapPoint.mapPointWithGeoCoord(Latitude, Longitude), 2, true); // 지도 중심점 자기 위치로 변경  // 줌레벨

        }
        else
        {
            mapView.setMapCenterPointAndZoomLevel(MapPoint.mapPointWithGeoCoord(37.578064, 127.009469), 15, true); // GPS 설정 안되있음 서울로 마춰놈  // 줌레벨
        }
        locationManager.removeUpdates(locationListener);   //  GPS 끔
        StopShow();
    }

    @Override
    public void onMapViewCenterPointMoved(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewZoomLevelChanged(MapView mapView, int i) {

    }

    @Override
    public void onMapViewSingleTapped(MapView mapView, MapPoint mapPoint)
    {
        //mapView.removeAllPOIItems();

        MapPoint.GeoCoordinate mapPointGeo = mapPoint.getMapPointGeoCoord();
        MapPoint.PlainCoordinate mapPointScreenLocation = mapPoint.getMapPointScreenLocation();

        Log.i("mapPoint","Daum mapPoint :"+MapPoint.mapPointWithGeoCoord(mapPointGeo.latitude, mapPointGeo.longitude));

        MapPOIItem marker = new MapPOIItem();

        marker.setItemName("낚중일기_POINT");
        marker.setTag(0);
        marker.setMapPoint(MapPoint.mapPointWithGeoCoord(mapPointGeo.latitude, mapPointGeo.longitude));
        marker.setMarkerType(MapPOIItem.MarkerType.BluePin); // 기본으로 제공하는 BluePin 마커 모양.
        marker.setSelectedMarkerType(MapPOIItem.MarkerType.RedPin); // 마커를 클릭했을때, 기본으로 제공하는 RedPin 마커 모양.
        marker.setCustomImageAutoscale(false);

        mapView.addPOIItem(marker);

    }

    @Override
    public void onMapViewDoubleTapped(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewLongPressed(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewDragStarted(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewDragEnded(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewMoveFinished(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onPOIItemSelected(MapView mapView, MapPOIItem mapPOIItem)   // 포인트 아이콘 클릭 했을때
    {
        Toast.makeText(this, mapPOIItem.getItemName(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onCalloutBalloonOfPOIItemTouched(MapView mapView, MapPOIItem mapPOIItem)  // 포인트 아이콘 클릭하고 올라오는 팝업 클릭했을때
    {
        if(mapPOIItem.getItemName().equals("낚중일기_POINT"))
        {
            Toast.makeText(this, mapPOIItem.getItemName() +" 선택되었음", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onCalloutBalloonOfPOIItemTouched(MapView mapView, MapPOIItem mapPOIItem, MapPOIItem.CalloutBalloonButtonType calloutBalloonButtonType) {

    }

    @Override
    public void onDraggablePOIItemMoved(MapView mapView, MapPOIItem mapPOIItem, MapPoint mapPoint)
    {

    }



    // 프로그레스 설정
    public void InitShow()
    {
        loading.setProgress(ProgressDialog.STYLE_SPINNER);
        loading.setMessage("정보를 불러오는 중입니다..");
    }
    public void SetmsgShow(String value)
    {
        loading.setMessage(value);
    }
    public void StartShow() {loading.show();}
    public void StopShow() {loading.dismiss();}
}
