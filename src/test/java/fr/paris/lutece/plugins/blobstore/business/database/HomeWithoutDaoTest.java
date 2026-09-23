/*
 * Copyright (c) 2002-2026, City of Paris
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *  1. Redistributions of source code must retain the above copyright notice
 *     and the following disclaimer.
 *
 *  2. Redistributions in binary form must reproduce the above copyright notice
 *     and the following disclaimer in the documentation and/or other materials
 *     provided with the distribution.
 *
 *  3. Neither the name of 'Mairie de Paris' nor 'Lutece' nor the names of its
 *     contributors may be used to endorse or promote products derived from
 *     this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 * License 1.0
 */
package fr.paris.lutece.plugins.blobstore.business.database;

import static org.junit.jupiter.api.Assertions.assertThrows;

import jakarta.enterprise.inject.UnsatisfiedResolutionException;

import org.jboss.weld.junit5.EnableWeld;
import org.jboss.weld.junit5.WeldInitiator;
import org.jboss.weld.junit5.WeldSetup;
import org.junit.jupiter.api.Test;

import fr.paris.lutece.plugins.blobstore.business.filesystem.FileSystemBlobStoreHome;
import fr.paris.lutece.plugins.blobstore.business.filesystem.IFileSystemBlobStoreHome;

/**
 * Tests that the library deploys without plugin-blobstore: the homes start, and only a call needing the missing DAO fails.
 */
@EnableWeld
public class HomeWithoutDaoTest
{
    @WeldSetup
    public WeldInitiator _weld = WeldInitiator.of( DatabaseBlobStoreHome.class, FileSystemBlobStoreHome.class );

    /**
     * The database home resolves, and a call reports the missing DAO.
     */
    @Test
    public void testDatabaseHomeWithoutDao( )
    {
        IDatabaseBlobStoreHome home = _weld.select( IDatabaseBlobStoreHome.class ).get( );

        assertThrows( UnsatisfiedResolutionException.class, ( ) -> home.findByPrimaryKey( "any" ) );
    }

    /**
     * The file system home resolves, and a call reports the missing DAO.
     */
    @Test
    public void testFileSystemHomeWithoutDao( )
    {
        IFileSystemBlobStoreHome home = _weld.select( IFileSystemBlobStoreHome.class ).get( );

        assertThrows( UnsatisfiedResolutionException.class, ( ) -> home.findByPrimaryKey( "any", "/tmp", 1 ) );
    }
}
