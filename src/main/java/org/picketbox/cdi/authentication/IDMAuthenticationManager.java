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

package org.picketbox.cdi.authentication;

import java.security.Principal;

import javax.inject.Inject;

import org.jboss.picketlink.idm.IdentityManager;
import org.jboss.picketlink.idm.model.User;
import org.picketbox.core.PicketBoxPrincipal;
import org.picketbox.core.authentication.AbstractAuthenticationManager;
import org.picketbox.core.authentication.AuthenticationManager;
import org.picketbox.core.exceptions.AuthenticationException;

/**
 * <p>{@link AuthenticationManager} that uses the PicketLink IDM to check user credentials.</p>
 *
 * @author <a href="mailto:psilva@redhat.com">Pedro Silva</a>
 *
 */
public class IDMAuthenticationManager extends AbstractAuthenticationManager {

    @Inject
    private IdentityManager identityManager;

    /* (non-Javadoc)
     * @see org.picketbox.core.authentication.AbstractAuthenticationManager#authenticate(java.lang.String, java.lang.Object)
     */
    @Override
    public Principal authenticate(String username, Object credential) throws AuthenticationException {
        User user = this.identityManager.getUser(username);

        if (user != null) {
            if (this.identityManager.validatePassword(user, credential.toString())) {
                return new PicketBoxPrincipal(username);
            }
        }

        return null;
    }



}