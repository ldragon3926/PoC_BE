package org.example.poc.security;

import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;
import org.springframework.stereotype.Component;

import java.util.function.Supplier;

@Component
public class DatabaseAuthorizationManager implements AuthorizationManager<RequestAuthorizationContext> {
    private final RequestPermissionMapper requestPermissionMapper;
    private final AbacEvaluator abacEvaluator;

    public DatabaseAuthorizationManager(RequestPermissionMapper requestPermissionMapper,
                                        AbacEvaluator abacEvaluator) {
        this.requestPermissionMapper = requestPermissionMapper;
        this.abacEvaluator = abacEvaluator;
    }

    @Override
    public AuthorizationDecision authorize(Supplier<? extends Authentication> authenticationSupplier,
                                           RequestAuthorizationContext context) {
        Authentication authentication = authenticationSupplier.get();
        if (authentication == null || !authentication.isAuthenticated()) {
            return new AuthorizationDecision(false);
        }

        boolean rbacAllowed = requestPermissionMapper.map(context.getRequest())
                .map(requiredPermission -> authentication.getAuthorities().stream()
                        .anyMatch(authority -> requiredPermission.equals(authority.getAuthority())))
                .orElse(true);

        boolean abacAllowed = abacEvaluator.isAllowed(authentication, context.getRequest());
        return new AuthorizationDecision(rbacAllowed && abacAllowed);
    }
}


