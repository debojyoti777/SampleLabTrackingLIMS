package com.labtrack.sampletracking.Exceptions;


/**
 * Throws when an attempt has been made for an illegal update.
 */
public class IllegalUpdateException extends RuntimeException {

    public IllegalUpdateException(){
        super("This is an illegal update prohibited by the logic. Enter correct details");
    }
}