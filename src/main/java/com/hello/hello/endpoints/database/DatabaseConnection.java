package com.hello.hello.endpoints.database;

import java.sql.Statement;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.gson.JsonObject;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;


public class DatabaseConnection {
    
    //Connection to DBS
    public Connection getConn(){
        Connection c;
        try {
            c = DriverManager.getConnection("jdbc:postgresql://localhost:5500/postgres", "postgres", "123456789");
            return c;
        } catch (SQLException e) {
            return null;
        }
    }

    //DBS Select
    public JsonObject getData(String query){
        Connection c = getConn();
        Statement s = null;
        try {
            s = c.createStatement();
            ResultSet rs = s.executeQuery(query);
            return getRecordsByRows(rs);

        } catch (SQLException e) {
            e.printStackTrace();
            JsonObject json = new JsonObject();
            json.addProperty("msg", "Connection failed.");
            return json;
        }
    }


    //Get Data by whole rows
    public JsonObject getRecordsByRows(ResultSet rs) throws SQLException{
        ResultSetMetaData rsmd = rs.getMetaData();
        JsonObject jsonAll = new JsonObject();
        
        int cnt = 1;
        
        while (rs.next()){
            JsonObject jsonRow = new JsonObject();
            for(int i = 1; i<=rsmd.getColumnCount(); i++){
                jsonRow.addProperty(rsmd.getColumnName(i), rs.getString(i));
            }
            jsonAll.add("row"+cnt, jsonRow);
            cnt++;
        }
        return jsonAll;
    }

    //Queries for update dele and insert
    public String updateData(String query){
        Connection c = getConn();
        Statement s = null;
        try {
            s = c.createStatement();
            s.executeUpdate(query);
            return "Success.";
        } catch (SQLException e) {
            e.printStackTrace();
            return "Connection failed.";
        }
    }
    /**
     * Tato aj nasledujuca metoda su spravene tak, ze ak mi dojde JSON subor tak na zaklade JSON suboru vytvori UPDATE a INSERT Query.
     * Toto mi moze potencionalne usetrit cas ak vytvaram vela endpointov, ze iba prijmem JSON subor toto spraci UPDATE a INSERT Query a nemusi zakazdym pisat SQL INSERT UPDATE query.
     * Jedna sa iba o jednoduch UPDATE a INSERT Queries
     * @param data
     * @param tableName
     * @return
     */
    public String insertQueryCreator(Map<String, Object> data, String tableName){
        Set<String> keys = data.keySet();
        List<String> list = new LinkedList<String>();

        for(String s : keys){
            list.add(s);
        }

        String columns = list.get(0);
        String values =  "";
        
        if(true == isNumeric(data.get(list.get(0)).toString())){
            values =  data.get(list.get(0)).toString();
        }
        else values = "'" + data.get(list.get(0)).toString() + "'"; 

        for(int i = 1; i < list.size(); i++){
            columns = columns + "," + list.get(i) ;
            if(true == isNumeric(data.get(list.get(i)).toString())){
                values =  values + "," +  data.get(list.get(i)).toString();
            }
            else values = values + ",'" + data.get(list.get(i)).toString() + "'";
        }
        return "INSERT INTO " + tableName + " (" + columns + ") VALUES (" + values + ")";
    }


    public String updateQueryCreator(String columnName, int id, Map<String, Object> data, String tableName){
        Set<String> keys = data.keySet();
        List<String> list = new LinkedList<String>();

        for(String s : keys){
            list.add(s);
        }

        String update = "";
        
        if(true == isNumeric(data.get(list.get(0)).toString())){
            update = list.get(0) + " = " + data.get(list.get(0)).toString();
        }
        else update = list.get(0) + " = '" + data.get(list.get(0)).toString() + "'"; 

        for(int i = 1; i < list.size(); i++){
            if(true == isNumeric(data.get(list.get(i)).toString())){
                update =  update + "," + list.get(i) + " = " +  data.get(list.get(i)).toString();
            }
            else update = update + "," + list.get(i) + " = '" + data.get(list.get(i)).toString() + "'";
        }
        return "UPDATE " + tableName + " SET " + update + " WHERE " + columnName + " = " + id;
        //UPDATE COMPANY SET ADDRESS = 'Texas', SALARY=20000;
    }

    //Metoda na kontrolu ci je String cislo alebo string.
    public static boolean isNumeric(String string) {
        int intValue;
 
        if(string == null || string.equals("")) {
            System.out.println("String cannot be parsed, it is null or empty.");
            return false;
        }
        
        try {
            intValue = Integer.parseInt(string);
            return true;
        } catch (NumberFormatException e) {
            System.out.println("Input String cannot be parsed to Integer.");
        }
        return false;
    }
}
