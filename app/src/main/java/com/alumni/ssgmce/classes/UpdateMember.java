package com.alumni.ssgmce.classes;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import com.alumni.ssgmce.R;
import com.alumni.ssgmce.activity.Activity_PersonalDetails;
import com.alumni.ssgmce.starts.Activity_Home;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.MarshalBase64;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import dmax.dialog.SpotsDialog;

/**
 * Created by venki on 20/9/16.
 */
public class UpdateMember extends AsyncTask<String, Void, String> {

    private ImageView myimage;
    String res = "";

    String signUpURL = "http://ssgmcealumni.nxglabs.in/alumniWebservice.asmx";

    final String NAMESPACE = "http://ssgmcealumni.nxglabs.in/alumniWebservice.asmx";
    final String METHOD_NAME_memberRegistration = "updateMember";

    final String SOAP_ACTION_memberRegistration = Constant.UPDATE_MEMBER;

    Activity_PersonalDetails context;
    AlertDialog progressDialog;
    BitmapDrawable bitmapDrawable;
    byte[] bArray;
    String filename;
    String filePath = "";
    FileOutputStream fileOutputStream;


    public UpdateMember(Activity_PersonalDetails context, ImageView myimage, File photo1){
        this.myimage = myimage;
        this.photo1 = photo1;
        this.context = context;
        bitmapDrawable = (BitmapDrawable) myimage.getDrawable();
    }

    File photo1;

    @Override
    protected void onPreExecute() {
        // TODO Auto-generated method stub
        super.onPreExecute();

        progressDialog = new SpotsDialog(context, R.style.Custom);
        progressDialog.show();
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    @Override
    protected String doInBackground(String... params) {
        // TODO Auto-generated method stub
        final SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME_memberRegistration);
        Bitmap photo = null;
        if (bitmapDrawable != null) {
            photo = bitmapDrawable.getBitmap();
        }
        FileInputStream fileInputStream = null;
        if (photo != null) {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            photo.compress(Bitmap.CompressFormat.JPEG, 100, bos);
            bArray = bos.toByteArray();

            Date date = new Date();
            SimpleDateFormat format = new SimpleDateFormat("ddMMMyyyy_HH_mm_ss");
            String dateTostring = format.format(date);
            filename = "" + dateTostring + ".png";

            filePath = Environment.getExternalStorageDirectory() + "/" + filename;
            File photo1 = new File(filePath);

            Log.e("file path", filePath);
            if (photo1.exists()) {
                photo1.delete();
            }
            try {
                fileOutputStream = new FileOutputStream(filePath);
                fileOutputStream.write(bArray);
                fileOutputStream.close();
            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            File file = new File(filePath);
            byte[] bFile = new byte[(int) file.length()];
            try {
                fileInputStream = new FileInputStream(file);
                fileInputStream.read(bFile);
                fileInputStream.close();
                System.out.println("Done");
            } catch (Exception e) {
                e.printStackTrace();
            }
            // SerializationUtils.serialize(bFile);
            Log.e("bfile", "" + bFile);
            request.addProperty("MemberType", params[0]);
            request.addProperty("FirstName", params[1]);
            request.addProperty("MiddleName", params[2]);
            request.addProperty("LastName", params[3]);
            request.addProperty("MobileNo", params[4]);
            request.addProperty("Designation", params[5]);
            request.addProperty("Department", params[6]);
            request.addProperty("JoiningYear", params[7]);
            request.addProperty("CourseId", params[8]);
            request.addProperty("BranchId", params[9]);
            request.addProperty("ProfilePhoto", filename);
            request.addProperty("CurrentLocation", params[10]);
            request.addProperty("EmailIdAsUserName", "true");
            request.addProperty("EmailId", params[11]);
            request.addProperty("Password", params[12]);
            request.addProperty("SiteStatus", params[13]);
            request.addProperty("DateOfBirth", params[14]);
            request.addProperty("State", params[15]);
            request.addProperty("Country", params[16]);
            request.addProperty("Gender", params[17]);
            request.addProperty("InstituteId", params[18]);
            request.addProperty("MemberId", params[19]);
            request.addProperty("UserId", "");
            request.addProperty("registerUpdate", "No");

            request.addProperty("f", bFile);


            final SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
                    SoapEnvelope.VER11);
            new MarshalBase64().register(envelope);
            envelope.setOutputSoapObject(request);
            envelope.dotNet = true;
            HttpTransportSE androidHttpTransport = new HttpTransportSE(signUpURL);
            try {
                androidHttpTransport.call(SOAP_ACTION_memberRegistration, envelope);
                SoapPrimitive response = (SoapPrimitive) envelope.getResponse();
                res = response.toString();

            } catch (Exception e) {
                Log.e("exception", e.getMessage(), e);
            }
        } else {
            byte[] bFile = new byte[0];
            request.addProperty("MemberType", params[0]);
            request.addProperty("FirstName", params[1]);
            request.addProperty("MiddleName", params[2]);
            request.addProperty("LastName", params[3]);
            request.addProperty("MobileNo", params[4]);
            request.addProperty("Designation", params[5]);
            request.addProperty("Department", params[6]);
            request.addProperty("JoiningYear", params[7]);
            request.addProperty("CourseId", params[8]);
            request.addProperty("BranchId", params[9]);
            request.addProperty("ProfilePhoto", filename);
            request.addProperty("CurrentLocation", params[10]);
            request.addProperty("EmailIdAsUserName", "true");
            request.addProperty("EmailId", params[11]);
            request.addProperty("Password", params[12]);
            request.addProperty("SiteStatus", params[13]);
            request.addProperty("DateOfBirth", params[14]);
            request.addProperty("State", params[15]);
            request.addProperty("Country", params[16]);
            request.addProperty("Gender", params[17]);
            request.addProperty("InstituteId", params[18]);
            request.addProperty("MemberId", params[19]);
            request.addProperty("UserId", "");
            request.addProperty("registerUpdate", "No");

            request.addProperty("f", bFile);

            //  mLastName, mGender, mCountry, mState, mCity, mCollege
            final SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
                    SoapEnvelope.VER11);
            new MarshalBase64().register(envelope);
            envelope.setOutputSoapObject(request);
            envelope.dotNet = true;
            HttpTransportSE androidHttpTransport = new HttpTransportSE(signUpURL);
            try {
                androidHttpTransport.call(SOAP_ACTION_memberRegistration, envelope);
                SoapPrimitive response = (SoapPrimitive) envelope.getResponse();
                res = response.toString();
            } catch (Exception e) {
                Log.e("exception", e.getMessage(), e);
            }

        }
        return res;
    }

    @Override
    protected void onPostExecute(String result) {
        // TODO Auto-generated method stub
        super.onPostExecute(result);
        Log.e("Result", result);
        progressDialog.dismiss();
        String[] res = result.split(",");
        if (res[0].equalsIgnoreCase("1")) {
            Toast.makeText(context, "Successful!!", Toast.LENGTH_SHORT).show();
            context.saveData();
        }
        else if(result.equalsIgnoreCase("0"))
        {
            Toast.makeText(context,"Unable to update, please try later", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(context,"Unable to update, please try later", Toast.LENGTH_SHORT).show();

        }
    }
}