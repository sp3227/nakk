package com.moduse.nakk;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.vending.billing.IInAppBillingService;
import com.example.android.trivialdrivesample.util.IabHelper;
import com.example.android.trivialdrivesample.util.IabResult;

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
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Locale;

/**
 * Created by sejung on 2017-02-27.
 */

public class Tab4_sponsor extends Activity
{

    ProgressDialog loading;

    // 앱 정보
    AppInfo appInfo;

    // 상수
    final int UPDATE_INTENT_CODE = 303;

    //통신
    phpdown task;

    // 레이아웃
    TextView infotext;
    TextView payvlue;

    // 아이템
    private String item1 = "nakk_sponsor_50000";
    private String item2 = "nakk_sponsor_100000";
    String SelectItem = "none";

    // 결제 부분
    IInAppBillingService mService;
    IabHelper mHelper;
    private String base64EncodedPublicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAubYNKMBqAWHEpZ+ctChruoVTIeDqy0anyt6kB1HXfASvUkaIVvxWq2MTMs30wU5XoEBKqw1qAeAuWhi3gNBXZoo0LFNP+K32j7/6b94qzgHfwXhCRBBU578jWY3kkLH2yigJUrH243F5vVPIcs8GMauTmtEPZs7hp6sPn2DUKN4w5vCoowDN0OIuYnDEDhwnfe1OCFzN3Doc6LmOvhP/ZfN+Apv/wsPmIxxu2xbj2OWt12T6OayX1mSm+A4n7kVV9iW5AzYehoV+icWaxh/iu1ZCENlghAdOQq8YcGZRrRiMTMQrBBH7pYDdACocpAS/TsSlIobNtUXc6Ns36USOuQIDAQA";


    ServiceConnection mServiceConn = new ServiceConnection() {
        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.d("InAppBilling", "서비스 연결안됨");
            mService = null;
        }
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.d("InAppBilling", "서비스연결");
            mService = IInAppBillingService.Stub.asInterface(service);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tab4_sponsor);

        appInfo = new AppInfo();

        loading = new ProgressDialog(this);

        infotext = (TextView) findViewById(R.id.sponsorText);
        payvlue = (TextView) findViewById(R.id.tab4_sponsor_payvalue);

        SelectItem = "none";

        ColorChange();

        /** 구글 인앱 결제 초기 세팅*/
        Intent intent = new Intent("com.android.vending.billing.InAppBillingService.BIND");

        // 롤리팝에서 발생하는 버그, 암시적인 바인드문을 명시적으로 변경 (패키지 이름 등록)
        intent.setPackage("com.android.vending");
        // 인앱 바인드
        bindService(intent, mServiceConn, Context.BIND_AUTO_CREATE);
        IAB_Init();

    }

    //인앱 초기화
    public void IAB_Init()
    {
        mHelper = new IabHelper(this, base64EncodedPublicKey);
        mHelper.enableDebugLogging(true);
        mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
            public void onIabSetupFinished(IabResult result) {
                if (!result.isSuccess()) {
                    Log.d("InAppBilling", "구매오류");
                    // 구매오류처리 ( 토스트하나 띄우고 결제팝업 종료시키면 되겠습니다 )
                    Toast.makeText(getApplicationContext(), "구매 실패", Toast.LENGTH_SHORT).show();
                }
                Log.d("InAppBilling1", "구매목록 초기화");
                AlreadyPurchaseItems();
                // AlreadyPurchaseItems(); 메서드는 구매목록을 초기화하는 메서드입니다.
                // v3으로 넘어오면서 구매기록이 모두 남게 되는데 재구매 가능한 상품( 게임에서는 코인같은아이템은 ) 구매후 삭제해주어야 합니다.  이 메서드는 상품 구매전 혹은 후에 반드시 호출해야합니다. ( 재구매가 불가능한 1회성 아이템의경우 호출하면 안됩니다 )

            }
        });
    }

  //////////////////////////////////////////////////////////////////////////// 아이템 초기화 함수 (재구매 가능하게)
    public void AlreadyPurchaseItems() {
        try {
            Log.d("InAppBilling2",  "토큰확인1");
            Bundle ownedItems = mService.getPurchases(3, getPackageName(), "inapp", null);
            int response = ownedItems.getInt("RESPONSE_CODE");
            if (response == 0) {
                Log.d("InAppBilling3",  "토큰확인2");
                ArrayList purchaseDataList = ownedItems
                        .getStringArrayList("INAPP_PURCHASE_DATA_LIST");
                String[] tokens = new String[purchaseDataList.size()];
                for (int i = 0; i < purchaseDataList.size(); ++i) {
                    String purchaseData = (String) purchaseDataList.get(i);
                    JSONObject jo = new JSONObject(purchaseData);
                    tokens[i] = jo.getString("purchaseToken");
                    // 여기서 tokens를 모두 컨슘 해주기
                    mService.consumePurchase(3, getPackageName(), tokens[i]);
                }
            }
            Log.d("InAppBilling4", "토큰확인3");

            // 토큰을 모두 컨슘했으니 구매 메서드 처리
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////// 결제 실행 함수
    public void Buy(String id_item) {
        // Var.ind_item = index;
        try {
            Log.d("InAppBilling5",  "구매1");
            Bundle buyIntentBundle = mService.getBuyIntent(3, getPackageName(), SelectItem, "inapp", "bGoa+V7g/yqDXvKRqq+JTFn4uQZbPiQJo4pf9RzJ");
            PendingIntent pendingIntent = buyIntentBundle.getParcelable("BUY_INTENT");

            if (pendingIntent != null) {
                startIntentSenderForResult(pendingIntent.getIntentSender(), 1001, new Intent(), Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(0));
            }
            else
            {
                // 구매가 막혔다면
                AlreadyPurchaseItems();
            }

        } catch (Exception e) {
            Log.d("InAppBilling",  "구매에러2");
            Log.d("InAppBilling", e.toString());

            // 토큰 초기화
            AlreadyPurchaseItems();
            e.printStackTrace();
        }
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////// 결제가 완료 이후 인증처리
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(requestCode == UPDATE_INTENT_CODE)
        {
            moveTaskToBack(true);
            finish();
            android.os.Process.killProcess(android.os.Process.myPid());
        }

        Log.d("InAppBilling6",  "requestCode : " + requestCode);
        if(requestCode == 1001)
            if (resultCode == RESULT_OK) {
                if (!mHelper.handleActivityResult(requestCode, resultCode, data)) {
                    super.onActivityResult(requestCode, resultCode, data);

                    int responseCode = data.getIntExtra("RESPONSE_CODE", 0);
                    String purchaseData = data.getStringExtra("INAPP_PURCHASE_DATA");
                    String dataSignature = data.getStringExtra("INAPP_DATA_SIGNATURE");

                    Log.d("InAppBilling",  "responseCode : " + String.valueOf(responseCode));
                    Log.d("InAppBilling", "purchaseData : " + purchaseData);
                    Log.d("InAppBilling", "dataSignature : " + dataSignature);

                    try {
                        JSONObject obj = new JSONObject(purchaseData);

//                        Log.d("InAppBilling", MyApplication.GetServerDomain() + "/php/add_point_manager.php?type=google_pay&device_id=" + myApp.myInfo.getDevice_id() + "&item_id=" + obj.getString("productId")
//                                + "&data_info=" + URLEncoder.encode(obj.getString("orderId")) + "&secret_key=" + MyApplication.secretKey);

                        StartShow();

                        // 결제 성공하면 포인트 추가
                        ArrayList<NameValuePair> post = new ArrayList<NameValuePair>();
                        post.add(new BasicNameValuePair("type", "google_pay"));                                // 결제 타입
                        post.add(new BasicNameValuePair("productId",obj.getString("productId")));                // 아이템 아이디
                        post.add(new BasicNameValuePair("orderId",obj.getString("orderId")));                  //영수증
                        post.add(new BasicNameValuePair("purchaseToken",obj.getString("purchaseToken")));    // 토큰
                        post.add(new BasicNameValuePair("loginID", AppInfo.MY_LOGINID));                        // 로그인 아이디
                        post.add(new BasicNameValuePair("deviceID", AppInfo.MY_DEVICEID));                      // 디바이스 아이디
                        if(SelectItem.toString().equals("nakk_sponsor_50000"))
                        {
                            post.add(new BasicNameValuePair("money", "50000"));                               // 금액 머니
                        }
                        else if(SelectItem.toString().equals("100000"))
                        {
                            post.add(new BasicNameValuePair("money", AppInfo.MY_DEVICEID));
                        }

                        try {
                            UrlEncodedFormEntity entity = new UrlEncodedFormEntity(post, "UTF-8");
                            HttpPost httpPost = new HttpPost(appInfo.Get_Tab4_sponsor_pay());
                            httpPost.setEntity(entity);

                            task = new phpdown();
                            task.execute(httpPost);
                        }
                        catch(Exception e)
                        {
                            // 서버에 연결할 수 없습니다 토스트 메세지 보내기
                            Toast.makeText(getApplicationContext(),"서버가 불안정 합니다.", Toast.LENGTH_SHORT).show();
                            Log.e("Exception Error", e.toString());
                        }

//                        task = new phpDown();
//                        task.execute(MyApplication.GetServerDomain() + "/php/add_point_manager.php?type=google_pay&device_id=" + myApp.myInfo.getDevice_id() + "&item_id=" + obj.getString("productId")
//                                + "&data_info=" + URLEncoder.encode(obj.getString("orderId")) + "&secret_key=" + MyApplication.secretKey);
                    }

                    catch(Exception e)
                    {
                        Log.d("InAppBilling",e.toString());
                    }

                    // 여기서 아이템 추가 해주시면 됩니다.
                    // 만약 서버로 영수증 체크후에 아이템 추가한다면, 서버로 purchaseData , dataSignature 2개 보내시면 됩니다.
                } else {
                    // 구매취소 처리
                }
            }else{
                // 구매취소 처리
            }
        else{
            // 구매취소 처리
        }
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
            } catch (Exception e)
            {
                Log.e("TalkPagePost Exception", e.toString());
            }

            return returnData;
        }

        @Override
        protected void onPostExecute(String str)
        {
            StopShow();
            Log.i("PAY_STR",str);
            if(str.toString().equals("SUCCESS"))
            {
                Toast.makeText(getApplicationContext(),"후원에 감사드립니다. 후원 뺏지가 적용되었습니다.",Toast.LENGTH_SHORT).show();
            }
            else if(str.toString().equals("FAILURE1"))
            {
                Toast.makeText(getApplicationContext(),"결제가 실패 되었습니다.(CODE 1)",Toast.LENGTH_SHORT).show();
            }
            else if(str.toString().equals("FAILURE2"))
            {
                Toast.makeText(getApplicationContext(),"결제가 실패 되었습니다.(CODE 2)",Toast.LENGTH_SHORT).show();
            }
            else
            {
                Toast.makeText(getApplicationContext(),"인터넷 환경이 불안정 합니다.",Toast.LENGTH_SHORT).show();
            }

        }
    }




    // 설명글 컬러 부분 체인지
    public void ColorChange()
    {
        SpannableStringBuilder builder = new SpannableStringBuilder(getResources().getString(R.string.sponsortext));
        builder.setSpan(new ForegroundColorSpan(Color.parseColor("#01bfd7")), 0, 4, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        builder.setSpan(new ForegroundColorSpan(Color.parseColor("#01bfd7")), 88,93, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        infotext.append(builder);

    }

    // 스폰 금액 설정 리스너
    public void tab4_sponsor_value(View v)
    {
        final CharSequence[] item = {"50,000원", "100,000원","취소"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("후원 금액을 선택해주세요.") // 제목 설정
                .setItems(item, new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialogInterface, int i)
                    {
                        if (item[i].toString().equals(item[0]))
                        {
                            // 금액 5만원
                            payvlue.setText(item[0]+" ");
                            SelectItem = item1;
                        }
                        else if (item[i].toString().equals(item[1]))
                        {
                            // 금액 10만원
                            payvlue.setText(item[1]+" ");
                            SelectItem = item2;
                        }
                        else if (item[i].toString().equals(item[2]))
                        {
                            // 금액 취소
                            payvlue.setText("- ");
                            SelectItem = "none";
                        }
                        else
                        {
                            SelectItem = "none";
                            dialogInterface.dismiss();
                        }
                    }
                }).show();

        AlertDialog alert = builder.create();  //알림 객체 생성
    }


    // 스폰하기 결제 클릭 리스너
    public void tab4_sponsor_submit(View v)
    {
        if(!SelectItem.toString().equals("none")) {
            try {
                Buy(SelectItem);
            } catch (Exception e) {
                Log.d("BuyError", e.toString());
            }
        }
        else
        {
            Toast.makeText(getApplicationContext(),"후원 금액을 선택해 주세요.",Toast.LENGTH_SHORT).show();
        }
    }



    // Activity 종료시 인앱 서비스 종료
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mService != null) {
            unbindService(mServiceConn);
        }
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

