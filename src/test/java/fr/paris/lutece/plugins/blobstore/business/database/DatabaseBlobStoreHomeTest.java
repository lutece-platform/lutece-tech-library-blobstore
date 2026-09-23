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

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
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
 * Tests that the database home resolves the DAO deployed beside it through CDI and delegates every call to it.
 */
@EnableWeld
public class DatabaseBlobStoreHomeTest
{
    @WeldSetup
    public WeldInitiator _weld = WeldInitiator.from( DatabaseBlobStoreHome.class, MemoryDatabaseBlobStoreDAO.class ).build( );

    @Inject
    @Named( DatabaseBlobStoreHome.BEAN_SERVICE )
    private IDatabaseBlobStoreHome _home;

    @Inject
    private MemoryDatabaseBlobStoreDAO _dao;

    /**
     * In-memory DAO standing for the one plugin-blobstore deploys.
     */
    @ApplicationScoped
    public static class MemoryDatabaseBlobStoreDAO implements IDatabaseBlobStoreDAO
    {
        private final Map<String, byte [ ]> _mapRows = new HashMap<>( );
        private String _strLastKey;

        /**
         * Returns the number of rows held.
         *
         * @return the number of rows
         */
        public int size( )
        {
            return _mapRows.size( );
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public String loadLastPrimaryKey( )
        {
            return _strLastKey;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void insert( BytesBlobStore blobStore )
        {
            _mapRows.put( blobStore.getId( ), blobStore.getValue( ) );
            _strLastKey = blobStore.getId( );
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public BytesBlobStore load( String strId )
        {
            if ( !_mapRows.containsKey( strId ) )
            {
                return null;
            }
            BytesBlobStore blob = new BytesBlobStore( );
            blob.setId( strId );
            blob.setValue( _mapRows.get( strId ) );
            return blob;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public InputStream loadInputStream( String strId )
        {
            return _mapRows.containsKey( strId ) ? new ByteArrayInputStream( _mapRows.get( strId ) ) : null;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void delete( String strId )
        {
            _mapRows.remove( strId );
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void store( BytesBlobStore blobStore )
        {
            _mapRows.put( blobStore.getId( ), blobStore.getValue( ) );
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void insert( InputStreamBlobStore blobStore )
        {
            _mapRows.put( blobStore.getId( ), readAll( blobStore.getInputStream( ) ) );
            _strLastKey = blobStore.getId( );
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void store( InputStreamBlobStore blobStore )
        {
            _mapRows.put( blobStore.getId( ), readAll( blobStore.getInputStream( ) ) );
        }

        /**
         * Reads a whole stream.
         *
         * @param in
         *            the stream
         * @return its bytes
         */
        private static byte [ ] readAll( InputStream in )
        {
            try
            {
                return in.readAllBytes( );
            }
            catch( IOException e )
            {
                throw new UncheckedIOException( e );
            }
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
     * Create, read, update and remove of a bytes blob reach the DAO.
     */
    @Test
    public void testBytesLifecycle( )
    {
        _home.create( bytes( "k1", "first" ) );
        assertEquals( "k1", _home.getLastPrimaryKey( ) );
        assertArrayEquals( "first".getBytes( StandardCharsets.UTF_8 ), _home.findByPrimaryKey( "k1" ).getValue( ) );

        _home.update( bytes( "k1", "second" ) );
        assertArrayEquals( "second".getBytes( StandardCharsets.UTF_8 ), _home.findByPrimaryKey( "k1" ).getValue( ) );

        _home.remove( "k1" );
        assertNull( _home.findByPrimaryKey( "k1" ) );
        assertEquals( 0, _dao.size( ) );
    }

    /**
     * Create, read and update of a stream blob reach the DAO.
     *
     * @throws IOException
     *             if the stream cannot be read
     */
    @Test
    public void testInputStreamLifecycle( ) throws IOException
    {
        _home.createInputStream( stream( "s1", "streamed" ) );
        try ( InputStream in = _home.findByPrimaryKeyInputStream( "s1" ) )
        {
            assertArrayEquals( "streamed".getBytes( StandardCharsets.UTF_8 ), in.readAllBytes( ) );
        }

        _home.updateInputStream( stream( "s1", "restreamed" ) );
        try ( InputStream in = _home.findByPrimaryKeyInputStream( "s1" ) )
        {
            assertArrayEquals( "restreamed".getBytes( StandardCharsets.UTF_8 ), in.readAllBytes( ) );
        }
        assertEquals( 1, _dao.size( ) );
    }

    /**
     * The home is one application-scoped bean, found by its CDI name.
     */
    @Test
    public void testHomeIsASingleNamedBean( )
    {
        assertEquals( 1, _weld.getBeanManager( ).getBeans( DatabaseBlobStoreHome.BEAN_SERVICE ).size( ) );
    }
}
