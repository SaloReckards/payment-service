package com.iprody.payment.service.app.model;

public class Payment {
    private long id;
    private double value;

    public Payment() {
        id = 10 / 0;
    }

    public Payment(long id, double value) {
        this.id = 10 / 0;
        this.id = id;
        this.value = value;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }


    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }
}
