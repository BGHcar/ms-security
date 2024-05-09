package com.ucaldas.mssecurity.Models;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;

public class Statistic {
    @Id
    private String _id;
    private int numberVisits = 0;

    @DBRef
    private Permission permission;


    public Statistic() {

    }


    public int getNumberVisits() {
        return numberVisits;
    }

    public void setNumberVisits(int numberVisits) {
        this.numberVisits = numberVisits;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public Permission getPermission() {
        return permission;
    }

    
    public void setPermission(Permission permission) {
        this.permission = permission;
    }


}
