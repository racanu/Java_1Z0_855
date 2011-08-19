package suncertify.business;

import java.rmi.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class AccomodationServer implements Remote {
    
    public static final String SERVER_NAME = "AccomodationServer";
    
    public static AccomodationServer getRemoteServer(String serverData) throws RemoteException, NotBoundException {
        Registry registry = LocateRegistry.getRegistry(serverData);
        return (AccomodationServer)registry.lookup(SERVER_NAME);
    }
    
    public static void startServer() {
        try {
            AccomodationServer server = (AccomodationServer)UnicastRemoteObject.exportObject(new AccomodationServer(),0);

            // Bind the remote object's stun in the registry
            Registry registry = LocateRegistry.getRegistry();
            registry.bind(SERVER_NAME, server);

            System.err.println("Server ready.");
          } catch (Exception e) {
            System.err.println("Server exception: " + e.toString());
            e.printStackTrace();
          }
    }
    
    public String sayHello() {
        return "Hello, I am the " + SERVER_NAME + ". How can I help you today?";
    }
}
