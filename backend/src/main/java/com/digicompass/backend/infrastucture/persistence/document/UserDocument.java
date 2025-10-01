package com.digicompass.backend.infrastucture.persistence.document;

import com.sun.jna.WString;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "users")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDocument {
    @Id
    private String id;

    @NotNull(message = "Username cannot be null or blank.")
    @Indexed(unique=true)
    private String username;

    @NotNull(message = "Email cannot be null or blank.")
    @Indexed(unique=true)
    @Email(message = "Email should be valid")
    private String email;

    @NotNull(message = "Age cannot be null or blank.")
    @Min(value = 14, message = "Age should not be less than 14")
    private byte age;

    @NotBlank
    @Size(min = 8, message = "Password must be at least 8 characters long")
    @Pattern(
            regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=]).*$",
            message = "Password must contain at least one digit, one lowercase, one uppercase, and one special character"
    )
    private String password;
}