package com.moduse.nakk;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapReverseGeoCoder;
import net.daum.mf.map.api.MapView;

/**
 * Created by HOMESJ on 2017-02-18.
 */


public class Tab1_map extends Activity implements MapView.MapViewEventListener, MapView.POIItemEventListener, MapReverseGeoCoder.ReverseGeoCodingResultListener
{

    AppInfo appInfo;   // 앱 데이터

    //GPS 부분
    private LocationManager locationManager;
    LocationListener locationListener;
    boolean isGPSEnabled;

    private final int GPS_START = 0;
    private final int GPS_RESET = 1;

    //토크에서 넘겨받은 인텐트 데이터
    private Intent talkintent;

    // 맵 유형 타입
    String Map_type;

    // 토크에서 가져온 포인트 좌표
    double Talk_Point_latitude = 0;
    double Talk_Point_longitude = 0;
    String Talk_Point_domicile;

    // 수정하기에서 가져온 포인트
    double Fix_Point_latitude = 0;
    double Fix_Point_longitude = 0;


    // 다음 맵부분
    MapView mapView ;   // 다음 맵
    RelativeLayout DaumLaout;

    ProgressDialog loading;   // 프로그레스 설정

    // 포인트 찍기 텝1 작성 위치 부분
    boolean TagetPoint_state = false;
    MapPOIItem Select_item = null;

    LinearLayout PointSelect_Menu;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tab1_map);

        loading = new ProgressDialog(Tab1_map.this);  // 프로그래스

        appInfo = new AppInfo();  // 앱 데이터 설정

        // 다이얼로그 초기화
        InitShow();



        PointSelect_Menu = (LinearLayout) findViewById(R.id.View_point_linear);

        // GPS 확인
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);  // LocationManager  객체 얻어오기
        isGPSEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);   // 사용 체크 GPS2

        // 토크에서 데이터 가져오기
        talkintent = getIntent();

        Map_type = talkintent.getStringExtra("type");

        if (Map_type.toString().equals("view_point")) // 토크에서 포인트 보기면 좌표값 저장
        {
            // 다음맵 초기화 & 레이아웃 설정
            mapView = new MapView(this);
            mapView.setDaumMapApiKey(appInfo.Get_DaumKey());   //  다음 키설정
            DaumLaout = (RelativeLayout) findViewById(R.id.map_view);  // 레이아웃 설정

            PointSelect_Menu.setVisibility(View.GONE);
            Talk_Point_latitude = talkintent.getDoubleExtra("latitude", 0);
            Talk_Point_longitude = talkintent.getDoubleExtra("longitude", 0);

            Find_domicile_start(Talk_Point_latitude, Talk_Point_longitude);

            DaumMap_Strat();
        }
        else if(Map_type.toString().equals("point_select"))
        {

            // 다음맵 초기화 & 레이아웃 설정
            mapView = new MapView(this);
            mapView.setDaumMapApiKey(appInfo.Get_DaumKey());   //  다음 키설정
            DaumLaout = (RelativeLayout) findViewById(R.id.map_view);  // 레이아웃 설정

            PointSelect_Menu.setVisibility(View.VISIBLE);

            My_locationinit(GPS_START);
        }
        else if(Map_type.toString().equals("point_select_fix"))
        {

            // 수정하기에서 넘어온 좌표 저장
            Fix_Point_latitude = talkintent.getDoubleExtra("fix_latitude",0);
            Fix_Point_longitude = talkintent.getDoubleExtra("fix_longitude",0);


            // 다음맵 초기화 & 레이아웃 설정
            mapView = new MapView(this);
            mapView.setDaumMapApiKey(appInfo.Get_DaumKey());   //  다음 키설정
            DaumLaout = (RelativeLayout) findViewById(R.id.map_view);  // 레이아웃 설정

            PointSelect_Menu.setVisibility(View.VISIBLE);

            DaumMap_Strat();
        }


    }

    // 포인트 찍기 완료 버튼
    public void Point_select_submit(View v)
    {

        if(Map_type.toString().equals("point_select"))
        {
            //  인텐트 setResult

            if (Talk_Point_latitude != 0 && Talk_Point_longitude != 0 && TagetPoint_state) {
                talkintent.putExtra("point_state", "true");
                talkintent.putExtra("point_latitude", Talk_Point_latitude);
                talkintent.putExtra("point_longitude", Talk_Point_longitude);
                talkintent.putExtra("point_address", Talk_Point_domicile);
                setResult(RESULT_OK, talkintent);

                // 다음맵 닫기
                mapView.onPause();
                DaumLaout.removeAllViews();
                finish();

            } else {
                Toast.makeText(getApplicationContext(), "찍은 포인트가 없습니다!", Toast.LENGTH_SHORT).show();
            }
        }
        else if(Map_type.toString().equals("point_select_fix"))
        {
            if (Fix_Point_latitude != 0 && Fix_Point_longitude != 0 && TagetPoint_state) {
                talkintent.putExtra("point_state", "true");
                talkintent.putExtra("point_latitude", Fix_Point_latitude);
                talkintent.putExtra("point_longitude", Fix_Point_longitude);
                talkintent.putExtra("point_address", Talk_Point_domicile);
                setResult(RESULT_OK, talkintent);

                // 다음맵 닫기
                mapView.onPause();
                DaumLaout.removeAllViews();
                finish();
            } else {
                Toast.makeText(getApplicationContext(), "찍은 포인트가 없습니다!", Toast.LENGTH_SHORT).show();
            }
        }


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

                            DaumMap_Strat();  // 다음맵 시작
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

                    DaumMap_Strat();  // 다음맵 시작
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

    // 다음 지도 시작
    public void DaumMap_Strat()
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
                            mapView.setMapViewEventListener(Tab1_map.this);
                            mapView.setPOIItemEventListener(Tab1_map.this);
                        }
                    });
                } catch(Exception e){
                    Log.e("error", e.toString());
                }
            }
        });
        t.start();
    }

    public void Find_domicile_start(double lat, double lng)
    {
        // 주소 찾기 초기화
        MapPoint clsPoint = MapPoint.mapPointWithGeoCoord( lat, lng );

        MapReverseGeoCoder mapGeoCoder = new MapReverseGeoCoder( appInfo.Get_DaumKey(), clsPoint, this, this );
        mapGeoCoder.startFindingAddress( );


    }


    // 다음 맵 이벤트 리스너

    @Override
    public void onMapViewInitialized(MapView mapView)  // 맵 초기 설정
    {
        if(Map_type.toString().equals("view_point"))
        {
            // 토크에서 가져온 GPS 좌표값으로 중심점 마춤
            mapView.setMapCenterPointAndZoomLevel(MapPoint.mapPointWithGeoCoord(Talk_Point_latitude, Talk_Point_longitude), 5, true); // GPS 설정 안되있음 서울로 마춰놈  // 줌레벨

            // 토크에서 가져온 마크 적용
            MapPOIItem marker = new MapPOIItem();

            marker.setItemName("[여기]");
            marker.setTag(0);
            marker.setMapPoint(MapPoint.mapPointWithGeoCoord(Talk_Point_latitude, Talk_Point_longitude));
            marker.setMarkerType(MapPOIItem.MarkerType.YellowPin); // 기본으로 제공하는 BluePin 마커 모양.
            marker.setSelectedMarkerType(MapPOIItem.MarkerType.RedPin); // 마커를 클릭했을때, 기본으로 제공하는 RedPin 마커 모양.
            marker.setCustomImageAutoscale(false);

            mapView.addPOIItem(marker);
        }
        else if(Map_type.toString().equals("point_select"))
        {
            double Latitude = appInfo.Get_Latitude();
            double Longitude = appInfo.Get_Longitude();

            mapView.setMapCenterPointAndZoomLevel(MapPoint.mapPointWithGeoCoord(Latitude, Longitude), 2, true); // 지도 중심점 자기 위치로 변경  // 줌레벨
        }
        else if(Map_type.toString().equals("point_select_fix"))
        {
            // 토크수정에서 가져온 GPS 좌표값으로 중심점 마춤
            mapView.setMapCenterPointAndZoomLevel(MapPoint.mapPointWithGeoCoord(Fix_Point_latitude, Fix_Point_longitude), 5, true); // GPS 설정 안되있음 서울로 마춰놈  // 줌레벨

            // 토크에서 가져온 마크 적용
            MapPOIItem marker = new MapPOIItem();

            marker.setItemName("[여기]");
            marker.setTag(0);
            marker.setMapPoint(MapPoint.mapPointWithGeoCoord(Fix_Point_latitude, Fix_Point_longitude));
            marker.setMarkerType(MapPOIItem.MarkerType.YellowPin); // 기본으로 제공하는 BluePin 마커 모양.
            marker.setSelectedMarkerType(MapPOIItem.MarkerType.RedPin); // 마커를 클릭했을때, 기본으로 제공하는 RedPin 마커 모양.
            marker.setCustomImageAutoscale(false);

            mapView.addPOIItem(marker);
        }




        //다이얼로그 종료
        StopShow();
    }

    @Override
    public void onMapViewCenterPointMoved(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewZoomLevelChanged(MapView mapView, int i) {

    }

    @Override
    public void onMapViewSingleTapped(MapView mapView, MapPoint mapPoint)  // 마커 아이템 세팅하는 부분
    {
        //mapView.removeAllPOIItems();

        if(Map_type.toString().equals("point_select"))  // 포인트 찍기
        {
            MapPoint.GeoCoordinate mapPointGeo = mapPoint.getMapPointGeoCoord();
            MapPoint.PlainCoordinate mapPointScreenLocation = mapPoint.getMapPointScreenLocation();

           // Log.i("mapPoint", "Daum mapPoint :" + MapPoint.mapPointWithGeoCoord(Talk_Point_latitude, Talk_Point_longitude));

            Talk_Point_latitude = mapPointGeo.latitude;
            Talk_Point_longitude =  mapPointGeo.longitude;

            MapPOIItem marker = new MapPOIItem();

            if(Select_item != null)
            {
                mapView.removePOIItem(Select_item);
                Select_item = null;
            }

            marker.setItemName("포인트");
            marker.setTag(0);
            marker.setMapPoint(MapPoint.mapPointWithGeoCoord(mapPointGeo.latitude, mapPointGeo.longitude));
            marker.setMarkerType(MapPOIItem.MarkerType.BluePin); // 기본으로 제공하는 BluePin 마커 모양.
            marker.setSelectedMarkerType(MapPOIItem.MarkerType.RedPin); // 마커를 클릭했을때, 기본으로 제공하는 RedPin 마커 모양.
            marker.setCustomImageAutoscale(false);

            TagetPoint_state = true;
            Select_item = marker;
            // 주소찾기
            Find_domicile_start(Talk_Point_latitude, Talk_Point_longitude);
            mapView.addPOIItem(marker);
        }
        else if(Map_type.toString().equals("point_select_fix"))  //수정 포인트 찍기
        {
            // 기존 포인트 삭제
            mapView.removeAllPOIItems();

            //
            MapPoint.GeoCoordinate mapPointGeo = mapPoint.getMapPointGeoCoord();
            MapPoint.PlainCoordinate mapPointScreenLocation = mapPoint.getMapPointScreenLocation();

            //주소
            Fix_Point_latitude = mapPointGeo.latitude;
            Fix_Point_longitude =  mapPointGeo.longitude;

            // 토크에서 가져온 마크 적용
            MapPOIItem marker = new MapPOIItem();

            marker.setItemName("[여기]");
            marker.setTag(0);
            marker.setMapPoint(MapPoint.mapPointWithGeoCoord(mapPointGeo.latitude, mapPointGeo.longitude));
            marker.setMarkerType(MapPOIItem.MarkerType.YellowPin); // 기본으로 제공하는 BluePin 마커 모양.
            marker.setSelectedMarkerType(MapPOIItem.MarkerType.RedPin); // 마커를 클릭했을때, 기본으로 제공하는 RedPin 마커 모양.
            marker.setCustomImageAutoscale(false);

            Find_domicile_start(Fix_Point_latitude, Fix_Point_longitude);

            TagetPoint_state = true;

            mapView.addPOIItem(marker);
        }


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
        Toast.makeText(this, Talk_Point_domicile, Toast.LENGTH_SHORT).show();
        mapPOIItem.setItemName(Talk_Point_domicile); // 지도 주소로 타이틀 변경
    }

    @Override
    public void onCalloutBalloonOfPOIItemTouched(MapView mapView, MapPOIItem mapPOIItem)  // 포인트 아이콘 클릭하고 올라오는 팝업 클릭했을때
    {
        if(Map_type.toString().equals("view_point"))
        {
            if (mapPOIItem.getItemName().equals(Talk_Point_domicile)) {
                Toast.makeText(this, Talk_Point_domicile, Toast.LENGTH_SHORT).show();
            }
        }
        else if(Map_type.toString().equals("point_select"))
        {
            if (mapPOIItem.getItemName().equals("포인트")) {
                Toast.makeText(this, "여기 찍었음", Toast.LENGTH_SHORT).show();
            }
        }
    {

    }
    }

    @Override
    public void onCalloutBalloonOfPOIItemTouched(MapView mapView, MapPOIItem mapPOIItem, MapPOIItem.CalloutBalloonButtonType calloutBalloonButtonType) {

    }

    @Override
    public void onDraggablePOIItemMoved(MapView mapView, MapPOIItem mapPOIItem, MapPoint mapPoint)
    {

    }

    @Override
    public void onReverseGeoCoderFailedToFindAddress( MapReverseGeoCoder arg0 )
    {
        Talk_Point_domicile = "주소 불러오기 실패";
    }

    @Override
    public void onReverseGeoCoderFoundAddress( MapReverseGeoCoder arg0, String arg1 )
    {
        Talk_Point_domicile = arg1;

        // arg1 가 검색된 주소이다.
    }

    // 위치 지도 닫기

    public void tab1_map_close(View v)
    {
        if(Map_type.toString().equals("view_point"))
        {
            AppInfo.SaveIndex = true;
        }
        DaumLaout.removeAllViews();
        mapView.onPause();
        finish();
    }



    // 뒤로가기 (댓글창 닫기)
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        switch (keyCode) {
            //하드웨어 뒤로가기 버튼에 따른 이벤트 설정
            case KeyEvent.KEYCODE_BACK:
            {
                if(Map_type.toString().equals("view_point"))
                {
                    AppInfo.SaveIndex = true;
                }
                DaumLaout.removeAllViews();
                mapView.onPause();
               finish();
            }
        }
        return super.onKeyDown(keyCode, event);
    }


    @Override
    protected void onPause()
    {
        super.onPause();

        DaumLaout.removeAllViews();
        mapView.onPause();
    }

    // 프로그레스 설정
    public void InitShow()
    {
        loading.setProgress(ProgressDialog.STYLE_SPINNER);
        loading.setMessage("정보를 불러오는 중입니다..");
        loading.setCanceledOnTouchOutside(false);   // 옆에 터치해도 안사라지게
    }
    public void SetmsgShow(String value)
    {
        loading.setMessage(value);
    }
    public void StartShow() {loading.show();}
    public void StopShow() {loading.dismiss();}
}
