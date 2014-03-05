/*
 * Copyright (c) 2002-2013, Mairie de Paris
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

import java.io.IOException;
import java.io.InputStream;

import javax.inject.Inject;

import fr.paris.lutece.plugins.blobstore.business.BytesBlobStore;
import fr.paris.lutece.plugins.blobstore.business.InputStreamBlobStore;


/**
 * 
 * FileSystemBlobStoreHome
 * 
 */
public final class FileSystemBlobStoreHome implements IFileSystemBlobStoreHome
{
    public static final String BEAN_SERVICE = "blobstore.fileSystemBlobStoreHome";

    @Inject
    private IFileSystemBlobStoreDAO _dao;

    /*
     * (non-Javadoc)
     * 
     * @see fr.paris.lutece.plugins.blobstore.business.filesystem.
     * IFileSystemBlobStoreHome
     * #create(fr.paris.lutece.plugins.blobstore.business.BytesBlobStore,
     * java.lang.String)
     */
    @Override
    public void create( BytesBlobStore blobStore, String strBasePath ) throws IOException, FileAlreadyExistsException
    {
        _dao.insert( blobStore, strBasePath );
    }

    /*
     * (non-Javadoc)
     * 
     * @see fr.paris.lutece.plugins.blobstore.business.filesystem.
     * IFileSystemBlobStoreHome
     * #update(fr.paris.lutece.plugins.blobstore.business.BytesBlobStore,
     * java.lang.String)
     */
    @Override
    public void update( BytesBlobStore blobStore, String strBasePath ) throws IOException
    {
        _dao.store( blobStore, strBasePath );
    }

    /*
     * (non-Javadoc)
     * 
     * @see fr.paris.lutece.plugins.blobstore.business.filesystem.
     * IFileSystemBlobStoreHome
     * #updateInputStream(fr.paris.lutece.plugins.blobstore
     * .business.InputStreamBlobStore, java.lang.String)
     */
    @Override
    public void updateInputStream( InputStreamBlobStore blobStore, String strBasePath ) throws IOException
    {
        _dao.storeInputStream( blobStore, strBasePath );
    }

    /*
     * (non-Javadoc)
     * 
     * @see fr.paris.lutece.plugins.blobstore.business.filesystem.
     * IFileSystemBlobStoreHome#remove(java.lang.String, java.lang.String)
     */
    @Override
    public boolean remove( String strKey, String strBasePath ) throws IOException
    {
        return _dao.delete( strKey, strBasePath );
    }

    /*
     * (non-Javadoc)
     * 
     * @see fr.paris.lutece.plugins.blobstore.business.filesystem.
     * IFileSystemBlobStoreHome#findByPrimaryKey(java.lang.String,
     * java.lang.String)
     */
    @Override
    public BytesBlobStore findByPrimaryKey( String strKey, String strBasePath ) throws IOException
    {
        return _dao.load( strKey, strBasePath );
    }

    /*
     * (non-Javadoc)
     * 
     * @see fr.paris.lutece.plugins.blobstore.business.filesystem.
     * IFileSystemBlobStoreHome#findByPrimaryKeyInputStream(java.lang.String,
     * java.lang.String)
     */
    @Override
    public InputStream findByPrimaryKeyInputStream( String strKey, String strBasePath ) throws IOException
    {
        return _dao.loadInputStream( strKey, strBasePath );
    }

    /*
     * (non-Javadoc)
     * 
     * @see fr.paris.lutece.plugins.blobstore.business.filesystem.
     * IFileSystemBlobStoreHome
     * #createInputStream(fr.paris.lutece.plugins.blobstore
     * .business.InputStreamBlobStore, java.lang.String)
     */
    @Override
    public void createInputStream( InputStreamBlobStore blobStore, String strBasePath )
            throws FileAlreadyExistsException, IOException
    {
        _dao.insert( blobStore, strBasePath );
    }
}
