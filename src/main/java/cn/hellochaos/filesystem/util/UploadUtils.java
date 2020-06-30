package cn.hellochaos.filesystem.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

/**
 * 用于上传文件到服务器
 *
 * @author <a href="mailto:kobe524348@gmail.com">黄钰朝</a>
 * @date 2020-04-05 11:05
 */
@Slf4j
public class UploadUtils {


    private static final String PATH = System.getProperty("user.dir") + "/uploads";

    /**
     * @param file     表单上传的MultipartFile对象
     * @param fileName 文件名，用于写入本地文件
     * @return 写入成功后的本地文件对象
     */
    public static File upload(MultipartFile file, String fileName) {
        if (file.isEmpty()) {
            throw new RuntimeException("文件为空");
        }
        File uploadPath = new File(PATH);
        if (!uploadPath.exists()) {
            uploadPath.mkdir();
        }

        File dest = new File(PATH + "/" + fileName);
        try {
            if (dest.exists()) {
                log.warn("文件{}已存在!" + dest.getAbsolutePath());
                throw new RuntimeException("文件" + dest.getAbsolutePath() + "已存在!");
            }
            file.transferTo(dest);
            log.info("上传成功:" + dest.getAbsolutePath());
            return dest;
        } catch (IOException e) {
            log.error(e.toString(), e);
        }
        throw new RuntimeException("上传失败");

    }


    /**
     * 删除上传的文件
     *
     * @param fileName 文件名
     */
    public static void deleteFile(String fileName) {
        File file = new File(PATH + "/" + fileName);
        if (file.exists()) {
            file.delete();
        }
    }

    /**
     * 获取服务器端的图片名称
     *
     * @param originName 图片原始名称
     * @return : java.lang.String
     * @author : huange7
     * @date : 2020-05-07 20:28
     */
    public static String getFileName(String originName) {
        if (StringUtils.isEmpty(originName)) {
            return UUID.randomUUID().toString();
        }
        int index = originName.lastIndexOf(".");
        String suffix = originName.substring(index == -1 ? 0 : index);
        return UUID.randomUUID().toString() + suffix;
    }

}
