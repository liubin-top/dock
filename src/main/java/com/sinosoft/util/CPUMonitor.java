package com.sinosoft.util;

import java.io.File;
import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.math.BigDecimal;
import java.math.RoundingMode;


public class CPUMonitor {
        private static final int INTERVAL = 1000; // 每隔1秒采样一次

        public static void main(String[] args) {
            OperatingSystemMXBean osBean = ManagementFactory.getOperatingSystemMXBean();
            com.sun.management.OperatingSystemMXBean sunOsBean = (com.sun.management.OperatingSystemMXBean) osBean;
            BigDecimal num = new BigDecimal(1024*1024*1024);
            File[] roots = File.listRoots(); // 获取所有的磁盘根目录
            while (true) {
                double cpuUsage = sunOsBean.getSystemCpuLoad() * 100;
                long freePhysicalMemorySize = sunOsBean.getFreePhysicalMemorySize();
                long totalPhysicalMemorySize = sunOsBean.getTotalPhysicalMemorySize();
                long freeSwapSpaceSize = sunOsBean.getFreeSwapSpaceSize();
                System.out.println("CPU占有率: " + cpuUsage + "%");
                System.out.println("已用物理内存总量: " + ((totalPhysicalMemorySize-freePhysicalMemorySize)>>30) + "GB");
                System.out.println("可用物理内存总量: " + (freePhysicalMemorySize >> 30) + "GB");
                System.out.println("物理内存总量: " + (totalPhysicalMemorySize>>30) + "GB");

                BigDecimal num3 = new BigDecimal(totalPhysicalMemorySize);
                BigDecimal divNum5 = num3.divide(num,1, RoundingMode.HALF_UP);
                System.out.println("物理内存总量: " + divNum5 + "GB");

                BigDecimal num1 = new BigDecimal(freePhysicalMemorySize);
                BigDecimal divNum4 = num1.divide(num,1, RoundingMode.HALF_UP);
                System.out.println("可用物理内存总量: " + divNum4 + "GB");

                BigDecimal num5 = new BigDecimal(totalPhysicalMemorySize-freePhysicalMemorySize);
                BigDecimal divNum6 = num5.divide(num,1, RoundingMode.HALF_UP);
                System.out.println("物理内存总量: " + divNum6 + "GB");

                BigDecimal num7 = new BigDecimal(freeSwapSpaceSize);
                BigDecimal divNum7 = num7.divide(num,1, RoundingMode.HALF_UP);
                System.out.println("可用交换空间量: " + divNum7 + "GB");

                for (File root : roots) {
                    long totalSpace = root.getTotalSpace(); // 获取磁盘的总空间
                    long freeSpace = root.getFreeSpace(); // 获取磁盘的可用空间
                    System.out.println("磁盘：" + root.getAbsolutePath());
                    System.out.println("总空间：" + new BigDecimal(totalSpace).divide(num,1,RoundingMode.HALF_UP) + " GB");
                    System.out.println("可用空间：" + new BigDecimal(freeSpace).divide(num,1,RoundingMode.HALF_UP) + " GB");
                }


                try {
                    Thread.sleep(INTERVAL);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
