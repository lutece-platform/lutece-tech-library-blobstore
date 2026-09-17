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
package fr.paris.lutece.plugins.blobstore.business.filesystem;

import fr.paris.lutece.plugins.blobstore.business.BytesBlobStore;
import fr.paris.lutece.plugins.blobstore.business.InputStreamBlobStore;

import java.io.IOException;
import java.io.InputStream;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Instance;
import jakarta.inject.Inject;
import jakarta.inject.Named;

/**
 * FileSystemBlobStoreHome.
 */
@ApplicationScoped
@Named( FileSystemBlobStoreHome.BEAN_SERVICE )
public class FileSystemBlobStoreHome implements IFileSystemBlobStoreHome
{
    /** The Constant BEAN_SERVICE. */
    public static final String BEAN_SERVICE = "blobstore.fileSystemBlobStoreHome";

    /** The getDao( ). */
    @Inject
    private Instance<IFileSystemBlobStoreDAO> _dao;

    /**
     * Resolves the DAO, whose implementation is deployed by plugin-blobstore.
     * 
     * @return the DAO
     * @throws jakarta.enterprise.inject.UnsatisfiedResolutionException
     *             when no blob store DAO is deployed
     */
    private IFileSystemBlobStoreDAO getDao( )
    {
        return _dao.get( );
    }

    /*
     * (non-Javadoc)
     *
     * @see fr.paris.lutece.plugins.blobstore.business.filesystem. IFileSystemBlobStoreHome #create(fr.paris.lutece.plugins.blobstore.business.BytesBlobStore,
     * java.lang.String, java.lang.Integer)
     */
    @Override
    public void create( final BytesBlobStore blobStore, final String strBasePath, final Integer depth ) throws IOException, FileAlreadyExistsException
    {
        getDao( ).insert( blobStore, strBasePath, depth );
    }

    /*
     * (non-Javadoc)
     *
     * @see fr.paris.lutece.plugins.blobstore.business.filesystem. IFileSystemBlobStoreHome #update(fr.paris.lutece.plugins.blobstore.business.BytesBlobStore,
     * java.lang.String, java.lang.Integer)
     */
    @Override
    public void update( final BytesBlobStore blobStore, final String strBasePath, final Integer depth ) throws IOException
    {
        getDao( ).store( blobStore, strBasePath, depth );
    }

    /*
     * (non-Javadoc)
     *
     * @see fr.paris.lutece.plugins.blobstore.business.filesystem. IFileSystemBlobStoreHome #updateInputStream(fr.paris.lutece.plugins.blobstore
     * .business.InputStreamBlobStore, java.lang.String, java.lang.Integer)
     */
    @Override
    public void updateInputStream( final InputStreamBlobStore blobStore, final String strBasePath, final Integer depth ) throws IOException
    {
        getDao( ).storeInputStream( blobStore, strBasePath, depth );
    }

    /*
     * (non-Javadoc)
     *
     * @see fr.paris.lutece.plugins.blobstore.business.filesystem. IFileSystemBlobStoreHome#remove(java.lang.String, java.lang.String, java.lang.Integer)
     */
    @Override
    public boolean remove( final String strKey, final String strBasePath, final Integer depth ) throws IOException
    {
        return getDao( ).delete( strKey, strBasePath, depth );
    }

    /*
     * (non-Javadoc)
     *
     * @see fr.paris.lutece.plugins.blobstore.business.filesystem. IFileSystemBlobStoreHome#findByPrimaryKey(java.lang.String, java.lang.String,
     * java.lang.Integer)
     */
    @Override
    public BytesBlobStore findByPrimaryKey( final String strKey, final String strBasePath, final Integer depth ) throws IOException
    {
        return getDao( ).load( strKey, strBasePath, depth );
    }

    /*
     * (non-Javadoc)
     *
     * @see fr.paris.lutece.plugins.blobstore.business.filesystem. IFileSystemBlobStoreHome#findByPrimaryKeyInputStream(java.lang.String, java.lang.String,
     * java.lang.Integer)
     */
    @Override
    public InputStream findByPrimaryKeyInputStream( final String strKey, final String strBasePath, final Integer depth ) throws IOException
    {
        return getDao( ).loadInputStream( strKey, strBasePath, depth );
    }

    /*
     * (non-Javadoc)
     *
     * @see fr.paris.lutece.plugins.blobstore.business.filesystem. IFileSystemBlobStoreHome #createInputStream(fr.paris.lutece.plugins.blobstore
     * .business.InputStreamBlobStore, java.lang.String, java.lang.Integer)
     */
    @Override
    public void createInputStream( final InputStreamBlobStore blobStore, final String strBasePath, final Integer depth )
            throws FileAlreadyExistsException, IOException
    {
        getDao( ).insert( blobStore, strBasePath, depth );
    }
}
