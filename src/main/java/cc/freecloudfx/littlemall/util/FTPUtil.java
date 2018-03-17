package cc.freecloudfx.littlemall.util;

import org.apache.commons.net.ftp.FTPClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

/**
 * @author StReaM on 3/11/2018
 */
public class FTPUtil {

    private static String ftpIP = PropertiesUtil.getProperty("ftp.server.ip");
    private static String ftpUser = PropertiesUtil.getProperty("ftp.user");
    private static String ftpPass = PropertiesUtil.getProperty("ftp.pass");

    private static final Logger logger = LoggerFactory.getLogger(FTPUtil.class);

    private String ip;
    private int port;
    private String user;
    private String pass;
    private FTPClient ftpClient;

    public FTPUtil(String ip, int port, String user, String pass) {
        this.ip = ip;
        this.port = port;
        this.user = user;
        this.pass = pass;
    }

    public static boolean uploadFile(List<File> files) throws IOException {
        FTPUtil ftpUtil = new FTPUtil(ftpIP, 21, ftpUser, ftpPass);
        logger.info("开始连接 FTP 服务器");
        boolean result = ftpUtil.uploadFile("img", files);
        logger.info("上传结束，上传结果{}", result?"成功":"失败");
        return result;
    }

    private boolean uploadFile(String remotePath, List<File> files) throws IOException {
        boolean uploaded = true;
        FileInputStream fileInputStream = null;

        if (connectFTPServer(ip, port, user, pass)) {
            try {
                ftpClient.changeWorkingDirectory(remotePath);
                ftpClient.setBufferSize(1024);
                ftpClient.setControlEncoding("UTF-8");
                ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE); // 这个类型能防止乱码
                ftpClient.enterLocalPassiveMode();
                for (File file: files) {
                    fileInputStream = new FileInputStream(file);
                    ftpClient.storeFile(file.getName(), fileInputStream);
                }
            } catch (IOException e) {
                logger.error("上传文件异常", e);
                uploaded = false;
            } finally {
                // release connection
                fileInputStream.close();
                ftpClient.disconnect();
            }
        }
        return uploaded;
    }

    private boolean connectFTPServer(String ip, int port, String user, String pass) {
        boolean isSuccess = false;
        ftpClient = new FTPClient();
        try {
            ftpClient.connect(ip);
            isSuccess = ftpClient.login(user, pass);
        } catch (IOException e) {
            logger.error("连接 FTP 服务器异常", e);
        }
        return isSuccess;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    public FTPClient getFtpClient() {
        return ftpClient;
    }

    public void setFtpClient(FTPClient ftpClient) {
        this.ftpClient = ftpClient;
    }
}
