package com.team6.krafty;

import android.widget.EditText;

public class Validator{

    public Validator(){

    }

    public static void validateBasicEditText(EditText et, String field) throws KraftyRuntimeException{
        if(et.getText().toString().equals("")){
            throw new KraftyRuntimeException("No text in field " + field, null);
        }
    }

    public static void validateIntEt(EditText et, String field) throws KraftyRuntimeException{
        if(et.getText().toString().equals("")){
            throw new KraftyRuntimeException("No input in field " + field, null);
        }
        try{
            String n = et.getText().toString();
            int nint = Integer.parseInt(n);
        }
        catch (Exception e){
            throw new KraftyRuntimeException("Not a valid number in field " + field, null);
        }
    }

    public static void validateDoubleEt(EditText et, String field){
        if(et.getText().toString().equals("")){
            throw new KraftyRuntimeException("No input in field " + field, null);
        }
        try{
            String n = et.getText().toString();
            double ndouble = Double.parseDouble(n);
        }
        catch (Exception e){
            throw new KraftyRuntimeException("Not a valid value in field " + field, null);
        }
    }
}
