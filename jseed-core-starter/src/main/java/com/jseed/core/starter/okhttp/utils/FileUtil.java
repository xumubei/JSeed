package com.jseed.core.starter.okhttp.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;

/**
 * @description: File utilities
 * @author cyrus
 * @create: 2024-07-08
 **/
public class FileUtil {

    private static final Logger log = LoggerFactory.getLogger(FileUtil.class);

    /**
     * Gets the file.
     *
     * @param path
     * @return
     */
    public static File getFile(String path) {
        return new File(path);
    }

    /**
     * Gets a file input stream.
     *
     * @param path
     * @return
     * @throws FileNotFoundException
     */
    public static InputStream getFileInputStream(String path) throws FileNotFoundException {
        return new FileInputStream(path);
    }

    /**
     * Converts an input stream to a byte array.
     *
     * @param inStream file input stream
     * @return
     */
    public static byte[] streamToByteArray(InputStream inStream) {
        if (inStream == null) {
            return null;
        }
        byte[] in2b = null;
        BufferedInputStream in = new BufferedInputStream(inStream);
        ByteArrayOutputStream swapStream = new ByteArrayOutputStream();
        int rc = 0;
        try {
            while ((rc = in.read()) != -1) {
                swapStream.write(rc);
            }
            in2b = swapStream.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            closeIo(inStream, in, swapStream);
        }
        return in2b;
    }

    /**
     * Converts a file to a byte array.
     *
     * @param file file
     * @return
     */
    public static byte[] fileToByteArray(File file) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream((int) file.length());
        BufferedInputStream in;
        try {
            in = new BufferedInputStream(new FileInputStream(file));
            int bufSize = 1024;
            byte[] buffer = new byte[bufSize];
            int len = 0;
            while (-1 != (len = in.read(buffer, 0, bufSize))) {
                bos.write(buffer, 0, len);
            }
            return bos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Converts a file to a byte array.
     *
     * @param filename file path
     * @return
     * @throws FileNotFoundException
     */
    public static byte[] fileToByteArray(String filename) throws FileNotFoundException {
        File file = new File(filename);
        if (!file.exists()) {
            throw new FileNotFoundException(filename);
        }
        return fileToByteArray(file);
    }


    /**
     * Closes the stream.
     */
    public static void closeIo(Closeable... closeable) {
        if (null == closeable || closeable.length <= 0) {
            return;
        }
        for (Closeable cb : closeable) {
            try {
                if (null == cb) {
                    continue;
                }
                cb.close();
            } catch (IOException e) {
                throw new RuntimeException(
                        FileUtil.class.getName(), e);
            }
        }
    }

    /**
     * Writes a file input stream to disk.
     *
     * @param is       file input stream
     * @param filePath destination path
     */
    public static void streamToFile(InputStream is, String filePath) throws IOException {
        File file = new File(filePath);
        // Create the parent directory if it does not exist.
        if (!file.getParentFile().exists()) {
            if (!file.getParentFile().mkdirs()) {
                // Throw an exception if directory creation fails.
                throw new IOException("创建文件(父层文件夹)失败, filepath: " + file.getAbsolutePath());
            }
        }
        // Create the file if it does not exist.
        if (!file.exists()) {
            if (!file.createNewFile()) {
                // Throw an exception if file creation fails.
                throw new IOException("创建文件失败, filepath: " + file.getAbsolutePath());
            }
        }
        FileOutputStream fileOut = null;
        FileChannel fileChannel = null;
        try {
            fileOut = new FileOutputStream(file);
            fileChannel = fileOut.getChannel();

            ReadableByteChannel readableChannel = Channels.newChannel(is);
            ByteBuffer buffer = ByteBuffer.allocate(1024 * 32);
            while (true) {
                buffer.clear();
                if (readableChannel.read(buffer) == -1) {
                    readableChannel.close();
                    break;
                }
                buffer.flip();
                fileChannel.write(buffer);
            }
        } catch (FileNotFoundException e) {
            throw new FileNotFoundException("保存文件失败, filepath: " + file.getAbsolutePath());
        } catch (IOException e) {
            throw new IOException("保存文件失败, filepath: " + file.getAbsolutePath(), e);
        } finally {
            closeIo(fileOut, is, fileChannel);
        }
    }

    /**
     * Writes a byte array to a file.
     *
     * @param bytes    file contents
     * @param filePath destination path
     */
    public static void byteArrayToFile(byte[] bytes, String filePath) throws IOException {
        streamToFile(new ByteArrayInputStream(bytes), filePath);
    }

    /**
     * Saves the file.
     *
     * @param filePath
     * @param fileName
     * @param content
     * @return
     */
    public static boolean save(String filePath, String fileName, byte[] content) {
        try {
            File filedir = new File(filePath);
            if (!filedir.exists()) {
                filedir.mkdirs();
            }
            File file = new File(filedir, fileName);
            OutputStream os = new FileOutputStream(file);
            os.write(content, 0, content.length);
            os.flush();
            os.close();
        } catch (Exception e) {
            log.error("保存文件流异常，函数名称：save，fileName：{},filePath：{}，异常原因：", fileName, filePath, e);
            return false;
        }
        return true;
    }

    /**
     * Deletes the file.
     *
     * @param filePath
     * @param fileName
     */
    public static Boolean del(String filePath, String fileName) {
        File file = new File(filePath.concat(fileName));
        if (file.exists()) {
            return file.delete();
        }
        return Boolean.FALSE;
    }
}
