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

import org.picketbox.core.PicketBoxSubject;
import org.picketbox.core.session.DefaultSessionId;
import org.picketlink.idm.model.User;

/**
 * <p>
 * Custom implementation of {@link PicketBoxSubject}. This class only exists to allow setting the {@link User} from the
 * PicketLink IDM API.
 * </p>
 *
 * TODO: PicketBox Core native support for the PicketLink IDM API.
 *
 * @author <a href="mailto:psilva@redhat.com">Pedro Silva</a>
 *
 */
public class PicketBoxCDISubject extends PicketBoxSubject {

    private User idmUser;

    public PicketBoxCDISubject(DefaultSessionId defaultSessionId) {
        super(defaultSessionId);
    }

    /**
     * @return the idmUser
     */
    public User getIdmUser() {
        return idmUser;
    }

    /**
     * @param idmUser the idmUser to set
     */
    public void setIdmUser(User idmUser) {
        this.idmUser = idmUser;
    }

}