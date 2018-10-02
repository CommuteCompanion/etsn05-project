package se.lth.base.server.mail;

import org.simplejavamail.email.Email;
import org.simplejavamail.email.EmailBuilder;
import org.simplejavamail.email.Recipient;
import org.slf4j.LoggerFactory;
import se.lth.base.server.data.DriveWrap;
import se.lth.base.server.data.User;

import javax.mail.Message;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Creates a SimpleEmail object based on a standard predefined EmailType
 *
 * @author Group 1 ETSN05 2018
 */
public class CustomEmail {
    private final DriveWrap driveWrap;
    private final EmailType emailType;
    private List<Recipient> recipients;

    /**
     * Basic custom email, handles most scenarios
     *
     * @param driveWrap DriveWrap DTO
     * @param emailType EmailType defined by se.lth.base.server.mail.EmailType
     */
    public CustomEmail(DriveWrap driveWrap, EmailType emailType) {
        this(driveWrap, emailType, null);
    }

    /**
     * Used to send non drive related emails
     *
     * @param user User to be warned
     * @param emailType EmailType defined by se.lth.base.server.mail.EmailType
     */
    public CustomEmail(User user, EmailType emailType) {
        this(null, emailType, user);
    }

    // TODO: Additional constructor to handle search filter match and adjust master constructor
    public CustomEmail(DriveWrap driveWrap, EmailType emailType, User user) {
        this.driveWrap = driveWrap;
        this.emailType = emailType;
        this.recipients = new ArrayList<>();

        if (user != null) {
            // TODO: Add user attributes when implemented
            recipients.add(new Recipient("name", "email", Message.RecipientType.TO));
        }
    }


    public Email getEmail() {
        Map<String, String> subjectAndBody = getSubjectAndBody();

        return EmailBuilder.startingBlank()
                .from(MailHandler.DEFAULT_SENDER)
                .to(recipients)
                .withSubject(subjectAndBody.get("subject"))
                .withHTMLText(subjectAndBody.get("body"))
                .withPlainText("Please view this email in a modern email client!")
                .buildEmail();
    }

    public Map<String, String> getSubjectAndBody() {
        String subject = "", body = "";
        Map<String, String> subjectAndBody = new HashMap<>();

        switch (emailType) {
            case WELCOME: {
                subject = getWelcomeSubject();
                body = getWelcomeBody();
                break;
            }
            case NEW_PASSENGER_ON_TRIP: {
                subject = getNewPassengerOnTripSubject();
                body = getNewPassengerOnTripBody();
            }
            case BOOKING_CONFIRMED: {
                subject = getBookingConfirmedSubject();
                body = getBookingConfirmedBody();
                break;
            }
            case RATING: {
                subject = getRatingSubject();
                body = getRatingBody();
                break;
            }
            case FILTER_MATCH: {
                subject = getFilterMatchSubject();
                body = getFilterMatchBody();
                break;
            }
            case PASSENGER_CANCELLED_TRIP: {
                subject = getPassengerCancelledTripSubject();
                body = getPassengerCancelledTripBody();
                break;
            }
            case DRIVER_CANCELLED_DRIVE: {
                subject = getDriverCancelledDriveSubject();
                body = getDriverCancelledDriveBody();
                break;
            }
            case WARNING: {
                subject = getWarningSubject();
                body = getWarningBody();
                break;
            }
            case DRIVER_REMOVED_PASSENGER: {
                subject = getDriverRemovedPassengerSubject();
                body = getDriverRemovedPassengerBody();
                break;
            }
        }

        subjectAndBody.put("subject", subject);
        subjectAndBody.put("body", body);
        return subjectAndBody;
    }

    // Helper methods fo tech HTML email template
    private String getResource(String resourceName) {
        InputStream inputStream = CustomEmail.class.getResourceAsStream(resourceName);
        InputStreamReader streamReader = new InputStreamReader(inputStream);


        try (BufferedReader br = new BufferedReader(streamReader)) {
            return br.lines().collect(Collectors.joining(System.lineSeparator()));
        } catch (IOException e) {
            LoggerFactory.getLogger(CustomEmail.class).error(e.getMessage(), e);
        }

        return "Error: could not read email template";
    }

    private String parseResource(String emailTemplate, Map<String, String> replacements) {
        for (Map.Entry<String, String> e : replacements.entrySet()) {
            emailTemplate = emailTemplate.replaceAll("{{" + e.getKey() + "}}", e.getValue());
        }

        return emailTemplate;
    }

    private String getButton(String link, String text) {
        Map<String, String> buttonReplacements = new HashMap<>();
        buttonReplacements.put("buttonLink", link);
        buttonReplacements.put("buttonText", text);
        return parseResource(getResource("button-template.html"), buttonReplacements);
    }

    // ---------- STANDARD EMAILS BELOW --------------
    private String getWelcomeSubject() {
        return "";
    }

    private String getWelcomeBody() {
        return "";
    }

    private String getNewPassengerOnTripSubject() {
        return "";
    }

    private String getNewPassengerOnTripBody() {
        return "";
    }

    private String getBookingConfirmedSubject() {
        return "";
    }

    private String getBookingConfirmedBody() {
        return "";
    }

    private String getRatingSubject() {
        return "";
    }

    private String getRatingBody() {
        return "";
    }

    private String getFilterMatchSubject() {
        return "";
    }

    private String getFilterMatchBody() {
        return "";
    }

    private String getPassengerCancelledTripSubject() {
        return "";
    }

    private String getPassengerCancelledTripBody() {
        return "";
    }

    private String getDriverCancelledDriveSubject() {
        return "";
    }

    private String getDriverCancelledDriveBody() {
        return "";
    }

    private String getWarningSubject() {
        return "";
    }

    private String getWarningBody() {
        return "";
    }

    private String getDriverRemovedPassengerSubject() {
        return "";
    }

    private String getDriverRemovedPassengerBody() {
        return "";
    }
}
