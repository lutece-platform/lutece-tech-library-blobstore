package fr.paris.lutece.plugins.blobstore.business.database;

import java.io.InputStream;

import fr.paris.lutece.plugins.blobstore.business.BytesBlobStore;
import fr.paris.lutece.plugins.blobstore.business.InputStreamBlobStore;


public interface IDatabaseBlobStoreHome
{

    /**
     * Get the last primary key
     * @return The last primary key
     */
    public abstract String getLastPrimaryKey( );

    /**
     * Creation of an instance of record physical file
     * @param blobStore The instance of the physical file which contains the
     *            informations to store
     */
    public abstract void create( BytesBlobStore blobStore );

    /**
     * Update of physical file which is specified in parameter
     * @param blobStore The instance of the record physicalFile which contains
     *            the informations to update
     */
    public abstract void update( BytesBlobStore blobStore );

    /**
     * Update of physical file which is specified in parameter
     * @param blobStore The instance of the record physicalFile which contains
     *            the informations to update
     */
    public abstract void updateInputStream( InputStreamBlobStore blobStore );

    /**
     * Delete the physical file whose identifier is specified in parameter
     * @param strKey The identifier of the record physical file
     */
    public abstract void remove( String strKey );

    /**
     * Returns an instance of a physical file whose identifier is specified in
     * parameter
     * 
     * @param strKey The file primary key
     * @return an instance of physical file
     */
    public abstract BytesBlobStore findByPrimaryKey( String strKey );

    /**
     * Returns an instance of a physical file whose identifier is specified in
     * parameter
     * 
     * @param strKey The file primary key
     * @return an instance of physical file
     */
    public abstract InputStream findByPrimaryKeyInputStream( String strKey );

    /**
     * Creation of an instance of record physical file
     * @param blobStore The instance of the physical file which contains the
     *            inputstream to store
     */
    public abstract void createInputStream( InputStreamBlobStore blobStore );

}