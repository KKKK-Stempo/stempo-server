package com.stempo.filter;

import com.stempo.util.HttpReqResUtils;
import com.stempo.util.IpWhitelistValidator;
import com.stempo.util.ResponseUtils;
import com.stempo.util.WhitelistPathMatcher;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotNull;
import java.io.IOException;
import java.util.Base64;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

@Slf4j
public class CustomBasicAuthenticationFilter extends BasicAuthenticationFilter {

    private final AuthenticationManager whitelistAuthenticationManager;
    private final IpWhitelistValidator ipWhitelistValidator;

    public CustomBasicAuthenticationFilter(
            AuthenticationManager whitelistAuthenticationManager,
            IpWhitelistValidator ipWhitelistValidator
    ) {
        super(whitelistAuthenticationManager);
        this.whitelistAuthenticationManager = whitelistAuthenticationManager;
        this.ipWhitelistValidator = ipWhitelistValidator;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        String path = request.getRequestURI();
        if (!WhitelistPathMatcher.isWhitelistRequest(path)) {
            chain.doFilter(request, response);
            return;
        }

        if (!verifyIpAddressAccess(response)) {
            return;
        }

        if (!authenticateUserCredentials(request, response)) {
            return;
        }

        super.doFilterInternal(request, response, chain);
    }

    private boolean verifyIpAddressAccess(HttpServletResponse response) throws IOException {
        String clientIpAddress = HttpReqResUtils.getClientIpAddressIfServletRequestExist();
        if (!ipWhitelistValidator.isIpWhitelisted(clientIpAddress)) {
            log.info("[{}] : Unauthorized access from non-whitelisted IP", clientIpAddress);
            ResponseUtils.sendErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED);
            return false;
        }
        return true;
    }

    private boolean authenticateUserCredentials(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        String authorizationHeader = request.getHeader("Authorization");
        if (authorizationHeader == null || !authorizationHeader.startsWith("Basic ")) {
            response.setHeader("WWW-Authenticate", "Basic realm=\"Please enter your username and password\"");
            ResponseUtils.sendErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED);
            return false;
        }

        String[] credentials = decodeCredentials(authorizationHeader);
        if (credentials.length < 2) {
            log.warn("Invalid Authorization header format");
            ResponseUtils.sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST);
            return false;
        }

        String username = credentials[0];
        String password = credentials[1];
        UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(username, password);

        try {
            Authentication authentication = whitelistAuthenticationManager.authenticate(authRequest);
            SecurityContextHolder.getContext().setAuthentication(authentication);
        } catch (BadCredentialsException e) {
            log.warn("Authentication failed for user: {}", username);
            ResponseUtils.sendErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED);
            return false;
        } catch (Exception e) {
            log.error("Unexpected error during authentication", e);
            ResponseUtils.sendErrorResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            return false;
        }

        return true;
    }

    @NotNull
    private static String[] decodeCredentials(String authorizationHeader) {
        String base64Credentials = authorizationHeader.substring("Basic ".length());
        String credentials = new String(Base64.getDecoder().decode(base64Credentials));
        return credentials.split(":", 2);
    }
}
