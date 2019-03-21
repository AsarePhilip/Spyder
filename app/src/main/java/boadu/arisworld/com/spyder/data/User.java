/*
* Created By Boadu Philip Asare
* This class represents a user registration data object
* */


package boadu.arisworld.com.spyder.data;

public class User {

    public User() {
    }

    ;

    public User(String email, String phone, String fName, String oName) {
        this.mEmail = email;
        this.mPhoneNumber = phone;
        this.mFirstname = fName;
        this.mOthername = oName;
    }


    /*
     * Basic user perssonal information.
     * Needed to initiate account creation process
     * */
    private String mEmail;          //Email address
    private String mFirstname;      //Firstname
    private String mOthername;      //Othername
    private String mPhoneNumber;    //Phone number

    /*
     * Basic data on user vehicle.
     * Can be updated later on but must be updated before user account will approved.
     * */
    private String mVehodel;        //Vehicle model
    private String mVehRegYear;     //year in which vehicle was registered
    private String mVehType;        //Vehicle type (Commercial or Private)


    public String getmVehodel() {
        return mVehodel;
    }

    public String getmVehRegYear() {
        return mVehRegYear;
    }

    public String getmVeType() {
        return mVehType;
    }

    public String getmOthername() {
        return mOthername;
    }

    public String getmFirstname() {
        return mFirstname;
    }

    public String getmPhoneNumber() {
        return mPhoneNumber;
    }

    public User getUserData(User user) {
        return user;
    }

    public String getmEmail() {
        return mEmail;
    }

    public void setmEmail(String mEmail) {
        this.mEmail = mEmail;
    }

    public void setmPhoneNumber(String mPhoneNumber) {
        this.mPhoneNumber = mPhoneNumber;
    }

    public void setmFirstname(String mFirstname) {
        this.mFirstname = mFirstname;
    }

    public void setmOthername(String mOthername) {
        this.mOthername = mOthername;
    }

    public void setmVehType(String mVehicleType) {
        this.mVehType = mVehicleType;
    }

    public void setmVehRegYear(String mVehRegYear) {
        this.mVehRegYear = mVehRegYear;
    }

    public void setmVehodel(String mVehodel) {
        this.mVehodel = mVehodel;
    }

}