package certification.business;

public class ServicesException extends Exception {
    // We don't allow direct instantiation of this exception, because it is too generic to be useful     // Subclasses are more explanatory
    protected ServicesException() { }
    protected ServicesException(String message) { super(message); }
}
