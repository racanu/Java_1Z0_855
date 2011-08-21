package suncertify.persistence;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.io.*;

import suncertify.business.*;
import suncertify.db.*;
import suncertify.util.*;

/*
class YesNoFieldDefinition extends DatabaseFieldDefinition {
    YesNoFieldDefinition(String name) {
        super(name, 1);
    }
    
    @Override
    String validateValue(String value) throws FieldValueException {
        value = super.validateValue(value);
        value = value.toUpperCase();
        if ( !value.matches("[YN]") ) {
            throw new FieldValueException("The value of field " + this.fieldName + " may only be Y or N");
        }
        return value;
    }
}

class DateFieldDefinition extends DatabaseFieldDefinition {
    DateFieldDefinition(String name) {
        super(name, 10);
    }
    
    @Override
    String validateValue(String value) throws FieldValueException {
        try {
            value = super.validateValue(value);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
            sdf.parse(value);
            return value;
        } catch (ParseException e) {
            throw new FieldValueException("The value of field " + this.fieldName + " must follow the format yyyy/mm/dd");
        }
    }
}

class NumericFieldDefinition extends DatabaseFieldDefinition {
    private final int minFieldLength;
    
    NumericFieldDefinition(String name, int length, int minLength) {
        super(name, length);
        this.minFieldLength = minLength;
    }
    
    @Override
    String validateValue(String value) throws FieldValueException {
        value = super.validateValue(value);
        if ( !value.matches("\\d{" + this.minFieldLength + "," + this.fieldLength + "}") ) {
            throw new FieldValueException("The value of field " + this.fieldName + " must be a number of at least " 
                    + this.minFieldLength + " and at most " + this.fieldLength + " digits.");
        }
        return value;
    }
}

class CurrencyFieldDefinition extends DatabaseFieldDefinition {
    CurrencyFieldDefinition(String name) {
        super(name, 8);
    }
    
    @Override
    String validateValue(String value) throws FieldValueException {
        value = super.validateValue(value);
        try {
            DecimalFormat df = new DecimalFormat("$###0.00");
            df.parse(value);
            return value;
        } catch (ParseException e) {
            //TODO: Ideally any currency representation should be accepted in any locale.
            // Any mix of currencies and their representations should also be supported.
            // For the sake of simplicity we are only accepting '$' as the currency symbol
            // regardless of user's locale. This avoids problems when accessing the database
            // with clients in  different locales and forces operators to convert all rates to USD,
            // or use some other externally defined convention for representing room rates.
            String symbol = Currency.getInstance(Locale.getDefault()).getSymbol();
            throw new FieldValueException("The value of field " + this.fieldName 
                    + " must be a currency representation of the form $xxxx.xx"); 
        }
    }
}

class OwnerFieldDefinition extends DatabaseFieldDefinition {
    public static final int FIELD_SIZE = 8;
    
    OwnerFieldDefinition(String name) {
        super(name, FIELD_SIZE);
    }
    
    @Override
    String validateValue(String value) throws FieldValueException {
        value = super.validateValue(value);
        if ( !value.matches("(\\d{" + FIELD_SIZE + "})?") ) {
            throw new FieldValueException(
                    "The value of field " + this.fieldName 
                    + " must be a number of exactly " + FIELD_SIZE + " digits "
                    + " or an empty string.");
        }
        return value;
    }
}
*/

public class Storage {
    
}
