package bookaugmenter.ohmaker.com.bookaugmenter.fragment;

import android.app.Fragment;
import android.nfc.tech.Ndef;
import android.os.Bundle;

import bookaugmenter.ohmaker.com.bookaugmenter.activity.MainActivity;
import bookaugmenter.ohmaker.com.bookaugmenter.irkit.IRKitDeviceController;

/**
 * Fragment共通操作
 */
public class BaseFragment extends Fragment{

    /**
     * 親ActivityでFragment切り替え
     * @param tag
     * @param args
     */
    protected void replaceFragment(FragmentTag tag, Bundle args){
        MainActivity activity = (MainActivity)getActivity();

        activity.replaceFragment(tag, args);
    }

    /**
     * NFCイベント発生
     * @param ndef
     * @param message
     */
    public void onNfcDiscovered(Ndef ndef, String message){
        //Should Override
    }

    /**
     * IRKit Controllerを取得
     * @return
     */
    public IRKitDeviceController getIRKitDeviceController(){
        MainActivity activity = (MainActivity)getActivity();

        return activity.getIRKitDeviceController();
    }
}
