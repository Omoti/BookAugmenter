package bookaugmenter.ohmaker.com.bookaugmenter.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.nfc.tech.Ndef;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import bookaugmenter.ohmaker.com.bookaugmenter.R;
import bookaugmenter.ohmaker.com.bookaugmenter.irkit.IRKitMessage;

/**
 * 本をよむ画面
 */
public class ReadBookFragment extends BaseFragment{

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_read_book, container, false);

        return view;
    }

    /**
     * NFCからメッセージ取得
     * @param message
     */
    @Override
    public void onNfcDiscovered(Ndef ndef, String message){
        TextView nfcTextView = (TextView)this.getView().findViewById(R.id.text_nfc_message);
        nfcTextView.setText(message);

        if(message != null) {
            //TODO : NFCの内容によってメッセージを変える
            sendMessage();
        }
    }

    //コマンド送信(デバッグ用)
    private void sendMessage(){
        SharedPreferences preferences = getActivity().getSharedPreferences("DATA", Context.MODE_PRIVATE);
        String data = preferences.getString("TEST", null);

        if (data != null){
            try {
                JSONObject jsonObject = new JSONObject(data);

                IRKitMessage message = new IRKitMessage(jsonObject);

                getIRKitDeviceController().sendMessage(message);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
