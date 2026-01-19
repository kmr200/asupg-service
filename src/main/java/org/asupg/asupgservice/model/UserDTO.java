package org.asupg.asupgservice.model;

import com.azure.spring.data.cosmos.core.mapping.Container;
import com.azure.spring.data.cosmos.core.mapping.PartitionKey;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import org.springframework.data.annotation.Id;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Set;

@Container(containerName = "Users", autoCreateContainer = false)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@Schema(description = "DTO class describing user entity")
public class UserDTO {

    @Id
    @PartitionKey
    @Schema(description = "Username of the user", examples = "user")
    private String username;

    @Schema(description = "First name of the user", examples = "John")
    private String firstName;

    @Schema(description = "Last name of the user", examples = "Doe")
    private String lastName;

    @Schema(description = "Hashed password of the user. Not returned as a response")
    private String passwordHash;

    @Schema(description = "Roles of the user. Currently only 'ADMIN' and 'USER' roles are supported", examples = "['ADMIN']")
    private Set<String> roles;

    @Schema(description = "If the users account is enabled")
    private Boolean enabled;

    @Schema(description = "If the users account is locked")
    private Boolean locked;

    @Schema(description = "Type of the user. Currently only 'USER' type is supported")
    private UserType type;

    @Schema(description = "When the users account was created")
    private LocalDateTime createdAt;

    public UserDTO(String username, String firstName, String lastName, String passwordHash, Set<String> roles) {
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.passwordHash = passwordHash;
        this.roles = roles;
        this.enabled = true;
        this.locked = false;
        this.type = UserType.USER;
        this.createdAt = LocalDateTime.now();
    }

    public enum UserType {
        USER
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        UserDTO userDTO = (UserDTO) o;
        return Objects.equals(username, userDTO.username);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(username);
    }
}
