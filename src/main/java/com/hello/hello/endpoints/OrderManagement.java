package com.hello.hello.endpoints;

import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.google.gson.JsonObject;
import com.hello.hello.endpoints.database.DatabaseConnection;
import com.hello.hello.order.Order;

@RestController
public class OrderManagement {

    //Endpoint na získanie zoznamu objednávok s názvom, celkovou cenou a produktmi a menom usera, ktorý objednávku vytvoril
    /**
     * Tento endpoint sa najprv pripoji na databazu, vytiahne si udaje o objednavkach s DBS a vrati ich v JSON formate.
     * Nasledne potrebujeme si pozriet pole productcollection, kde som ulozil v textovom formate jednotlive (11,23,54,887) kde cisle reprezentuju ciselnik produktov.
     * Potom sa vytvori objekt Order kde sa vytiahnu na zaklade ID jednym selektom vsetky produkty a to sa spoji do jedneho JSON suboru so vsetkymi objednavkymi a udajmi zadanymi v ramci zadania.
     * @return
     */
    @GetMapping("/getOP")
    public String getData(){
        DatabaseConnection dbsConn = new DatabaseConnection();
        JsonObject tmpData = dbsConn.getData("SELECT orders.id, orders.name, orders.summaryprice, users.login, orders.productcollection FROM orders INNER JOIN users ON orders.userowner = users.id");
        JsonObject data = new JsonObject();
        for(int i = 1; i<=tmpData.size(); i++){
            JsonObject json = tmpData.get("row"+i).getAsJsonObject();
            Order order = new Order(json.get("id").toString().replace("\"", ""),
            json.get("name").toString().replace("\"", ""),
            json.get("summaryprice").toString().replace("\"", ""),
            json.get("login").toString().replace("\"", ""),
            json.get("productcollection").toString().replace("\"", ""));
            data.add("Objednavka c. " + i,order.Json);
        }
        return data.toString();
    }
    
    //post product for creating DBS record
    @PostMapping(value = "/postProduct", consumes = "application/json")
    public String postProductToDBS(@RequestBody Map<String, Object> data){
        DatabaseConnection dbsConn = new DatabaseConnection();
        return dbsConn.updateData(dbsConn.insertQueryCreator(data,"products"));
    }

    //post order to DBS
    @PostMapping(value = "/postOrder", consumes = "application/json")
    public String postOrderToDBS(@RequestBody Map<String, Object> data){
        DatabaseConnection dbsConn = new DatabaseConnection();
        return dbsConn.updateData(dbsConn.insertQueryCreator(data,"orders"));
    }

    //update product data in DBS
    @PostMapping(value = "/updateProduct/{id}", consumes = "application/json")
    public String deleteUserFromDBS(@PathVariable("id") int id, @RequestBody Map<String, Object> data){
        DatabaseConnection dbsConn = new DatabaseConnection();
        return dbsConn.updateData(dbsConn.updateQueryCreator("serialnumber", id, data, "products"));
    }

    //Table creation queries - tento endpoint sluzil na vytvaranie tabuliek pre ulohu 1 a pre ulohu 3, nezakomentovane query je vysledny SQL pre ulohu 3
    @GetMapping("/table")
    public String makeTable(){
        DatabaseConnection dbsConn = new DatabaseConnection();
        //This creates table for users
        //return dbsConn.updateData("CREATE TABLE users (id SERIAL PRIMARY KEY, login TEXT NOT NULL, email TEXT NOT NULL, password TEXT NOT NULL);");
        //return dbsConn.updateData("CREATE TABLE products (serialnumber INT PRIMARY KEY NOT NULL, name TEXT NOT NULL, description TEXT NOT NULL, price DOUBLE PRECISION NOT NULL);");
        //return dbsConn.updateData("CREATE TABLE orders (id SERIAL PRIMARY KEY, name TEXT NOT NULL, productcollection TEXT NOT NULL, discountsurcharge INT NOT NULL, userowner INT NOT NULL, summaryprice DOUBLE PRECISION NOT NULL);");
        //return dbsConn.updateData("CREATE TABLE objednavka (id SERIAL PRIMARY KEY NOT NULL, zakaznik INT NOT NULL, datum DATE NOT NULL);");
        //return dbsConn.updateData("CREATE TABLE zakaznik (id SERIAL PRIMARY KEY, name TEXT NOT NULL, surname TEXT NOT NULL, age INT NOT NULL);");
        return dbsConn.getData("SELECT zakaznik.id, zakaznik.name, zakaznik.surname, zakaznik.age FROM zakaznik INNER JOIN objednavka ON zakaznik.id = objednavka.zakaznik WHERE zakaznik.age>23 AND zakaznik.age<36 AND objednavka.datum > now() - interval '3 month' GROUP BY zakaznik.id HAVING COUNT(zakaznik.id)>1").toString();
        //return dbsConn.updateData("INSERT INTO objednavka (zakaznik,datum) VALUES (4,'2022-08-08')");
    }
}

