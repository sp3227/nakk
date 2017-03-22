package com.moduse.nakk;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapReverseGeoCoder;
import net.daum.mf.map.api.MapView;

import org.apache.http.Header;
import org.apache.http.HeaderIterator;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.ProtocolVersion;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Vector;

/**
 * Created by sejung on 2017-02-11.
 */

public class Tab2_read extends Activity implements MapView.MapViewEventListener, MapView.POIItemEventListener, MapReverseGeoCoder.ReverseGeoCodingResultListener
{


    AppInfo appInfo;  // 앱 정보 선언

    //GPS 부분
    private final int GPS_START = 0;
    private final int GPS_RESET = 1;

    private LocationManager locationManager;
    LocationListener locationListener;
    boolean isGPSEnabled;

    Double Mylocation_lat = .0;
    Double Mylocation_lon = .0;

    // 다음 맵부분
    MapView mapView ;   // 다음 맵
    RelativeLayout DaumLaout;
    final int Standard = 0;
    final int Hybrid = 1;


    // 선택 부분 좌표
    MapPOIItem Select_item = null;
    double Select_Point_latitude = 0;
    double Select_Point_longitude = 0;
    String Select_Point_adress = "";

    // 카테고리 (공개 , 비공개)
    private final int Categry_open = 0;
    private final int Categry_closed = 1;
    int Select_Categry = 0;


    // 프로그레스 설정
    ProgressDialog loading;

    //레이아웃
    TextView categry_text;

    //탭 부분
    LinearLayout tab1;
    LinearLayout tab2;
    LinearLayout tab3;
    LinearLayout tab4;

    //애드 부분
    ImageView adimg;
    String Ad_Type = "";
    String Ad_Data = "";
    String Ad_ImgURL = "";
    Intent adintent;

    //통신 부분
    phpdown downsever;

    //포인트 담을 데이터
    ArrayList<PointData> listItem = new ArrayList<PointData>();
    int tamp_index = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tab2);

        getWindow().setWindowAnimations(0);

        appInfo = new AppInfo();
        loading = new ProgressDialog(this);
        InitShow();

        //GPS 가져오기
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);  // LocationManager  객체 얻어오기
        //isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);   // 사용 체크 GPS1
        isGPSEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);   // 사용 체크 GPS2  (GPS1)은 정확하나 받아오기 실패 할 수 있음

        //레이아웃
        DaumLaout = (RelativeLayout) findViewById(R.id.tab2_map_view);
        categry_text = (TextView) findViewById(R.id.tab2_text_category);

        // 애드 인텐트 가져오기
        Intent intent = getIntent();

        Ad_Type = intent.getStringExtra("ad_type");
        Ad_Data = intent.getStringExtra("ad_data");
        Ad_ImgURL = intent.getStringExtra("ad_img");

        AdSetting();
        TabSetting();

        if(!isGPSEnabled && !AppInfo.GPSSAVE)
        {
            // GPS가 꺼져있을 시 앱이 수행할 작업 코드
            AppInfo.GPSSAVE = false;
            Toast.makeText(this,"GPS가 꺼져있습니다. 확인해주세요.",Toast.LENGTH_SHORT).show();
            DaumMap_Strat(AppInfo.Select_MapType);  // 다음맵 시작(맵타입)

        }
        else if(isGPSEnabled && !AppInfo.GPSSAVE)
        {
            My_locationinit(GPS_START);  // 자기 위치 가져오고 다음맵 실행
        }
        else if(AppInfo.GPSSAVE)
        {
            DaumMap_Strat(AppInfo.Select_MapType);  // 다음맵 시작(맵타입)
        }





    }

    // (버튼)애드 세팅
    public void AdSetting()
    {
        adimg = (ImageView) findViewById(R.id.tmp_adView);
        Glide.with(this.getApplicationContext()).load(appInfo.Get_Ad_loadimgURLL()+Ad_ImgURL).asGif().thumbnail(0.5f).diskCacheStrategy(DiskCacheStrategy.NONE).into(adimg);

        adimg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                if(Ad_Type.toString().equals("url"))
                {
                    adintent = new Intent(Intent.ACTION_VIEW, Uri.parse(Ad_Data));
                    startActivity(adintent);
                }
                else if(Ad_Type.toString().equals("call"))
                {
                    adintent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:"+Ad_Data));
                    startActivity(adintent);
                }
                else
                {
                    adintent = new Intent(Intent.ACTION_SEND);
                    adintent.setType("plain/text");
                    String[] tos = {Ad_Data};
                    adintent.putExtra(Intent.EXTRA_EMAIL, tos);
                    adintent.putExtra(Intent.EXTRA_SUBJECT, "[낚중일기] 광고 문의합니다.");
                    adintent.putExtra(Intent.EXTRA_TEXT, "광고 하고자 하는 내용을 작성 또는 첨부하여 보내주시면 답변드리겠습니다. \n\n-낚중일기-");
                    startActivity(adintent);
                }
            }
        });
    }

    // (버튼)탭 세팅
    public void TabSetting()
    {
        tab1 = (LinearLayout) findViewById(R.id.tmp_tab1);
        tab2 = (LinearLayout) findViewById(R.id.tmp_tab2);
        tab3 = (LinearLayout) findViewById(R.id.tmp_tab3);
        tab4 = (LinearLayout) findViewById(R.id.tmp_tab4);

        tab1.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                ((Main)Main.MinContext).ftn_Tab_1();
               // locationManager.removeUpdates(locationListener);  // GPS 닫기
                finish();
                overridePendingTransition(0, 0);
            }
        });

        tab2.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
            }
        });

        tab3.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                ((Main)Main.MinContext).ftn_Tab_3();
                //locationManager.removeUpdates(locationListener);  // GPS 닫기
                finish();
                overridePendingTransition(0, 0);
            }
        });

        tab4.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                ((Main)Main.MinContext).ftn_Tab_4();
               // locationManager.removeUpdates(locationListener);  // GPS 닫기
                finish();
                overridePendingTransition(0, 0);
            }
        });

    }


    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////지도안쪽 버튼 세팅 (자기위치가기, 맵 타입변경)

    // (버튼)자기 위치로 포커스 이동 시키기
    public void tab2_btn_LocationReset(View v)
    {
        if(isGPSEnabled && AppInfo.GPSSAVE)
        {
             double Latitude = AppInfo.MY_Latitude;
             double Longitude = AppInfo.MY_Longitude;

             mapView.setMapCenterPointAndZoomLevel(MapPoint.mapPointWithGeoCoord(Latitude, Longitude), 5, true); // 지도 중심점 자기 위치로 변경  // 줌레벨
        }
        else
        {
            AppInfo.GPSSAVE = false;
            Toast.makeText(this.getApplicationContext(), "위치를 찾을 수 없습니다. GPS설정후 재실행 해주세요.", Toast.LENGTH_SHORT).show();
        }
    }

    // 맵 타입 변경  (1. 스텐다드,  2. 하이드리브)
    public void tab2_btn_MapType_change(View v)
    {
        if(AppInfo.Select_MapType == Standard)
        {
            AppInfo.Select_MapType = Hybrid;
            mapView.setMapType(MapView.MapType.Hybrid);
        }
        else if(AppInfo.Select_MapType == Hybrid)
        {
            AppInfo.Select_MapType = Standard;
            mapView.setMapType(MapView.MapType.Standard);
        }
    }

    // (버튼) 카테고리 클릭 리스너
    public void tab2_btn_category(View v)
    {
        final CharSequence[] item = {"공개", "나만의"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("포인트 유형을 선택하세요.") // 제목 설정
                .setItems(item, new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialogInterface, int i)
                    {
                        if (item[i].toString().equals(item[0]))
                        {
                            mapView.removePOIItems(mapView.getPOIItems());
                            // 공개 포인트 불러오기 선택
                            categry_text.setText(item[0]);
                            Select_Categry = Categry_open;
                            tamp_index = 0;
                            listItem.clear();
                            Load_Point();  // 포인트 불러오기  PHP 통신
                        }
                        else if (item[i].toString().equals(item[1]))
                        {
                            mapView.removePOIItems(mapView.getPOIItems());
                            // 나만의 포인트 불러오기 선택
                            categry_text.setText(item[1]);
                            Select_Categry = Categry_closed;
                            tamp_index = 0;
                            listItem.clear();
                            Load_Point();  // 포인트 불러오기  PHP 통신
                        }
                        else
                        {
                            dialogInterface.dismiss();
                        }
                    }
                }).show();

        AlertDialog alert = builder.create();  //알림 객체 생성
    }

    ////////////////////////////////////////////////////////////// (버튼) 포인트 기록 등록하기
    public void tab1_btn_write(View v)
    {
        if(Select_item != null)
        {
            Intent intent = new Intent(this, Tab2_addpoint.class);

            intent.putExtra("point_type","ADD");
            intent.putExtra("point_latitude",Select_Point_latitude);
            intent.putExtra("point_longitude",Select_Point_longitude);
            intent.putExtra("point_address",Select_Point_adress);

            startActivityForResult(intent,3);
        }
        else
        {
            Toast.makeText(this,"기록 하고자 하는 위치를 선택하세요.",Toast.LENGTH_SHORT).show();
        }
    }


    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////// GPS 받아오기
    public void My_locationinit(int type)  // MY GPS 받아오기
    {
        StartShow();

        switch (type) {
            case GPS_START: {
                if (isGPSEnabled) {
                    locationListener = new LocationListener() {
                        @Override
                        public void onLocationChanged(Location location) {
                            //status.setText("위도: "+ location.getLatitude() + "\n경도: " + location.getLongitude() + "\n고도: " + location.getAltitude());

                                AppInfo.MY_Latitude = location.getLatitude();
                                AppInfo.MY_Longitude = location.getLongitude();


                            //appInfo.Set_Latitude(location.getLatitude());
                            //appInfo.Set_Longitude(location.getLongitude());

                            // 위치 정보를 가져올 수 있는 메소드입니다.
                            // 위치 이동이나 시간 경과 등으로 인해 호출됩니다.
                            // 최신 위치는 location 파라메터가 가지고 있습니다.
                            //최신 위치를 가져오려면, location 파라메터를 이용하시면 됩니다.
                            AppInfo.GPSSAVE = true;
                            DaumMap_Strat(AppInfo.Select_MapType);  // 다음맵 시작(맵타입)
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
                            1000, // 통지사이의 최소 시간간격 (miliSecond)
                            1000, locationListener); // 통지사이의 최소 변경거리 (m)

                    //locationManager.removeUpdates(locationListener);  // GPS 닫기
                }
                else if (!isGPSEnabled)
                {
                    Toast.makeText(this.getApplicationContext(), "위치를 찾을 수 없습니다. GPS설정후 재실행 해주세요.", Toast.LENGTH_SHORT).show();
                    AppInfo.GPSSAVE = false;
                    DaumMap_Strat(AppInfo.Select_MapType);  // 다음맵 시작(맵타입)
                }
                break;
            }
            case GPS_RESET: {
                if (isGPSEnabled) {
                    locationListener = new LocationListener() {
                        @Override
                        public void onLocationChanged(Location location) {
                            appInfo.Set_Latitude(location.getLatitude());
                            appInfo.Set_Longitude(location.getLongitude());

                            onMapViewInitialized(mapView);
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
                            1000, // 통지사이의 최소 시간간격 (miliSecond)
                            1000, locationListener); // 통지사이의 최소 변경거리 (m)

                    //locationManager.removeUpdates(locationListener);  // GPS 닫기
                } else if (!isGPSEnabled) {
                    Toast.makeText(this.getApplicationContext(), "위치를 찾을 수 없습니다. GPS설정후 재실행 해주세요.", Toast.LENGTH_SHORT).show();
                }
                break;
            }
        }
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////// 포인트 불러오기
    public void Load_Point()
    {
        StartShow();  // 다이얼로그 시작

        if (Select_Categry == Categry_open)
        {
            //post 인자값 전달
            Vector<NameValuePair> list = new Vector<NameValuePair>();

            //여기에 전달할 인자를 담는다. String으로 넣는것이 안전하다.
            list.add(new BasicNameValuePair("type", "OPEN"));
            list.add(new BasicNameValuePair("loginID", AppInfo.MY_LOGINID));
            list.add(new BasicNameValuePair("userID", AppInfo.MY_DEVICEID));

            try {
                UrlEncodedFormEntity entity = new UrlEncodedFormEntity(list, "UTF-8");
                String url = appInfo.Get_Tab2_PointselectURL();  // url 설정
                HttpPost request = new HttpPost(url);
                request.setEntity(entity);

                downsever = new phpdown();  // 쓰레드 생성
                downsever.execute(request);
            } catch (Exception e) {
                // 서버에 연결할 수 없습니다 토스트 메세지 보내기
                Toast.makeText(this.getApplicationContext(), "서버접속이 불안정합니다. 인터넷 환경을 확인해주세요.", Toast.LENGTH_SHORT).show();
                Log.e("Exception Error", e.toString());
            }
        } else if (Select_Categry == Categry_closed) {
            Log.i("phpinit", "2");
            //post 인자값 전달
            Vector<NameValuePair> list = new Vector<NameValuePair>();

            //여기에 전달할 인자를 담는다. String으로 넣는것이 안전하다.
            list.add(new BasicNameValuePair("type", "CLOSED"));
            list.add(new BasicNameValuePair("loginID", AppInfo.MY_LOGINID));
            list.add(new BasicNameValuePair("userID", AppInfo.MY_DEVICEID));

            try {
                UrlEncodedFormEntity entity = new UrlEncodedFormEntity(list, "UTF-8");
                String url = appInfo.Get_Tab2_PointselectURL();  // url 설정
                HttpPost request = new HttpPost(url);
                request.setEntity(entity);

                downsever = new phpdown();  // 쓰레드 생성
                downsever.execute(request);
            } catch (Exception e) {
                // 서버에 연결할 수 없습니다 토스트 메세지 보내기
                Toast.makeText(this.getApplicationContext(), "서버접속이 불안정합니다. 인터넷 환경을 확인해주세요.", Toast.LENGTH_SHORT).show();
                Log.e("Exception Error", e.toString());
            }
        }

    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////인텐트 REDULT
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        getWindow().setWindowAnimations(0);
        overridePendingTransition(0, 0);


        Log.i("UPLOAD_TYPE", "ture");
        if (requestCode == 2)
        {

            String tmpType = "";
            tmpType = data.getStringExtra("updatecode");

            if (tmpType.toString().equals("REFRASH"))
            {
                mapView.removePOIItems(mapView.getPOIItems());
                Select_Categry = Categry_open;
                tamp_index = 0;
                listItem.clear();
                Load_Point();  // 포인트 불러오기  PHP 통신
            }
        }
        if (requestCode == 3)
        {

            String tmpType = "";
            tmpType = data.getStringExtra("updatecode");

            if (tmpType.toString().equals("REFRASH")) {
                Select_item = null;
                mapView.removePOIItems(mapView.getPOIItems());
                Select_Categry = Categry_open;
                tamp_index = 0;
                listItem.clear();
                Load_Point();  // 포인트 불러오기  PHP 통신
            }
        }

    }


    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////// 다음 지도 시작
    public void DaumMap_Strat(int MapType)
    {
        mapView = new MapView(this);
        mapView.setDaumMapApiKey(appInfo.Get_DaumKey());   //  다음 키설정
        MapView.setMapTilePersistentCacheEnabled(true);
        if(Standard == MapType)
        {
            mapView.setMapType(MapView.MapType.Standard);  // 맵 타입 일반 (스텐다드)
        }
        else
        {
            mapView.setMapType(MapView.MapType.Hybrid);  // 맵 타입 하이드리브
        }
        DaumLaout.addView(mapView);

        Log.i("mapView", "true");



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
                            mapView.setMapViewEventListener(Tab2_read.this);
                            mapView.setPOIItemEventListener(Tab2_read.this);
                        }
                    });
                } catch(Exception e){
                    Log.e("error", e.toString());
                }
            }
        });
        t.start();


    }

    //////////////////////////////////////////////////////////////////////////////////////////////////// 주소 가져오기
    public void Find_domicile_start(double lat, double lng)
    {
        // 주소 찾기 초기화
        MapPoint clsPoint = MapPoint.mapPointWithGeoCoord( lat, lng );

        MapReverseGeoCoder mapGeoCoder = new MapReverseGeoCoder( appInfo.Get_DaumKey(), clsPoint, this, this );
        mapGeoCoder.startFindingAddress( );


    }


    //////////////////////////////////////////////////////////////////////////////////////////////// 다음 맵 이벤트 리스너

    @Override
    public void onMapViewInitialized(MapView mapView)  // 맵 초기 설정
    {
        try
        {
            Load_Point();  // 포인트 불러오기  PHP 통신
        }
        catch (Exception e)
        {
        }

        if (isGPSEnabled) {
           // locationManager.removeUpdates(locationListener);  // GPS 닫기
        }
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


            MapPoint.GeoCoordinate mapPointGeo = mapPoint.getMapPointGeoCoord();
            MapPoint.PlainCoordinate mapPointScreenLocation = mapPoint.getMapPointScreenLocation();

            // Log.i("mapPoint", "Daum mapPoint :" + MapPoint.mapPointWithGeoCoord(Talk_Point_latitude, Talk_Point_longitude));

            Select_Point_latitude = mapPointGeo.latitude;
            Select_Point_longitude = mapPointGeo.longitude;

            MapPOIItem marker = new MapPOIItem();

            if(Select_item != null)
            {
                mapView.removePOIItem(Select_item);
                Select_item = null;
            }

            marker.setItemName("여기");
            marker.setTag(0);
            marker.setMapPoint(MapPoint.mapPointWithGeoCoord(mapPointGeo.latitude, mapPointGeo.longitude));
            marker.setMarkerType(MapPOIItem.MarkerType.YellowPin); // 기본으로 제공하는 BluePin 마커 모양.
            marker.setSelectedMarkerType(MapPOIItem.MarkerType.YellowPin); // 마커를 클릭했을때, 기본으로 제공하는 RedPin 마커 모양.
            marker.setCustomImageAutoscale(false);

            // 주소찾기
            Find_domicile_start(Select_Point_latitude, Select_Point_longitude);

            Select_item = marker;
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
    public void onMapViewMoveFinished(MapView mapView, MapPoint mapPoint)
    {

    }

    @Override
    public void onPOIItemSelected(MapView mapView, MapPOIItem mapPOIItem)   // 포인트 아이콘 클릭 했을때
    {
        Toast.makeText(this,mapPOIItem.getItemName(),Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onCalloutBalloonOfPOIItemTouched(MapView mapView, MapPOIItem mapPOIItem)  // 포인트 아이콘 클릭하고 올라오는 팝업 클릭했을때
    {
       // Toast.makeText(this,""+mapPOIItem.getTag(),Toast.LENGTH_SHORT).show();
        if(Select_item != null)
        {
            if(mapPOIItem.getTag() == 0)
            {
                Intent intent = new Intent(this, Tab2_addpoint.class);

                intent.putExtra("point_type","ADD");
                intent.putExtra("point_latitude",Select_Point_latitude);
                intent.putExtra("point_longitude",Select_Point_longitude);
                intent.putExtra("point_address",Select_Point_adress);

                startActivityForResult(intent,3);
            }
            else
            {
                Intent intent = new Intent(this, Tab2_detail.class);
                intent.putExtra("Point_idx", "" + mapPOIItem.getTag());

                startActivityForResult(intent, 2);
            }
        }
        else
        {
            if(mapPOIItem.getTag() == 0)
            {
                Intent intent = new Intent(this, Tab2_addpoint.class);

                intent.putExtra("point_type","ADD");
                intent.putExtra("point_latitude",Select_Point_latitude);
                intent.putExtra("point_longitude",Select_Point_longitude);
                intent.putExtra("point_address",Select_Point_adress);

                startActivityForResult(intent,3);
            }
            else
            {
                Intent intent = new Intent(this, Tab2_detail.class);
                intent.putExtra("Point_idx", "" + mapPOIItem.getTag());

                startActivityForResult(intent, 2);
            }
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
        Select_Point_adress = "주소 없음";
    }

    @Override
    public void onReverseGeoCoderFoundAddress( MapReverseGeoCoder arg0, String arg1 )
    {
        Select_Point_adress = arg1;
    }




    ///////////////////////////////////////////////////////////////////////////////////////////////////////////// 통신

    private class phpdown extends AsyncTask<HttpPost, Integer, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected String doInBackground(HttpPost... urls) {
            String returnData = "";
            HttpClient httpclient = new DefaultHttpClient();
            HttpResponse response = new HttpResponse() {
                @Override
                public StatusLine getStatusLine() {
                    return null;
                }

                @Override
                public void setStatusLine(StatusLine statusLine) {
                }

                @Override
                public void setStatusLine(ProtocolVersion protocolVersion, int i) {
                }

                @Override
                public void setStatusLine(ProtocolVersion protocolVersion, int i, String s) {
                }

                @Override
                public void setStatusCode(int i) throws IllegalStateException {
                }

                @Override
                public void setReasonPhrase(String s) throws IllegalStateException {
                }

                @Override
                public HttpEntity getEntity() {
                    return null;
                }

                @Override
                public void setEntity(HttpEntity httpEntity) {
                }

                @Override
                public Locale getLocale() {
                    return null;
                }

                @Override
                public void setLocale(Locale locale) {
                }

                @Override
                public ProtocolVersion getProtocolVersion() {
                    return null;
                }

                @Override
                public boolean containsHeader(String s) {
                    return false;
                }

                @Override
                public Header[] getHeaders(String s) {
                    return new Header[0];
                }

                @Override
                public Header getFirstHeader(String s) {
                    return null;
                }

                @Override
                public Header getLastHeader(String s) {
                    return null;
                }

                @Override
                public Header[] getAllHeaders() {
                    return new Header[0];
                }

                @Override
                public void addHeader(Header header) {
                }

                @Override
                public void addHeader(String s, String s1) {
                }

                @Override
                public void setHeader(Header header) {
                }

                @Override
                public void setHeader(String s, String s1) {
                }

                @Override
                public void setHeaders(Header[] headers) {
                }

                @Override
                public void removeHeader(Header header) {
                }

                @Override
                public void removeHeaders(String s) {
                }

                @Override
                public HeaderIterator headerIterator() {
                    return null;
                }

                @Override
                public HeaderIterator headerIterator(String s) {
                    return null;
                }

                @Override
                public HttpParams getParams() {
                    return null;
                }

                @Override
                public void setParams(HttpParams httpParams) {
                }
            };


            try {
                response = httpclient.execute(urls[0]);
            } catch (Exception e) {
                // 서버에 연결할 수 없습니다 토스트 메세지 보내기
//                Toast.makeText((MainActivity) MainActivity.mContext, ((MainActivity) MainActivity.mContext).getResources().getText(R.string.server_connect_error), Toast.LENGTH_SHORT).show();
                Log.e("TalkPagePost Exception", e.toString());
            }

            try {
                BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
                StringBuilder builder = new StringBuilder();
                String str = "";

                while ((str = rd.readLine()) != null) {
                    builder.append(str);
                }

                returnData = builder.toString();
            } catch (Exception e) {
//                Toast.makeText((MainActivity) MainActivity.mContext, ((MainActivity) MainActivity.mContext).getResources().getText(R.string.server_connect_error), Toast.LENGTH_SHORT).show();
                Log.e("TalkPagePost Exception", e.toString());
            }

            return returnData;
        }

        @Override
        protected void onPostExecute(String str) {

            Log.i("Point_str22",str);
            // 보기 좋은 형태로 변수에 대입
            String idx__;

            String point_idx__;
            String point_state__;

            String loginID__;
            String deviceID__;

            String latitude__;
            String longtitude__;

            String addtime__;
            String nickname__;

            if (str.toString().equals("CHARFALSE")) {
                Toast.makeText(getApplicationContext(), "인터넷 환경이 불안정 합니다. (앱 재실행 권장)1", Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(getApplicationContext(), Login.class);
                startActivity(intent);
                finish();

            }
            else if (str.toString().equals("sql_error"))
            {
                Toast.makeText(getApplicationContext(), "인터넷 환경이 불안정 합니다. (앱 재실행 권장)2", Toast.LENGTH_SHORT).show();
            }
            else
            {
                try {
                    JSONObject root = new JSONObject(str);

                    JSONArray ja = root.getJSONArray("result");

                    for (int i = 0; i < ja.length(); i++) {

                        JSONObject jo = ja.getJSONObject(i);
                        idx__ = jo.getString("idx");
                        point_idx__ = jo.getString("point_idx");
                        point_state__ = jo.getString("point_state");
                        loginID__ = jo.getString("loginID");
                        deviceID__ = jo.getString("deviceID");
                        latitude__ = jo.getString("latitude");
                        longtitude__ = jo.getString("longtitude");
                        addtime__ = jo.getString("addtime");
                        nickname__ = jo.getString("nickname");

                        listItem.add(new PointData(idx__, point_idx__, point_state__, loginID__, deviceID__, latitude__, longtitude__, addtime__, nickname__));
                    }

                } catch (JSONException e)
                {
                    Log.i("errer1",e.toString());
                    e.printStackTrace();
                }

                DrawMarker();

            }
        }
    }

    // 마커 그리기
    public void DrawMarker()
    {
        try
        {
            for (PointData temp : listItem)
            {
                Double tmp_lat = Double.parseDouble(temp.GET_latitude());
                Double tmp_log = Double.parseDouble(temp.GET_longtitude());
                int tmp_idx =  Integer.parseInt(temp.GET_point_idx());

                MapPOIItem marker = new MapPOIItem();
                marker.setItemName(temp.GET_nickname()+"("+temp.GET_addtime()+")");
                marker.setTag(tmp_idx);
                marker.setMapPoint(MapPoint.mapPointWithGeoCoord(tmp_lat, tmp_log));
                if(temp.GET_loginID().toString().equals(AppInfo.MY_LOGINID))
                {
                    marker.setMarkerType(MapPOIItem.MarkerType.RedPin); // 기본으로 제공하는 BluePin 마커 모양.
                    marker.setSelectedMarkerType(MapPOIItem.MarkerType.RedPin); // 마커를 클릭했을때, 기본으로 제공하는 RedPin 마커 모양.
                }
                else
                {
                    marker.setMarkerType(MapPOIItem.MarkerType.BluePin); // 기본으로 제공하는 BluePin 마커 모양.
                    marker.setSelectedMarkerType(MapPOIItem.MarkerType.BluePin); // 마커를 클릭했을때, 기본으로 제공하는 RedPin 마커 모양.
                }

               // tamp_index += 1;

                mapView.addPOIItem(marker);
            }
         //   mapView.fitMapViewAreaToShowAllPOIItems();
        }
        catch(Exception e){
            Log.i("errer2",e.toString());
            Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show();
        }
        mapView.fitMapViewAreaToShowAllPOIItems();
        StopShow();    // 다이얼로그 종료
    }


    @Override
    public void onBackPressed() {
        return;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        switch (keyCode) {
            //하드웨어 뒤로가기 버튼에 따른 이벤트 설정
            case KeyEvent.KEYCODE_BACK:

                new AlertDialog.Builder(this)
                        .setTitle("[앱 종료]") // 제목 설정
                        .setMessage("낚중일기를 종료 하시겠습니까?")
                        .setCancelable(false)  //뒤로 버튼 클릭시 취소 설정
                        .setNegativeButton("예", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // 프로세스 종료.
                                ActivityCompat.finishAffinity(Main.mainAC);
                                finish();
                            }
                        })
                        .setPositiveButton("아니오", null)
                        .show();
                break;
            default:
                break;
        }
        return super.onKeyDown(keyCode, event);
    }


/*
            locationManager.removeUpdates(locationListener);  // GPS 닫기
    @Override
    protected void onDestroy()
    {
        super.onDestroy();

    }

*/

    @Override
    protected void onPause()
    {

        super.onPause();

    }

    public void RefreshMap()
    {
        mapView.removePOIItems(mapView.getPOIItems());
        Select_Categry = Categry_open;
        tamp_index = 0;
        listItem.clear();
        Load_Point();  // 포인트 불러오기  PHP 통신
    }


    // 프로그레스 설정
    public void InitShow()
    {
        loading.setProgress(ProgressDialog.STYLE_SPINNER);
        loading.setMessage("정보를 불러오는 중입니다..\n(지도가 느려요..)\n조금만 기다려 주세요^^");
        loading.setCanceledOnTouchOutside(false);
    }
    public void SetmsgShow(String value)
    {
        loading.setMessage(value);
    }
    public void StartShow() {loading.show();}
    public void StopShow() {loading.dismiss();}

}
