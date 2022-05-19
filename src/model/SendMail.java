package model;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

public class SendMail {

    private final String userEmail;

    public SendMail(String Email) {
        this.userEmail = Email;
    }

    public void Send(String code) {

        Properties properties = new Properties();

        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.ssl.enable", "true");
        properties.put("mail.smtp.host", "smtp.gmail.com");
        properties.put("mail.smtp.port", "465");

        Session session = Session.getInstance(properties, new Authenticator() {

            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication("masoomeh.mzb@gmail.com", "187069nem");
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setSubject("Verification Email");
            message.setRecipient(Message.RecipientType.TO,new InternetAddress(userEmail));
            message.setText("Your verification code is : " + code);
            Transport.send(message);


        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }
}