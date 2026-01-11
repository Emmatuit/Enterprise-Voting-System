package vote.Enum;

public enum OTPChannel {
	EMAIL, SMS, NONE;

	public static OTPChannel fromString(String value) {
		if (value == null) {
			return NONE;
		}

		try {
			return OTPChannel.valueOf(value.toUpperCase());
		} catch (IllegalArgumentException e) {
			return NONE;
		}
	}

	public boolean isEmail() {
		return this == EMAIL;
	}

	public boolean isNone() {
		return this == NONE;
	}

	public boolean isSms() {
		return this == SMS;
	}
}