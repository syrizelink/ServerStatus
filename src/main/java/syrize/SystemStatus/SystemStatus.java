package syrize.SystemStatus;

import com.sun.management.OperatingSystemMXBean;

import java.lang.management.ManagementFactory;

/**
 * @author Syrize
 */
@SuppressWarnings("restriction")
public class SystemStatus {
    private final OperatingSystemMXBean osmxb = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
    double totalMemory = osmxb.getTotalMemorySize();
    double freeMemory = osmxb.getFreeMemorySize();
    double cpuLoad = osmxb.getProcessCpuLoad();

    public double cpuStatus() {
        return cpuLoad;
    }

    public double memStatus() {
        return 1 - (freeMemory / totalMemory);
    }

    public double memValue(){
        return (totalMemory - freeMemory);
    }
}