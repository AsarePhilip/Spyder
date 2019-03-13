package boadu.arisworld.com.spyder.Fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import boadu.arisworld.com.spyder.R;

public class SignUpFragment extends Fragment {

    Button btnSubmit;
    EditText edtOname;
    EditText edtFname;
    EditText edtEmail;

    String fName = null;
    String oName = null;
    String email = null;

    @Override
    public void onCreate(Bundle savedInstantState){
        super.onCreate(savedInstantState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup contianer, Bundle savedInstanceState){
        View signUpView =  inflater.inflate(R.layout.frag_sign_up, contianer, false);
        btnSubmit = signUpView.findViewById(R.id.btnSubmit);
        edtOname = signUpView.findViewById(R.id.edtOname);
        edtFname = signUpView.findViewById(R.id.edtfName);
        edtEmail = signUpView.findViewById(R.id.edtEmail);




        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Check firstname
                if(!isEmpty(getText(edtFname))){
                    fName = getText(edtFname);
                }else{
                    Toast.makeText(getContext(), "Enter Firstname", Toast.LENGTH_SHORT).show();
                    edtFname.requestFocus();
                }


                //Check othername
                if(!isEmpty(getText(edtOname))){
                    oName = getText(edtOname);
                }else{
                    Toast.makeText(getContext(), "Enter Othername", Toast.LENGTH_SHORT).show();
                    edtOname.requestFocus();}

                //Validate email
                if (!isEmpty(getText(edtEmail))){
                    if(isEmailValide(getText(edtEmail))) {
                        email = getText(edtEmail);
                    }else{Toast.makeText(getContext(), "Invalid Email", Toast.LENGTH_SHORT).show();
                        edtEmail.requestFocus();}
                }else{
                    Toast.makeText(getContext(),"Enter email", Toast.LENGTH_SHORT).show();
                    edtEmail.requestFocus();
                }
            }
        });

        return signUpView;
    }



    //Get string from EditText widget
    private  String getText(EditText editText){
        return editText.getText().toString();
    }

    //Check if String is empty
    private boolean isEmpty(String string){
        return TextUtils.isEmpty(string);
    }

    //Check if email is
    private boolean isEmailValide(String email){
        String regex = "^.+@.+(\\.[^\\.]+)+$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher  = pattern.matcher(email);
        if(!matcher.matches()){
            return  false;
        }else return true;
    }

}
