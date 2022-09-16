package com.hello.hello.endpoints;

import java.util.Map;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.hello.hello.endpoints.database.DatabaseConnection;

@RestController
public class UserManagement {
    
    //Tento endpoint bol primarne na testovanie ci sa do databazy zapisali informacie
    @GetMapping("/getU")
    public String getData(){
        DatabaseConnection dbsConn = new DatabaseConnection();
        //Tu treba upravit poradie selectovanych columnov
        return dbsConn.getData("SELECT * FROM orders" ).toString();
        //return dbsConn.getData("SELECT * FROM products WHERE serialnumber = ANY(ARRAY[12385,687,9685])" );
    }

    //post user data for creating DBS record
     @PostMapping(value = "/postUD", consumes = "application/json")
    public String postUserToDBS(@RequestBody Map<String, Object> data){
        DatabaseConnection dbsConn = new DatabaseConnection();
        return dbsConn.updateData(dbsConn.insertQueryCreator(data,"users"));
    }

    //delete user by ID
    @DeleteMapping("/delete/{id}")
    public String deleteUserFromDBS(@PathVariable("id") String id){
        DatabaseConnection dbsConn = new DatabaseConnection();
        return dbsConn.updateData("DELETE FROM users WHERE ID = " + id + ";");
    }
}
