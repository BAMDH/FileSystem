package drive.webdrive.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable()) // <--- ¡Esta línea deshabilita la protección CSRF!
            .authorizeHttpRequests(auth -> auth
                .anyRequest().permitAll() // Permite todas las solicitudes sin autenticación
            )
            .logout(logout -> logout
                .logoutSuccessUrl("/login?logout=true") // Redirecciona después de cerrar sesión
                .permitAll() // Permite el acceso a la URL de logout para todos
            );

        // Si usas H2 console o necesitas iframes, podrías añadir esta línea:
        // .headers(headers -> headers.frameOptions(frameOptions -> frameOptions.sameOrigin()));

        return http.build();
    }

    @Bean
    public UserDetailsService userDetailsService(PasswordEncoder encoder) {
        // Configuración de un usuario en memoria. Aunque no se usa para restringir acceso con permitAll(),
        // es parte de la configuración base de seguridad.
        UserDetails user = User
            .withUsername("admin")
            .password(encoder.encode("1234")) // Codifica la contraseña
            .roles("USER") // Asigna el rol "USER"
            .build();

        return new InMemoryUserDetailsManager(user);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        // Bean para el codificador de contraseñas BCrypt, necesario si usas el userDetailsService
        return new BCryptPasswordEncoder();
    }
}
