package io.laaf.sns.config.filter;

import io.laaf.sns.model.User;
import io.laaf.sns.service.UserService;
import io.laaf.sns.util.JwtTokenUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
public class JwtTokenFilter extends OncePerRequestFilter {

    private final String secretKey;
    private final UserService userService;

    public JwtTokenFilter(String secretKey, UserService userService) {
        this.secretKey = secretKey;
        this.userService = userService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // get header
        final String header = request.getHeader(HttpHeaders.AUTHORIZATION);
        log.info("겟헤더= {}", header);
        if (header == null || !header.startsWith("Bearer ")) {
            log.error("Error occurs while getting header. header is null or invalid");
            filterChain.doFilter(request, response);
            return ;
        }

        try {
            final String token = header.split(" ")[1].trim();
            log.info("토큰 뿌려봐 = {}", token);
            // TODO: check token is valid
            if (JwtTokenUtils.isExpired(token, secretKey)) {
                log.error("Key is expired");
                filterChain.doFilter(request, response);
                return ;
            }
            // TODO: get username from token
            String username = JwtTokenUtils.getUserName(token, secretKey);
            log.info("유저네임이 없다는 거? ={} ", username);
            // TODO: check the user is valid
            User user = userService.loadUserByUserName(username);

            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                            user, null, user.getAuthorities());

            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authentication);
        } catch (RuntimeException e) {
            log.error("Error occurs while validating. 여기냐? {}", e.toString());
            filterChain.doFilter(request, response);
            return;
        }

        filterChain.doFilter(request, response);
    }
}