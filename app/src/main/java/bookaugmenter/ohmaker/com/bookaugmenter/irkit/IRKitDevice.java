package bookaugmenter.ohmaker.com.bookaugmenter.irkit;

/**
 * IRKitDevice
 */
public class IRKitDevice {
    private String mIp;
    private String mName;

    public IRKitDevice(String ip, String name){
        mIp = ip;
        mName = name;
    }

    public String getIp(){
        return mIp;
    }

    public String getName(){
        return mName;
    }

}
