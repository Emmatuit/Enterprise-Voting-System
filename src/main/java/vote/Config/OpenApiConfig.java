package vote.Config;


import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;

@Configuration
@OpenAPIDefinition(
    info = @Info(
        title = "Enterprise Voting System API",
        version = "1.0.0",
        description = "Policy-Driven, Multi-Tenant Voting Platform",
        contact = @Contact(
            name = "Enterprise Voting Team",
            email = "support@enterprise-voting.com"
        ),
        license = @License(
            name = "Proprietary",
            url = "https://enterprise-voting.com/license"
        )
    ),
    servers = {
        @Server(url = "http://localhost:8080/api", description = "Local Development Server"),
        @Server(url = "https://api.enterprise-voting.com", description = "Production Server")
    }
)
@SecurityScheme(
    name = "bearerAuth",
    type = SecuritySchemeType.HTTP,
    scheme = "bearer",
    bearerFormat = "JWT"
)
public class OpenApiConfig {
    // OpenAPI configuration is done via annotations
}