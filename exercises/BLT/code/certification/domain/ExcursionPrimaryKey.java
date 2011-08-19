package certification.domain;

import java.util.Date;
import java.io.Serializable;

public class ExcursionPrimaryKey implements Serializable {
    private final Excursion excursion;

    public ExcursionPrimaryKey(Excursion excursion) {
      this.excursion = excursion;
    }

    @Override
    public boolean equals(Object o) {
        if ( o != null && o instanceof ExcursionPrimaryKey ) {
            Excursion otherExcursion = ((ExcursionPrimaryKey) o).excursion;
          
            return otherExcursion.getBusNumber() == excursion.getBusNumber() 
                && otherExcursion.getCompanyName().equals(excursion.getCompanyName()) 
                && otherExcursion.getDeparture().equals(excursion.getDeparture());
        } else {
            return false;
        }
    }
  
    @Override
    public int hashCode() {
        return excursion.getBusNumber() 
          + excursion.getCompanyName().hashCode() 
          + excursion.getDeparture().hashCode();
    }
    
    public Excursion getExcursion() {
        return excursion;
    }
}

