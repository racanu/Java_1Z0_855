package suncertify.business;

public interface AccomodationServices {
    public void addAccomodation(Accomodation accomodation)
        throws AccomodationServicesException;
    public void deleteAccomodation(int recordNumber)
        throws AccomodationServicesException;
    public void updateAccomodation(int recordNumber, Accomodation accomodation)
        throws AccomodationServicesException;
}
