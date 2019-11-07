package util;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.KeyboardShortcutGroup;
import android.view.Menu;
import android.view.View;
import android.webkit.WebView;
import android.widget.TextView;
import android.widget.Toast;

import com.dreamforone.bshangoeul.R;

import java.util.List;

public class CustomDialog extends Dialog implements View.OnClickListener
{
    private static final int LAYOUT = R.layout.popup_dialog;
    WebView popupWebView;
    TextView closeBtn;
    String url="";
    View.OnClickListener positived;

    Context mContext;
    public CustomDialog(Context context,String url,View.OnClickListener positived) {
        super(context);
        mContext=context;
        this.url=url;
        this.positived=positived;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(LAYOUT);
        popupWebView=(WebView)findViewById(R.id.popupWebView);

        Toast.makeText(mContext, url, Toast.LENGTH_SHORT).show();
        popupWebView.loadUrl(url);
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void onProvideKeyboardShortcuts(List<KeyboardShortcutGroup> data, Menu menu, int deviceId) {

    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
}
