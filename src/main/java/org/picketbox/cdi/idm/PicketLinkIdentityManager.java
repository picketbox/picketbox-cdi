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

package org.picketbox.cdi.idm;

import java.util.ArrayList;
import java.util.List;

import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import org.jboss.picketlink.idm.model.Membership;
import org.jboss.picketlink.idm.model.User;
import org.jboss.picketlink.idm.query.MembershipQuery;
import org.jboss.picketlink.idm.spi.IdentityStore;
import org.picketbox.cdi.PicketBoxCDISubject;
import org.picketbox.core.PicketBoxSubject;
import org.picketbox.core.identity.IdentityManager;

/**
 * <p>This class provides a simple integration with the PicketLink IDM {@link org.jboss.picketlink.idm.IdentityManager} interface.</p>
 * <p>A {@link IdentityStore} must be provided/produced in order to load the user informations.</p>
 *
 * @author <a href="mailto:psilva@redhat.com">Pedro Silva</a>
 *
 */
public class PicketLinkIdentityManager implements IdentityManager {

    @Inject
    private Instance<org.jboss.picketlink.idm.IdentityManager> identityManager;

    /* (non-Javadoc)
     * @see org.picketbox.core.identity.IdentityManager#getIdentity(org.picketbox.core.PicketBoxSubject)
     */
    @Override
    public PicketBoxSubject getIdentity(PicketBoxSubject resultingSubject) {
        PicketBoxCDISubject cdiSubject = (PicketBoxCDISubject) resultingSubject;

        org.jboss.picketlink.idm.IdentityManager delegate = getIdentityManager();

        if (delegate != null) {
            // retrieve the user informations
            User user = delegate.getUser(cdiSubject.getUser().getName());

            cdiSubject.setIdmUser(user);

            // retrieve the memberships for the user
            MembershipQuery membershipQuery = delegate.createMembershipQuery();

            membershipQuery.setUser(user);

            List<Membership> roles = membershipQuery.executeQuery(membershipQuery);
            List<String> roleNames = new ArrayList<String>();

            for (Membership membership : roles) {
                roleNames.add(membership.getRole().getName());
            }

            cdiSubject.setRoleNames(roleNames);
        }

        return cdiSubject;
    }

    protected org.jboss.picketlink.idm.IdentityManager getIdentityManager() {
        try {
            return this.identityManager.get();
        } finally {
        }
    }

}