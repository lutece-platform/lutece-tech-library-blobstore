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

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Tests the blob-backed file item: metadata format, lazy content, deletion and refusals.
 */
public class BlobStoreFileItemTest
{
    private static final byte [ ] CONTENT = "Lutece blob content\n".getBytes( StandardCharsets.UTF_8 );

    private MemoryBlobStoreService _service;

    /**
     * Creates an empty in-memory store.
     */
    @BeforeEach
    public void setUp( )
    {
        _service = new MemoryBlobStoreService( );
    }

    /**
     * Stores a content blob and its metadata blob, the way plugin-blobstore does.
     *
     * @param strFileName
     *            the file name
     * @param strContentType
     *            the content type
     * @return the metadata blob key
     */
    private String storeFile( String strFileName, String strContentType )
    {
        String strFileKey = _service.store( CONTENT );
        String strMetadata = BlobStoreFileItem.buildFileMetadata( strFileName, CONTENT.length, strFileKey, strContentType );
        return _service.store( strMetadata.getBytes( ) );
    }

    /**
     * The metadata is a JSON object carrying the four documented keys, the size written as a string.
     *
     * @throws Exception
     *             if the metadata is not JSON
     */
    @Test
    public void testBuildFileMetadataFormat( ) throws Exception
    {
        JsonNode json = new ObjectMapper( ).readTree( BlobStoreFileItem.buildFileMetadata( "report.pdf", 1234L, "key-1", "application/pdf" ) );

        assertEquals( 4, json.size( ) );
        assertEquals( "1234", json.get( BlobStoreFileItem.JSON_KEY_FILE_SIZE ).textValue( ) );
        assertEquals( "report.pdf", json.get( BlobStoreFileItem.JSON_KEY_FILE_NAME ).textValue( ) );
        assertEquals( "key-1", json.get( BlobStoreFileItem.JSON_KEY_FILE_BLOB_ID ).textValue( ) );
        assertEquals( "application/pdf", json.get( BlobStoreFileItem.JSON_KEY_FILE_CONTENT_TYPE ).textValue( ) );
    }

    /**
     * A stored file reads back its name, size, type and content, lazily from the content blob.
     *
     * @throws Exception
     *             if the blob cannot be read
     */
    @Test
    public void testRoundTrip( ) throws Exception
    {
        String strMetadataKey = storeFile( "note.txt", "text/plain" );

        BlobStoreFileItem item = new BlobStoreFileItem( strMetadataKey, _service );

        assertEquals( strMetadataKey, item.getBlobId( ) );
        assertEquals( "note.txt", item.getName( ) );
        assertEquals( CONTENT.length, item.getSize( ) );
        assertEquals( "text/plain", item.getContentType( ) );
        assertNotNull( item.getFileBlobId( ) );
        assertArrayEquals( CONTENT, item.get( ) );
        assertEquals( new String( CONTENT, StandardCharsets.UTF_8 ), item.getString( "UTF-8" ) );
        try ( InputStream in = item.getInputStream( ) )
        {
            assertArrayEquals( CONTENT, in.readAllBytes( ) );
        }
        assertNull( item.getFieldName( ) );
        assertFalse( item.isFormField( ) );
        assertFalse( item.isInMemory( ) );
    }

    /**
     * The content is read when asked for, not when the item is built: a content changed in between is the one returned.
     *
     * @throws Exception
     *             if the blob cannot be read
     */
    @Test
    public void testContentIsReadLazily( ) throws Exception
    {
        BlobStoreFileItem item = new BlobStoreFileItem( storeFile( "lazy.txt", "text/plain" ), _service );
        byte [ ] updated = "updated".getBytes( StandardCharsets.UTF_8 );

        _service.update( item.getFileBlobId( ), updated );

        assertArrayEquals( updated, item.get( ) );
    }

    /**
     * A file name outside ASCII survives the metadata round trip.
     *
     * @throws Exception
     *             if the blob cannot be read
     */
    @Test
    public void testNonAsciiFileName( ) throws Exception
    {
        String strName = "déclaration d'impôts \"2026\" 文件.pdf";

        BlobStoreFileItem item = new BlobStoreFileItem( storeFile( strName, "application/pdf" ), _service );

        assertEquals( strName, item.getName( ) );
    }

    /**
     * Metadata written by the v7 library (json-lib) still loads.
     *
     * @throws Exception
     *             if the blob cannot be read
     */
    @Test
    public void testReadsMetadataWrittenByV7( ) throws Exception
    {
        String strFileKey = _service.store( CONTENT );
        String strV7 = "{\"fileSize\":\"" + CONTENT.length + "\",\"fileName\":\"old.txt\",\"fileBlobId\":\"" + strFileKey
                + "\",\"fileContentType\":\"text/plain\"}";

        BlobStoreFileItem item = new BlobStoreFileItem( _service.store( strV7.getBytes( ) ), _service );

        assertEquals( "old.txt", item.getName( ) );
        assertEquals( CONTENT.length, item.getSize( ) );
        assertArrayEquals( CONTENT, item.get( ) );
    }

    /**
     * Deleting the item removes both the metadata blob and the content blob.
     *
     * @throws Exception
     *             if the blob cannot be read
     */
    @Test
    public void testDeleteRemovesBothBlobs( ) throws Exception
    {
        BlobStoreFileItem item = new BlobStoreFileItem( storeFile( "gone.txt", "text/plain" ), _service );
        assertEquals( 2, _service.size( ) );

        item.delete( );

        assertEquals( 0, _service.size( ) );
        assertNull( _service.getBlob( item.getBlobId( ) ) );
        assertNull( _service.getBlob( item.getFileBlobId( ) ) );
    }

    /**
     * An unknown key is refused with NoSuchBlobException.
     */
    @Test
    public void testUnknownKeyRefused( )
    {
        assertThrows( NoSuchBlobException.class, ( ) -> new BlobStoreFileItem( "unknown", _service ) );
    }

    /**
     * A blob that is not JSON is refused with NoSuchBlobException.
     */
    @Test
    public void testNotJsonRefused( )
    {
        String strKey = _service.store( CONTENT );

        assertThrows( NoSuchBlobException.class, ( ) -> new BlobStoreFileItem( strKey, _service ) );
    }

    /**
     * An empty blob is refused with NoSuchBlobException.
     */
    @Test
    public void testEmptyBlobRefused( )
    {
        String strKey = _service.store( new byte [ 0 ] );

        assertThrows( NoSuchBlobException.class, ( ) -> new BlobStoreFileItem( strKey, _service ) );
    }

    /**
     * JSON that lacks a metadata key is refused with NoSuchBlobException.
     */
    @Test
    public void testIncompleteMetadataRefused( )
    {
        String strKey = _service.store( "{\"fileName\":\"a.txt\",\"fileBlobId\":\"x\",\"fileContentType\":\"text/plain\"}".getBytes( ) );

        assertThrows( NoSuchBlobException.class, ( ) -> new BlobStoreFileItem( strKey, _service ) );
    }

    /**
     * JSON whose size is not a number is refused with NoSuchBlobException.
     */
    @Test
    public void testNonNumericSizeRefused( )
    {
        String strKey = _service.store( BlobStoreFileItem.buildFileMetadata( "a.txt", 1, "x", "text/plain" ).replace( "\"1\"", "\"one\"" ).getBytes( ) );

        assertThrows( NoSuchBlobException.class, ( ) -> new BlobStoreFileItem( strKey, _service ) );
    }

    /**
     * A JSON value that is not an object is refused with NoSuchBlobException.
     */
    @Test
    public void testJsonArrayRefused( )
    {
        String strKey = _service.store( "[1,2,3]".getBytes( ) );

        assertThrows( NoSuchBlobException.class, ( ) -> new BlobStoreFileItem( strKey, _service ) );
    }

    /**
     * Writing the item to a file or an output stream is not supported.
     *
     * @throws Exception
     *             if the blob cannot be read
     */
    @Test
    public void testWriteUnsupported( ) throws Exception
    {
        BlobStoreFileItem item = new BlobStoreFileItem( storeFile( "w.txt", "text/plain" ), _service );

        assertThrows( UnsupportedOperationException.class, ( ) -> item.write( null ) );
        assertThrows( UnsupportedOperationException.class, item::getOutputStream );
    }
}
