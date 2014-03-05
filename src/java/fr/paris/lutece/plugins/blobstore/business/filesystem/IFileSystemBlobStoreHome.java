package fr.paris.lutece.plugins.blobstore.business.filesystem;

import java.io.IOException;
import java.io.InputStream;

import fr.paris.lutece.plugins.blobstore.business.BytesBlobStore;
import fr.paris.lutece.plugins.blobstore.business.InputStreamBlobStore;


public interface IFileSystemBlobStoreHome
{

    /**
     * Creation of an instance of record physical file
     * @param blobStore The instance of the physical file which contains the
     *            informations to store
     * @param strBasePath base directory
     * @throws FileAlreadyExistsException already exists
     * @throws IOException ioe
     */
    public abstract void create( BytesBlobStore blobStore, String strBasePath ) throws IOException,
            FileAlreadyExistsException;

    /**
     * Update of physical file which is specified in parameter
     * @param blobStore The instance of the record physicalFile which contains
     *            the informations to update
     * @param strBasePath base directory
     * @throws IOException ioe
     */
    public abstract void update( BytesBlobStore blobStore, String strBasePath ) throws IOException;

    /**
     * Update of physical file which is specified in parameter
     * @param blobStore The instance of the record physicalFile which contains
     *            the informations to update
     * @param strBasePath base directory
     * @throws IOException ioe
     */
    public abstract void updateInputStream( InputStreamBlobStore blobStore, String strBasePath ) throws IOException;

    /**
     * Delete the physical file whose identifier is specified in parameter
     * @param strKey The identifier of the record physical file
     * @param strBasePath base directory
     * @return <code>true</code> if the file is removed, <code>false</code>
     *         otherwise.
     * @throws IOException ioe
     */
    public abstract boolean remove( String strKey, String strBasePath ) throws IOException;

    /**
     * Returns an instance of a physical file whose identifier is specified in
     * parameter
     * 
     * @param strKey The file primary key
     * @param strBasePath base directory
     * @return an instance of physical file
     * @throws IOException ioe
     */
    public abstract BytesBlobStore findByPrimaryKey( String strKey, String strBasePath ) throws IOException;

    /**
     * Returns an instance of a physical file whose identifier is specified in
     * parameter
     * 
     * @param strKey The file primary key
     * @param strBasePath base directory
     * @return an instance of physical file
     * @throws IOException ioe
     */
    public abstract InputStream findByPrimaryKeyInputStream( String strKey, String strBasePath ) throws IOException;

    /**
     * Creation of an instance of record physical file
     * @param blobStore The instance of the physical file which contains the
     *            inputstream to store
     * @param strBasePath base directory
     * @throws IOException ioe
     * @throws FileAlreadyExistsException already exists
     */
    public abstract void createInputStream( InputStreamBlobStore blobStore, String strBasePath )
            throws FileAlreadyExistsException, IOException;

}