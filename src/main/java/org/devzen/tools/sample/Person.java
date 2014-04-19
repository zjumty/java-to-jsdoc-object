package org.devzen.tools.sample;

import org.devzen.tools.JsType;

import java.util.List;

/**
 * User: matianyi
 * Date: 14-4-20
 * Time: 上午12:06
 */
@JsType
public class Person {
    private String name;
    private int age;
    private List<Car> cars;
    private List<Nothing> nothings;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public List<Car> getCars() {
        return cars;
    }

    public void setCars(List<Car> cars) {
        this.cars = cars;
    }

    public List<Nothing> getNothings() {
        return nothings;
    }

    public void setNothings(List<Nothing> nothings) {
        this.nothings = nothings;
    }
}
