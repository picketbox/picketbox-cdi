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

import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Instance;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;

import org.jboss.picketlink.idm.IdentityManager;
import org.jboss.picketlink.idm.internal.DefaultIdentityManager;
import org.jboss.picketlink.idm.spi.IdentityStore;

/**
 * <p>Produces an {@link IdentityManager}.</p>
 * <p> {@link IdentityManager} instances are only produced if there is some {@link IdentityStore} bean registered.</p>
 *
 * @author <a href="mailto:psilva@redhat.com">Pedro Silva</a>
 *
 */
public class IdentityManagerProducer {

    @Inject
    private Instance<IdentityStore> identityStore;

    @Produces
    @RequestScoped
    public IdentityManager produceIdentityManager() {
        IdentityStore store = null;

        try {
            store = this.identityStore.get();

        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            if (store == null) {
                return null;
            }
        }

        DefaultIdentityManager identityStore = new DefaultIdentityManager();

        identityStore.setIdentityStore(store);

        return identityStore;
    }

}
