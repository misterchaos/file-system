package cn.hellochaos.filesystem;

import cn.hellochaos.filesystem.entity.Dirent;
import cn.hellochaos.filesystem.entity.vo.File;
import cn.hellochaos.filesystem.service.FileService;
import cn.hellochaos.filesystem.service.VolumeService;
import cn.hellochaos.filesystem.util.BeanUtils;
import cn.hutool.core.bean.BeanUtil;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class FileSystemApplication {

  public static void main(String[] args) {
    ConfigurableApplicationContext applicationContext =
        SpringApplication.run(FileSystemApplication.class, args);
    BeanUtils.applicationContext = applicationContext;
    VolumeService volumeService = BeanUtils.getBean(VolumeService.class);
    volumeService.init();
    test();
  }

  public static void test(){
    VolumeService volumeService = BeanUtils.getBean(VolumeService.class);
    FileService fileService = BeanUtils.getBean(FileService.class);
    System.out.println("========================创建test文件夹");
    File root = fileService.open(volumeService.getRootDirentId());
    System.out.println(root.toString());
//    File test = fileService.create("test4",volumeService.getRootDirentId(), File.DIR,0);
    File  test = null;
    for(int i = 0; i < root.getFiles().size(); i++) {
      File subFile = root.getFiles().get(i);
      if(subFile.getFilename().equals("test4")){
        test = fileService.open(subFile.getDirentId());
      }
    }
    System.out.println("========================创建\\test\\hello.txt");
    File txt = fileService.create("hello.txt",test.getDirentId(),File.TXT,0);
    System.out.println(txt.toString());
    System.out.println("========================打开");
    fileService.open(txt.getDirentId());
    System.out.println("========================更新");
    txt.setData("这是一段文字，hello world");
    fileService.update(txt);
    System.out.println("========================关闭");
    fileService.close(txt.getDirentId());
    System.out.println("========================删除");
    fileService.delete(txt.getDirentId());

  }
}
