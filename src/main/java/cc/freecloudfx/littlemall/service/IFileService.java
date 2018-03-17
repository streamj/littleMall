package cc.freecloudfx.littlemall.service;

import org.springframework.web.multipart.MultipartFile;

/**
 * @author StReaM on 3/11/2018
 */
public interface IFileService {

    String upload(MultipartFile file, String path);
}
