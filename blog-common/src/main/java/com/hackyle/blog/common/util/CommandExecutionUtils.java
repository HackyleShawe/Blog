package com.hackyle.blog.common.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;

/**
 * Windows环境如何执行命令：cmd.exe /c command：执行完命令后终止
 * Linux环境下如何执行命令：此Java进程是以那个用户启动的，执行时就使用该用户的Shell解释器
 *    例如以root用户启动某个Java进程：nohup java -jar demo.jar >/dev/null 2>&1 &
 *    则在"runtime.exec()"中直接写命令即可，不需要加额外Shell解释器：bash、sh、/sbin/nologin等
 *    此时使用的是root用户的Shell解释器执行命令
 */
public class CommandExecutionUtils {
    private static final Logger LOGGER = LoggerFactory.getLogger(CommandExecutionUtils.class);
    private static final Runtime RUNTIME = Runtime.getRuntime();

    /**
     * 执行命令，既没有要导出的结果，也没有要导入的数据
     * @param command 要执行的命令
     * @return 命令是否执行成功
     * @exception IOException 工具类的异常统一向外部抛，由调用方处理
     */
    public static boolean executeCommand(String command) throws IOException, InterruptedException {
        return executeCommand(command, null);
    }

    /**
     * 执行命令，既没有要导出的结果，也没有要导入的数据
     * @param command 要执行的命令
     * @param execDir 在那个目录下执行
     * @return 命令是否执行成功
     */
    public static boolean executeCommand(String command, File execDir) throws IOException, InterruptedException {
        if(null == command || "".equals(command)) {
            return false;
        }

        Process process = RUNTIME.exec(command, null, execDir);
        int exitVal = process.waitFor(); //阻塞，等待执行完毕
        if(exitVal != 0) { //0-成功，1-失败
            LOGGER.error("命令'{}'执行有错误信息：{}", command, new String(process.getErrorStream().readAllBytes()));
        }

        return exitVal == 0;
    }


    /**
     * 执行命令，并将结果导出到临时文件
     * @param command 要执行的命令
     * @param execDir 在那个目录执行
     * @param exportTmpFile 导出结果到那个文件里
     * @exception IOException 工具类的异常统一向外部抛，由调用方处理
     */
    public static boolean executeCommandAndExportResult(String command, File execDir, File exportTmpFile) throws IOException, InterruptedException {
        if(null == command || "".equals(command)) {
            return false;
        }

        Runtime runtime = Runtime.getRuntime();
        Process process = runtime.exec(command, null, execDir);

        InputStream inputStream = process.getInputStream();

        //为什么读取用字符流（BufferedReader）而不用字节流（BufferedInputStream）？
        //因为对于一个命令来说，一般情况下执行结果都是人类可读的，也就是字符的形式呈现，所以使用字符流
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
        OutputStreamWriter outputStreamWriter = new OutputStreamWriter(new FileOutputStream(exportTmpFile), StandardCharsets.UTF_8);

        String str;
        while ((str = bufferedReader.readLine()) != null) {
            outputStreamWriter.write(str + "\r\n");
        }
        outputStreamWriter.flush();
        inputStream.close();
        bufferedReader.close();
        outputStreamWriter.close();

        int exitVal = process.waitFor(); //阻塞，等待执行完毕
        if(exitVal == 0) {
            LOGGER.error("命令'{}'在目录'{}'执行成功，结果存于'{}'", command, execDir == null ? "-":execDir.getAbsolutePath(), exportTmpFile.getAbsolutePath());
        } else {
            LOGGER.error("命令'{}'在目录'{}'执行有错误信息：{}", command, execDir == null ? "-":execDir.getAbsolutePath(), new String(process.getErrorStream().readAllBytes()));
        }

        return exitVal == 0;
    }

    /**
     * 实例：执行命令
     */

    /**
     * 执行命令，并且导入数据
     * @param command 需要执行的命令
     * @param execDir 在那个目录执行
     * @param importStream 执行命令需要加载的数据流
     * @exception IOException 工具类的异常统一向外部抛，由调用方处理
     */
    public static boolean executeCommandAndImportData(String command, File execDir, InputStream importStream) throws IOException, InterruptedException {
        if(command == null || "".equals(command.trim())) {
            return false;
        }

        Process process = RUNTIME.exec(command, null, execDir);
        OutputStream outputStream = process.getOutputStream();
        //BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(restoreFilePath), StandardCharsets.UTF_8));
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(importStream));
        OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream, StandardCharsets.UTF_8);
        String str;
        while ((str = bufferedReader.readLine()) != null) {
            outputStreamWriter.write(str + "\r\n");
        }
        outputStreamWriter.flush();
        outputStream.close();
        bufferedReader.close();
        outputStreamWriter.close();

        int exitVal = process.waitFor(); //阻塞，等待执行完毕
        if(exitVal == 0) {
            LOGGER.error("命令'{}'在目录'{}'执行成功", command, execDir == null ? "-":execDir.getAbsolutePath());
        } else {
            LOGGER.error("命令'{}'在目录'{}'执行有错误信息：{}", command, execDir == null ? "-":execDir.getAbsolutePath(), new String(process.getErrorStream().readAllBytes()));
        }

        return exitVal == 0;
    }

    /**
     * 执行命令，并且加载数据
     * @param command 需要执行的命令
     * @param execDir 在那个目录执行
     * @param importFile 执行命令需要加载的数据文件
     * @exception IOException 工具类的异常统一向外部抛，由调用方处理
     */
    public static boolean executeCommandAndImportData(String command, File execDir, File importFile) throws IOException, InterruptedException {
        if(command == null || "".equals(command.trim())) {
            return false;
        }

        Process process = RUNTIME.exec(command, null, execDir);
        OutputStream outputStream = process.getOutputStream();
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(importFile), StandardCharsets.UTF_8));
        //BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(importFile));

        OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream, StandardCharsets.UTF_8);
        String str;
        while ((str = bufferedReader.readLine()) != null) {
            outputStreamWriter.write(str + "\r\n");
        }
        outputStreamWriter.flush();
        outputStream.close();
        bufferedReader.close();
        outputStreamWriter.close();

        int exitVal = process.waitFor(); //阻塞，等待执行完毕
        if(exitVal == 0) {
            LOGGER.error("命令'{}'在目录'{}'执行成功", command, execDir == null ? "-":execDir.getAbsolutePath());
        } else {
            LOGGER.error("命令'{}'在目录'{}'执行有错误信息：{}", command, execDir == null ? "-":execDir.getAbsolutePath(), new String(process.getErrorStream().readAllBytes()));
        }

        return exitVal == 0;
    }

}
