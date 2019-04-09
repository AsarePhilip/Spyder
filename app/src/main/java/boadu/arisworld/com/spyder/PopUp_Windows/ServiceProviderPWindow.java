package boadu.arisworld.com.spyder.PopUp_Windows;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import boadu.arisworld.com.spyder.R;
import boadu.arisworld.com.spyder.data.AutoMechanic;
import boadu.arisworld.com.spyder.data.ServiceProvider;
import boadu.arisworld.com.spyder.data.TireService;
import boadu.arisworld.com.spyder.data.TowingService;

public class ServiceProviderPWindow {


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
    TextView txtExpertise = null;
    TextView txtTechnicianName = null;
    TextView txtTown = null;
    TextView txtLocation = null;
    TextView txtDistance = null;
    TextView txtTitle = null;



    ClipboardManager clipboardManager;
    ClipData clipData;
    PackageManager packageManager;


    public ServiceProviderPWindow(Context mContext){
        this.mContext = mContext;
    }

    public void getWidgets (){

       try {
           popupView = (ViewGroup) LayoutInflater.from(mContext).inflate(R.layout.service_provider, null, true);
           popUpWindow = new PopupWindow(popupView, width, height, fucusable);
           packageManager = mContext.getPackageManager();
           clipboardManager = (android.content.ClipboardManager) mContext.getSystemService(mContext.CLIPBOARD_SERVICE);

       }catch (InflateException err){
           Log.e("INFLATEOR ERROR ",err.getMessage());
           Toast.makeText(mContext,err.getMessage(), Toast.LENGTH_SHORT).show();
       }
       btnCopy =  popupView.findViewById(R.id.btnCopy);
       btnClose = popupView.findViewById(R.id.btnClose);
       txtPhone1 = popupView.findViewById(R.id.txtPhone1);
       txtPhone2 = popupView.findViewById(R.id.txtPhone2);
       txtEmail = popupView.findViewById(R.id.txtEmail);
       txtExpertise = popupView.findViewById(R.id.txtExpertiseList);
       txtTechnicianName = popupView.findViewById(R.id.txtTechnicianName);
       txtTown = popupView.findViewById(R.id.txtTown);
       txtLocation = popupView.findViewById(R.id.txtGpsLocation);
       txtTitle = popupView.findViewById(R.id.txtTitle);
    }


    public void setValues(final ServiceProvider serviceProvider){
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popUpWindow.dismiss();
            }
        });
        txtPhone1.setText("Phone1: " + serviceProvider.getPhone1());
        txtPhone2.setText("Phone2: " + serviceProvider.getPhone2());
        txtEmail.setText("Email: " + serviceProvider.getEmail());

        txtExpertise.setText(serviceProvider.getExpertise().toString());
        txtTown.setText(serviceProvider.getTown());
        txtLocation.setText(
                (String.valueOf(serviceProvider.getLatitude()) + " " +
                    String.valueOf(serviceProvider.getLongitude())
                ));
        txtTechnicianName.setText(serviceProvider.getTechnicianName());

        if(serviceProvider instanceof AutoMechanic){
            txtTitle.setText(serviceProvider.getShopName());
        }else if(serviceProvider instanceof TireService){
            txtTitle.setText(serviceProvider.getShopName());
        }else if(serviceProvider instanceof TowingService){
            txtTitle.setText(serviceProvider.getShopName());
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
