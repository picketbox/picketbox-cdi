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

import org.picketbox.core.DefaultPicketBoxManager;
import org.picketbox.core.PicketBoxManager;
import org.picketbox.core.PicketBoxSubject;
import org.picketbox.core.authorization.Resource;
import org.picketbox.core.config.PicketBoxConfiguration;
import org.picketbox.core.exceptions.AuthenticationException;
import org.picketbox.http.PicketBoxHTTPManager;
import org.picketbox.http.config.PicketBoxHTTPConfiguration;

/**
 * @author <a href="mailto:psilva@redhat.com">Pedro Silva</a>
 *
 */
public class PicketBoxManagerWrapper implements PicketBoxManager {

    private PicketBoxManager delegate;

    public PicketBoxManagerWrapper(PicketBoxConfiguration configuration) {
        if (configuration instanceof PicketBoxHTTPConfiguration) {
            delegate = new PicketBoxHTTPManager((PicketBoxHTTPConfiguration) configuration);
        } else {
            delegate = new DefaultPicketBoxManager(configuration);
        }
    }

    /**
     * @return
     * @see org.picketbox.core.PicketBoxLifecycle#started()
     */
    public boolean started() {
        return delegate.started();
    }

    /**
     *
     * @see org.picketbox.core.PicketBoxLifecycle#start()
     */
    public void start() {
        delegate.start();
    }

    /**
     * @return
     * @see org.picketbox.core.PicketBoxLifecycle#stopped()
     */
    public boolean stopped() {
        return delegate.stopped();
    }

    /**
     *
     * @see org.picketbox.core.PicketBoxLifecycle#stop()
     */
    public void stop() {
        delegate.stop();
    }

    /**
     * @param subject
     * @return
     * @throws AuthenticationException
     * @see org.picketbox.core.PicketBoxManager#authenticate(org.picketbox.core.PicketBoxSubject)
     */
    public PicketBoxSubject authenticate(PicketBoxSubject subject) throws AuthenticationException {
        return delegate.authenticate(subject);
    }

    /**
     * @param subject
     * @param resource
     * @return
     * @see org.picketbox.core.PicketBoxManager#authorize(org.picketbox.core.PicketBoxSubject, org.picketbox.core.authorization.Resource)
     */
    public boolean authorize(PicketBoxSubject subject, Resource resource) {
        return delegate.authorize(subject, resource);
    }

    /**
     * @param authenticatedUser
     * @throws IllegalStateException
     * @see org.picketbox.core.PicketBoxManager#logout(org.picketbox.core.PicketBoxSubject)
     */
    public void logout(PicketBoxSubject authenticatedUser) throws IllegalStateException {
        delegate.logout(authenticatedUser);
    }

}