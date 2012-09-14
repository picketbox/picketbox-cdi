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

import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.picketlink.cdi.Identity;
import org.jboss.picketlink.cdi.credential.Credential;
import org.jboss.picketlink.cdi.credential.LoginCredentials;
import org.jboss.picketlink.idm.IdentityManager;
import org.jboss.picketlink.idm.internal.JPAIdentityStore;
import org.jboss.picketlink.idm.internal.jpa.JPATemplate;
import org.jboss.picketlink.idm.model.Group;
import org.jboss.picketlink.idm.model.Role;
import org.jboss.picketlink.idm.model.User;
import org.jboss.picketlink.idm.spi.IdentityStore;
import org.jboss.picketlink.test.idm.internal.jpa.AbstractJPAIdentityStoreTestCase;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.picketbox.cdi.PicketBoxUser;
import org.picketbox.cdi.test.arquillian.ArchiveUtil;
import org.picketbox.core.authentication.credential.UsernamePasswordCredential;

/**
 * <p>Test for the PicketLink IDM support.</p>
 *
 * @author <a href="mailto:psilva@redhat.com">Pedro Silva</a>
 *
 */
@RunWith(Arquillian.class)
public class IdentityManagerTestCase extends AbstractJPAIdentityStoreTestCase {

    protected static final String USER_NAME = "pedroigor";
    protected static final String USER_PASSWORD = "123";

    @Inject
    private Identity identity;

    @Inject
    private LoginCredentials credential;
    
    @Inject
    private IdentityManager identityManager;

    @Deployment
    public static WebArchive createTestArchive() {
        WebArchive archive = ArchiveUtil.createTestArchive();
        
        archive.delete("/WEB-INF/classes/org/picketbox/cdi/test/PicketBoxConfigurer.class");
        
        return archive;
    }
    
    /**
     * <p>Creates an user using the PicketLink IDM API and performs an authentication.</p>
     * 
     * @throws Exception
     */
    @Test
    public void testAuthentication() throws Exception {
        User abstractj = this.identityManager.createUser("pedroigor");

        abstractj.setEmail("pedroigor@picketbox.com");
        abstractj.setFirstName("Pedro");
        abstractj.setLastName("Igor");
        
        this.identityManager.updatePassword(abstractj, "123");
        
        Role roleDeveloper = this.identityManager.createRole("developer");
        Role roleAdmin = this.identityManager.createRole("admin");

        Group groupCoreDeveloper = identityManager.createGroup("PicketBox Group");

        this.identityManager.grantRole(roleDeveloper, abstractj, groupCoreDeveloper);
        this.identityManager.grantRole(roleAdmin, abstractj, groupCoreDeveloper);

        populateUserCredential();
        
        this.identity.login();
        
        Assert.assertTrue(this.identity.isLoggedIn());
        
        PicketBoxUser user = (PicketBoxUser) this.identity.getUser();
        
        Assert.assertTrue(user.hasRole("developer"));
        Assert.assertTrue(user.hasRole("admin"));
    }
    
    /**
     * <p>
     * Populates the {@link LoginCredential} with the username and password.
     * </p>
     */
    private void populateUserCredential() {
        credential.setUserId(USER_NAME);
        credential.setCredential(new Credential<UsernamePasswordCredential>() {

            @Override
            public UsernamePasswordCredential getValue() {
                return new UsernamePasswordCredential(USER_NAME, USER_PASSWORD);
            }
        });
    }
    
    @Produces
    public IdentityStore produceIdentityStore() {
        JPAIdentityStore identityStore = (JPAIdentityStore) createIdentityStore();
        
        JPATemplate jpaTemplate = new JPATemplate();
        
        EntityManager entityManager = emf.createEntityManager();
        
        entityManager.getTransaction().begin();
        
        jpaTemplate.setEntityManager(entityManager);
        
        identityStore.setJpaTemplate(jpaTemplate);
        
        return identityStore;
    }
    
}