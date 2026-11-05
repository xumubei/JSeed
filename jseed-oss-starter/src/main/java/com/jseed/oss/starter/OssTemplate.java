package com.jseed.oss.starter;

import com.jseed.oss.starter.model.BladeFile;
import com.jseed.oss.starter.model.OssFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.InputStream;
import java.util.List;

/**
 * Object storage API
 *
 * @author cyrus
 */
public interface OssTemplate {

	/**
	 * Creates a bucket.
	 *
	 * @param bucketName bucket name
	 */
	void makeBucket(String bucketName);

	/**
	 * Deletes a bucket.
	 *
	 * @param bucketName bucket name
	 */
	void removeBucket(String bucketName);

	/**
	 * Checks whether the bucket exists.
	 *
	 * @param bucketName bucket name
	 * @return boolean
	 */
	boolean bucketExists(String bucketName);

	/**
	 * Copies an object.
	 *
	 * @param bucketName     source bucket name
	 * @param fileName       source object name
	 * @param destBucketName destination bucket name
	 */
	void copyFile(String bucketName, String fileName, String destBucketName);

	/**
	 * Copies an object.
	 *
	 * @param bucketName     source bucket name
	 * @param fileName       source object name
	 * @param destBucketName destination bucket name
	 * @param destFileName   destination object name
	 */
	void copyFile(String bucketName, String fileName, String destBucketName, String destFileName);

	/**
	 * Gets object metadata.
	 *
	 * @param fileName object name
	 * @return InputStream
	 */
	OssFile statFile(String fileName);

	/**
	 * Gets object metadata.
	 *
	 * @param bucketName bucket name
	 * @param fileName   object name
	 * @return InputStream
	 */
	OssFile statFile(String bucketName, String fileName);

	/**
	 * Gets the object's relative path.
	 *
	 * @param fileName object name
	 * @return String
	 */
	String filePath(String fileName);

	/**
	 * Gets the object's relative path.
	 *
	 * @param bucketName bucket name
	 * @param fileName   object name
	 * @return String
	 */
	String filePath(String bucketName, String fileName);

	/**
	 * Gets the object URL.
	 *
	 * @param fileName object name
	 * @return String
	 */
	String fileLink(String fileName);

	/**
	 * Gets the object URL.
	 *
	 * @param bucketName bucket name
	 * @param fileName   object name
	 * @return String
	 */
	String fileLink(String bucketName, String fileName);

	/**
	 * Uploads a file.
	 *
	 * @param file file to upload
	 * @return BladeFile
	 */
	BladeFile putFile(MultipartFile file);

	/**
	 * Uploads a file.
	 *
	 * @param file     file to upload
	 * @param fileName upload file name
	 * @return BladeFile
	 */
	BladeFile putFile(String fileName, MultipartFile file);

	/**
	 * Uploads a file.
	 *
	 * @param bucketName bucket name
	 * @param fileName   upload file name
	 * @param file       file to upload
	 * @return BladeFile
	 */
	BladeFile putFile(String bucketName, String fileName, MultipartFile file);


	/**
	 * Uploads a file.
	 * @param fileName object name to preserve
	 * @param contentBytes file contents
	 * @return metadata for the uploaded object
	 */
	BladeFile putFile(String fileName, byte[] contentBytes);

	/**
	 * Uploads a file.
	 *
	 * @param fileName object name
	 * @param stream   file input stream
	 * @return BladeFile
	 */
	BladeFile putFile(String fileName, InputStream stream);

	/**
	 * Uploads a file.
	 *
	 * @param fileName object name
	 * @param stream   file input stream
	 * @param isRename whether to generate a new object name
	 * @return
	 */
    BladeFile putFile(String fileName, InputStream stream, boolean isRename);

	/**
	 * Uploads a file.
	 *
	 * @param bucketName bucket name
	 * @param fileName   object name
	 * @param stream     file input stream
	 * @return BladeFile
	 */
	BladeFile putFile(String bucketName, String fileName, InputStream stream);

	/**
	 * Deletes the file.
	 *
	 * @param fileName object name
	 */
	void removeFile(String fileName);

	/**
	 * Deletes the file.
	 *
	 * @param bucketName bucket name
	 * @param fileName   object name
	 */
	void removeFile(String bucketName, String fileName);

	/**
	 * Deletes multiple objects.
	 *
	 * @param fileNames object names to delete
	 */
	void removeFiles(List<String> fileNames);

	/**
	 * Deletes multiple objects.
	 *
	 * @param bucketName bucket name
	 * @param fileNames  object names to delete
	 */
	void removeFiles(String bucketName, List<String> fileNames);


	/**
	 * Downloads an object.
	 * @param fileName
	 * @return downloaded temporary file, which the caller must delete
	 */
	File downloadFile(String fileName);

	/**
	 * Downloads an object.
	 * @param bucketName
	 * @param fileName
	 * @return downloaded temporary file, which the caller must delete
	 */
	File downloadFile(String bucketName, String fileName);

	/**
	 * Downloads an object.
	 * @param fileName
	 * @return
	 */
	byte[] downloadFileAsBytes(String fileName);

	/**
	 * Downloads an object.
	 * @param bucketName
	 * @param fileName
	 * @return
	 */
	byte[] downloadFileAsBytes(String bucketName, String fileName);

	/**
	 * Checks whether the object exists.
	 *
	 * @param fileName object name
	 * @return boolean
	 */
	boolean isFileExists(String fileName);
}
