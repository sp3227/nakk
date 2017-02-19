package com.moduse.nakk;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
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


    //토크에서 넘겨받은 인텐트 데이터
    private Intent talkintent;

    // 토크에서 가져온 포인트 좌표
    String Map_type;
    double Talk_Point_latitude;
    double Talk_Point_longitude;
    String Talk_Point_domicile;

    // 다음 맵부분
    MapView mapView ;   // 다음 맵
    RelativeLayout DaumLaout;

    ProgressDialog loading;   // 프로그레스 설정

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tab1_map);

        loading = new ProgressDialog(Tab1_map.this);  // 프로그래스

        appInfo = new AppInfo();  // 앱 데이터 설정

        // 다이얼로그 초기화
        InitShow();

        // 다음맵 초기화 & 레이아웃 설정
        mapView = new MapView(this);
        mapView.setDaumMapApiKey(appInfo.Get_DaumKey());   //  다음 키설정
        DaumLaout = (RelativeLayout) findViewById(R.id.map_view);  // 레이아웃 설정


        // 토크에서 데이터 가져오기
        talkintent = getIntent();
        Map_type = talkintent.getStringExtra("type");

        if(Map_type.toString().equals("view_point")) // 토크에서 포인트 보기면 좌표값 저장
        {
            Talk_Point_latitude = talkintent.getDoubleExtra("latitude",0);
            Talk_Point_longitude = talkintent.getDoubleExtra("longitude",0);

            Find_domicile_start(Talk_Point_latitude, Talk_Point_longitude);

            DauMap_Strat();

        }

    }




    // 다음 지도 시작
    public void DauMap_Strat()
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

    public void Find_domicile_start(Double lat, double lng)
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

        MapPoint.GeoCoordinate mapPointGeo = mapPoint.getMapPointGeoCoord();
        MapPoint.PlainCoordinate mapPointScreenLocation = mapPoint.getMapPointScreenLocation();

        Log.i("mapPoint","Daum mapPoint :"+MapPoint.mapPointWithGeoCoord(Talk_Point_latitude, Talk_Point_longitude));


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
        if(mapPOIItem.getItemName().equals("[여기]"))
        {
            Toast.makeText(this,Talk_Point_domicile, Toast.LENGTH_SHORT).show();
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
        finish();
    }



    // 뒤로가기 (댓글창 닫기)
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        switch (keyCode) {
            //하드웨어 뒤로가기 버튼에 따른 이벤트 설정
            case KeyEvent.KEYCODE_BACK: {
               finish();
            }
        }
        return super.onKeyDown(keyCode, event);
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
