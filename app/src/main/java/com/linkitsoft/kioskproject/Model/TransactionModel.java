package com.linkitsoft.kioskproject.Model;

import java.util.Date;

public class TransactionModel {

   int Id;
   int customerId;
    int KioskId;
   int quantityDispensed;
   double amount;
   Date date;
   String prepaidcode;
   int pid;

    public TransactionModel() {
    }

    public TransactionModel(int id, int customerId, int kioskId, int quantityDispensed, double amount, Date date, String prepaidcode, int pid) {
        Id = id;
        this.customerId = customerId;
        KioskId = kioskId;
        this.quantityDispensed = quantityDispensed;
        this.amount = amount;
        this.date = date;
        this.prepaidcode = prepaidcode;
        this.pid = pid;
    }

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public int getKioskId() {
        return KioskId;
    }

    public void setKioskId(int kioskId) {
        KioskId = kioskId;
    }

    public int getQuantityDispensed() {
        return quantityDispensed;
    }

    public void setQuantityDispensed(int quantityDispensed) {
        this.quantityDispensed = quantityDispensed;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getPrepaidcode() {
        return prepaidcode;
    }

    public void setPrepaidcode(String prepaidcode) {
        this.prepaidcode = prepaidcode;
    }

    public int getPid() {
        return pid;
    }

    public void setPid(int pid) {
        this.pid = pid;
    }
}
