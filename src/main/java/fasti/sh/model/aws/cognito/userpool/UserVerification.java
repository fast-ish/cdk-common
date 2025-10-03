package fasti.sh.model.aws.cognito.userpool;

/**
 * Configuration record for Cognito User Pool user verification settings.
 *
 * <p>
 * Defines user verification message templates for email and SMS notifications during the user registration and verification process.
 *
 * @param emailBody
 *          Email verification message body template
 * @param emailStyle
 *          Email message style formatting
 * @param emailSubject
 *          Email verification subject line
 * @param smsMessage
 *          SMS verification message template
 */
public record UserVerification(
  String emailBody,
  String emailStyle,
  String emailSubject,
  String smsMessage
) {}
