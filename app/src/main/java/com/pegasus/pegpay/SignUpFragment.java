package com.pegasus.pegpay;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class SignUpFragment extends Fragment {
    View view;
    String dateString;
    static AQuery aq;
    Spinner accountTypeSpinner;
    static SingupLoginActivity ac;
    EditText PhoneNumber, FirstName, LastName, Email, DateOfBirth, MaritalStatus, Gender, Nationality, IdType, IdImage, ProfileImage, NextOfKin, NextOfKinPhone, BankCode, AccessId, Signature;

    Button signUp;
    static int photoSelectionField = -1;

    String error = null;

    ProgressDialog dialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.signup_fragment, container, false);
        aq = new AQuery(view);
        ac = (SingupLoginActivity) getActivity();
        FirstName = (EditText) view.findViewById(R.id.txt_first_name);
        signUp = (Button) view.findViewById(R.id.btn_signup);
        // aq.id(R.id.txt_select_avatar).clicked(new View.OnClickListener() {
        //    @Override
        //   public void onClick(View v) {
        //       dispatchPhotoSelectionEvent(0);
        //  }
        // });
        // aq.id(R.id.img_avatar).clicked(new View.OnClickListener() {
        //  @Override
        // public void onClick(View v) {
        //     dispatchPhotoSelectionEvent(0);
        //  }
        //  });

        aq.id(R.id.txt_select_id).clicked(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchPhotoSelectionEvent(1);
            }
        });
        aq.id(R.id.img_id).clicked(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchPhotoSelectionEvent(1);
            }
        });
        aq.id(R.id.txt_owner_select_id).clicked(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchPhotoSelectionEvent(2);
            }
        });
        aq.id(R.id.img_owner_id).clicked(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchPhotoSelectionEvent(2);
            }
        });

        aq.id(R.id.txt_select_business_licence).clicked(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchPhotoSelectionEvent(3);
            }
        });
        aq.id(R.id.img_business_licence).clicked(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchPhotoSelectionEvent(3);
            }
        });

        accountTypeSpinner = (Spinner) view.findViewById(R.id.spn_type);
        accountTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 2) {
                    aq.id(R.id.ln_non_individual_sme).visible();
                    aq.id(R.id.ln_individual).gone();
                } else {
                    aq.id(R.id.ln_non_individual_sme).gone();
                    aq.id(R.id.ln_individual).visible();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        aq.id(R.id.txt_dob).getEditText().setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    Calendar c = Calendar.getInstance();
                    int year = c.get(Calendar.YEAR);
                    int month = c.get(Calendar.MONTH);
                    int day = c.get(Calendar.DAY_OF_MONTH);

                    new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                            dateString = Utils.formatDate(year, monthOfYear, dayOfMonth, "d MMM, yyyy");
                            aq.id(R.id.txt_dob).text(dateString);
                        }
                    }, year, month, day).show();
                    return true;
                }
                return false;
            }
        });

       /* aq.id(R.id.btn_submit).clicked(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                processRegistration();
            }
        });

      */
        signUp.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                sendSoapData();
            }
        });

        populateFields();

        accountTypeSpinner.setSelection(2);

        return view;
    }

    private void populateFields() {
        aq.id(R.id.spn_type).setSelection(1);
        if (AccountManager.isLoggedIn(getActivity())) {
            String firstName = AccountManager.getFirstName(getActivity());
            String lastName = AccountManager.getLastName(getActivity());
            String email = AccountManager.getEmail(getActivity());
            String sex = AccountManager.getSex(getActivity());
            String avatarPath = AccountManager.getAvatarPath(getActivity());
            String idPath = AccountManager.getIDPath(getActivity());
            String dob = AccountManager.getDOB(getActivity());
            String idType = AccountManager.getIDType(getActivity());
            String idNumber = AccountManager.getIDNumber(getActivity());
            String phone = AccountManager.getPhone(getActivity());
            String licencePath = AccountManager.getLicencePath(getActivity());

            aq.id(R.id.txt_first_name).text(firstName);
            aq.id(R.id.txt_last_name).text(lastName);

            aq.id(R.id.txt_email).text(email).enabled(false);

            aq.id(R.id.txt_id_number).text(idNumber);
            aq.id(R.id.txt_phone_number).text(phone);
            if (!TextUtils.isEmpty(dob)) {
                aq.id(R.id.txt_dob).text(dob);
                dateString = dob;
            }

            if (!TextUtils.isEmpty(sex)) {
                aq.id(R.id.spn_sex).setSelection(Utils.getSpinnerItemIndex(aq.id(R.id.spn_sex).getSpinner(), sex));
            }
            if (!TextUtils.isEmpty(idType)) {
                aq.id(R.id.spn_id_type).setSelection(Utils.getSpinnerItemIndex(aq.id(R.id.spn_id_type).getSpinner(), idType));
            }
            //  if(!TextUtils.isEmpty(avatarPath)){
            //      File avatar = new File(avatarPath);
            //      if(avatar.exists()){
            //         aq.id(R.id.img_avatar).image(avatar, 200);
            //    }
            // }
            if (!TextUtils.isEmpty(idPath)) {
                File id = new File(idPath);
                if (id.exists()) {
                    aq.id(R.id.img_id).image(id, 200);
                }
            }
            if (!TextUtils.isEmpty(licencePath)) {
                File licence = new File(licencePath);
                if (licence.exists()) {
                    aq.id(R.id.img_business_licence).image(licence, 200);
                }
            }
        }
    }

    private void dispatchPhotoSelectionEvent(int mode) {
        photoSelectionField = mode;
        new PhotoSelectionDialog().show(getActivity().getFragmentManager(), "select");
    }

    public static void setImage(File image) {
        Utils.log("setImage() -> " + photoSelectionField);
        switch (photoSelectionField) {
            // case 0:
            // aq.id(R.id.img_avatar).image(image, 200);
            //Delete previous image
            //  try{
            //       String avatar = AccountManager.getAvatarPath(ac);
            //     if(avatar != null){
            //         File f = new File(avatar);
            //         f.delete();
            //    }
            // }catch (Exception ex){
            //    Utils.log("Error deleting previous avatar");
            // }
            // AccountManager.saveAvatarPath(ac, image.getAbsolutePath());
            // break;
            case 0:
                aq.id(R.id.img_id).image(image, 200);
                //Delete previous image
                try {
                    String id = AccountManager.getIDPath(ac);
                    if (id != null) {
                        File f = new File(id);
                        f.delete();
                    }
                } catch (Exception ex) {
                    Utils.log("Error deleting previous id");
                }
                AccountManager.saveIDPath(ac, image.getAbsolutePath());
                break;
            case 1:
                aq.id(R.id.img_owner_id).image(image, 200);
                //Delete previous image
                try {
                    String id = AccountManager.getIDPath(ac);
                    if (id != null) {
                        File f = new File(id);
                        f.delete();
                    }
                } catch (Exception ex) {
                    Utils.log("Error deleting previous id");
                }
                AccountManager.saveIDPath(ac, image.getAbsolutePath());
                break;
            case 2:
                aq.id(R.id.img_business_licence).image(image, 200);
                //Delete previous image
                try {
                    String id = AccountManager.getLicencePath(ac);
                    if (id != null) {
                        File f = new File(id);
                        f.delete();
                    }
                } catch (Exception ex) {
                    Utils.log("Error deleting previous id");
                }
                AccountManager.saveLicencePath(ac, image.getAbsolutePath());
                break;
        }
    }

    public static File createImageFile() {
        String prefix = photoSelectionField == 0 ? "avatar_" : "id_";
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = prefix + timeStamp + "_";
        File storageDir = ac.getExternalFilesDir(null);
        try {
            File image = File.createTempFile(imageFileName, ".jpg", storageDir);
            Utils.log(image.getAbsolutePath());
            return image;
        } catch (IOException ex) {
            Toast.makeText(ac, "Failed to save image", Toast.LENGTH_SHORT).show();
        }

        return null;
    }

    private void processRegistration() {
        int registrationType = aq.id(R.id.spn_type).getSelectedItemPosition();

        if (registrationType == 0) {
            showErrorToast("Please select account type");
            return;
        }

        HashMap<String, Object> params = new HashMap<>();

        if (registrationType == 1) {
            String firstName = aq.id(R.id.txt_first_name).getText().toString().trim();
            String lastName = aq.id(R.id.txt_last_name).getText().toString().trim();
            String email = aq.id(R.id.txt_email).getText().toString().trim();
            String phone = aq.id(R.id.txt_phone_number).getText().toString().trim();
            String sex, idType;
            int sexSelection = aq.id(R.id.spn_sex).getSelectedItemPosition();
            int idSelection = aq.id(R.id.spn_id_type).getSelectedItemPosition();
            String idNumber = aq.id(R.id.txt_id_number).getText().toString().trim();

            if (firstName.isEmpty()) {
                showErrorToast("Please enter your first name");
                return;
            }
            if (lastName.isEmpty()) {
                showErrorToast("Please enter your last name");
                return;
            }
            if (phone.length() < 10) {
                showErrorToast("Please enter a valid phone number");
                return;
            }
            if (!email.isEmpty() && !Utils.isValidEmail(email)) {
                showErrorToast("Please enter a valid email address");
                return;
            }
            if (sexSelection == 0) {
                showErrorToast("Please select your gender");
                return;
            } else {
                sex = aq.id(R.id.spn_sex).getSelectedItem().toString();
            }
            if (dateString == null) {
                showErrorToast("Please select your date of birth");
                return;
            }
            if (idSelection == 0) {
                showErrorToast("Please select an ID type");
                return;
            } else {
                idType = aq.id(R.id.spn_id_type).getSelectedItem().toString();
            }
            if (idNumber.isEmpty()) {
                showErrorToast("Please enter your ID Number");
                return;
            }

            AccountManager.saveFirstName(getActivity(), firstName);
            AccountManager.saveLastName(getActivity(), lastName);
            AccountManager.saveEmail(getActivity(), email);
            AccountManager.saveSex(getActivity(), sex);
            AccountManager.saveIDType(getActivity(), idType);
            AccountManager.saveDOB(getActivity(), dateString);
            AccountManager.saveIDNumber(getActivity(), idNumber);
            AccountManager.savePhone(getActivity(), phone);

            params.put("type", registrationType);
            params.put("first_name", firstName);
            params.put("last_name", lastName);
            params.put("email", email);
            params.put("id_number", idNumber);
            params.put("dob", dateString);
            params.put("sex", sexSelection);
        } else {
            //   String businessName = aq.id(R.id.txt_company_name).getText().toString().trim();
            //  String businessPhone = aq.id(R.id.txt_company_phone).getText().toString().trim();
            //  String businessEmail = aq.id(R.id.txt_company_email).getText().toString().trim();
            String businessLocation = aq.id(R.id.txt_location).getText().toString().trim();

            /*if(businessName.isEmpty()){
                showErrorToast("Please enter business name");
                return;
            }
            if(businessEmail.isEmpty()){
                showErrorToast("Please enter business email address");
                return;
            }

            AccountManager.saveFirstName(getActivity(), businessName);
            AccountManager.saveLastName(getActivity(), "");
            AccountManager.saveEmail(getActivity(), businessEmail);

            params.put("type", registrationType);
            params.put("business_name", businessName);
            params.put("email", businessEmail);*/
        }

        AccountManager.saveAccountType(getActivity(), registrationType);

        postRegistrationData(params);
    }

    private void sendSoapData() {

        dialog = new ProgressDialog(getActivity());
        dialog.setIndeterminate(true);
        dialog.setMessage("Please wait....");
        dialog.show();
        //https://test.pegasus.co.ug:8019/TestAppApi/Service.asmx?WSDL
        //  String url = "http://vpointconsultancy.com/pegpay/api.php?action=register";
        String NAMESPACE = "https://test.pegasus.co.ug:8019/TestAppApi/";
        String URL = "https://test.pegasus.co.ug:8019/TestAppApi/Service.asmx?WSDL";
        String SOAP_ACTION = "https://test.pegasus.co.ug:8019/TestAppApi/RegisterCustomer";
        String METHOD_NAME = "RegisterCustomer";

        String url = "https://test.pegasus.co.ug:8019/TestAppApi/RegisterCustomer";
        SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
        request.addProperty("phone_number", PhoneNumber.getText());
        request.addProperty("first_name", FirstName.getText());
        request.addProperty("last_name", LastName.getText());
        request.addProperty("email", Email.getText());
        request.addProperty("date_of_birth", dateString);
        request.addProperty("marital_status", MaritalStatus.getText());
        request.addProperty("gender", "");
        //the spinner at the momeent is only getting the position of the item in the drop down

        request.addProperty("nationality", Nationality.getText());
        request.addProperty("id_type", IdType.getText());
        request.addProperty("id_image", IdImage.getText());
        request.addProperty("profile_image", ProfileImage.getText());
        request.addProperty("next_of_kin", NextOfKin.getText());
        request.addProperty("next_of_kin_phone", NextOfKinPhone.getText());
        request.addProperty("bank_code", BankCode.getText());
        request.addProperty("access_id", AccessId.getText());
        request.addProperty("signature", Signature.getText());
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.setOutputSoapObject(request);
        HttpTransportSE androidHttpTransport = new HttpTransportSE(URL);
        try {

            androidHttpTransport.call(SOAP_ACTION, envelope);
            SoapObject resultsRequestSOAP = (SoapObject) envelope.bodyIn;
            Toast.makeText(getContext(),"POSTING ",Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            System.out.println("Error" + e);
            Toast.makeText(getContext(),"ERROR + "+e,Toast.LENGTH_LONG).show();
        }
    }

    private void postRegistrationData(HashMap<String, Object> params) {
        dialog = new ProgressDialog(getActivity());
        dialog.setIndeterminate(true);
        dialog.setMessage("Please wait....");
        dialog.show();
        //https://test.pegasus.co.ug:8019/TestAppApi/Service.asmx?WSDL
        //  String url = "http://vpointconsultancy.com/pegpay/api.php?action=register";
        String NAMESPACE = "https://test.pegasus.co.ug:8019/TestAppApi/";
        String URL = "https://test.pegasus.co.ug:8019/TestAppApi/Service.asmx?WSDL";
        String SOAP_ACTION = "https://test.pegasus.co.ug:8019/TestAppApi/RegisterCustomer";
        String METHOD_NAME = "RegisterCustomer";

        String url = "https://test.pegasus.co.ug:8019/TestAppApi/RegisterCustomer";
        SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
        request.addProperty("phone_number", PhoneNumber.getText());
        request.addProperty("first_name", FirstName.getText());
        request.addProperty("last_name", LastName.getText());
        request.addProperty("email", Email.getText());
        request.addProperty("date_of_birth", dateString);
        request.addProperty("marital_status", MaritalStatus.getText());
        request.addProperty("gender", "");
        //the spinner at the momeent is only getting the position of the item in the drop down

        request.addProperty("nationality", Nationality.getText());
        request.addProperty("id_type", IdType.getText());
        request.addProperty("id_image", IdImage.getText());
        request.addProperty("profile_image", ProfileImage.getText());
        request.addProperty("next_of_kin", NextOfKin.getText());
        request.addProperty("next_of_kin_phone", NextOfKinPhone.getText());
        request.addProperty("bank_code", BankCode.getText());
        request.addProperty("access_id", AccessId.getText());
        request.addProperty("signature", Signature.getText());


        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.setOutputSoapObject(request);
        HttpTransportSE androidHttpTransport = new HttpTransportSE(URL);
        try {
            androidHttpTransport.call(SOAP_ACTION, envelope);

            //SoapPrimitive  resultsRequestSOAP = (SoapPrimitive) envelope.getResponse();
            // SoapPrimitive  resultsRequestSOAP = (SoapPrimitive) envelope.getResponse();
            SoapObject resultsRequestSOAP = (SoapObject) envelope.bodyIn;


            //  lblResult.setText(resultsRequestSOAP.toString());
            System.out.println("Response::" + resultsRequestSOAP.toString());


        } catch (Exception e) {
            System.out.println("Error" + e);
        }
        aq.ajax(url, params, String.class, new AjaxCallback<String>() {
            @Override
            public void callback(String url, String response, AjaxStatus status) {
                dialog.dismiss();
                Toast.makeText(getActivity(), "Registration successful. Please login to access PegPay account", Toast.LENGTH_SHORT).show();
                if (AccountManager.isLoggedIn(getActivity())) {
                    getActivity().finish();
                } else {
                    ac.switchTab(1);
                }
            }
        });
    }

    private void showErrorToast(String error) {
        Toast.makeText(getActivity(), error, Toast.LENGTH_SHORT).show();
    }
}
