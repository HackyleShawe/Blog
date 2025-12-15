package com.hackyle.blog.admin.module.monitor.model.vo;

import cn.hutool.core.net.NetUtil;
import cn.hutool.core.util.NumberUtil;
import lombok.Data;
import oshi.hardware.CentralProcessor;
import oshi.hardware.CentralProcessor.TickType;
import oshi.hardware.GlobalMemory;
import oshi.hardware.HardwareAbstractionLayer;
import oshi.software.os.FileSystem;
import oshi.software.os.OSFileStore;
import oshi.software.os.OperatingSystem;
import oshi.util.Util;

import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

@Data
public class ServerInfoVo {
    private static final int KB = 1024;
    private static final int MB = KB * 1024;
    private static final int GB = MB * 1024;

    private static final int OSHI_WAIT_SECOND = 1000;

    /**
     * CPU相关信息
     */
    private CpuInfoVo cpuInfo = new CpuInfoVo();

    /**
     * 內存相关信息
     */
    private MemoryInfoVo memoryInfo = new MemoryInfoVo();

    /**
     * JVM相关信息
     */
    private JvmInfoVo jvmInfo = new JvmInfoVo();

    /**
     * 服务器相关信息
     */
    private SystemInfoVo systemInfo = new SystemInfoVo();

    /**
     * 磁盘相关信息
     */
    private List<DiskInfoVo> diskInfos = new LinkedList<>();

    public static ServerInfoVo fillInfo() {
        ServerInfoVo serverInfo = new ServerInfoVo();

        oshi.SystemInfo si = new oshi.SystemInfo();
        HardwareAbstractionLayer hal = si.getHardware();

        serverInfo.fillCpuInfo(hal.getProcessor());
        serverInfo.fillMemoryInfo(hal.getMemory());
        serverInfo.fillSystemInfo();
        serverInfo.fillJvmInfo();
        serverInfo.fillDiskInfos(si.getOperatingSystem());

        return serverInfo;
    }

    /**
     * 设置CPU信息
     */
    private void fillCpuInfo(CentralProcessor processor) {
        // CPU信息
        long[] prevTicks = processor.getSystemCpuLoadTicks();
        Util.sleep(OSHI_WAIT_SECOND);
        long[] ticks = processor.getSystemCpuLoadTicks();
        long nice = ticks[TickType.NICE.getIndex()] - prevTicks[TickType.NICE.getIndex()];
        long irq = ticks[TickType.IRQ.getIndex()] - prevTicks[TickType.IRQ.getIndex()];
        long softIrq = ticks[TickType.SOFTIRQ.getIndex()] - prevTicks[TickType.SOFTIRQ.getIndex()];
        long steal = ticks[TickType.STEAL.getIndex()] - prevTicks[TickType.STEAL.getIndex()];
        long cSys = ticks[TickType.SYSTEM.getIndex()] - prevTicks[TickType.SYSTEM.getIndex()];
        long user = ticks[TickType.USER.getIndex()] - prevTicks[TickType.USER.getIndex()];
        long ioWait = ticks[TickType.IOWAIT.getIndex()] - prevTicks[TickType.IOWAIT.getIndex()];
        long idle = ticks[TickType.IDLE.getIndex()] - prevTicks[TickType.IDLE.getIndex()];
        long totalCpu = user + nice + cSys + idle + ioWait + irq + softIrq + steal;
        cpuInfo.setCpuNum(processor.getLogicalProcessorCount());
        cpuInfo.setTotal(totalCpu);
        cpuInfo.setSys(cSys);
        cpuInfo.setUsed(user);
        cpuInfo.setWait(ioWait);
        cpuInfo.setFree(idle);
    }

    /**
     * 设置内存信息
     */
    private void fillMemoryInfo(GlobalMemory memory) {
        memoryInfo.setTotal(memory.getTotal());
        memoryInfo.setUsed(memory.getTotal() - memory.getAvailable());
        memoryInfo.setFree(memory.getAvailable());
    }

    /**
     * 设置服务器信息
     */
    private void fillSystemInfo() {
        Properties props = System.getProperties();

        systemInfo.setComputerName(NetUtil.getLocalHostName());
        systemInfo.setComputerIp(NetUtil.getLocalhost().getHostAddress());
        systemInfo.setOsName(props.getProperty("os.name"));
        systemInfo.setOsArch(props.getProperty("os.arch"));
        systemInfo.setUserDir(props.getProperty("user.dir"));
    }

    /**
     * 设置Java虚拟机
     */
    private void fillJvmInfo() {
        Properties props = System.getProperties();
        jvmInfo.setTotal(Runtime.getRuntime().totalMemory());
        jvmInfo.setMax(Runtime.getRuntime().maxMemory());
        jvmInfo.setFree(Runtime.getRuntime().freeMemory());
        jvmInfo.setVersion(props.getProperty("java.version"));
        jvmInfo.setHome(props.getProperty("java.home"));
    }

    /**
     * 设置磁盘信息
     */
    private void fillDiskInfos(OperatingSystem os) {
        FileSystem fileSystem = os.getFileSystem();
        List<OSFileStore> fsArray = fileSystem.getFileStores();
        for (OSFileStore fs : fsArray) {
            long free = fs.getUsableSpace();
            long total = fs.getTotalSpace();
            long used = total - free;
            DiskInfoVo diskInfo = new DiskInfoVo();
            diskInfo.setDirName(fs.getMount());
            diskInfo.setSysTypeName(fs.getType());
            diskInfo.setTypeName(fs.getName());
            diskInfo.setTotal(convertFileSize(total));
            diskInfo.setFree(convertFileSize(free));
            diskInfo.setUsed(convertFileSize(used));
            if (total != 0){
                diskInfo.setUsage(NumberUtil.div(used * 100, total, 4));
            } else {
                //Windows下如果有光驱（可能是虚拟光驱），total为0，不能做除数
                diskInfo.setUsage(0);
            }
            diskInfos.add(diskInfo);
        }
    }

    /**
     * 字节转换
     *
     * @param size 字节大小
     * @return 转换后值
     */
    public String convertFileSize(long size) {
        float castedSize = (float) size;

        if (size >= GB) {
            return String.format("%.1f GB", castedSize / GB);
        }

        if (size >= MB) {
            return String.format("%.1f MB", castedSize / MB);
        }

        return String.format("%.1f KB", castedSize / KB);
    }
}
