package basePack.DTO;

import basePack.model.Role;
import lombok.AllArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import java.util.Collection;

@Component
public class UserDetailsImplementation implements UserDetails {

    public final Users_postgres usersPostgres;
    private Role role;

    public UserDetailsImplementation(Users_postgres usersPostgres) {
        this.usersPostgres = usersPostgres;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return usersPostgres.getRole().getAuthorities();
    }

    @Override
    public String getPassword() {
        return usersPostgres.getPassword();
    }

    @Override
    public String getUsername() {
        return usersPostgres.getEmail();
    }

    @Override
    public boolean isAccountNonExpired() {
        return UserDetails.super.isAccountNonExpired();
    }

    @Override
    public boolean isAccountNonLocked() {
        return UserDetails.super.isAccountNonLocked();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return UserDetails.super.isCredentialsNonExpired();
    }

    @Override
    public boolean isEnabled() {
        return UserDetails.super.isEnabled();
    }
}
