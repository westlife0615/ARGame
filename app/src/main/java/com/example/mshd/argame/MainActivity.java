package com.example.mshd.argame;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private CameraView cameraView;
    private GameView gameView;
    private SQLiteDatabase sd;
    private DBHelper dbHelper;
    private Geocoder geocoder;

    private String bThoroughfare = "";

    private ArrayList<Unit> units = new ArrayList<Unit>();
    private Cursor c;

    //현재 핸드폰의 방위각
    float headingAngle = 0.0f;

    //현재 위치
    Location curLocation;

    //진동 처리를 위해
    Vibrator vibrator;

    //배경음 재생을 위해
    MediaPlayer mediaPlayer;

    LogHandler logHandler ;


    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    Log.d("@@@@@@@@@@@@@@@@@@@", "현재 units의 사이즈 " + units.size());
                    BitmapDescriptor image = BitmapDescriptorFactory.fromResource(android.R.drawable.ic_lock_idle_low_battery);
                    for (Unit u : units) {
                        Marker marker = mMap.addMarker(new MarkerOptions().position(new LatLng(u.getLat(), u.getLon())).
                                title("몬스터 이름 : " + u.getName()).icon(image).draggable(true));
                        Log.d("@@@@@@@@@@@@@@@@@@@", "화면에 다음 몬스터 그림 " + u.getName());
                    }
                    break;
                case 1:
                    Animation animation = AnimationUtils.loadAnimation(MainActivity.this, R.anim.animation);
                    gameView.startAnimation(animation);
                    break;
            }

        }
    };

    //특정 지역(동)의 몬스터를 DB로 부터 읽어들여 ArrayList units에 할당
    class QueryThread extends Thread {
        String thoroughfare = "";

        public QueryThread(String thoroughfare) {
            this.thoroughfare = thoroughfare;
        }

        public void run() {
            units.clear();
//            sd.query("")
            c = sd.rawQuery("select * from monster where address='" + thoroughfare + "';", null);
            Log.d("!!!!!!!!!!!!!!!!!!!!", c.getColumnCount() + "");
            c.moveToFirst();
            while (c.isAfterLast() == false) {
                Unit u = new Unit();
                u.setName(c.getString(1));
                u.setAddr(c.getString(2));
                u.setLat(Double.parseDouble(c.getString(3)));
                u.setLon(Double.parseDouble(c.getString(4)));
                units.add(u);
                Log.d("######################", "units에 몬스터 추가 " + u.getName());
                c.moveToNext();
            }
            handler.sendEmptyMessage(0);
            Log.d("######################", "address 가져와서 unit에 할당");
        }
    }


    class MyLocationListener implements LocationListener {
        @Override
        public void onLocationChanged(Location location) {
            if (mMap != null) {
                curLocation = location;
                //현재 위치의 주소(동이름) 가져오기
                List<Address> addr = null;
                try {
                    addr = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                    String curThoroughfare = addr.get(0).getThoroughfare();
//                    new QueryThread().start();

                    Log.d("######################", "address 비교" + " bThoroughfare =" + bThoroughfare + " curThoroughfare " + curThoroughfare);
                    if (!bThoroughfare.equals(curThoroughfare)) {  //이동된 위치가 이전 동 주소와 다르다면
                        bThoroughfare = curThoroughfare;
                        Log.d("######################", "Thread start");
                        new QueryThread(curThoroughfare).start();
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }

                //현재 위치로 지도를 지속적으로 이동
                LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                CameraPosition cb = new CameraPosition.Builder().target(latLng).zoom(15).build();
                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cb));
            }
        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {
        }

        @Override
        public void onProviderEnabled(String s) {
        }

        @Override
        public void onProviderDisabled(String s) {
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mainactivity);

        logHandler = new LogHandler(this);

        //전체 화면 설정
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        //DB 접근을 위한 SQLiteDatabase
        dbHelper = new DBHelper(this);
        sd = dbHelper.getWritableDatabase();
//        dbHelper.close();

        //gameView 할당
        gameView = (GameView) findViewById(R.id.gameview);

        //위치 <-> 주소 변환을 위한 geocoder
        geocoder = new Geocoder(this);

        //MapFragment
        MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //위치 정보 수신 처리
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, new MyLocationListener());
//        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, new MyLocationListener());

        //
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        //몬스터 검색 Thread 시작
        new SearchingThread().start();

        //현재 핸드폰의 방위각 확인 시작
        getCurHeadingAngle();

        //배경음재생을 위해
        mediaPlayer = MediaPlayer.create(this, R.raw.mysong);


    }

    @Override
    protected void onPause() {
        super.onPause();
        mediaPlayer.pause();
        System.exit(0);
//                finish();
//        int pid = android.os.Process.myPid();
//        android.os.Process.killProcess(pid);


    }

    @Override
    protected void onStart() {
        super.onStart();
        mediaPlayer.start();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMyLocationEnabled(true);

    }

    //현재 디바이스의 방향센서를 사용하여 X축(방위각) : headingAngle 가져오기
    public void getCurHeadingAngle() {
        SensorManager manager = (SensorManager) getSystemService(SENSOR_SERVICE);
        SensorEventListener mListener = new SensorEventListener() {
            public void onSensorChanged(SensorEvent event) {
                if (event.accuracy != SensorManager.SENSOR_STATUS_UNRELIABLE) {
                    headingAngle = event.values[0];
                }
            }

            public void onAccuracyChanged(Sensor sensor, int accuracy) {
            }
        };
        manager.registerListener(mListener, manager.getDefaultSensor(Sensor.TYPE_ORIENTATION),
                SensorManager.SENSOR_DELAY_UI);
    }

    //목표와의 방위각 계산
    public int getBearing(double P1_latitude, double P1_longitude, double P2_latitude, double P2_longitude) {
        // 현재 위치 : 위도/경도 라디안으로 변환한다.
        double Cur_Lat_radian = P1_latitude * (3.141592 / 180);
        double Cur_Lon_radian = P1_longitude * (3.141592 / 180);
        // 목표 위치 : 위도/경도 라디안으로 변환한다.
        double Dest_Lat_radian = P2_latitude * (3.141592 / 180);
        double Dest_Lon_radian = P2_longitude * (3.141592 / 180);
        // radian distance
        double radian_distance = 0;
        radian_distance = Math.acos(Math.sin(Cur_Lat_radian) * Math.sin(Dest_Lat_radian) +
                Math.cos(Cur_Lat_radian) * Math.cos(Dest_Lat_radian) * Math.cos(Cur_Lon_radian - Dest_Lon_radian));
        // 목표위치와의 방향 구하기(라디안), acos 인수(radian)
        double radian_bearing = Math.acos((Math.sin(Dest_Lat_radian) - Math.sin(Cur_Lat_radian) *
                Math.cos(radian_distance)) / (Math.cos(Cur_Lat_radian) * Math.sin(radian_distance)));
        double true_bearing = 0;
        if (Math.sin(Dest_Lon_radian - Cur_Lon_radian) < 0) {
            true_bearing = radian_bearing * (180 / 3.141592);
            true_bearing = 360 - true_bearing;
        } else {
            true_bearing = radian_bearing * (180 / 3.141592);
        }
        return (int) true_bearing;
    }

    class SearchingThread extends Thread {
        public void run() {
            while (true) {
                //범위내의 모든 유닛들과의 방위각 계산
                for (Unit u : units) {
//                    int x = getBearing(u.getLat(), u.getLon(), curLocation.getLatitude(), curLocation.getLongitude());
                      int x = getBearing(curLocation.getLatitude(), curLocation.getLongitude(), u.getLat(), u.getLon());

                    Log.d("@@@@@@@@@@@@@@@@@@@@" , "센서 방위각 = " + headingAngle + " // 목표와의 방위각 = " + x);
                    //유닛과의 방위각이 +- 30 범위면 출현 판정
                    if ((headingAngle - 15) < x && x < (headingAngle + 15) && u.getVisible_time() <= 0) {
                        if (gameView.u == null) {

                            logHandler.sendEmptyMessage(0);

                            u.setVisible_time(10); //화면 좌/우측을 기준으로 몹의 이동 횟수 결정(화면 노출 시간), 0일 경우 노출종료 상태
                            vibrator.vibrate(1000); //몹 발견시 진동으로 알림

                            gameView.showUnit(u);   //gameVIew에서 해당 몬스터 보여주도록 처리
                            handler.sendEmptyMessage(1);
                        }else {
                            logHandler.sendEmptyMessage(1);
                        }

                        try {
                            Thread.sleep(5000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    Log.d("*************", "방위각 비교 ");
                }
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    //목표와의 거리 계산
    public int getDistance(Location from, Location to) {
        int distance = (int) from.distanceTo(to);
        return distance;
    }

}
