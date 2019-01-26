package com.george.softwareenginnering;



import java.awt.image.BufferedImage;
import org.opencv.core.Mat;
import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.*;
import java.awt.Graphics2D;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.*;
import java.awt.event.*;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import javax.swing.border.TitledBorder;


public class Main extends JPanel
{
    private int i = 0;
    //private Timer timer;

    private QRcodeverifier verify = new QRcodeverifier();
    public ExtractStudentList extractStudentList = new ExtractStudentList();
    public ExtractStudentInfo extractStudentInfo = new ExtractStudentInfo();
    private Capture cam = new Capture();
    private JButton b1 = new JButton("START");
    private JButton b2 = new JButton("CLOSE");
    private JLabel image = new JLabel();
    private JLabel info = new JLabel("Welcome to the ");// THIS LABLE WILL CARRY ALL THE INFO THAT NEEDS TO BE DISPLAYED ON THE SCREEN
    private JLabel info2 = new JLabel("Automated Attendence System ");// THIS LABLE WILL CARRY ALL THE INFO THAT NEEDS TO BE DISPLAYED ON THE SCREEN
    private JLabel mesg = new JLabel("");
    private JLabel name = new JLabel("");
    private static String QR_CODE_IMAGE_PATH = "C:\\AUTOMATEDATTENDENCESYSTEM\\sanpshot1.png";

    //private int i = 0; //counter for refreshing images due to system limitation
    final BufferedImage backgroundImage = new BufferedImage(
            200, 200, BufferedImage.TYPE_INT_RGB);


    String decode;
    Mat matrix = null;



    private ActionListener action1 = new ActionListener()
    {
        public void actionPerformed(ActionEvent ae)
        {
            String[] result;
            i++;
            DateTimeFormatter ftime = DateTimeFormatter.ofPattern("HH:mm");
            //info.setText("POSITION THE BAR CODE 5 CM FROM THE CAMERA AND CLICK SCAN");
            cam.capureSnapShot();
            cam.saveImage(i);
            //modification 11/9/18 8.09AM
            //THE EXPRESSION WILL PULL THAT SAVED IMAGE AND DISPLAY ON THE SCREEN
            image.setIcon(new javax.swing.ImageIcon("C:\\AUTOMATEDATTENDENCESYSTEM\\sanpshot" + i + ".png"));

            System.out.println(i);
            b1.setText("SCAN");
            decode = verify.check("C:\\AUTOMATEDATTENDENCESYSTEM\\sanpshot" + i + ".png");//LOCATION WHERE PICTURE WILL BE SAVED
            System.out.println(decode);
            result = ExtractStudentList.checkDecodedQrText(decode);

            //MESSAGE DISPLAY WILL BE HERE BUT THE FUNCTION IMPLEMENTATION WILL BE IN EXTRACTSTUDENTLIST
            revalidate();
            repaint();

            info.setText("");
            info2.setText("");

            System.out.println(result[0] + "    " + result[1]);

            if(result[0].equals("0"))
                info.setText("");//THIS IS WHERE WE WILL ENTER THE MONGODB UPDATE
            else {
                info.setText(result[1]);
                info2.setText("The time is " + LocalTime.now().format(ftime));
            }
            mesg.setText("Keep the QR code 5 inches from the camera");


            //name.setText(decode);
            //CALL THE FUNCTION checkDecodedQrText FROM EXTRACT STUDENT LIST AND RETURN A MESSAGE

            //DECODE THE QR CODE CONTENT
            //SEND THE TEXT TO ExtractStudentList class WHERE WE CHECK IT WITH THE PRESENT CLASS TABLE AND RETURN A TEXT THAT HAS TO BE DISPLAYED
            //CHECK THE INFORMATION AGAINST THE LIST IF THE (1)ROOM NUMBER (2) DATE (3) TIME (4) STUDENT ID ELSE DISPLAY THE QR CODE CONTENT AND ASK TO RESCAN
            // EACH SCAN UPDATE THE LOCAL ARRAY


            // removed from here 1

            revalidate();
            repaint();
        }
    };

    private ActionListener action2 = new ActionListener()
    {
        public void actionPerformed(ActionEvent ae)
        {
            System.exit(0);// THIS IS A COMMAND TO TERMINATE A PROGRAM

        }
    };

    //COLORING
    protected void paintComponent( Graphics g ) {
        Graphics2D g2d = (Graphics2D) g;
        int w = getWidth();
        int h = getHeight();
        Color color1 = Color.LIGHT_GRAY;
        Color color2 = Color.YELLOW;
        GradientPaint gp = new GradientPaint(0, 0, color1, 0, h, color2, true);
        g2d.setPaint(gp);
        g2d.fillRect(0, 5, w, h);
    }

    public Main()
    {

        setLayout(new GridBagLayout());


        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.anchor = GridBagConstraints.NORTH;
        gbc.anchor = GridBagConstraints.CENTER;
        //gbc.fill = GridBagConstraints.HORIZONTAL;
        GridBagConstraints gbc2 =gbc;


        info.setFont(new Font("Open Sans", Font.PLAIN, 50));
        info.setForeground(Color.DARK_GRAY);
        info.setHorizontalAlignment(SwingConstants.CENTER);
        info2.setFont(new Font("Open Sans", Font.PLAIN, 50));
        info2.setForeground(Color.DARK_GRAY);
        info2.setHorizontalAlignment(SwingConstants.CENTER);
        mesg.setFont(new Font("Open Sans", Font.PLAIN, 50));
        mesg.setForeground(Color.BLACK);
        mesg.setHorizontalAlignment(SwingConstants.CENTER);
        name.setFont(new Font("Open Sans", Font.PLAIN, 50));
        name.setForeground(Color.BLACK);
        name.setHorizontalAlignment(SwingConstants.CENTER);


        b1.addActionListener(action1);
        b2.addActionListener(action2);
        b1.setFont(new Font("Open Sans", Font.PLAIN, 50));
        b2.setFont(new Font("Open Sans", Font.PLAIN, 50));
        //info.setLayout(new Lay);


        add(image, gbc);

        //add(b2, gbc);
        add(info,gbc2);
        add(info2,gbc2);
        add(name,gbc2);
        add(mesg,gbc);
        add(b1, gbc);
        gbc.weighty = 1;

        setVisible(true);

    }


    public static void main(String... args)
    {

        //PULL THE STUDENT LIST FROM DATABASE AND STORE INFORMATION IN LOCAL ARRAY
        /*
        ExtractStudentInfo extractStudentInfo = new ExtractStudentInfo();
        extractStudentInfo.extract();
        */
         //THE TIMED TASK WILL BE USED INSTEAD OF THE TWO STATEMENTS ABOVE

/* CHANGING
        java.util.Timer timer = new java.util.Timer();
        TimerTask task = new TimedTasks();
        //REFRESH TIME HAS TO BE READ FROM FILE
        String path = "C:\\Users\\George\\Documents\\software engineering\\images\\Timer.txt";

        //public static String roomNo = "108";//THIS ROOM NUMBER SHOULD BE LINKED TO THE ROOM NO IN EXTRACTSTUDENTINFO CLASS  A TEXT FILE WILL CARRY THIS INFO.
        ReadFile readFile = new ReadFile();
        String timerString = readFile.readFile(path);
        int timeCounter = Integer.parseInt(timerString);
        timer.schedule(task,100,(timeCounter * 60000));//SET SCHEDULE BASED ON THE SCHOOL REQUIRNMENTS USUALLY CLASSES ARE 1 HOUR LONG
*/
        //GENERATE QR CODE FOR EACH STUDENT -(ID, ROOMNUMBER, DATE, TIME, RANDNUMBER)
        //EMAIL QR CODES TO ALL STUDENTS
        SwingUtilities.invokeLater(new Runnable()
        {
            public void run(){
                JFrame frame = new JFrame();
                Main panel = new Main();
                frame.add(panel);
                frame.setSize(600,500);
                frame.setLocationRelativeTo(null);
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.setVisible(true);
            }
        });
        //END OF CLASS UPDATE THE MONGO DB STUDENT COLLECTION ONLY IF STUDENT IS ABSENT OR LATE
        //HERE WE PUT THE INFINITE WHILE LOOP THAT WILL CONSTANTLY UPDATE THE STUDENT LIST BY PULLING DATA FROM THE DATABASE AND SEND EMAILS AND UPDATE THE ATTENEDENCE FOR EACH STUDENT
        // THE REFRESH RATE OR DELAY TIME SINCE WE WILL SUE SLEEP FUNCTION WILL BE DETERMINED BY THE CLASS DURATION, THE TIME GAP BETWEEN CLASSES, THE TIME TO EMAIL THE STUDENTS SO
        // THE STUDENTS CAN CHECK EMAILS BEFORE CLASS AND DOWNLOAD THE QR CODE.
        //TIME GAP AND EMAIL TIME SHOULD BE SET IN THE TEXT FILE
        //REFRESH TIME = TIME GAP IN THE BEGENNING
        //REFRESHTIME GAP IS THE LENSE TO SEARCH FO THE TIMEWINDOW BETWEEN CLASSTIME AND THE PRESENT TIME
        int refreshTime, emailTime, refreshTimeGap;

        //REFRESH TIME HAS TO BE READ FROM FILE
        String path = "C:\\AUTOMATEDATTENDENCESYSTEM\\EmailTime.txt";

        ReadFile readFile = new ReadFile();
        String infoFromTextFile = readFile.readFile(path);
        refreshTimeGap = Integer.parseInt(infoFromTextFile);
        refreshTime = refreshTimeGap;

        // READ EMAIL TIME FROM FILE
        emailTime = refreshTime;
        String classInfo[] = {"0","0"};
        ExtractStudentList extractStudentList = new ExtractStudentList();
        ExtractStudentInfo extractStudentInfo = new ExtractStudentInfo();

        while (true){
            int duration = 0,  addTime;
            String classtime;
            classInfo = extractStudentInfo.extract();
            duration = Integer.parseInt(classInfo[0]);
            classtime = classInfo[1];
            System.out.println("THE DURATION IS : - " + duration + "  THE CLASS TIME IS  " + classtime);

            if(duration > 0 && refreshTime < duration)//IF THE CONDITION IS SATISFIED CLASS WAS EXTRACTED
            {
                long diffInMinutes = java.time.Duration.between( LocalTime.now(),LocalTime.parse(classtime)).toMinutes();
                addTime = (int)diffInMinutes;
                refreshTime = duration + addTime;
                System.out.println("ADDTIME : - "+ addTime + " duration : - "+ duration + " refreshtime " + refreshTime);
            }
            try {
                System.out.println("started running...");
                Thread.sleep(refreshTime*60000);
                System.out.println("THE NEW REFRESH TIME : -"+refreshTime + "THE LOCAL TIME IS "+ LocalTime.now().toString());
            } catch (InterruptedException e) {
            }
            extractStudentList.updateStudentDatabase();
            extractStudentList.recipientEmails();
            refreshTime = refreshTimeGap;
            System.out.println("THE NEW REFRESH TIME : -"+refreshTime + "THE LOCAL TIME IS "+ LocalTime.now().toString());
        }
    }
}




