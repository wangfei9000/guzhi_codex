package com.admin.system.security;

import com.admin.system.entity.SysUser;
import com.admin.system.repository.SysUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final SysUserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        SysUser user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("用户不存在: " + username));

        if (user.getStatus() != 1) {
            throw new UsernameNotFoundException("用户已被禁用: " + username);
        }

        var authorities = user.getRoles().stream()
                .flatMap(role -> role.getPermissions().stream())
                .map(perm -> new SimpleGrantedAuthority(perm.getPermCode()))
                .collect(Collectors.toSet());

        user.getRoles().forEach(role ->
                authorities.add(new SimpleGrantedAuthority(role.getRoleCode())));

        return User.builder()
                .username(user.getUsername())
                .password(user.getPassword())
                .authorities(authorities)
                .build();
    }
}
