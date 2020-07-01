package cn.hellochaos.filesystem;

import cn.hellochaos.filesystem.entity.User;
import cn.hellochaos.filesystem.entity.vo.File;
import cn.hellochaos.filesystem.service.FileService;
import cn.hellochaos.filesystem.service.DiskService;
import cn.hellochaos.filesystem.util.BeanUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class FileSystemApplication {

  public static void main(String[] args) {
    ConfigurableApplicationContext applicationContext =
        SpringApplication.run(FileSystemApplication.class, args);
    BeanUtils.applicationContext = applicationContext;
    DiskService diskService = BeanUtils.getBean(DiskService.class);
    diskService.init();
  }
}
