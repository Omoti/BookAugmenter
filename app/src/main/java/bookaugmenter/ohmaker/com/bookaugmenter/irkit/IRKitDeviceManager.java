package bookaugmenter.ohmaker.com.bookaugmenter.irkit;

import android.content.Context;
import android.net.nsd.NsdManager;
import android.net.nsd.NsdServiceInfo;
import android.util.Log;

/**
 * IRKitDeviceManager
 * ネットワーク上のIRKitをBonjourで検索
 * (NSDManagerからjmDNSに差し替えたほうがよさそう？)
 */
public class IRKitDeviceManager {
    /**
     * IRKitDevice
     */
    public interface IRKitDeviceListener {
        void onFoundDevice(IRKitDevice device);
    }

    private final static String TAG = IRKitDeviceManager.class.getSimpleName();

    private static final String DNS_TYPE = "_irkit._tcp";
    private NsdManager.DiscoveryListener mDiscoveryListener;
    private NsdManager.ResolveListener mResolveListener;
    private NsdManager mNsdManager;
    private Context mContext;
    private IRKitDeviceListener mDeviceListener;

    /*
     * public methods
     */

    public IRKitDeviceManager(Context context){
        mContext = context;
        setupNsdManager();
    }

    /**
     * 検索開始
     */
    public void startDiscovery(){
        mNsdManager.discoverServices(DNS_TYPE, NsdManager.PROTOCOL_DNS_SD, mDiscoveryListener);
    }

    /**
     * 検索中止
     */
    public void stopDiscovery(){
        mNsdManager.stopServiceDiscovery(mDiscoveryListener);
    }

    /**
     * Listener登録
     * @param listener device discovery listener
     */
    public void registerDeviceListener(IRKitDeviceListener listener){
        mDeviceListener = listener;
    }

    /**
     * Listener登録解除
     */
    public void unregisterDeviceListener(){
        mDeviceListener = null;
    }

    /*
     * private methods
     */

    /**
     * NSDManagerセットアップ
     */
    private void setupNsdManager(){
        mDiscoveryListener = new IRKitDiscoveryListener();
        mResolveListener = new IRKitResolveListener();
        mNsdManager = (NsdManager)mContext.getSystemService(Context.NSD_SERVICE);
    }

    /**
     * NSD検索リスナー
     */
    private class IRKitDiscoveryListener implements NsdManager.DiscoveryListener{
        @Override
        public void onStartDiscoveryFailed(String serviceType, int errorCode) {

        }

        @Override
        public void onStopDiscoveryFailed(String serviceType, int errorCode) {

        }

        @Override
        public void onDiscoveryStarted(String serviceType) {

        }

        @Override
        public void onDiscoveryStopped(String serviceType) {

        }

        @Override
        public void onServiceFound(NsdServiceInfo serviceInfo) {
            Log.d(TAG, "Service Found");
            mNsdManager.resolveService(serviceInfo, mResolveListener);
        }

        @Override
        public void onServiceLost(NsdServiceInfo serviceInfo) {

        }
    }

    /**
     * NSD Resolve
     */
    private class IRKitResolveListener implements NsdManager.ResolveListener{

        @Override
        public void onResolveFailed(NsdServiceInfo serviceInfo, int errorCode) {

        }

        @Override
        public void onServiceResolved(NsdServiceInfo serviceInfo) {
            Log.d(TAG, "Resolved : " + serviceInfo.getHost().getHostAddress());

            if(mDeviceListener != null){
                mDeviceListener.onFoundDevice(
                        new IRKitDevice(
                                serviceInfo.getHost().getHostAddress(),
                                serviceInfo.getServiceName()
                        )
                );
            }
        }
    }
}
