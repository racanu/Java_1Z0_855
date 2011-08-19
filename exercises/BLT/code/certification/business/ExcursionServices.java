package certification.business;

import java.util.*;
import java.rmi.*;
import certification.domain.*;
import certification.database.*;

public interface ExcursionServices {
    public void createExcursion(Excursion excursion) throws RemoteException;
    public void deleteExcursion(long recordNumber) throws RemoteException, ExcursionNotFoundException;
    public Excursion readExcursion(ExcursionPrimaryKey excursionKey) throws RemoteException, ExcursionNotFoundException;
    public Map<Long, Excursion> getAllRecords() throws RemoteException;
    public void saveData() throws RemoteException, ServicesException;
}
