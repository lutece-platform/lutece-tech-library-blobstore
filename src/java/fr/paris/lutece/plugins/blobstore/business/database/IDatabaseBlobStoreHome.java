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
package fr.paris.lutece.plugins.blobstore.business.database;

import fr.paris.lutece.plugins.blobstore.business.BytesBlobStore;
import fr.paris.lutece.plugins.blobstore.business.InputStreamBlobStore;

import java.io.InputStream;


/**
 * The Interface IDatabaseBlobStoreHome.
 */
public interface IDatabaseBlobStoreHome
{
    /**
     * Get the last primary key.
     *
     * @return The last primary key
     */
    String getLastPrimaryKey(  );

    /**
     * Creation of an instance of record physical file.
     *
     * @param blobStore The instance of the physical file which contains the
     *            informations to store
     */
    void create( BytesBlobStore blobStore );

    /**
     * Update of physical file which is specified in parameter.
     *
     * @param blobStore The instance of the record physicalFile which contains
     *            the informations to update
     */
    void update( BytesBlobStore blobStore );

    /**
     * Update of physical file which is specified in parameter.
     *
     * @param blobStore The instance of the record physicalFile which contains
     *            the informations to update
     */
    void updateInputStream( InputStreamBlobStore blobStore );

    /**
     * Delete the physical file whose identifier is specified in parameter.
     *
     * @param strKey The identifier of the record physical file
     */
    void remove( String strKey );

    /**
     * Returns an instance of a physical file whose identifier is specified in
     * parameter.
     *
     * @param strKey The file primary key
     * @return an instance of physical file
     */
    BytesBlobStore findByPrimaryKey( String strKey );

    /**
     * Returns an instance of a physical file whose identifier is specified in
     * parameter.
     *
     * @param strKey The file primary key
     * @return an instance of physical file
     */
    InputStream findByPrimaryKeyInputStream( String strKey );

    /**
     * Creation of an instance of record physical file.
     *
     * @param blobStore The instance of the physical file which contains the
     *            inputstream to store
     */
    void createInputStream( InputStreamBlobStore blobStore );
}
