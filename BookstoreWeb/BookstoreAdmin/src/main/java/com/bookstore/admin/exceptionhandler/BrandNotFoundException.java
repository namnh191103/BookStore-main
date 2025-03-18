package com.bookstore.admin.exceptionhandler;

public class BrandNotFoundException extends Exception{

    public BrandNotFoundException(String message) {
		super(message);
	}

    public BrandNotFoundException() {
        
    }

}
