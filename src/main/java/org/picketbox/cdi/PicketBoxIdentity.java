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

package org.picketbox.cdi;

import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.picketlink.cdi.Identity;
import org.jboss.picketlink.cdi.authentication.AuthenticationException;
import org.jboss.picketlink.cdi.credential.LoginCredentials;
import org.jboss.picketlink.cdi.internal.DefaultIdentity;
import org.jboss.picketlink.idm.model.User;
import org.picketbox.core.Credential;
import org.picketbox.core.PicketBoxManager;
import org.picketbox.core.PicketBoxSubject;

/**
 * <p>PicketBox implementation for the {@link Identity} component. This implementation is the main integration point for DeltaSpike.</p>
 *
 * @author <a href="mailto:psilva@redhat.com">Pedro Silva</a>
 *
 */
@SessionScoped
@Named ("identity")
public class PicketBoxIdentity extends DefaultIdentity {

    private static final long serialVersionUID = -290838764498141080L;

    @Inject
    private LoginCredentials credential;

    @Inject
    private PicketBoxManager picketBoxManager;

    private PicketBoxUser user;

    /* (non-Javadoc)
     * @see org.apache.deltaspike.security.impl.authentication.DefaultIdentity#authenticate()
     */
    @Override
    public boolean authenticate() throws AuthenticationException {
        PicketBoxSubject subject = null;

        try {
            PicketBoxSubject authenticationSubject = new PicketBoxSubject();

            authenticationSubject.setCredential((Credential) this.credential.getCredential().getValue());

            subject = this.picketBoxManager.authenticate(authenticationSubject);
        } catch (Exception e) {
            //TODO: better exception handling
            throw new AuthenticationException(e.getMessage());
        }

        if (subject != null && subject.isAuthenticated()) {
            createUser(subject);
            return true;
        } else {
            return false;
        }
    }

    /* (non-Javadoc)
     * @see org.apache.deltaspike.security.impl.authentication.DefaultIdentity#logout()
     */
    @Override
    public void logout() {
        if (isLoggedIn()) {
            super.logout();
            this.picketBoxManager.logout(this.user.getSubject());
            this.user = null;
        }
    }

    private void createUser(PicketBoxSubject subject) {
        this.user = new PicketBoxUser(subject);
    }

    /* (non-Javadoc)
     * @see org.apache.deltaspike.security.impl.authentication.DefaultIdentity#isLoggedIn()
     */
    @Override
    public boolean isLoggedIn() {
        return this.user != null && this.user.getSubject().isAuthenticated();
    }

    /* (non-Javadoc)
     * @see org.apache.deltaspike.security.spi.authentication.Authenticator#getUser()
     */
    @Override
    public User getUser() {
        return this.user;
    }
}
