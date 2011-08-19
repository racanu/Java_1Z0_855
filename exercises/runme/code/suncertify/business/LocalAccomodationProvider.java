package suncertify.business;

import suncertify.db.*;

public class LocalAccomodationProvider implements AccomodationServices {

    LocalAccomodationProvider(DB dbManager) {
        
    }
    
    @Override
    public void addAccomodation(Accomodation accomodation)
    {
        // TODO Auto-generated method stub
    }

    @Override
    public void deleteAccomodation(int recordNumber)
            throws AccomodationServicesException {
        // TODO Auto-generated method stub

    }

    @Override
    public void updateAccomodation(int recordNumber, Accomodation accomodation)
            throws AccomodationNotFoundException {
        // TODO Auto-generated method stub

    }

}
