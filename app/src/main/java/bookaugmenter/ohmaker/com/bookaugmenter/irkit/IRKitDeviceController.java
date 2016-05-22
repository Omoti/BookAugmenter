package bookaugmenter.ohmaker.com.bookaugmenter.irkit;

import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

/**
 * IRKitDeviceController
 * IRKitを操作
 */
public class IRKitDeviceController {
    private static final String TAG = System.class.getSimpleName();

    IRKitDevice mDevice;
    IRKitDeviceControllerCallback mCallback;
    RequestQueue mRequestQueue;

    /**
     * 操作完了コールバック
     */
    public interface IRKitDeviceControllerCallback{
        void onReceiveRecentMessage(IRKitMessage message);
    }

    /*
    * コンストラクタ
    * RequestQueueシングルトンをもらう
    */
    public IRKitDeviceController(IRKitDevice device, RequestQueue requestQueue){
        mDevice = device;
        mRequestQueue = requestQueue;
    }

    /*
     * コールバックセット
     */
    public void setCallback(IRKitDeviceControllerCallback callback){
        mCallback = callback;
    }

    /*
     * 信号を送信
     */
    public void sendMessage(IRKitMessage message){
        if (mDevice == null || mDevice.getIp() == null){
            Log.d(TAG, "invalid device.");
            return;
        }

        if (message == null){
            Log.d(TAG, "invalid message.");
            return;
        }

        String url = "http://" + mDevice.getIp() + "/messages";

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST,
                url,
                message.toJSONObject(),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, "success : " + response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d(TAG, "error : " + error.getMessage());
                    }
                }
        );

        if (mRequestQueue != null){
            mRequestQueue.add(request);
        }else{
            Log.d(TAG, "RequestQueue is null.");
        }
    }

    /*
     * 最新の受信信号を取得
     */
    public void requestRecentMessage(){
        if (mDevice == null || mDevice.getIp() == null){
            Log.d(TAG, "invalid device.");
            return;
        }

        String url = "http://" + mDevice.getIp() + "/messages";

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET,
                url,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        IRKitMessage message = new IRKitMessage(response);
                        if (mCallback != null) {
                            mCallback.onReceiveRecentMessage(message);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (mCallback != null) {
                            mCallback.onReceiveRecentMessage(null);
                        }
                    }
                });

        if (mRequestQueue != null){
            mRequestQueue.add(request);
        }else{
            Log.d(TAG, "RequestQueue is null.");
        }
    }
}
