package pocket.ledger.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.media.Schema;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

  @Value("${server.port:8080}")
  private String serverPort;

  @Bean
  public OpenAPI pocketLedgerOpenAPI() {
    Contact contact = new Contact();
    contact.setEmail("info@pocketledger.com");
    contact.setName("Pocket Ledger Team");

    License mitLicense =
        new License().name("MIT License").url("https://choosealicense.com/licenses/mit/");

    Info info =
        new Info()
            .title("Pocket Ledger API")
            .version("1.0.0")
            .contact(contact)
            .description(
                "A simple ledger system for managing financial transactions. "
                    + "This API provides endpoints for creating transactions, checking balances, "
                    + "and retrieving transaction history with filtering capabilities.")
            .termsOfService("https://www.pocketledger.com/terms")
            .license(mitLicense);

    Components components = new Components();

    Schema<?> errorSchema =
        new Schema<>()
            .type("object")
            .description("Standard error response")
            .addProperty("message", new Schema<>().type("string").description("Error message"))
            .addProperty("status", new Schema<>().type("integer").description("HTTP status code"))
            .addProperty(
                "timestamp",
                new Schema<>().type("string").format("date-time").description("Error timestamp"))
            .addProperty(
                "path", new Schema<>().type("string").description("Error ID or request path"))
            .addProperty(
                "validationErrors",
                new Schema<>()
                    .type("object")
                    .description("Field validation errors (present only for validation failures)")
                    .additionalProperties(new Schema<>()));

    components.addSchemas("ErrorResponse", errorSchema);

    return new OpenAPI().info(info).components(components);
  }
}
