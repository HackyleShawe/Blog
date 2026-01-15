package com.hackyle.blog.admin.module.article.job;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.hackyle.blog.admin.infrastructure.config.ResConfig;
import com.hackyle.blog.admin.module.article.model.entity.FileEntity;
import com.hackyle.blog.admin.module.article.service.FileService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Component
@EnableScheduling  //开启SpringTask
public class ArticleFileCheckJob {
    @Autowired
    private FileService fileService;
    /**
     * res开头的配置项，静态资源
     */
    @Autowired
    private ResConfig resConfig;

    private List<File> fileList = new ArrayList<>();


    /**
     * 文件检查和清理
     * 清理数据库：没有绑定文章的文件
     * 清理文件系统：没有存在于数据库中的文件
     *
     * 如果是多节点部署，注意应该保证任务只在一个节点上执行
     */
    @Scheduled(cron = "0 0 1 ? * SUN")
    //@Scheduled(cron = "0 */2 * * * ?") //测试使用
    public void fileCheckAndClean() {
        log.info("=======ArticleFileCheckJob Start=======");
        try {
            fileService.clean();
        } catch (Exception e) {
            log.error("ArticleFileCheckJob-clean出现异常：", e);
        }

        try {
            delFile();
        }catch (Exception e) {
            log.error("ArticleFileCheckJob-delFile出现异常：", e);
        }
        log.info("=======ArticleFileCheckJob End=======");
    }

    /**
     * 主要思路：
     * 递归扫描资源文件夹下的所有文件
     * 每个文件根据文件名，去数据库进行like匹配查询，查询到了则说明是有效文件，否则执行删除
     */
    private void delFile() {
        File rootDir = new File(resConfig.getStoragePath());
        if(!rootDir.exists() || !rootDir.isDirectory()) {
            log.info("静态资源目标文件夹不存在或者不是一个目录，Job结束");
            return;
        }

        File[] dirs = rootDir.listFiles(File::isDirectory);
        File[] files = rootDir.listFiles(File::isFile);
        if(dirs != null) {
            for (File dir : dirs) {
                scanFile(dir);
            }

        }
        if(files != null) {
            fileList.addAll(Arrays.asList(files));
        }

        for (File file : fileList) {
            String name = file.getName();
            boolean exist = existDatabase(name);
            log.info("检查{}是否在数据库中存在：{}", name, exist);
            if(!exist) {
                boolean deleted = file.delete();
                log.info("从文件系统中删除文件：name={}, deleted={}", name, deleted);
            }
        }
    }

    private void scanFile(File file) {
        if(file.isFile()) {
            return;
        }

        File[] dirs = file.listFiles(File::isDirectory);
        File[] files = file.listFiles(File::isFile);
        if(dirs != null) {
            for (File dir : dirs) {
                scanFile(dir);
            }
        }

        if(files != null) {
            fileList.addAll(Arrays.asList(files));
        }
    }

    private boolean existDatabase(String name) {
        LambdaQueryWrapper<FileEntity> query = new LambdaQueryWrapper<>();
        query.like(FileEntity::getFileLink, name);
        long count = fileService.count(query);
        return count > 0;
    }

}
