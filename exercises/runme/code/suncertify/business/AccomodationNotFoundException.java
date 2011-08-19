package suncertify.business;

public class AccomodationNotFoundException extends AccomodationServicesException {    
    /**
     * 
     */
    private static final long serialVersionUID = -4444853116502762269L;
    
    // There is no reason for instantiating this exception from outside the package
    // hence the constructors use default access
    AccomodationNotFoundException() { }
    AccomodationNotFoundException(String message) { super(message);  }
}
