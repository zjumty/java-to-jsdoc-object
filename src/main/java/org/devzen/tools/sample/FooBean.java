package org.devzen.tools.sample;

import org.devzen.tools.JsType;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;
import java.util.Map;

/**
 * User: matianyi
 * Date: 14-4-19
 * Time: 下午11:56
 */
@JsType("Earth")
public class FooBean {
    private String strValue;
    private int intValue;
    private Date dateValue;
    private long longValue;
    private boolean boolValue;
    private short shortValue;
    private BigDecimal decimalValue;
    private BigInteger bigIntegerValue;
    private Thread shouldNotConvertValue;
    private Person person;
    private Nothing nothing;
    private Car[] cars;
    private Map<String, Person> mapValue;

    public String getStrValue() {
        return strValue;
    }

    public void setStrValue(String strValue) {
        this.strValue = strValue;
    }

    public int getIntValue() {
        return intValue;
    }

    public void setIntValue(int intValue) {
        this.intValue = intValue;
    }

    public Date getDateValue() {
        return dateValue;
    }

    public void setDateValue(Date dateValue) {
        this.dateValue = dateValue;
    }

    public long getLongValue() {
        return longValue;
    }

    public void setLongValue(long longValue) {
        this.longValue = longValue;
    }

    public boolean isBoolValue() {
        return boolValue;
    }

    public void setBoolValue(boolean boolValue) {
        this.boolValue = boolValue;
    }

    public short getShortValue() {
        return shortValue;
    }

    public void setShortValue(short shortValue) {
        this.shortValue = shortValue;
    }

    public BigDecimal getDecimalValue() {
        return decimalValue;
    }

    public void setDecimalValue(BigDecimal decimalValue) {
        this.decimalValue = decimalValue;
    }

    public BigInteger getBigIntegerValue() {
        return bigIntegerValue;
    }

    public void setBigIntegerValue(BigInteger bigIntegerValue) {
        this.bigIntegerValue = bigIntegerValue;
    }

    public Thread getShouldNotConvertValue() {
        return shouldNotConvertValue;
    }

    public void setShouldNotConvertValue(Thread shouldNotConvertValue) {
        this.shouldNotConvertValue = shouldNotConvertValue;
    }

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }

    public Nothing getNothing() {
        return nothing;
    }

    public void setNothing(Nothing nothing) {
        this.nothing = nothing;
    }

    public Car[] getCars() {
        return cars;
    }

    public void setCars(Car[] cars) {
        this.cars = cars;
    }

    public Map<String, Person> getMapValue() {
        return mapValue;
    }

    public void setMapValue(Map<String, Person> mapValue) {
        this.mapValue = mapValue;
    }
}
