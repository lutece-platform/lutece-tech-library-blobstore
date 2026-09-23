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
package fr.paris.lutece.plugins.blobstore.service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.util.HashMap;
import java.util.Map;

import fr.paris.lutece.plugins.blobstore.util.BlobStoreLibUtils;
import fr.paris.lutece.portal.service.upload.MultipartItem;

/**
 * In-memory blob store used as the storage behind the library classes under test.
 */
public class MemoryBlobStoreService implements IBlobStoreService
{
    private static final long serialVersionUID = 1L;
    private final Map<String, byte [ ]> _mapBlobs = new HashMap<>( );
    private String _strName = "memory";

    /**
     * Returns the number of blobs held.
     *
     * @return the number of blobs
     */
    public int size( )
    {
        return _mapBlobs.size( );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String store( byte [ ] blob )
    {
        String strKey = BlobStoreLibUtils.generateNewIdBlob( );
        _mapBlobs.put( strKey, blob );
        return strKey;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String storeInputStream( InputStream inputStream )
    {
        try
        {
            return store( inputStream.readAllBytes( ) );
        }
        catch( IOException e )
        {
            throw new UncheckedIOException( e );
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public byte [ ] getBlob( String strKey )
    {
        return _mapBlobs.get( strKey );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public InputStream getBlobInputStream( String strKey )
    {
        byte [ ] blob = _mapBlobs.get( strKey );
        return blob == null ? null : new ByteArrayInputStream( blob );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String storeFileItem( MultipartItem fileItem )
    {
        String strFileKey = store( fileItem.get( ) );
        String strMetadata = BlobStoreFileItem.buildFileMetadata( fileItem.getName( ), fileItem.getSize( ), strFileKey, fileItem.getContentType( ) );
        return store( strMetadata.getBytes( ) );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void update( String strKey, byte [ ] blob )
    {
        _mapBlobs.put( strKey, blob );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void updateInputStream( String strKey, InputStream inputStream )
    {
        try
        {
            update( strKey, inputStream.readAllBytes( ) );
        }
        catch( IOException e )
        {
            throw new UncheckedIOException( e );
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void delete( String strKey )
    {
        _mapBlobs.remove( strKey );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getBlobUrl( String strKey )
    {
        return "blob/" + strKey;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getFileUrl( String strKey )
    {
        return "file/" + strKey;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName( )
    {
        return _strName;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setName( String strName )
    {
        _strName = strName;
    }
}
