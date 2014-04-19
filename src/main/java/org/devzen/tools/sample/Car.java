package org.devzen.tools.sample;

import org.devzen.tools.JsType;

/**
 * User: matianyi
 * Date: 14-4-20
 * Time: 上午12:06
 */
@JsType
public class Car {
    private String model;
    private int price;

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }
}
