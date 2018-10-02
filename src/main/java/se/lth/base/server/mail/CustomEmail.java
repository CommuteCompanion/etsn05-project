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
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Creates a SimpleEmail object based on a standard predefined EmailType
 *
 * @author Group 1 ETSN05 2018
 */
public class CustomEmail {
    private final static String TITLE = "title", PREVIEW = "preview", INTRO = "intro",
            BUTTON = "button", OUTRO = "outro", WEBSITE_LINK = "http://www.yourcommutecompanion.herokuapp.com",
            EMAIL_TEMPLATE = "email-template.html";

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
     * @param user      User to be warned
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
                break;
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

    // Helper methods to fetch HTML email template
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
        // Matches all instances of Mustache wraps
        Matcher m = Pattern.compile("\\{\\{[A-z]\\w+}}").matcher(emailTemplate);

        while (m.find()) {
            String mustache = m.group();
            String replacement = replacements.get(mustache);
            emailTemplate.replaceAll("{{" + mustache + "}}", replacement == null ? "" : replacement);
        }

        return emailTemplate;
    }

    private String getButton(String link, String text) {
        Map<String, String> buttonContent = new HashMap<>();
        buttonContent.put("buttonLink", link);
        buttonContent.put("buttonText", text);
        return parseResource(getResource("button-template.html"), buttonContent);
    }

    // TODO: Customize after user functionality has been expanded
    // ---------- STANDARD EMAILS BELOW --------------
    private String getWelcomeSubject() {
        return "Jon, Welcome to CommuteCompanion";
    }

    private String getWelcomeBody() {
        Map<String, String> content = new HashMap<>();
        content.put(TITLE, getWelcomeSubject());
        content.put(PREVIEW, "Jon, welcome to CommuteCompanion. Please visit our website to find or post trips.");

        StringBuilder intro = new StringBuilder();
        intro.append("<p>Hi Jon,</p>");
        intro.append("<p>Welcome to CommuteCompanion! Please visit our website to post or find your first trip.</p>");

        content.put(INTRO, intro.toString());
        content.put(BUTTON, getButton(WEBSITE_LINK, "Get Started Here!"));

        return parseResource(getResource(EMAIL_TEMPLATE), content);
    }

    private String getNewPassengerOnTripSubject() {
        return "New passenger request for your drive to " + driveWrap.getDrive().getStop();
    }

    private String getNewPassengerOnTripBody() {
        Map<String, String> content = new HashMap<>();
        content.put(TITLE, getNewPassengerOnTripSubject());

        return parseResource(getResource(EMAIL_TEMPLATE), content);
    }

    private String getBookingConfirmedSubject() {
        return "Your booking request is confirmed";
    }

    private String getBookingConfirmedBody() {
        Map<String, String> content = new HashMap<>();
        content.put(TITLE, getBookingConfirmedSubject());

        return parseResource(getResource(EMAIL_TEMPLATE), content);
    }

    private String getRatingSubject() {
        return "Please rate your latest trip";
    }

    private String getRatingBody() {
        Map<String, String> content = new HashMap<>();
        content.put(TITLE, getRatingSubject());

        return parseResource(getResource(EMAIL_TEMPLATE), content);
    }

    private String getFilterMatchSubject() {
        return "A new trip has matched your filter";
    }

    private String getFilterMatchBody() {
        Map<String, String> content = new HashMap<>();
        content.put(TITLE, getFilterMatchSubject());

        return parseResource(getResource(EMAIL_TEMPLATE), content);
    }

    private String getPassengerCancelledTripSubject() {
        return "A passenger has cancelled their trip";
    }

    private String getPassengerCancelledTripBody() {
        Map<String, String> content = new HashMap<>();
        content.put(TITLE, getPassengerCancelledTripSubject());

        return parseResource(getResource(EMAIL_TEMPLATE), content);
    }

    private String getDriverCancelledDriveSubject() {
        return "Your trip has been cancelled";
    }

    private String getDriverCancelledDriveBody() {
        Map<String, String> content = new HashMap<>();
        content.put(TITLE, getDriverCancelledDriveSubject());

        return parseResource(getResource(EMAIL_TEMPLATE), content);
    }

    private String getWarningSubject() {
        return "A warning has been issued";
    }

    private String getWarningBody() {
        Map<String, String> content = new HashMap<>();
        content.put(TITLE, getWarningSubject());

        return parseResource(getResource(EMAIL_TEMPLATE), content);
    }

    private String getDriverRemovedPassengerSubject() {
        return "You have been removed from your trip";
    }

    private String getDriverRemovedPassengerBody() {
        Map<String, String> content = new HashMap<>();
        content.put(TITLE, getDriverRemovedPassengerSubject());

        return parseResource(getResource(EMAIL_TEMPLATE), content);
    }
}
