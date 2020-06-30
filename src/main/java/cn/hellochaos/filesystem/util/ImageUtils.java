package cn.hellochaos.filesystem.util;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;

/**
 * @author <a href="mailto:kobe524348@gmail.com">黄钰朝</a>
 * @description 图片工具类
 * @date 2020-05-08 15:49
 */
public class ImageUtils {

    /**
     * 校验图片格式
     *
     * @param originFile 文件
     * @return 返回该文件是否是图片
     * @name isImage
     * @notice none
     * @author <a href="mailto:kobe524348@gmail.com">黄钰朝</a>
     * @date 2019-08-02
     */
    public static boolean isImage(InputStream originFile) {
        ImageInputStream imageInputStream = null;
        try {
            imageInputStream = ImageIO.createImageInputStream(originFile);
            Iterator<ImageReader> iterator = ImageIO.getImageReaders(imageInputStream);
            if (iterator.hasNext()) {
                return true;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } finally {
            if (imageInputStream != null) {
                try {
                    imageInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return false;
    }

}
