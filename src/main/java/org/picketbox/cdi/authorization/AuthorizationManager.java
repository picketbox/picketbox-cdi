/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2012, Red Hat, Inc., and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package org.picketbox.cdi.authorization;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import javax.enterprise.context.ApplicationScoped;
import javax.interceptor.InvocationContext;

import org.apache.deltaspike.security.api.authorization.annotation.Secures;
import org.jboss.picketlink.cdi.Identity;
import org.picketbox.cdi.PicketBoxUser;
import org.picketbox.core.PicketBoxSubject;

/**
 * <p>
 * Provides all authorization capabilities for applications using PicketBox.
 * </p>
 *
 * @author <a href="mailto:psilva@redhat.com">Pedro Silva</a>
 *
 */
@ApplicationScoped
public class AuthorizationManager {

    /**
     * <p>
     * Authorization method for the {@link RestrictedRoles} annotation.
     * </p>
     *
     * @param ctx
     * @param identity
     * @return
     */
    @Secures
    @RestrictedRoles
    public boolean restrictRoles(InvocationContext ctx, Identity identity) {
        if (!identity.isLoggedIn()) {
            return false;
        }

        PicketBoxUser user = (PicketBoxUser) identity.getUser();
        PicketBoxSubject subject = user.getSubject();

        String[] restrictedRoles = getRestrictedRoles(ctx);

        for (String restrictedRole : restrictedRoles) {
            if (subject.hasRole(restrictedRole)) {
                return true;
            }
        }

        return false;
    }

    /**
     * <p>Checks if the resources protected with the {@link UserLoggedIn} annotation are visible only for authenticated users.</p>
     *
     * @param ctx
     * @param identity
     * @return
     */
    @Secures
    @UserLoggedIn
    public boolean isUserLoggedIn(InvocationContext ctx, Identity identity) {
        return identity.isLoggedIn();
    }

    /**
     * <p>
     * Returns the restricted roles defined by the use of the {@link RestrictedRoles} annotation. If the annotation is not
     * present a empty array is returned.
     * </p>
     *
     * @param ctx
     * @return
     */
    private String[] getRestrictedRoles(InvocationContext ctx) {
        RestrictedRoles restrictedRolesAnnotation = (RestrictedRoles) getDeclaredAnnotation(RestrictedRoles.class, ctx);

        if (restrictedRolesAnnotation != null) {
            return restrictedRolesAnnotation.value();
        }

        return new String[] {};
    }

    /**
     * <p>
     * Returns the an {@link Annotation} instance giving its class. The annotation will be looked up on method and type levels,
     * only.
     * </p>
     *
     * @param annotationClass
     * @param ctx
     * @return
     */
    private Annotation getDeclaredAnnotation(Class<? extends Annotation> annotationClass, InvocationContext ctx) {
        Method method = ctx.getMethod();
        Class<?> type = method.getDeclaringClass();

        if (method.isAnnotationPresent(RestrictedRoles.class)) {
            return method.getAnnotation(RestrictedRoles.class);
        }

        if (type.isAnnotationPresent(RestrictedRoles.class)) {
            return type.getAnnotation(RestrictedRoles.class);
        }

        return null;
    }

}
