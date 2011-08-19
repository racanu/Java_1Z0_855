package suncertify.business;

public class RemoteAccomodationProvider implements AccomodationServices {
    
    RemoteAccomodationProvider(String serverData) {
        try {
            AccomodationServer server = AccomodationServer.getRemoteServer(serverData);
            String response = server.sayHello();
            System.out.println("Response: " + response);
          } catch (Exception e) {
            System.err.println("Client exception: " + e.toString());
            e.printStackTrace();
          }
    }
    
    @Override
    public void addAccomodation(Accomodation accomodation)
            throws AccomodationServicesException {
        // TODO Auto-generated method stub

    }

    @Override
    public void deleteAccomodation(int recordNumber) 
            throws AccomodationServicesException {
        // TODO Auto-generated method stub

    }

    @Override
    public void updateAccomodation(int recordNumber, Accomodation accomodation)
            throws AccomodationServicesException {
        // TODO Auto-generated method stub

    }

}
