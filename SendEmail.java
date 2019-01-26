package com.george.softwareenginnering;
//THIS CLASS TAKES CARE OF EMAIL ANYONE IT IS SENT THE CONTENT AND THE EMAIL ADDRESS

import java.util.Properties;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.*;
import javax.mail.*;
import javax.activation.*;

public class SendEmail {
    static Properties mailServerProperties;
    static Session getMailSession;
    static MimeMessage generateMailMessage;

    public static void main(String args[]) throws AddressException, MessagingException {
        //generateAndSendEmail();
        System.out.println("\n\n ===> Your Java Program has just sent an Email successfully. Check your email..");
    }

    static void generateAndSendEmail(String receiver, String message, String path, String[] recipents ) throws AddressException, MessagingException {

        // Step1
        System.out.println(message +  "\n"+ receiver);
        System.out.println("\n 1st ===> setup Mail Server Properties..");
        mailServerProperties = System.getProperties();
        mailServerProperties.put("mail.smtp.port", "587");
        mailServerProperties.put("mail.smtp.auth", "true");
        mailServerProperties.put("mail.smtp.starttls.enable", "true");
        System.out.println("Mail Server Properties have been setup successfully..");

        // Step2
        System.out.println("\n\n 2nd ===> get Mail Session..");
        getMailSession = Session.getDefaultInstance(mailServerProperties, null);
        generateMailMessage = new MimeMessage(getMailSession);
        generateMailMessage.addRecipient(Message.RecipientType.TO, new InternetAddress(receiver));
        generateMailMessage.addRecipient(Message.RecipientType.CC, new InternetAddress("mettin60@yahoo.com"));
        if (recipents != null)
        {
            for(int i =0; i<recipents.length; i++)
            {
                if (recipents[i] != null)
                {
                    generateMailMessage.addRecipient(Message.RecipientType.CC, new InternetAddress(recipents[i]));
                }

            }
        }


        generateMailMessage.setSubject("Greetings from ATTENDENCE MANAGMENT SYSTEM.");
        //String emailBody = "Please check your meail for the QRCode attached and use it for signing in to class. " + "<br><br> Regards, <br>Crunchify Admin";
        String emailBody = message;


        // MY ADDITION
        //String filename ="C:/Users/George/Documents/software engineering/project/sanpshot1.png";
        String filename = path;
        // Create the message part
        BodyPart messageBodyPart = new MimeBodyPart();

        // Now set the actual message
        messageBodyPart.setText(emailBody);

        // Create a multipar message
        Multipart multipart = new MimeMultipart();

        // Set text message part
        multipart.addBodyPart(messageBodyPart);

        // Part two is attachment
        messageBodyPart = new MimeBodyPart();
        if (filename != null)
        {
            DataSource source = new FileDataSource(filename);
            messageBodyPart.setDataHandler(new DataHandler(source));
            messageBodyPart.setFileName(filename);
            multipart.addBodyPart(messageBodyPart);

        }



        //

        generateMailMessage.setContent(multipart);

        System.out.println("Mail Session has been created successfully..");


        // Step3
        System.out.println("\n\n 3rd ===> Get Session and Send mail");
        Transport transport = getMailSession.getTransport("smtp");

        // Enter your correct gmail UserID and Password
        // if you have 2FA enabled then provide App Specific Password
        transport.connect("smtp.gmail.com", "USE", "TYPE YOU PASSWPRD");//("smtp.gmail.com", "USERNAM", "PASSWORD") for your email
        //transport.connect("smtp.gmail.com", "EMAILADDRESS", "PASSWORD");
        transport.sendMessage(generateMailMessage, generateMailMessage.getAllRecipients());
        transport.close();
        System.out.println("\n\n ===> Your Java Program has just sent an Email successfully. Check your email..");
    }


}
