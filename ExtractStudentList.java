package com.george.softwareenginnering;
//HERE WE WILL FIRST DETERMINE THE CORRECT COURSE THAT IS RUNNING FOR THIS SESSION AND EXTRACT THE ARRAY OF THE STUDENTS FROM THE COURSE AND SEND THAT ARRAY TO Extract Student Info

import com.google.zxing.WriterException;
import com.mongodb.*;
import com.sun.javafx.image.BytePixelSetter;

import javax.mail.MessagingException;
import javax.mail.internet.AddressException;
import java.io.IOException;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class ExtractStudentList {
    private QRcodeverifier generate = new QRcodeverifier();
    private SendEmail email = new SendEmail();
    public static String QR_CODE_IMAGE_PATH;
    public static String[][] studentList;
    //ROOM NUMBER HAS TO BE READ FROM FILE
    String path = "C:\\AUTOMATEDATTENDENCESYSTEM\\ROOM.txt";

    //public static String roomNo = "108";//THIS ROOM NUMBER SHOULD BE LINKED TO THE ROOM NO IN EXTRACTSTUDENTINFO CLASS  A TEXT FILE WILL CARRY THIS INFO.
    ReadFile readFile = new ReadFile();
    public String roomNo = readFile.readFile(path);

    void studentList(Object[] studentid,String cID, String courseName,String time, String timeWindow, String pEmail)
    {
        studentList = new String[studentid.length][13];
        String qrCodeText;
        String message;

        MongoClient mongoClient = new MongoClient(new MongoClientURI("PUT IN PATH FOR YOUR MONGODBSERVER"));//CONNECTING TO THE MONGO DB SERVER
        //MongoClient mongoClient = new MongoClient("localhost", 9010);
        System.out.println("Server connection succesfully done");
        DB db = mongoClient.getDB("project");//ACCESSING THE DATABASE project
        DBCollection collection1 = db.getCollection("Student");//ACCESSING THE COLLECTION Courses
        BasicDBObject query = new BasicDBObject();//DECLARING THE QUERY
        DateTimeFormatter fdate = DateTimeFormatter.ofPattern("MM/dd");//date format

        for(int i = 0; i < studentid.length; i++)//THIS LOOP WHILE GO THROUGH EACH STUDENT ID AND LOOK FOR THE STUDENT INFO TO UPDATE THE ARRAY studentList
        {
            studentList[i][0] = (String)studentid[i];
            studentList[i][4] = cID;
            studentList[i][5] = courseName;
            studentList[i][6] = time;
            studentList[i][7] = timeWindow;
            studentList[i][8] = LocalDate.now().format(fdate);// STORE THE DATE ---!!!!!UPDATE THE DATE HERE
            studentList[i][9] = "0";// STORE THE STATUS AS  1 - TARDY 0 - ABSENT OR EMPTY STRING -ON TIME
            studentList[i][10] = "";// STORE THE TIME THE STUDENT SIGNED IN
            studentList[i][12] = pEmail; //STORE THE PROFESSOR EMAIL SO THE EMAILS CAN BE SENT TO THEM WHEN THE STUDENT IS LATE OR ABSENT


            if(timeWindow == "0")
            {
                studentList[i][10] = time;    // IF THE TIME WINDOW IS SWITCHED OFF THEN NO NEED TO SEND EMAILS OR UPDATE THE REGISTER
            }
            else
            {
                studentList[i][9] = "0";    //THE REGISTER IS UPDATED AS ABSENT ONLY IF THE TIME IS NOT IN THE INTERVAL TIME AND TIME+TIMEWINDOW
            }

            query.put("_id", studentid[i]);//CREATING THE QUERY THAT WILL SEARCH ALL THE STUDENT DOCUMENTS THAT HAVE THE STUDENT ID AS sID
            DBCursor c = collection1.find(query);// THE QUERY IS USED HERE TO FIND ALL THE DOCUMENT THAT HAVE THE roomNO AND SETTING THE CURSOR TO THAT PARTICULAR DOCUMENT
            while (c.hasNext())
            {
                DBObject obj1 = c.next();
                studentList[i][1] = obj1.get("first_Name").toString();// EXTRACT THE STUDENT FIRST NAME
                studentList[i][2] = obj1.get("last_Name").toString();//EXTRACT THE STUDENT LAST NAME
                studentList[i][3] = obj1.get("student_email").toString();//EXTRACT THE STUDENT EMAIL
                // extract the recipents list along with the list of students and pass it as a 2d array

            }
            //CALL THE GENERATE QR CODE FUNCTION AND GENERATE THE QR CODE
            QR_CODE_IMAGE_PATH= "C:\\AUTOMATEDATTENDENCESYSTEM\\ "+studentList[i][0]+".png";
            if (timeWindow != "0")
            {
                qrCodeText = studentList[i][0]+";"+ studentList[i][1]+";"+ studentList[i][2]+";"+ studentList[i][3]+";"+ studentList[i][4]+";"+ studentList[i][5]+";"+ studentList[i][6]+ ";"+studentList[i][7]+";"+roomNo+";";

                studentList[i][11] = qrCodeText;//THE QRCODE IS SAVED IN TO THE ARRAY FOR VERIFICATION

                //ADD ABOVE
                try {
                    generate.generateQRCodeImage(qrCodeText,350, 350, QR_CODE_IMAGE_PATH);

                } catch (WriterException e) {
                    System.out.println("Could not generate QR Code, WriterException :: " + e.getMessage());
                } catch (IOException e) {
                    System.out.println("Could not generate QR Code, IOException :: " + e.getMessage());
                }
                //CALL THE FUNCTION TO EMAIL THE QRCODE, THE MESSAGE, THE SENDER AND RECEIVER EMAIL ADDRESS
                message = "Please check your mail for the QRCode attached and use it for signing in to class. Your class starts at "+time+ " in Room  "+ roomNo+". Please be advised that you will be marked tardy if you reach "+ timeWindow+"mins after the class starts. "+ "           Regards,    Automated Attendence System";
                try{
                    email.generateAndSendEmail(studentList[i][3],message,QR_CODE_IMAGE_PATH,null);// NULL IS ADDED AS THE FUNCTION REQUIRES A STRING ARRAY
                }
                catch(MessagingException e){
                    System.out.println("Could not Send the Message :: " + e.getMessage());
                }
            }


        }
        //THIS IS JUST THE TEST PRINT TO ENSURE ALL THE INFO IS SAVED IN THE ARRAY TO BE REMOVED
        for(int i = 0; i < studentid.length; i++)
        {
            System.out.println(studentList[i][0]+ "    "+ studentList[i][1]+ "    "+ studentList[i][2]+ "    "+ studentList[i][3]+ "    "+ studentList[i][4]+ "    "+ studentList[i][5]+ "    "+ studentList[i][6]+ "    "+ studentList[i][7]+"    "+ studentList[i][8] +"    "+ studentList[i][11] );
        }

    }
    static String[] checkDecodedQrText(String decodedText)
    {
        //HERE A CONDITION HAS TO BE ADDED THAT IF NO TEXT IS DECODED TO RESECAN THE QR CODE if (decodedText.indexOf(";")== -1)
        String[] result = {"0",""};
        LocalTime time;//THIS IS WHERE TIME TIME IS STORED THAT IS USED FOR VERIFICATION
        DateTimeFormatter fdate = DateTimeFormatter.ofPattern("MM/dd");//date format
        DateTimeFormatter ftime = DateTimeFormatter.ofPattern("HH:mm");
        String date = LocalDate.now().format(fdate).toString();
        String timeNow = LocalTime.now().format(ftime).toString();
        int timeWindow ;
        if(studentList == null)
        {
            result[0] = "1";
            result[1] = "There is no class scheduled at this time";
        }
        else
        {
            if (decodedText.indexOf(";")== -1)
            {
                result[0] = "1";
                result[1] = "This is not the right QRcode. Please rescan";
            }
            else
            {
                timeWindow = Integer.parseInt(studentList[0][7]);
                time = LocalTime.parse(studentList[0][6]);
                time = time.plusMinutes(timeWindow);
                System.out.println("FROM THE ELSE " + decodedText );// to be removed

                for(int i = 0; i < studentList.length; i++)
                {
                    if (studentList[i][11].equals(decodedText) )
                    {
                        System.out.println("FROM THE for found the student" + studentList[i][1] + " " + studentList[i][2]);// to be removed
                        if(LocalTime.now().isBefore(time))
                        {
                            studentList[i][9] = ""; //REGISTER STATUS AS PRESENT FOR STUDENTS ON TIME WITH ""
                        }
                        else
                            studentList[i][9] = "1";//REGISTER STATUS AS PRESENT BUT TARDAY WITH "1"

                        studentList[i][8] = date;//REGISTER date
                        studentList[i][10] = timeNow;//REGISTER time
                        result[0] = "1";
                        result[1] = "Welcome " + studentList[i][1] + " " + studentList[i][2] ;
                        System.out.println(studentList[i][0]+ "    "+ studentList[i][1]+ "    "+ studentList[i][2]+ "    "+ studentList[i][3]+ "    "+ studentList[i][4]+ "    "+ studentList[i][5]+ "    "+ studentList[i][6]+ "    "+ studentList[i][9]+"    "+ studentList[i][8] +"    "+ studentList[i][10] );

                    }/*
                    else
                    {
                        result[0] = "1";
                        result[1] = "This QRcode is not for this session. Please use the correct QRCode";
                    }*/
                }

            }

        }


        return result;
    }

    void updateStudentDatabase(){
        if (studentList == null)
        {
            System.out.println("The list is empty now"); // THIS IS TO ENSURE THAT WHEN IT BEGINS THE STUDENT LIST IS EMPTY
        }
        else
        {

            //MongoClient mongoClient = new MongoClient("localhost", 9010);
            MongoClient mongoClient = new MongoClient(new MongoClientURI("mongodb://George:george1@ds159563.mlab.com:59563/project"));//CONNECTING TO THE MONGO DB SERVER
            System.out.println("Server connection succesfully done");
            DB db = mongoClient.getDB("project");//ACCESSING THE DATABASE project
            DBCollection collstudent = db.getCollection("Student");//ACCESSING THE COLLECTION Courses
            BasicDBObject query = new BasicDBObject();//DECLARING THE QUERY
            //complete this function that will search for the students and update their date and time for attendence
            for(int i = 0; i < studentList.length; i++)
            {

                System.out.println("FROM UPDATE  "+studentList[i][0]+ "    "+ studentList[i][1]+ "    "+ studentList[i][2]+ "    "+ studentList[i][3]+ "    "+ studentList[i][4]+ "    "+ studentList[i][5]+ "    "+ studentList[i][6]+ "    "+ studentList[i][8]+"    "+ studentList[i][9] +"    "+ studentList[i][10] );

                query = new BasicDBObject("_id", studentList[i][0]);
                DBObject update = new BasicDBObject("cID_Attendence."+studentList[i][4]+".0", studentList[i][8]);
                DBObject updateQuery = new BasicDBObject("$push",update);
                collstudent.update(query,updateQuery);
                update = new BasicDBObject("cID_Attendence."+studentList[i][4]+".1", studentList[i][9]);
                updateQuery = new BasicDBObject("$push",update);
                collstudent.update(query,updateQuery);
                update = new BasicDBObject("cID_Attendence."+studentList[i][4]+".2", studentList[i][10]);
                updateQuery = new BasicDBObject("$push",update);
                collstudent.update(query,updateQuery);
            }

        }

    }

    void recipientEmails(){
        if (studentList == null)
        {
            System.out.println("The list is empty now"); // THIS IS TO ENSURE THAT WHEN IT BEGINS THE STUDENT LIST IS EMPTY
        }
        else
        {
            for(int i = 0; i < studentList.length; i++)
            {
                if(studentList[i][9].equals("0") || studentList[i][9].equals("1"))
                {
                    //late
                    // extract the recipents list along with the list of students and pass it as a 2d array
                    //MongoClient mongoClient = new MongoClient("localhost", 9010);
                    MongoClient mongoClient = new MongoClient(new MongoClientURI("mongodb://George:george1@ds159563.mlab.com:59563/project"));
                    System.out.println("Server connection succesfully done");
                    DB db = mongoClient.getDB("project");
                    DBCollection collection1 = db.getCollection("Student");//ACCESSING THE COLLECTION Courses
                    DBObject query = new BasicDBObject("_id", studentList[i][0]);
                    DBCursor c = collection1.find(query);// THE QUERY IS USED HERE TO FIND ALL THE DOCUMENT THAT HAVE THE roomNO AND SETTING THE CURSOR TO THAT PARTICULAR DOCUMENT
                    String rec[] = new String[12];// ARRAY TO STORE THE EMAILS
                    String recp;// THE VALUES OF THE RECEIPIENT ARRAY WILL BE STORED IN THIS VARIABLE
                    while (c.hasNext())// BELOW ALL THE EMAIL ADDRESSES WILL BE EXTRACTED AND STORED IN THE RECP ARRAY
                    {
                        DBObject obj1 = c.next();
                        recp = obj1.get("recepient").toString();// EXTRACT THE RECEPIENT LIST
                        rec[0] = studentList[i][12];
                        int startIndex, endIndex, cnt = 1;
                        System.out.println(recp);
                        startIndex = recp.indexOf("\"");
                        System.out.println(startIndex);
                        endIndex = recp.indexOf("\"",(startIndex+1));


                        while (recp.indexOf("\"",(endIndex+1))!= -1)
                        {
                            startIndex = recp.indexOf("\"",(endIndex+1));
                            endIndex = recp.indexOf("\"",(startIndex+1));
                            rec[cnt] = recp.substring(startIndex+1,endIndex);
                            if(recp.indexOf("\"",(endIndex+1))== -1)
                                break;

                            startIndex = recp.indexOf("\"",(endIndex+1));
                            endIndex = recp.indexOf("\"",(startIndex+1));

                            cnt = cnt+1;
                        }

                    }
                    // send email
                    String message = "";
                    if(studentList[i][9].equals("1"))
                    {
                        message = "This is to inform you that " + studentList[i][1] + " " + studentList[i][2] + " " + " was late to class " + studentList[i][4] + ", at "+ studentList[i][6]+". " + studentList[i][1] + " came to class at " + studentList[i][10] + " on " + studentList[i][8] + ". Thus has been marked Tardy "; //COME UP WITH A MESSAGE FOR THE LATE STUDENTS

                    }
                    else if (studentList[i][9].equals("0"))
                    {
                        message = "This is to inform you that " + studentList[i][1] + " " + studentList[i][2] + " " + " was absent at class " + studentList[i][4] + ", at "+ studentList[i][6] + " on " + studentList[i][8] + ".  Thus has been marked absent "; //COME UP WITH A MESSAGE FOR THE LATE STUDENTS
                    }

                    try{
                        email.generateAndSendEmail(studentList[i][3],message,null,rec);// NULL IS ADDED AS THE FUNCTION REQUIRES A STRING ARRAY
                    }
                    catch(MessagingException e){
                        System.out.println("Could not Send the Message :: " + e.getMessage());
                    }
                }
            }
            studentList = null;
        }
        for(int i = 0; i < studentList.length; i++)
        {
            System.out.println(studentList[i][0]+ "    "+ studentList[i][1]+ "    "+ studentList[i][2]+ "    "+ studentList[i][3]+ "    "+ studentList[i][4]+ "    "+ studentList[i][5]+ "    "+ studentList[i][6]+ "    "+ studentList[i][7]+"    "+ studentList[i][8] +"    "+ studentList[i][11] );
        }
        studentList = null;

    }
}

 /* will come before the return statement
 THIS IS FOR AN UPGRADED VERSION THAT WILL CHECK IF THE QR CODE INFO IF THE CLASS INFO IS CORRECT AND IF NOT REDIRECT THE STUDENT
        String decodedStudentlist[] = new String[10];//THIS IS JUST A SAMPLE TEXT TO SEE IF I CAN ACCESS THE ARRAY
        int startIndex, endIndex, i =1;
        //CHECK THE INFORMATION AGAINST THE LIST IF THE (1)ROOM NUMBER (2) DATE (3) TIME (4) STUDENT ID ELSE DISPLAY THE QR CODE CONTENT AND ASK TO RESCAN
        // I ALSO HAVE TO CHECK THE CODE BELOW BECUASE THERE IS CHANGING TO STRING ARRAY WHEN THE CODE IS DECODED
        startIndex = decodedText.indexOf(";");
        endIndex = decodedText.indexOf(";",startIndex+1);
        decodedStudentlist[0] = decodedText.substring(startIndex+1,endIndex);
        while(decodedText.indexOf(";", endIndex)!= 0)
        {
            startIndex = decodedText.indexOf(";", endIndex+1);
            endIndex = decodedText.indexOf(";",startIndex+1);
            decodedStudentlist[i] = decodedText.substring(startIndex+1,endIndex);
            i += 1;
        }
        for( i = 0; i < decodedStudentlist.length; i++)
        {
            System.out.println(decodedStudentlist[i]);
        }*/
// EACH SCAN UPDATE THE LOCAL ARRAY AND AT THE END OF CLASS THE TIME ACTION HAS TO RUN HERE OR ON THE MAIN AND UPDATE THE MONGODB DATA BASE TO DO THE UPDATE
//SEND THE INFO WHICH WILL BE AN ARRAY OF INFO THAT CAN BE USED TO UPDATE THE DOCUMENT
//CHECK THE TIME HERE TO DO THE UPDATE

//CHANGE THE DEOCDED TEXT TO STRING ARRAY

//CHECK IF THE  CID AND TIME MATCH

//WRONG CLASS ROOM AND WHAT IS THE CLASS ROOM
//WRONG COURSE AND WHAT IS THE CORRECT COURSE
//WRONG QR CODE SO SEND THE DECODED TEXT AS IT IS TO DISPLAY

//IF CORRECT CHECK THE SID AND THE TIME
//IF TIME IS BEFORE THE TIME PERIOD THEN THE TIME COLUMN SHOULD HAVE ONE  PASS MESSAGE PRESENT
//IF TIME IS AFTER THE TIME PERIOD THEN CALL THEN UPDATE THE TIME PASS MESSAGE PRESENT BUT LATE
