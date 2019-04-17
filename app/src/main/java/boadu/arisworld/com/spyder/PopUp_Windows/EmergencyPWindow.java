package boadu.arisworld.com.spyder.PopUp_Windows;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.util.Log;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import boadu.arisworld.com.spyder.R;
import boadu.arisworld.com.spyder.data.Ambulance;
import boadu.arisworld.com.spyder.data.EmergencyService;
import boadu.arisworld.com.spyder.data.FireService;
import boadu.arisworld.com.spyder.data.Police;

public class EmergencyPWindow {

    final int width = LinearLayout.LayoutParams.MATCH_PARENT;
    final int height  = LinearLayout.LayoutParams.MATCH_PARENT;
    final boolean fucusable = true;
    PopupWindow popUpWindow = null ;
    Context mContext = null;
    ViewGroup popupView = null;

    // Layout Widgets
    Button btnClose = null;
    Button btnCopy = null;
    TextView txtPhone1 = null;
    TextView txtPhone2 = null;
    TextView txtEmail = null;
    TextView txtTown = null;
    TextView txtLocation = null;
    TextView txtDistance = null;
    TextView txtTitle = null;



    public EmergencyPWindow(Context mContext){
        this.mContext = mContext;
    }

    public void getWidgets (){

        try {
            popupView = (ViewGroup) LayoutInflater.from(mContext).inflate(R.layout.emergency_service, null, true);
            popUpWindow = new PopupWindow(popupView, width, height, fucusable);

        }catch (InflateException err){
            Log.e("INFLATEOR ERROR ",err.getMessage());
            Toast.makeText(mContext,err.getMessage(), Toast.LENGTH_SHORT).show();
        }
        btnCopy =  popupView.findViewById(R.id.btnCopy);
        btnClose = popupView.findViewById(R.id.btnClose);
        txtPhone1 = popupView.findViewById(R.id.txtPhone1);
        txtPhone2 = popupView.findViewById(R.id.txtPhone2);
        txtEmail = popupView.findViewById(R.id.txtEmail);
        txtTown = popupView.findViewById(R.id.txtTown);
        txtLocation = popupView.findViewById(R.id.txtGpsLocation);
        txtTitle = popupView.findViewById(R.id.txtTitle);
    }




    public void setValues(final EmergencyService emergencyService){
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popUpWindow.dismiss();
            }
        });
        txtPhone1.setText(emergencyService.getPhone1());
        txtPhone2.setText(emergencyService.getPhone2());
        txtEmail.setText(emergencyService.getEmail());

        txtTown.setText(emergencyService.getTown());
        txtLocation.setText(
                (String.valueOf(emergencyService.getLatitude()) + ", " +
                        String.valueOf(emergencyService.getLongitude())
                ));
        if(emergencyService instanceof Police){
            txtTitle.setText(emergencyService.getName());
        }else if(emergencyService instanceof Ambulance){
            txtTitle.setText(emergencyService.getName());
        }else if(emergencyService instanceof FireService){
            txtTitle.setText(emergencyService.getName());
        }
    }

    public PopupWindow getPopUpWindow() {
        return popUpWindow;
    }


    public TextView getTextView(int id){
        return (TextView)popUpWindow.getContentView().findViewById(id);
    }

    public Button getButton(int id){
        return (Button)popUpWindow.getContentView().findViewById(id);
    }

}

