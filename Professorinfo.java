package com.george.softwareenginnering;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import java.time.format.DateTimeFormatter;
import java.util.Iterator;
import org.bson.Document;
import com.mongodb.MongoClient;

import com.mongodb.*;
import java.util.*;
import java.util.prefs.BackingStoreException;
import java.time.*;

public class Professorinfo {

    public String profEmail(String cID){
        //MongoClient mongoClient = new MongoClient("localhost", 9010);
        MongoClient mongoClient = new MongoClient(new MongoClientURI("PUT IN PATH FOR YOUR MONGODBSERVER"));
        System.out.println("Server connection succesfully done");
        DB db = mongoClient.getDB("project");
        DBCollection collection1 = db.getCollection("Student");//ACCESSING THE COLLECTION Courses
        DBObject query = new BasicDBObject("_id", "s001");
        DBCursor c = collection1.find(query);// THE QUERY IS USED HERE TO FIND ALL THE DOCUMENT THAT HAVE THE roomNO AND SETTING THE CURSOR TO THAT PARTICULAR DOCUMENT

        DBCollection collection2 = db.getCollection("Courses");
        query.put("_id", "CIS2006");//CREATING THE QUERY THAT WILL SEARCH ALL THE COURSES DOCUMENTS THAT HAVE THE ROOM NUMBER AS roomNo
        String pID = "";

        c = collection2.find(query);
        while (c.hasNext())
        {
            DBObject obj1 = c.next();
            pID = obj1.get( "pID").toString();// EXTRACT THE STUDENT FIRST NAME

        }
        System.out.println("PID = " + pID);
        DBCollection collection3 = db.getCollection("Professor");
        query.put("_id", pID);//CREATING THE QUERY THAT WILL SEARCH ALL THE COURSES DOCUMENTS THAT HAVE THE ROOM NUMBER AS roomNo
        String pEmail = "";

        c = collection3.find(query);
        while (c.hasNext())
        {
            DBObject obj1 = c.next();
            pEmail= obj1.get("professor_email").toString();// EXTRACT THE STUDENT FIRST NAME

        }
        System.out.println("pEMAIL = " + pEmail);

        return(pEmail);
    }
}
