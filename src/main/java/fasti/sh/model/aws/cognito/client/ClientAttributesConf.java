package fasti.sh.model.aws.cognito.client;

/**
 * Configuration record for Cognito User Pool client attribute settings.
 *
 * <p>
 * Defines which user attributes the client can read or write, controlling access permissions for user profile data.
 *
 * @param address
 *          Whether address attribute is accessible
 * @param birthdate
 *          Whether birthdate attribute is accessible
 * @param email
 *          Whether email attribute is accessible
 * @param emailVerified
 *          Whether email verification status is accessible
 * @param familyName
 *          Whether family name attribute is accessible
 * @param fullname
 *          Whether full name attribute is accessible
 * @param gender
 *          Whether gender attribute is accessible
 * @param givenName
 *          Whether given name attribute is accessible
 * @param lastUpdateTime
 *          Whether last update timestamp is accessible
 * @param locale
 *          Whether locale attribute is accessible
 * @param middleName
 *          Whether middle name attribute is accessible
 * @param nickname
 *          Whether nickname attribute is accessible
 * @param phoneNumber
 *          Whether phone number attribute is accessible
 * @param phoneNumberVerified
 *          Whether phone verification status is accessible
 * @param preferredUsername
 *          Whether preferred username is accessible
 * @param profilePage
 *          Whether profile page URL is accessible
 * @param profilePicture
 *          Whether profile picture URL is accessible
 * @param timezone
 *          Whether timezone attribute is accessible
 * @param website
 *          Whether website URL attribute is accessible
 */
public record ClientAttributesConf(
  boolean address,
  boolean birthdate,
  boolean email,
  boolean email_verified,
  boolean family_name,
  boolean name,
  boolean gender,
  boolean given_name,
  boolean updated_at,
  boolean locale,
  boolean middle_name,
  boolean nickname,
  boolean phone_number,
  boolean phone_number_verified,
  boolean preferred_username,
  boolean profile_page,
  boolean profile_picture,
  boolean timezone,
  boolean website
) {}
