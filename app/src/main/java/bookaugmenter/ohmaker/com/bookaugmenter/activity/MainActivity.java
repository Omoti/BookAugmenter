package bookaugmenter.ohmaker.com.bookaugmenter.activity;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import java.io.UnsupportedEncodingException;

import bookaugmenter.ohmaker.com.bookaugmenter.R;
import bookaugmenter.ohmaker.com.bookaugmenter.app.BookAugmenterApp;
import bookaugmenter.ohmaker.com.bookaugmenter.fragment.BaseFragment;
import bookaugmenter.ohmaker.com.bookaugmenter.fragment.CreateTagFragment;
import bookaugmenter.ohmaker.com.bookaugmenter.fragment.FragmentTag;
import bookaugmenter.ohmaker.com.bookaugmenter.fragment.MainFragment;
import bookaugmenter.ohmaker.com.bookaugmenter.fragment.ReadBookFragment;
import bookaugmenter.ohmaker.com.bookaugmenter.irkit.IRKitDevice;
import bookaugmenter.ohmaker.com.bookaugmenter.irkit.IRKitDeviceController;
import bookaugmenter.ohmaker.com.bookaugmenter.irkit.IRKitDeviceManager;

public class MainActivity extends AppCompatActivity implements IRKitDeviceManager.IRKitDeviceListener {
    private static final String TAG = MainActivity.class.getSimpleName();

    /**
     * NFC
     */
    private NfcAdapter mNfcAdapter;
    private IntentFilter[] mIntentFilters;

    /**
     * IRKit
     */
    private IRKitDeviceManager mDeviceManager;
    private IRKitDeviceController mDeviceController;

    /**
     * Fragment
     */
    private FragmentManager mFragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //NFCアダプター取得
        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);

        //onResumeでIntentFilter適用時に仕様
        Intent intent = new Intent(getApplicationContext(), getClass());
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

        IntentFilter intentFilter = new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED);
        mIntentFilters = new IntentFilter[]{intentFilter};

        mDeviceManager = new IRKitDeviceManager(this);
        mDeviceManager.registerDeviceListener(this);

        //Fragment
        mFragmentManager = getFragmentManager();
        replaceFragment(FragmentTag.MAIN, null);

        mDeviceManager.startDiscovery();
    }


    @Override
    protected void onResume() {
        super.onResume();

        PendingIntent pendingIntent = createPendingIntent();
        mNfcAdapter.enableForegroundDispatch(this,pendingIntent,mIntentFilters, null);
    }

    @Override
    protected void onPause() {
        super.onPause();

        mNfcAdapter.disableForegroundDispatch(this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        if (tag == null) {
            Log.d(TAG, "NFC tag NOT found");
            return;
        }

        Ndef ndef = Ndef.get(tag);

        if (ndef == null) {
            Log.d(TAG, "no NDEF");
            return;
        }

        String message = null;
        String tag_name = mFragmentManager.getBackStackEntryAt(mFragmentManager.getBackStackEntryCount() - 1).getName();
        if (FragmentTag.READ_BOOK.getName().equals(tag_name)){
            message = getMessageFromNdef(intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES));
        }

        //Fragmentに通知
        BaseFragment fragment = (BaseFragment)mFragmentManager.findFragmentByTag(tag_name);
        if (fragment != null){
            fragment.onNfcDiscovered(ndef, message);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private PendingIntent createPendingIntent() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        return PendingIntent.getActivity(this, 0, intent, 0);
    }

    /**
     * Fragment切り替え
     */
    public void replaceFragment(FragmentTag tag, Bundle args){
        FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();

        Fragment fragment = null;

        switch (tag){
            case MAIN:
                fragment = new MainFragment();
                break;
            case READ_BOOK:
                fragment = new ReadBookFragment();
                break;
            case CREATE_TAG:
                fragment = new CreateTagFragment();
                break;
            default:
                //Nothing to do.
        }

        if (fragment == null){
            return;
        }

        fragment.setArguments(args);

        fragmentTransaction.replace(R.id.container, fragment, tag.getName());
        fragmentTransaction.addToBackStack(tag.getName());
        fragmentTransaction.commit();
    }

    /**
     * NDEFから文字列取得
     * @param raw
     * @return
     */
    private String getMessageFromNdef(Parcelable[] raw){
        NdefMessage msgs[];
        String ret = null;

        if (raw != null) {
            msgs = new NdefMessage[raw.length];
            for (int i = 0; i < raw.length; i++) {
                msgs[i] = (NdefMessage) raw[i];

                NdefRecord records[] = msgs[0].getRecords();
                byte[] bytes = records[0].getPayload();
                byte[] payload = new byte[bytes.length - 3];
                System.arraycopy(bytes, 3, payload, 0, payload.length);

                try {
                    ret = new String(payload, "UTF-8");

                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        }

        return ret;
    }

    /**
     * IRKit発見
     * @param device
     */
    @Override
    public void onFoundDevice(IRKitDevice device) {
        Snackbar.make(findViewById(R.id.layout_main), device.getName(), Snackbar.LENGTH_LONG).setAction("Action", null).show();

        mDeviceManager.stopDiscovery();

        mDeviceController = new IRKitDeviceController(device,
                ((BookAugmenterApp)getApplication()).getRequestQueue());
    }

    synchronized public IRKitDeviceController getIRKitDeviceController(){
        return mDeviceController;
    }
}
