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
package fr.paris.lutece.plugins.blobstore.business.filesystem;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import org.jboss.weld.junit5.EnableWeld;
import org.jboss.weld.junit5.WeldInitiator;
import org.jboss.weld.junit5.WeldSetup;
import org.junit.jupiter.api.Test;

import fr.paris.lutece.plugins.blobstore.business.BytesBlobStore;
import fr.paris.lutece.plugins.blobstore.business.InputStreamBlobStore;

/**
 * Tests that the file system home resolves the DAO deployed beside it and passes the base path and depth through.
 */
@EnableWeld
public class FileSystemBlobStoreHomeTest
{
    private static final String BASE_PATH = "/var/blobs";
    private static final Integer DEPTH = 3;

    @WeldSetup
    public WeldInitiator _weld = WeldInitiator.from( FileSystemBlobStoreHome.class, MemoryFileSystemBlobStoreDAO.class ).build( );

    @Inject
    @Named( FileSystemBlobStoreHome.BEAN_SERVICE )
    private IFileSystemBlobStoreHome _home;

    /**
     * In-memory DAO standing for the one plugin-blobstore deploys; keys are prefixed by the location they were given.
     */
    @ApplicationScoped
    public static class MemoryFileSystemBlobStoreDAO implements IFileSystemBlobStoreDAO
    {
        private final Map<String, byte [ ]> _mapFiles = new HashMap<>( );

        /**
         * Builds the storage key of a blob at a location.
         *
         * @param strId
         *            the id
         * @param strBasePath
         *            the base path
         * @param depth
         *            the depth
         * @return the storage key
         */
        private static String path( String strId, String strBasePath, Integer depth )
        {
            return strBasePath + "#" + depth + "#" + strId;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void insert( BytesBlobStore blobStore, String strBasePath, Integer depth ) throws FileAlreadyExistsException
        {
            String strPath = path( blobStore.getId( ), strBasePath, depth );
            if ( _mapFiles.containsKey( strPath ) )
            {
                throw new FileAlreadyExistsException( strPath );
            }
            _mapFiles.put( strPath, blobStore.getValue( ) );
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void insert( InputStreamBlobStore blobStore, String strBasePath, Integer depth ) throws FileAlreadyExistsException, IOException
        {
            String strPath = path( blobStore.getId( ), strBasePath, depth );
            if ( _mapFiles.containsKey( strPath ) )
            {
                throw new FileAlreadyExistsException( strPath );
            }
            _mapFiles.put( strPath, blobStore.getInputStream( ).readAllBytes( ) );
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public BytesBlobStore load( String strId, String strBasePath, Integer depth )
        {
            byte [ ] value = _mapFiles.get( path( strId, strBasePath, depth ) );
            if ( value == null )
            {
                return null;
            }
            BytesBlobStore blob = new BytesBlobStore( );
            blob.setId( strId );
            blob.setValue( value );
            return blob;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public InputStream loadInputStream( String strId, String strBasePath, Integer depth )
        {
            byte [ ] value = _mapFiles.get( path( strId, strBasePath, depth ) );
            return value == null ? null : new ByteArrayInputStream( value );
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void store( BytesBlobStore blobStore, String strBasePath, Integer depth )
        {
            _mapFiles.put( path( blobStore.getId( ), strBasePath, depth ), blobStore.getValue( ) );
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void storeInputStream( InputStreamBlobStore blobStore, String strBasePath, Integer depth ) throws IOException
        {
            _mapFiles.put( path( blobStore.getId( ), strBasePath, depth ), blobStore.getInputStream( ).readAllBytes( ) );
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean delete( String strKey, String strBasePath, Integer depth )
        {
            return _mapFiles.remove( path( strKey, strBasePath, depth ) ) != null;
        }
    }

    /**
     * Builds a bytes blob.
     *
     * @param strId
     *            the id
     * @param strValue
     *            the value
     * @return the blob
     */
    private static BytesBlobStore bytes( String strId, String strValue )
    {
        BytesBlobStore blob = new BytesBlobStore( );
        blob.setId( strId );
        blob.setValue( strValue.getBytes( StandardCharsets.UTF_8 ) );
        return blob;
    }

    /**
     * Builds a stream blob.
     *
     * @param strId
     *            the id
     * @param strValue
     *            the value
     * @return the blob
     */
    private static InputStreamBlobStore stream( String strId, String strValue )
    {
        InputStreamBlobStore blob = new InputStreamBlobStore( );
        blob.setId( strId );
        blob.setInputStream( new ByteArrayInputStream( strValue.getBytes( StandardCharsets.UTF_8 ) ) );
        return blob;
    }

    /**
     * Create, read, update and remove reach the DAO at the location given, and a second create is refused.
     *
     * @throws Exception
     *             if the DAO fails
     */
    @Test
    public void testBytesLifecycle( ) throws Exception
    {
        _home.create( bytes( "f1", "first" ), BASE_PATH, DEPTH );
        assertThrows( FileAlreadyExistsException.class, ( ) -> _home.create( bytes( "f1", "again" ), BASE_PATH, DEPTH ) );
        assertArrayEquals( "first".getBytes( StandardCharsets.UTF_8 ), _home.findByPrimaryKey( "f1", BASE_PATH, DEPTH ).getValue( ) );
        assertNull( _home.findByPrimaryKey( "f1", "/elsewhere", DEPTH ) );

        _home.update( bytes( "f1", "second" ), BASE_PATH, DEPTH );
        assertArrayEquals( "second".getBytes( StandardCharsets.UTF_8 ), _home.findByPrimaryKey( "f1", BASE_PATH, DEPTH ).getValue( ) );

        assertTrue( _home.remove( "f1", BASE_PATH, DEPTH ) );
        assertFalse( _home.remove( "f1", BASE_PATH, DEPTH ) );
    }

    /**
     * Create, read and update of a stream blob reach the DAO at the location given.
     *
     * @throws Exception
     *             if the DAO fails
     */
    @Test
    public void testInputStreamLifecycle( ) throws Exception
    {
        _home.createInputStream( stream( "s1", "streamed" ), BASE_PATH, DEPTH );
        try ( InputStream in = _home.findByPrimaryKeyInputStream( "s1", BASE_PATH, DEPTH ) )
        {
            assertArrayEquals( "streamed".getBytes( StandardCharsets.UTF_8 ), in.readAllBytes( ) );
        }

        _home.updateInputStream( stream( "s1", "restreamed" ), BASE_PATH, DEPTH );
        try ( InputStream in = _home.findByPrimaryKeyInputStream( "s1", BASE_PATH, DEPTH ) )
        {
            assertEquals( "restreamed", new String( in.readAllBytes( ), StandardCharsets.UTF_8 ) );
        }
    }
}
