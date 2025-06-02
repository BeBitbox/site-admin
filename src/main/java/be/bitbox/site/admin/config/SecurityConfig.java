package be.bitbox.site.admin.config;

import static org.springframework.security.config.Customizer.withDefaults;

import java.util.Arrays;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientProviderBuilder;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.DefaultOAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizedClientRepository;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@EnableWebSecurity
@Configuration
public class SecurityConfig {
  private final static List<String> ALLOWED_ORIGINS = List.of("http://localhost:8081", "http://localhost:3000", "https://administratie.site", "https://vlaanderen.click");
  private final static Logger LOGGER = LoggerFactory.getLogger(SecurityConfig.class);

  private final List<String> meulemeershoeveUsers;
  private final List<String> vlaanderenClickUsers;

  public SecurityConfig(@Value("${allowed.user.meulemeershoeve}") String meulemeershoeveUsers, @Value("${allowed.user.vlaanderenclick}") String vlaanderenClickUsers) {
    this.meulemeershoeveUsers = Arrays.asList(meulemeershoeveUsers.split(","));
    this.vlaanderenClickUsers = Arrays.asList(vlaanderenClickUsers.split(","));
  }

  @Bean
  SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http.authorizeHttpRequests(authorize -> authorize
            .requestMatchers(
                new AntPathRequestMatcher("/error"),
                new AntPathRequestMatcher("/free"),
                new AntPathRequestMatcher("/css/**"),
                new AntPathRequestMatcher("/js/**"),
                new AntPathRequestMatcher("/vlaanderen.click/**"),
                new AntPathRequestMatcher("/actuator/**"),
                new AntPathRequestMatcher("/accessDenied"))
            .permitAll()
            .anyRequest().authenticated()
        )
        .oauth2Login(login ->
            login.successHandler((request, response, authentication) -> {
              if (authentication.getPrincipal() instanceof OAuth2User oAuth2User) {
                String email = oAuth2User.getAttribute("email");
                LOGGER.info("trying to login {}", email);
                if (!meulemeershoeveUsers.contains(email) && !vlaanderenClickUsers.contains(email)) {
                  response.sendRedirect("/accessDenied?email=" + email);
                  return;
                }
              }
              response.sendRedirect("/"); // Redirect to home page or a dashboard upon successful login
            }))
        .oauth2Client(withDefaults())
        .csrf(AbstractHttpConfigurer::disable)
        .cors(cors -> cors.configurationSource(corsConfigurationSource()));
    return http.build();
  }

  @Bean
  OAuth2AuthorizedClientManager authorizedClientManager(ClientRegistrationRepository clientRegistrationRepository,
      OAuth2AuthorizedClientRepository authorizedClientRepository) {
    var authorizedClientProvider = OAuth2AuthorizedClientProviderBuilder.builder()
        .authorizationCode()
        .refreshToken()
        .clientCredentials()
        .build();

    var authorizedClientManager = new DefaultOAuth2AuthorizedClientManager(clientRegistrationRepository, authorizedClientRepository);
    authorizedClientManager.setAuthorizedClientProvider(authorizedClientProvider);

    return authorizedClientManager;
  }

  @Bean
  CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration configuration = new CorsConfiguration();
    configuration.setAllowedOrigins(ALLOWED_ORIGINS);
    configuration.setAllowedMethods(List.of("GET", "POST", "OPTIONS"));
    configuration.setAllowedHeaders(List.of("*"));
    configuration.setAllowCredentials(true);

    var source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", configuration);
    return source;
  }

  public boolean isMeulemeershoeveUser() {
    var authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication != null && authentication.getPrincipal() instanceof OAuth2User oAuth2User) {
      var email = oAuth2User.getAttribute("email");
      return meulemeershoeveUsers.contains(email);
    }
    return false;
  }

  public boolean isVlaanderenClickUser() {
    var authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication != null && authentication.getPrincipal() instanceof OAuth2User oAuth2User) {
      var email = oAuth2User.getAttribute("email");
      return vlaanderenClickUsers.contains(email);
    }
    return false;
  }
}