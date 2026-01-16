package org.asupg.asupgservice.model;

import com.azure.spring.data.cosmos.core.mapping.Container;
import com.azure.spring.data.cosmos.core.mapping.PartitionKey;
import com.fasterxml.jackson.annotation.JsonIgnore;
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
public class UserDTO {

    @Id
    @PartitionKey
    private String username;

    private String firstName;

    private String lastName;

    private String passwordHash;

    private Set<String> roles;

    private Boolean enabled;

    private Boolean locked;

    private UserType type;

    private LocalDateTime createdAt;

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
