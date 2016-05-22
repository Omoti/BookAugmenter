package bookaugmenter.ohmaker.com.bookaugmenter.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.nfc.FormatException;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.TagLostException;
import android.nfc.tech.Ndef;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.io.IOException;

import bookaugmenter.ohmaker.com.bookaugmenter.R;
import bookaugmenter.ohmaker.com.bookaugmenter.irkit.IRKitDeviceController;
import bookaugmenter.ohmaker.com.bookaugmenter.irkit.IRKitMessage;

/**
 * 本をよむ画面
 */
public class CreateTagFragment extends BaseFragment implements IRKitDeviceController.IRKitDeviceControllerCallback {
    private static final String TAG = CreateTagFragment.class.getSimpleName();

    private String mLastMessage = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_create_tag, container, false);

        view.findViewById(R.id.bt_get_message).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getIRKitDeviceController().setCallback(CreateTagFragment.this);
                getIRKitDeviceController().requestRecentMessage();
            }
        });

        return view;
    }

    @Override
    public void onNfcDiscovered(Ndef ndef, String message) {
        if (mLastMessage == null){
            return;
        }

        NdefMessage ndefMessage = createNdefRecord(mLastMessage);
        if (writeNdefMessage(ndef, ndefMessage)) {
            Log.d(TAG, "Success : " + mLastMessage);
            Snackbar.make(this.getView(), "Success", Snackbar.LENGTH_LONG).setAction("Action", null).show();
        }else{
            Log.d(TAG, "Failed : " + mLastMessage);
            Snackbar.make(this.getView(), "Failed", Snackbar.LENGTH_LONG).setAction("Action", null).show();
        }
    }

    private NdefMessage createNdefRecord(String text) {
        NdefMessage msg = new NdefMessage(NdefRecord.createTextRecord(null, text));

        NdefRecord spRecord
                = new NdefRecord(NdefRecord.TNF_WELL_KNOWN,
                NdefRecord.RTD_TEXT,
                new byte[0],
                msg.toByteArray());

        return msg;//new NdefMessage(new NdefRecord[]{spRecord});
    }

    /**
     * NFC書き込み
     * @param ndefTag
     * @param ndefMessage
     * @return
     */
    private boolean writeNdefMessage(Ndef ndefTag, NdefMessage ndefMessage) {
        if (!ndefTag.isWritable()) {
            return false;
        }
        int messageSize = ndefMessage.toByteArray().length;
        if (messageSize > ndefTag.getMaxSize()) {
            return false;
        }

        try {
            if (!ndefTag.isConnected()) {
                ndefTag.connect();
            }
            ndefTag.writeNdefMessage(ndefMessage);
            return true;
        } catch (TagLostException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } catch (FormatException e) {
            e.printStackTrace();
            return false;
        } finally {
            try {
                ndefTag.close();
            } catch (IOException e) {

            }
        }
    }

    @Override
    public void onReceiveRecentMessage(IRKitMessage message) {
        TextView last_text = (TextView) this.getView().findViewById(R.id.text_last_message);

        if (message != null) {
            mLastMessage = "test";//message.getRawJson().toString();
            last_text.setText(message.getRawJson().toString());

            //Preferenceに保存(デバッグ用)
            savePreference(message.getRawJson().toString());
        }else{
            last_text.setText("Error");
        }

        //TODO : SQLiteで複数のメッセージを管理できるようにする
    }

    private void savePreference(String data){
        SharedPreferences preferences = getActivity().getSharedPreferences("DATA", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("TEST", data);
        editor.apply();
    }
}
