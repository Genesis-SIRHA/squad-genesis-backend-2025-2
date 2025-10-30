package edu.dosw.security;

import edu.dosw.dto.UserInfoDto;
import edu.dosw.model.enums.Role;
import edu.dosw.services.AuthenticationService;
import edu.dosw.utils.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * JWT authentication filter that processes JWT tokens from incoming requests and sets up Spring
 * Security authentication context
 */
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

  private final JwtUtil jwtUtil;
  private final AuthenticationService authenticationService;

  /**
   * Determines which requests should not be filtered (public endpoints)
   *
   * @param request The HTTP request
   * @return true if the request should not be filtered, false otherwise
   */
  @Override
  protected boolean shouldNotFilter(HttpServletRequest request) {
    String path = request.getServletPath();

    return path.equals("/auth/login")
        || path.startsWith("/api/swagger-ui/")
        || path.startsWith("/v3/api-docs/")
        || path.equals("/swagger-ui.html")
        || path.startsWith("/swagger-ui/")
        || path.startsWith("/webjars/");
  }

  /**
   * Processes JWT token from Authorization header and sets up authentication context
   *
   * @param request The HTTP request
   * @param response The HTTP response
   * @param filterChain The filter chain
   * @throws ServletException If a servlet error occurs
   * @throws IOException If an I/O error occurs
   */
  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {

    final String header = request.getHeader("Authorization");
    String jwt = null;
    String email = null;

    if (header != null && header.startsWith("Bearer ")) {
      jwt = header.substring(7);
      try {
        email = jwtUtil.extractEmail(jwt);
      } catch (Exception e) {
        logger.warn("JWT parsing error: " + e.getMessage());
      }
    }

    if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
      if (jwtUtil.validateToken(jwt)) {
        try {
          UserInfoDto userInfo = authenticationService.getUserInfo(email);
          List<SimpleGrantedAuthority> authorities = determineAuthorities(userInfo);

          UsernamePasswordAuthenticationToken authentication =
              new UsernamePasswordAuthenticationToken(email, null, authorities);

          authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
          SecurityContextHolder.getContext().setAuthentication(authentication);

          logger.debug("Authenticated user: {" + email + " with role: " + userInfo.role());
        } catch (Exception e) {
          logger.warn("Failed to get user info for: " + email + ", error: " + e.getMessage());
        }
      }
    }

    filterChain.doFilter(request, response);
  }

  /**
   * Determines the authorities (roles) for the authenticated user
   *
   * @param userInfo The user information DTO
   * @return List of granted authorities for the user
   */
  private List<SimpleGrantedAuthority> determineAuthorities(UserInfoDto userInfo) {
    if (userInfo == null || userInfo.role() == null) {
      return List.of(new SimpleGrantedAuthority("ROLE_USER"));
    }

    Role userRole = userInfo.role();
    String roleAuthority = "ROLE_" + userRole.name();

    return List.of(new SimpleGrantedAuthority(roleAuthority));
  }
}
