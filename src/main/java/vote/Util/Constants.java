package vote.Util;

public class Constants {

	// Regex Patterns
	public static final String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@(.+)$";

	public static final String PHONE_REGEX = "^\\+?[1-9]\\d{1,14}$";
	public static final String MATRIC_NUMBER_REGEX = "^[A-Za-z0-9/\\-]+$";
	// CSV Headers
	public static final String[] VOTER_REGISTRY_HEADERS = { "matric_number", "email", "phone", "full_name" };

	// File Upload
	public static final long MAX_FILE_SIZE = 10 * 1024 * 1024; // 10MB

	public static final String[] ALLOWED_FILE_TYPES = { "text/csv", "application/vnd.ms-excel",
			"application/vnd.openxmlformats-officedocument.spreadsheetml.sheet" };
	// JWT
	public static final String JWT_TOKEN_PREFIX = "Bearer ";

	public static final String JWT_HEADER = "Authorization";
	// Cache Names
	public static final String ORGANIZATION_CACHE = "organization";

	public static final String IDENTITY_POLICY_CACHE = "identityPolicy";
	public static final String ELECTION_CACHE = "election";
	// Validation Messages
	public static final String FIELD_REQUIRED = "This field is required";

	public static final String INVALID_EMAIL = "Invalid email format";
	public static final String INVALID_PHONE = "Invalid phone number format";
	public static final String INVALID_MATRIC_NUMBER = "Invalid matric number format";
	private Constants() {
		// Utility class
	}
}