package certification.domain;

import java.util.Date;
import java.io.Serializable;

public class Excursion implements Serializable {
    private int busNumber;
    private String companyName = "";
    private Date departure = new Date();
    private String origin = "";
    private String destination = "";
    private String price = "";
    private byte capacity;
    
    public void setBusNumber(int busNumber) { this.busNumber = busNumber; }
    public int getBusNumber() { return this.busNumber; }
    public void setCompanyName(String companyName) { this.companyName = companyName; }
    public String getCompanyName() { return this.companyName; }
    public void setDeparture(Date departure) { this.departure = departure; }
    public Date getDeparture() { return this.departure; }
    public void setOrigin(String origin) { this.origin = origin; }
    public String getOrigin() { return this.origin; }
    public void setDestination(String destination) { this.destination = destination; }
    public String getDestination() { return this.destination; }
    public void setPrice(String price) { this.price = price; }
    public String getPrice() { return this.price; }
    public void setCapacity(byte capacity) { this.capacity = capacity; }
    public byte getCapacity() { return this.capacity; }
}

