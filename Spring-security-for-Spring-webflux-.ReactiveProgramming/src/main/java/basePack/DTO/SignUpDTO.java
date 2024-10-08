package basePack.DTO;
import basePack.model.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SignUpDTO {

    @NotEmpty
    private String firstname;

    @NotEmpty
    private String lastname;

    @Size(min = 6, message = "Password must have at least 6 characters")
    private String password;

    @Email
    @NotEmpty
    private String email;

    @NotEmpty(message = "Role must not be empty")
    private Role role;
}

