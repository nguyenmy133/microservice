package com.example.employeeservice.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.servers.Server;
import io.swagger.v3.oas.annotations.servers.Servers;

import java.nio.charset.StandardCharsets;

@OpenAPIDefinition(
        info=@Info (
                title="Employee API",
                description = "API Documentation for Employee Service",
                version = "1.0",
                contact = @Contact(
                        name="My Nguyen",
                        email = "mynguyen13324@gmail.com",
                        url = "localhost:9002"
                ),
                license = @License(
                        name = "MIT License",
                        url = ""
                ),
                termsOfService =""
        ),
        servers={
                @Server(
                        description = "Local env",
                        url="http://localhost:9002"
                ),
                @Server(
                        description="Dev env",
                        url=""
                )

        }


)
public class OpenApiConfig {

}
