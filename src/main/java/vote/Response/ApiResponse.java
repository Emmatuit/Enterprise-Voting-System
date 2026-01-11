package vote.Response;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {
	public static class Builder<T> {
		private LocalDateTime timestamp = LocalDateTime.now();
		private int status = HttpStatus.OK.value();
		private String message;
		private T data;
		private ErrorDetail error;

		public ApiResponse<T> build() {
			return new ApiResponse<>(this);
		}

		public Builder<T> data(T data) {
			this.data = data;
			return this;
		}

		public Builder<T> error(ErrorDetail error) {
			this.error = error;
			return this;
		}

		public Builder<T> message(String message) {
			this.message = message;
			return this;
		}

		public Builder<T> status(int status) {
			this.status = status;
			return this;
		}

		public Builder<T> timestamp(LocalDateTime timestamp) {
			this.timestamp = timestamp;
			return this;
		}
	}
	// ErrorDetail inner class
	public static class ErrorDetail {
		public static class Builder {
			private String code;
			private String description;
			private Object details;

			public ErrorDetail build() {
				return new ErrorDetail(code, description, details);
			}

			public Builder code(String code) {
				this.code = code;
				return this;
			}

			public Builder description(String description) {
				this.description = description;
				return this;
			}

			public Builder details(Object details) {
				this.details = details;
				return this;
			}
		}
		// Builder for ErrorDetail
		public static Builder builder() {
			return new Builder();
		}
		private String code;

		private String description;

		private Object details;

		public ErrorDetail() {
		}

		public ErrorDetail(String code, String description, Object details) {
			this.code = code;
			this.description = description;
			this.details = details;
		}

		// Getters and Setters
		public String getCode() {
			return code;
		}

		public String getDescription() {
			return description;
		}

		public Object getDetails() {
			return details;
		}

		public void setCode(String code) {
			this.code = code;
		}

		public void setDescription(String description) {
			this.description = description;
		}

		public void setDetails(Object details) {
			this.details = details;
		}
	}
	// Builder
	public static <T> Builder<T> builder() {
		return new Builder<>();
	}
	public static <T> ApiResponse<T> error(String message, ErrorDetail error) {
		return ApiResponse.<T>builder().status(HttpStatus.BAD_REQUEST.value()).message(message).error(error).build();
	}
	// Static factory methods
	public static <T> ApiResponse<T> success(T data) {
		return ApiResponse.<T>builder().data(data).message("Success").build();
	}

	public static <T> ApiResponse<T> success(T data, String message) {
		return ApiResponse.<T>builder().data(data).message(message).build();
	}

	private LocalDateTime timestamp;

	private int status;

	private String message;

	private T data;

	private ErrorDetail error;

	// Private constructor
	private ApiResponse(Builder<T> builder) {
		this.timestamp = builder.timestamp;
		this.status = builder.status;
		this.message = builder.message;
		this.data = builder.data;
		this.error = builder.error;
	}

	public T getData() {
		return data;
	}

	public ErrorDetail getError() {
		return error;
	}

	public String getMessage() {
		return message;
	}

	public int getStatus() {
		return status;
	}

	// Getters
	public LocalDateTime getTimestamp() {
		return timestamp;
	}
}