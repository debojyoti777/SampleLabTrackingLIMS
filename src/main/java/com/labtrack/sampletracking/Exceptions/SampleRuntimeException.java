package com.labtrack.sampletracking.Exceptions;

public class SampleRuntimeException extends RuntimeException {

    String message;
    public SampleRuntimeException(){

    }
    public void sampleNotFoundException(Long id) throws NullPointerException {
       throw new NullPointerException("Sample with " + id + " was not found");
    }

    public void wrongInputException(String value) throws IllegalArgumentException {
        throw new IllegalArgumentException("The " + value + " are incorrect, please enter correct input");
    }

    public void illegalUpdateException() throws IllegalArgumentException {
        throw new IllegalArgumentException("This Sample cannot be updated/changed , as the operation is not logically incorrect");
    }
}