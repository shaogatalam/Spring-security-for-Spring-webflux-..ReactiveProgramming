package basePack.model;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import static basePack.model.Permissions.*;
//@RequiredArgsConstructor
public enum Role {

    USER(Collections.emptySet()),
    ADMIN(Set.of(ADMIN_CREATE,ADMIN_UPDATE,ADMIN_DELETE,ADMIN_READ,MANAGER_CREATE,MANAGER_UPDATE,MANAGER_DELETE,MANAGER_READ)),
    MANAGER(Set.of(MANAGER_CREATE,MANAGER_UPDATE,MANAGER_DELETE,MANAGER_READ));
    private final Set<Permissions> permissions;

    Role(Set<Permissions> permissions) {
        this.permissions = permissions;
    }

    public List<SimpleGrantedAuthority> getAuthorities() {

        var authorities = getPermissions()
            .stream()
            .map(permission -> new SimpleGrantedAuthority(permission.getPermission()))
            .collect(Collectors.toList());

        authorities.add(new SimpleGrantedAuthority("ROLE_" + this.name()));

        return authorities;
    }

    public Set<Permissions> getPermissions() {
        return permissions;
    }
}
