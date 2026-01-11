package vote.Response;

import java.time.LocalDateTime;
import java.util.Map;

public class ErrorResponse {
	public static class Builder {
		private LocalDateTime timestamp;
		private int status;
		private String error;
		private String message;
		private Map<String, String> details;
		private String path;

		public ErrorResponse build() {
			return new ErrorResponse(this);
		}

		public Builder details(Map<String, String> details) {
			this.details = details;
			return this;
		}

		public Builder error(String error) {
			this.error = error;
			return this;
		}

		public Builder message(String message) {
			this.message = message;
			return this;
		}

		public Builder path(String path) {
			this.path = path;
			return this;
		}

		public Builder status(int status) {
			this.status = status;
			return this;
		}

		public Builder timestamp(LocalDateTime timestamp) {
			this.timestamp = timestamp;
			return this;
		}
	}
	// Builder
	public static Builder builder() {
		return new Builder();
	}
	private LocalDateTime timestamp;
	private int status;
	private String error;
	private String message;

	private Map<String, String> details;

	private String path;

	// Private constructor
	private ErrorResponse(Builder builder) {
		this.timestamp = builder.timestamp;
		this.status = builder.status;
		this.error = builder.error;
		this.message = builder.message;
		this.details = builder.details;
		this.path = builder.path;
	}

	public Map<String, String> getDetails() {
		return details;
	}

	public String getError() {
		return error;
	}

	public String getMessage() {
		return message;
	}

	public String getPath() {
		return path;
	}

	public int getStatus() {
		return status;
	}

	// Getters
	public LocalDateTime getTimestamp() {
		return timestamp;
	}
}