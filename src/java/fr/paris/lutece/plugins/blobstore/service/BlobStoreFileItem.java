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
package fr.paris.lutece.plugins.blobstore.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import fr.paris.lutece.portal.service.upload.MultipartItem;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

/**
 * Builds a fileItem from blobstore implementing {@link MultipartItem}. <br>
 * Metadata is stored in one blob, and content in another one. get() method is lazy preventing blob to be stored in-memory. Use
 * {@link #buildFileMetadata(String, long, String)} to build the FileMetadata.
 * 
 * @see #buildFileMetadata(String, long, String)
 * @see #BlobStoreFileItem(String, IBlobStoreService)
 *
 */
public class BlobStoreFileItem implements MultipartItem
{
    public static final String JSON_KEY_FILE_SIZE = "fileSize";
    public static final String JSON_KEY_FILE_NAME = "fileName";
    public static final String JSON_KEY_FILE_CONTENT_TYPE = "fileContentType";
    public static final String JSON_KEY_FILE_BLOB_ID = "fileBlobId";
    public static final String JSON_KEY_FILE_METADATA_BLOB_ID = "fileMetadata";
    private static final long serialVersionUID = 1L;
    private static Logger _logger = LogManager.getLogger( "lutece.blobstore" );
    private static final ObjectMapper MAPPER = new ObjectMapper( );
    private final IBlobStoreService _blobstoreService;
    private final String _strBlobId;
    private String _strFileName;
    private long _lFileSize;
    private String _strFileBlobId;
    private String _strContentType;

    /**
     * Builds a fileItem from blobstore. get() method is lazy. The {@link IBlobStoreService} is here to prevent specific usage for the fileItem so it can be
     * used as any other MultipartItem.
     * 
     * @param strBlobId
     *            the blob id
     * @param blobstoreService
     *            the blob service
     * @throws NoSuchBlobException
     *             if blob cannot be parsed
     */
    public BlobStoreFileItem( String strBlobId, IBlobStoreService blobstoreService ) throws NoSuchBlobException
    {
        _strBlobId = strBlobId;
        _blobstoreService = blobstoreService;

        // first, get the metadata
        byte [ ] blob = _blobstoreService.getBlob( _strBlobId );

        if ( blob == null )
        {
            throw new NoSuchBlobException( "No blob found for id " + strBlobId );
        }

        JsonNode jsonObject = parseBlob( blob );

        if ( jsonObject == null || !jsonObject.hasNonNull( JSON_KEY_FILE_SIZE ) || !jsonObject.hasNonNull( JSON_KEY_FILE_NAME )
                || !jsonObject.hasNonNull( JSON_KEY_FILE_BLOB_ID ) || !jsonObject.has( JSON_KEY_FILE_CONTENT_TYPE ) )
        {
            throw new NoSuchBlobException( strBlobId );
        }

        try
        {
            _lFileSize = Long.parseLong( jsonObject.get( JSON_KEY_FILE_SIZE ).asText( ) );
        }
        catch( NumberFormatException e )
        {
            throw new NoSuchBlobException( strBlobId );
        }

        _strFileName = jsonObject.get( JSON_KEY_FILE_NAME ).asText( );
        // store the real blob id - file will be fetch on demand (#get)
        _strFileBlobId = jsonObject.get( JSON_KEY_FILE_BLOB_ID ).asText( );
        _strContentType = jsonObject.get( JSON_KEY_FILE_CONTENT_TYPE ).asText( );
    }

    /**
     * Gets the metadata blob id
     * 
     * @return the metadata blob id
     */
    public String getBlobId( )
    {
        return _strBlobId;
    }

    /**
     * Gets the file blob id
     * 
     * @return the file blob id
     */
    public String getFileBlobId( )
    {
        return _strFileBlobId;
    }

    /**
     * Deletes both blobs : metadata <strong>AND</strong> content.
     */
    @Override
    public void delete( )
    {
        _blobstoreService.delete( _strFileBlobId );
        _blobstoreService.delete( _strBlobId );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public byte [ ] get( )
    {
        return _blobstoreService.getBlob( _strFileBlobId );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getContentType( )
    {
        return _strContentType;
    }

    /**
     * Not supported
     * 
     * @return null
     */
    @Override
    public String getFieldName( )
    {
        return null;
    }

    /**
     * {@inheritDoc}
     * 
     * @throws IOException
     *             ioexception
     */
    @Override
    public InputStream getInputStream( ) throws IOException
    {
        return _blobstoreService.getBlobInputStream( _strFileBlobId );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName( )
    {
        return _strFileName;
    }

    /**
     * Not supported - throws UnsupportedOperationException exception
     * 
     * @return nothing
     * @throws IOException
     *             ioe
     */
    public OutputStream getOutputStream( ) throws IOException
    {
        throw new UnsupportedOperationException( );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long getSize( )
    {
        return _lFileSize;
    }

    /**
     * {@inheritDoc}
     */
    public String getString( )
    {
        return new String( get( ) );
    }

    /**
     * {@inheritDoc}
     */
    public String getString( String encoding ) throws UnsupportedEncodingException
    {
        return new String( get( ), encoding );
    }

    /**
     * Not supported
     * 
     * @return false
     */
    public boolean isFormField( )
    {
        return false;
    }

    /**
     * Always false.
     * 
     * @return false
     */
    public boolean isInMemory( )
    {
        return false;
    }

    /**
     * Not supported
     * 
     * @param name
     *            -
     */
    public void setFieldName( String name )
    {
        // nothing
    }

    /**
     * Not supported
     * 
     * @param state
     *            -
     */
    public void setFormField( boolean state )
    {
        // nothing
    }

    /**
     * Not supported
     * 
     * @param file
     *            -
     * @throws Exception
     *             ex
     */
    public void write( File file ) throws Exception
    {
        throw new UnsupportedOperationException( );
    }

    /**
     * {@inheritDoc}
     */
    public String toString( )
    {
        return "BlobId:" + _strBlobId + " FileBlobId:" + _strFileBlobId + " FileName:" + _strFileName;
    }

    /**
     * Parses a blob to a json node
     * 
     * @param blob
     *            the blob
     * @return the parsed object, <code>null</code> if blob is null, is not a JSON object or an exception occur
     */
    private static JsonNode parseBlob( byte [ ] blob )
    {
        if ( blob == null )
        {
            return null;
        }

        try
        {
            JsonNode node = MAPPER.readTree( new String( blob ) );

            return node.isObject( ) ? node : null;
        }
        catch( IOException ioe )
        {
            _logger.error( ioe.getMessage( ), ioe );
        }

        return null;
    }

    /**
     * Builds the json value of a file metadata.
     * 
     * @param strFileName
     *            filename
     * @param lSize
     *            size
     * @param strFileBlobId
     *            the blob id
     * @param strContentType
     *            the content type
     * @return the json of the fileMetadata to store in BlobStore
     */
    public static final String buildFileMetadata( String strFileName, long lSize, String strFileBlobId, String strContentType )
    {
        ObjectNode json = MAPPER.createObjectNode( );
        json.put( BlobStoreFileItem.JSON_KEY_FILE_SIZE, Long.toString( lSize ) );
        json.put( BlobStoreFileItem.JSON_KEY_FILE_NAME, strFileName );
        json.put( BlobStoreFileItem.JSON_KEY_FILE_BLOB_ID, strFileBlobId );
        json.put( BlobStoreFileItem.JSON_KEY_FILE_CONTENT_TYPE, strContentType );

        return json.toString( );
    }

}
