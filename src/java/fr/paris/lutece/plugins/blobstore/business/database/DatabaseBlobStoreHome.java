/*
 * Copyright (c) 2002-2021, City of Paris
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

import fr.paris.lutece.plugins.blobstore.business.BytesBlobStore;
import fr.paris.lutece.plugins.blobstore.business.InputStreamBlobStore;

import java.io.InputStream;

import javax.inject.Inject;

/**
 * This class provides instances management methods (create, find, ...) for physical file objects
 */
public final class DatabaseBlobStoreHome implements IDatabaseBlobStoreHome
{
    public static final String BEAN_SERVICE = "blobstore.databaseBlobStoreHome";
    @Inject
    private IDatabaseBlobStoreDAO _dao;

    /*
     * (non-Javadoc)
     * 
     * @see fr.paris.lutece.plugins.blobstore.business.database.IDatabaseBlobStoreHome#getLastPrimaryKey()
     */
    @Override
    public String getLastPrimaryKey( )
    {
        return _dao.loadLastPrimaryKey( );
    }

    /*
     * (non-Javadoc)
     * 
     * @see fr.paris.lutece.plugins.blobstore.business.database.IDatabaseBlobStoreHome#create(fr.paris.lutece.plugins.blobstore.business.BytesBlobStore)
     */
    @Override
    public void create( BytesBlobStore blobStore )
    {
        _dao.insert( blobStore );
    }

    /*
     * (non-Javadoc)
     * 
     * @see fr.paris.lutece.plugins.blobstore.business.database.IDatabaseBlobStoreHome#update(fr.paris.lutece.plugins.blobstore.business.BytesBlobStore)
     */
    @Override
    public void update( BytesBlobStore blobStore )
    {
        _dao.store( blobStore );
    }

    /*
     * (non-Javadoc)
     * 
     * @see fr.paris.lutece.plugins.blobstore.business.database.IDatabaseBlobStoreHome#updateInputStream(fr.paris.lutece.plugins.blobstore.business.
     * InputStreamBlobStore)
     */
    @Override
    public void updateInputStream( InputStreamBlobStore blobStore )
    {
        _dao.store( blobStore );
    }

    /*
     * (non-Javadoc)
     * 
     * @see fr.paris.lutece.plugins.blobstore.business.database.IDatabaseBlobStoreHome#remove(java.lang.String)
     */
    @Override
    public void remove( String strKey )
    {
        _dao.delete( strKey );
    }

    /*
     * (non-Javadoc)
     * 
     * @see fr.paris.lutece.plugins.blobstore.business.database.IDatabaseBlobStoreHome#findByPrimaryKey(java.lang.String)
     */
    @Override
    public BytesBlobStore findByPrimaryKey( String strKey )
    {
        return _dao.load( strKey );
    }

    /*
     * (non-Javadoc)
     * 
     * @see fr.paris.lutece.plugins.blobstore.business.database.IDatabaseBlobStoreHome#findByPrimaryKeyInputStream(java.lang.String)
     */
    @Override
    public InputStream findByPrimaryKeyInputStream( String strKey )
    {
        return _dao.loadInputStream( strKey );
    }

    /*
     * (non-Javadoc)
     * 
     * @see fr.paris.lutece.plugins.blobstore.business.database.IDatabaseBlobStoreHome#createInputStream(fr.paris.lutece.plugins.blobstore.business.
     * InputStreamBlobStore)
     */
    @Override
    public void createInputStream( InputStreamBlobStore blobStore )
    {
        _dao.insert( blobStore );
    }
}
