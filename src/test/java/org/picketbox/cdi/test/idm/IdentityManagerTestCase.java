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

package org.picketbox.cdi.test.idm;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.picketbox.cdi.LoginCredential;
import org.picketbox.cdi.PicketBoxIdentity;
import org.picketbox.cdi.test.arquillian.ArchiveUtil;
import org.picketbox.core.authentication.credential.UsernamePasswordCredential;
import org.picketbox.core.identity.jpa.EntityManagerPropagationContext;
import org.picketlink.idm.IdentityManager;
import org.picketlink.idm.credential.Password;
import org.picketlink.idm.model.Group;
import org.picketlink.idm.model.Role;
import org.picketlink.idm.model.SimpleGroup;
import org.picketlink.idm.model.SimpleRole;
import org.picketlink.idm.model.SimpleUser;

/**
 * <p>Test for the PicketLink IDM support.</p>
 *
 * @author <a href="mailto:psilva@redhat.com">Pedro Silva</a>
 *
 */
@RunWith(Arquillian.class)
public class IdentityManagerTestCase {
    
    private EntityManagerFactory entityManagerFactory;
    
    protected static final String USER_NAME = "pedroigor";
    protected static final String USER_PASSWORD = "123";

    @Inject
    private PicketBoxIdentity identity;

    @Inject
    private LoginCredential credential;

    @Inject
    private IdentityManager identityManager;

    @Before
    public void onSetup() throws Exception {
        this.entityManagerFactory = Persistence.createEntityManagerFactory("picketbox-testing-pu");

        EntityManager entityManager = this.entityManagerFactory.createEntityManager();

        entityManager.getTransaction().begin();

        EntityManagerPropagationContext.set(entityManager);
    }

    @After
    public void onFinish() throws Exception {
        EntityManager entityManager = EntityManagerPropagationContext.get();

        entityManager.flush();
        entityManager.getTransaction().commit();
        entityManager.close();

        EntityManagerPropagationContext.clear();
        this.entityManagerFactory.close();
    }

    /**
     * <p>
     * Creates a simple {@link WebArchive} for deployment with the necessary structure/configuration to run the tests.
     * </p>
     *
     * @return
     */
    @Deployment
    public static WebArchive createTestArchive() {
        WebArchive archive = ArchiveUtil.createTestArchive();

        archive.addPackages(true, IdentityManagerTestCase.class.getPackage());

        return archive;
    }

    /**
     * <p>Creates an user using the PicketLink IDM API and performs an authentication.</p>
     *
     * @throws Exception
     */
    @Test
    public void testAuthentication() throws Exception {
        SimpleUser pedroigor = new SimpleUser("pedroigor");

        this.identityManager.add(pedroigor);

        pedroigor.setEmail("pedroigor@picketbox.com");
        pedroigor.setFirstName("Pedro");
        pedroigor.setLastName("Igor");

        this.identityManager.updateCredential(pedroigor, new Password("123".toCharArray()));

        Role roleDeveloper = new SimpleRole("developer");
        
        identityManager.add(roleDeveloper);
        
        Role roleAdmin = new SimpleRole("admin");
        
        this.identityManager.add(roleAdmin);

        Group groupCoreDeveloper = new SimpleGroup("PicketBox Group");
        
        this.identityManager.add(groupCoreDeveloper);

        this.identityManager.grantRole(pedroigor, roleDeveloper);
        this.identityManager.grantRole(pedroigor, roleAdmin);
        
        this.identityManager.addToGroup(pedroigor, groupCoreDeveloper);

        populateUserCredential();

        this.identity.login();

        Assert.assertTrue(this.identity.isLoggedIn());

        Assert.assertTrue(this.identity.hasRole("developer"));
        Assert.assertTrue(this.identity.hasRole("admin"));
    }

    /**
     * <p>
     * Populates the {@link LoginCredential} with the username and password.
     * </p>
     */
    private void populateUserCredential() {
        this.credential.setCredential(new UsernamePasswordCredential(USER_NAME, USER_PASSWORD));
    }

}