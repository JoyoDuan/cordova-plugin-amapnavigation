package com.joyo.cordova.navigation;

import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.PluginResult;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;
import android.view.View;

import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Poi;
import com.amap.api.navi.AmapNaviPage;
import com.amap.api.navi.AmapPageType;
import com.amap.api.navi.AmapNaviParams;
import com.amap.api.navi.AmapNaviType;
import com.amap.api.navi.INaviInfoCallback;
import com.amap.api.navi.model.AMapNaviLocation;
import com.amap.api.navi.model.NaviLatLng;


/**
 * 高德地图导航插件
 */
public class AMapNavigationPlugin extends CordovaPlugin implements INaviInfoCallback {
    // 导航失败
    private final int NAVI_FAIL = -1;


    private CallbackContext callbackContext;

    // 是否为模拟导航
    private boolean isEmulatorNavi = false;

    // 起点终点
    private Poi startPoint;
    private Poi endPoint;
    // 导航类型
    private String naviType;
    // 导航的页面类型
    private String pageType;


    // 导航类型，默认驾驶模式
    private AmapNaviType amapNaviType;
    // 导航页面类型，默认路径规划页面
    private AmapPageType amapPageType;


    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        if (action.equals("navigation")) {
            this.callbackContext = callbackContext;

            // 起点
            String startLng = args.getString(0);
            String startLat = args.getString(1);
            String startAddress = args.getString(2);

            // 终点
            String endLng = args.getString(3);
            String endLat = args.getString(4);
            String endAddress = args.getString(5);

            // 导航类型
            naviType = args.getString(6);
            // 导航的页面类型
            pageType = args.getString(7);

            startPoint = new Poi(startAddress, new LatLng(Float.parseFloat(startLat), Float.parseFloat(startLng)), "");
            endPoint = new Poi(endAddress, new LatLng(Float.parseFloat(endLat), Float.parseFloat(endLng)), "");
            
            // 准备导航
            initNavi(startPoint, endPoint, naviType, pageType);

            return true;
        }
        return false;
    }


    /*
     * 初始化数据
     *
     * Created By JoyoDuan On 2019-12-03
     */
    private void initNavi(Poi startPoint, Poi endPoint, String naviType, String pageType) {
        // 步行
        if ("3".equals(naviType)) {
            amapNaviType = AmapNaviType.WALK;
        } else if ("2".equals(naviType)) { // 骑行
            amapNaviType = AmapNaviType.RIDE;
        } else { // 驾车
            amapNaviType = AmapNaviType.DRIVER;
        }

        // 导航页面类型 1: 路线规划  2: 直接导航
        if ("2".equals(pageType)) {
            amapPageType = AmapPageType.NAVI;
        } else {
            amapPageType = AmapPageType.ROUTE;
        }

        AmapNaviParams params = new AmapNaviParams(startPoint, null, endPoint, amapNaviType, amapPageType);
        params.setUseInnerVoice(true);
        AmapNaviPage.getInstance().showRouteActivity(this.cordova.getActivity().getApplicationContext(), params, AMapNavigationPlugin.this);
    }


    /**
     * 回调结果
     */
    private void onResult(int i) {
        JSONObject json = new JSONObject();
        try {
            json.put("status", i);
            PluginResult pluginResult = new PluginResult(PluginResult.Status.OK, json);
            callbackContext.sendPluginResult(pluginResult);
        } catch (JSONException ex) {
            Log.e("onResult", ex.getMessage());
            callbackContext.error(ex.getMessage());
        }
    }


    private void keepCallback(NaviLatLng point) {
        JSONObject json = new JSONObject();
        try {
            json.put("status", 1);
            json.put("lat", point.getLatitude());
            json.put("lng", point.getLongitude());
        } catch (JSONException ex) {
            Log.e("keepCallback", ex.getMessage());
        }
        PluginResult pluginResult = new PluginResult(PluginResult.Status.OK, json);
        pluginResult.setKeepCallback(true);
        callbackContext.sendPluginResult(pluginResult);
    }


    @Override
    public void onInitNaviFailure() {
        Log.i("onInitNaviFailure","导航失败");

        // 初始化导航失败
        this.onResult(NAVI_FAIL);
    }

    @Override
    public void onGetNavigationText(String s) {

    }

    @Override
    public void onLocationChange(AMapNaviLocation aMapNaviLocation) {
        this.keepCallback(aMapNaviLocation.getCoord());
    }

    @Override
    public void onArriveDestination(boolean b) {
        Log.i("onArriveDestination", "到达目的地");
    }

    @Override
    public void onStartNavi(int i) {
        Log.i("onStartNavi","启动导航");
    }

    @Override
    public void onCalculateRouteSuccess(int[] ints) {
        if (isEmulatorNavi) {
            Log.i("onCalculateRouteSuccess","模拟导航");

        } else {
            Log.i("onCalculateRouteSuccess","实时导航");
        }
    }

    @Override
    public void onCalculateRouteFailure(int i) {
        Log.i("onCalculateRouteFailure", "路径计算失败，请检查网络或输入参数 - " + i);
    }

    @Override
    public void onStopSpeaking() {

    }

    @Override
    public void onReCalculateRoute(int i) {

    }

    @Override
    public void onExitPage(int i) {
        Log.i("onExitPage", "退出导航");

        this.onResult(i);
    }

    @Override
    public void onStrategyChanged(int i) {

    }

    @Override
    public View getCustomNaviBottomView() {
        return null;
    }

    @Override
    public View getCustomNaviView() {
        return null;
    }

    @Override
    public void onArrivedWayPoint(int i) {

    }

    @Override
    public void onMapTypeChanged(int i) {

    }

    @Override
    public View getCustomMiddleView() {
        return null;
    }

    @Override
    public void onNaviDirectionChanged(int i) {

    }

    @Override
    public void onDayAndNightModeChanged(int i) {

    }

    @Override
    public void onBroadcastModeChanged(int i) {

    }

    @Override
    public void onScaleAutoChanged(boolean b) {

    }
}
