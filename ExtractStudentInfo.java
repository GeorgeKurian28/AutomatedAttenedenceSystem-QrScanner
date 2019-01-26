package com.george.softwareenginnering;
// THIS CLASS CONTAINS THE ARRAY OF STUDENTS THEIR EMAIL ADDRESS FIRST NAME AND LAST NAME  STUDENT ID COURSE NAME DAY AND TIME AND AN EMPTY COLOUMN FOR ATTENDENCE TIME
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Iterator;
import org.bson.Document;
import com.mongodb.MongoClient;

import com.mongodb.*;
import java.util.*;

public class ExtractStudentInfo {
    private String day, time, cID, courseName,sID, timeWindow, pEmail = "", duration;
    // ROOM NUMBER HAS TO BE READ FROM FILE
    //private String roomNo = "108";//SET THE ROOM  NUMBER A TEXT FILE WILL CARRY THIS INFO
    String path = "C:\\AUTOMATEDATTENDENCESYSTEM\\ROOM.txt";
    ReadFile readFile = new ReadFile();
    public String roomNo = readFile.readFile(path);
    Professorinfo pInfo = new Professorinfo();


    String[] extract(){
        ExtractStudentList extractStudentlist = new ExtractStudentList();
        //MongoClient mongoClient = new MongoClient("localhost", 9010);
        MongoClient mongoClient = new MongoClient(new MongoClientURI("PUT IN PATH FOR YOUR MONGODBSERVER"));//CONNECTING TO THE MONGO DB SERVER
        System.out.println("Server connection succesfully done");
        DB db = mongoClient.getDB("project");//ACCESSING THE DATABASE project
        DBCollection collection1 = db.getCollection("Courses");//ACCESSING THE COLLECTION Courses
        BasicDBObject query = new BasicDBObject();//DECLARING THE QUERY
        DateTimeFormatter ftime = DateTimeFormatter.ofPattern("HH:mm");//SET THE TIME FORMAT TO HH:MM

        query.put("room_Number", roomNo);//CREATING THE QUERY THAT WILL SEARCH ALL THE COURSES DOCUMENTS THAT HAVE THE ROOM NUMBER AS roomNo

        DBCursor c = collection1.find(query);// THE QUERY IS USED HERE TO FIND ALL THE DOCUMENT THAT HAVE THE roomNO AND SETTING THE CURSOR TO THAT PARTICULAR DOCUMENT
        String dayTime1, daySystem,dayTime2,currentTime;
        LocalTime timeCurrent,timeDb;
        int startIndex, endIndex, classfound = 0;
        String[] classRefresh = {"0","0"};

        // CONVERTING THE DOCUMENT ARRAY TO A LIST
        List<String> daylist = new ArrayList();//THIS IS THE LIST THAT STORES THE DAYS
        List<String> timelist = new ArrayList();//THIS IS THE LIST THAT STORES THE TIME
        List<String> sidlist = new ArrayList();//THIS IS THE LIST THAT STORES THE STUDENT IDS
        while (c.hasNext())
        {
            DBObject obj1 = c.next();
            daylist.clear();//CLEARING THE LIST AT THE BEGENNING OF EACH RUN
            timelist.clear();//CLEARING THE LIST AT THE BEGENNING OF EACH RUN

            sID = obj1.get("sID").toString();// EXTRACT THE STUDENT ID
            cID = obj1.get("_id").toString();//EXTRACT THE COURSE ID
            timeWindow = obj1.get("time_Window").toString();
            courseName = obj1.get("course_Name").toString();//EXTRACT THE COURSE NAME
            duration =  obj1.get( "duration").toString();//EXTRACT THE CLASS DURATION TO USE IN THE CLASS REFRESH TIME IN THE MAIN CLASS
            System.out.println("THE CLASS DURATION WAS FOUND TO BE  : - " + classRefresh[0]);

            //THE DAY AND TIME HAS TO BE STORED IN A STRING ARRAYLIST WHICH HAS TO BE REST TO 0 EACH TIME THE WHILE LOOP START

            dayTime1  = obj1.get("day_Time").toString();
            startIndex = dayTime1.indexOf("\"");
            endIndex = dayTime1.indexOf("\"",(startIndex+1));
            daySystem = dayTime1.substring(startIndex+1,endIndex);
            daylist.add(daySystem);

            startIndex = dayTime1.indexOf("\"",(endIndex+1));
            endIndex = dayTime1.indexOf("\"",(startIndex+1));
            dayTime2 = dayTime1.substring(startIndex+1,endIndex);
            timeDb = LocalTime.parse(dayTime2);//TIME FROM THE TABLE HAS BEEN PARSED AND TYPE CASTED TO DATETIME TYPE
            timelist.add(dayTime2);

            while (dayTime1.indexOf("\"",(endIndex+1))!= -1)
            {
                dayTime1  = obj1.get("day_Time").toString();
                startIndex = dayTime1.indexOf("\"",(endIndex+1));
                endIndex = dayTime1.indexOf("\"",(startIndex+1));
                daySystem = dayTime1.substring(startIndex+1,endIndex);
                daylist.add(daySystem);

                startIndex = dayTime1.indexOf("\"",(endIndex+1));
                endIndex = dayTime1.indexOf("\"",(startIndex+1));
                dayTime2 = dayTime1.substring(startIndex+1,endIndex);
                System.out.println(dayTime2);
                System.out.println(dayTime1);
                timeDb = LocalTime.parse(dayTime2);//TIME FROM THE TABLE HAS BEEN PARSED AND TYPE CASTED TO DATETIME TYPE
                timelist.add(dayTime2);
            }

            day=LocalDate.now().getDayOfWeek().toString();//GET TODAYS DAY TO BE USED IN THE ACTUAL WORKING CODE
            //day = "Wednesday";//TEST SHOULD BE REMOVED LATER

            //CHECK IF THE CLASS IS SCHEDULED FOR TODAY AND IF YES THEN CHECK IF THE TIME DIFFERENCE IS LESS THAN 30 MINS IF YES THEN EXTRACT THE INFO ABD BREAK THE WHILE LOOP
            for(int i = 0; i < timelist.size(); i++)
            {
                if (day.equalsIgnoreCase(daylist.get(i)))
                {
                    timeDb = LocalTime.parse(timelist.get(i));//TIME FROM THE TABLE HAS BEEN PARSED AND TYPE CASTED TO DATETIME TYPE
                    timeCurrent = LocalTime.now();//THIS WILL BE USED TO THE ACUTAL CODE
                    //timeCurrent = LocalTime.of(7,00);//TEST SHOULD BE REMOVED FOR THE ACTUAL CODE
                    path = "C:\\AUTOMATEDATTENDENCESYSTEM\\Timegap.txt";
                    long diffInMinutes = java.time.Duration.between( timeCurrent,timeDb).toMinutes();
                    System.out.println("TIME FORM THE DATABASE    "+timeDb);
                    System.out.println("TIME FORM THE CURRENT TIME   "+timeCurrent);
                    System.out.println("TIME FORM THE difference    "+diffInMinutes);

                    ReadFile readFile = new ReadFile();
                    String timerString = readFile.readFile(path);
                    int timeWindow = Integer.parseInt(timerString);
                    System.out.println("The timer from file"+timerString);
                    System.out.println("TIME WINDOW " + timeWindow);

                    if (diffInMinutes < timeWindow && diffInMinutes > 0)//(timeCurrent.getMinute()- timeDb.getMinute())< 30//(timeDb.isAfter(timeCurrent)) THUS WILL SEARCH IN TIME WINDOW INTERVAL AND SHOULD CHANGE HERE FOR THE ACTUAL PRESENTATION
                    {
                        System.out.println("CONDITION IS SATISFIED ");
                        System.out.println(sID);
                        System.out.println(cID);
                        System.out.println(courseName);
                        time = timeDb.toString();
                        classRefresh[1] =time;//EXTRACT THE CLASS TIME TO USE IN THE CLASS REFRESH TIME IN THE MAIN CLASS
                        classRefresh[0] =duration; //EXTRACT THE CLASS DURATION TO USE IN THE CLASS REFRESH TIME IN THE MAIN CLASS
                        System.out.println("THE CLASS TIME WAS FOUND TO BE  : - " + classRefresh[1]);

                        //WE WILL EXTRACT THE PROFESSORS EMAIL ADDRESS


                        pEmail=pInfo.profEmail(cID);
                        System.out.println("pEmail = "+ pEmail);


                        //NOW WE WILL EXTRACT THE STUDENT INFO AND STORE IN AN ARRAY

                        String sid;

                        startIndex = sID.indexOf("\"");
                        endIndex = sID.indexOf("\"",(startIndex+1));
                        sid = sID.substring(startIndex+1,endIndex);

                        sidlist.add(sid);

                        while (sID.indexOf("\"",(endIndex+1))!= -1)
                        {
                            startIndex = sID.indexOf("\"",(endIndex+1));
                            endIndex = sID.indexOf("\"",(startIndex+1));
                            sid = sID.substring(startIndex+1,endIndex);
                            sidlist.add(sid);
                        }
                        classfound = 1;
                        i = timelist.size();
                    }
                }

            }
            if (classfound == 1)
                break;
        }
        if (classfound == 0)
            System.out.println("NO CLASS FOR THE NEXT 1 HR");
        else {
            Object[] sidArray = sidlist.toArray();
            extractStudentlist.studentList(sidArray,cID,courseName,time,timeWindow,pEmail);

        }

        //CALL THE GENERATE QR CODE FUNCTION AND SEND IT STUDENT_ARRAY [ID] [EMAIL ADDRESS], DATE, TIME,COURSE NO. ROOM NO IS TIED TO THE AAS
        return (classRefresh);
    }

}
