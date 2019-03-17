package com.team6.krafty;

//Checked exception that is checked at compile time
public class KraftyException extends Exception {
    public KraftyException(String errorMessage, Throwable err){
        super(errorMessage, err);
    }
}//

/* To use:
    try {
        *do data things*
    } catch (IllegalArgumentException e) {
        if (! isNumeric(data)) {
            throw new KraftyException("Input must be numeric : " + data, e);
        }
    }

*/
