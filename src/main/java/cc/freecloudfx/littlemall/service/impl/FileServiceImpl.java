package cc.freecloudfx.littlemall.service.impl;

import cc.freecloudfx.littlemall.service.IFileService;
import cc.freecloudfx.littlemall.util.FTPUtil;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

/**
 * @author StReaM on 3/11/2018
 */
@Service("iFileService")
@Slf4j
public class FileServiceImpl implements IFileService {


    @Override
    public String upload(MultipartFile file, String path) {
        String fileName = file.getOriginalFilename();
        String postfix = fileName.substring(fileName.lastIndexOf(".") + 1);
        String uploadFileName = UUID.randomUUID().toString() + "." + postfix;
        log.info("开始上传文件， 文件名: {}, 上传的路径: {}, 新文件名: {}", fileName, path, uploadFileName);

        File fileDir = new File(path);
        if (!fileDir.exists()) {
            fileDir.setWritable(true);
            fileDir.mkdirs();
        }
        File targetFile = new File(path, uploadFileName);

        try {
            file.transferTo(targetFile); // provided by SpringMVC
            // 到这里已经上传成功
            // todo upload to FTP Server
            FTPUtil.uploadFile(Lists.newArrayList(targetFile));
            // todo delete after uploading
            targetFile.delete();
        } catch (IOException e) {
            log.error("上传文件异常", e);
            return null;
        }
        return targetFile.getName();
    }
}
