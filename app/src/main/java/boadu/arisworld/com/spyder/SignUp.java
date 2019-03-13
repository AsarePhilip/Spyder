package boadu.arisworld.com.spyder;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import boadu.arisworld.com.spyder.data.RegData;

public class SignUp extends AppCompatActivity {

    Button btnSignUp;
    EditText edtFname;
    EditText edtOname;
    EditText edtEmail;

    String fName = null;
    String oName = null;
    String phone = null;
    String email = null;

    //Firebase
    private FirebaseDatabase mDatabase;
    private DatabaseReference mDatabaseReference;
    private ChildEventListener mChildEventListener;
    private FirebaseAuth mFirebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        Intent myIntent =getIntent();
        phone = myIntent.getStringExtra("phone");
        Toast.makeText(getBaseContext(), phone, Toast.LENGTH_SHORT).show();

        btnSignUp = findViewById(R.id.btnSignUp);
        edtFname = findViewById(R.id.edtfName);
        edtOname = findViewById(R.id.edtOname);
        edtEmail  = findViewById(R.id.edtEmail);


        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Check firstname
                if(!isEmpty(getText(edtFname))){
                    fName = getText(edtFname);
                }else{
                    Toast.makeText(getBaseContext(), "Enter Firstname", Toast.LENGTH_SHORT).show();
                    edtFname.requestFocus();
                }


                //Check othername
                if(!isEmpty(getText(edtOname))){
                    oName = getText(edtOname);
                }else{
                    Toast.makeText(getBaseContext(), "Enter Othername", Toast.LENGTH_SHORT).show();
                    edtOname.requestFocus();}

                //Validate email
                if (!isEmpty(getText(edtEmail))){
                    if(isEmailValide(getText(edtEmail))) {
                        email = getText(edtEmail);
                    }else{Toast.makeText(getBaseContext(), "Invalid Email", Toast.LENGTH_SHORT).show();
                        edtEmail.requestFocus();}
                }else{
                    Toast.makeText(getBaseContext(),"Enter email", Toast.LENGTH_SHORT).show();
                    edtEmail.requestFocus();
                }


                Intent mainActivityIntent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(mainActivityIntent);
            }
        });
    }

    private  String getText(EditText editText){
        return editText.getText().toString();
    }

    private boolean isEmpty(String string){
        return TextUtils.isEmpty(string);
    }

    private boolean isEmailValide(String email){
        String regex = "^.+@.+(\\.[^\\.]+)+$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher  = pattern.matcher(email);
        if(!matcher.matches()){
            return  false;
        }else return true;
    }
}
