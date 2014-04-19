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




}
