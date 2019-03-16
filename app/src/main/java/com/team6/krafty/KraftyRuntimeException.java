package com.team6.krafty;

//Runtime or Unchecked exception not checked at compiled time
public class KraftyRuntimeException extends RuntimeException {
    public KraftyRuntimeException(String errorMessage, Throwable err){
        super(errorMessage,err);
    }
}//

/* To use:
    try {
        *do data things*
    } catch (IllegalArgumentException e) {
        if (! isNumeric(data)) {
            throw new KraftyRunTimeException("Input must be numeric : " + data, e);
        }
    }
*/
