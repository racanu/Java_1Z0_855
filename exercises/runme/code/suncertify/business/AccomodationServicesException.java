package suncertify.business;

public class AccomodationServicesException extends Exception {
    /**
     * 
     */
    private static final long serialVersionUID = -8190981634935019398L;
    
    // There is no reason for instantiating this exception from outside the package
    // hence the constructors use default access
    AccomodationServicesException() { }
    AccomodationServicesException(String message) { super(message); }
}
