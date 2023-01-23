package syrize.SystemStatus;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Objects;

/**
 * @author Syrize
 */
public class Main extends JavaPlugin implements Listener {
    public Main() throws InterruptedException {}

    public void onEnable(){
        Objects.requireNonNull(Bukkit.getPluginCommand("status")).setExecutor(this);
        getLogger().info("插件已成功载入...");
    }
    @Override
    @ParametersAreNonnullByDefault
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        copy myCopy = new copy();

        final char process = '▋';//进度条
        int totalValue = 10;//进度条长度
        int valueCpu = (int) (myCopy.cpuStatus() * 10);//CPU占用率
        int valueMem = (int) (myCopy.memStatus() * 10);//内存占用率
        int memDifference = totalValue - valueMem;//空闲内存
        int cpuDifference = totalValue - valueCpu;//CPU空闲率
        int cpuCore = Runtime.getRuntime().availableProcessors() / 2;//CPU核心数
        int maxPlayers = Bukkit.getMaxPlayers();//最大玩家数
        int currentPlayers = Bukkit.getOnlinePlayers().size();//在线玩家数

        String x1 = String.valueOf(cpuUsage(valueCpu, process));
        String y1 = String.valueOf(memUsage(valueMem, process));
        String x2 = String.valueOf(cpuUsageFree(cpuDifference, process));
        String y2 = String.valueOf(memUsageFree(memDifference, process));

        commandSender.sendMessage("正在从服务器获取数据, 请稍后...");

        if (x1 != null || y1 != null ){
            commandSender.sendMessage(ChatColor.GRAY + "[" + ChatColor.RED + "S" + ChatColor.GOLD + "e" + ChatColor.YELLOW + "r" + ChatColor.DARK_GREEN + "v" + ChatColor.AQUA + "e" + ChatColor.BLUE + "r" + ChatColor.WHITE + " Status" + ChatColor.GRAY + "]");
            commandSender.sendMessage(ChatColor.DARK_GRAY + "|" + ChatColor.RESET + ChatColor.BOLD + "主机负载  " + LoadCal());
            commandSender.sendMessage(ChatColor.DARK_GRAY + "|" + ChatColor.RESET + ChatColor.BOLD + "当前玩家  " + dynamicPlayers(maxPlayers, currentPlayers));
            try {
                commandSender.sendMessage(ChatColor.DARK_GRAY + "|" + ChatColor.RESET + ChatColor.BOLD + "TPS  " + ServerTPS());
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            commandSender.sendMessage(ChatColor.DARK_GRAY + "|" + ChatColor.RESET + ChatColor.BOLD + "CPU 占用  " + ChatColor.RESET + ChatColor.DARK_GREEN + x1 + ChatColor.GRAY + x2 + ChatColor.WHITE + "  >>" + ChatColor.GRAY + "[ " + ChatColor.GOLD + cpuCore + ChatColor.DARK_GRAY + " Core" + ChatColor.WHITE + " / " + ChatColor.GOLD + String.format("%.2f",(myCopy.cpuStatus() * 100)) + ChatColor.DARK_GRAY + "%" + ChatColor.GRAY + " ]");
            commandSender.sendMessage(ChatColor.DARK_GRAY + "|" + ChatColor.RESET + ChatColor.BOLD + "RAM 占用  " + ChatColor.RESET + ChatColor.DARK_GREEN + y1 + ChatColor.GRAY + y2 + ChatColor.WHITE + "  >>" + ChatColor.GRAY + "[ " + ChatColor.GOLD + String.format("%.2f", (myCopy.memValue() / 1000000)) + ChatColor.DARK_GRAY + " MB" + ChatColor.WHITE + " / " + ChatColor.GOLD + String.format("%.2f",(myCopy.memStatus() * 100)) + ChatColor.DARK_GRAY + "%" + ChatColor.GRAY + " ]");
            try {
                commandSender.sendMessage(ChatColor.DARK_GRAY + "|" + ChatColor.RESET + ChatColor.BOLD + "综合流畅度  " + sysFluency());
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            return true;
        }else {
            return false;
        }

    }

    public StringBuffer memUsage(int valueMem, char process){
        StringBuffer x = new StringBuffer();
        while (valueMem > 0){
            x.append(process);
            valueMem --;
        }
        return x;
    }

    public StringBuffer memUsageFree(int memDifference, char process){
        StringBuffer x = new StringBuffer();
        while (memDifference > 0){
            x.append(process);
            memDifference--;
        }
        return x;
    }

    public StringBuffer cpuUsage(int valueCpu, char process){
        StringBuffer y = new StringBuffer();
        while (valueCpu > 0){
            y.append(process);
            valueCpu --;
        }
        return y;
    }

    public StringBuffer cpuUsageFree(int cpuDifference, char process){
        StringBuffer y = new StringBuffer();
        while (cpuDifference > 0){
            y.append(process);
            cpuDifference--;
        }
        return y;
    }

    public StringBuffer dynamicPlayers(int maxPlayers, int currentPlayers){
        String cPlayers = String.valueOf(currentPlayers);
        String mPlayers = String.valueOf(maxPlayers);
        StringBuffer x = new StringBuffer();
        x.append(ChatColor.GRAY).append("[ ");
        if (((double) currentPlayers / (double) maxPlayers) < 0.5){
            x.append(ChatColor.GREEN).append(cPlayers);
        }else if (((double) currentPlayers / (double) maxPlayers) > 0.5 && ((double) currentPlayers / (double) maxPlayers) < 0.7){
            x.append(ChatColor.YELLOW).append(cPlayers);
        }else {
            x.append(ChatColor.RED).append(cPlayers);
        }
        x.append(ChatColor.WHITE).append(" / ").append(ChatColor.AQUA).append(mPlayers);
        x.append(ChatColor.GRAY).append(" ]");
        return x;
    }

    public double getServerTPS() throws InterruptedException {
        new Thread();
        double one = Bukkit.getTPS()[0];
        Thread.sleep(1000);
        double two = Bukkit.getTPS()[0];
        Thread.sleep(1000);
        double thr = Bukkit.getTPS()[0];

        return ((one + two + thr) / 3);
    }


    double TPS = getServerTPS();

    public StringBuffer ServerTPS() throws InterruptedException {
        StringBuffer x = new StringBuffer();
        String TPSValue =  String.format("%.2f", TPS);

        x.append(ChatColor.GRAY).append("[ ");
        if (TPS > 19){
            x.append(ChatColor.GREEN).append(TPSValue);
        }else if (TPS < 19 && TPS >17){
            x.append(ChatColor.YELLOW).append(TPSValue);
        }else {
            x.append(ChatColor.RED).append(TPSValue);
        }
        x.append(ChatColor.WHITE).append(" / ").append(ChatColor.AQUA).append("-").append(String.format("%.2f", (20 - TPS)));
        x.append(ChatColor.GRAY).append(" ]");
        return x;
    }

    public StringBuffer LoadCal(){
        double maxPlayers = Bukkit.getMaxPlayers();//最大玩家数
        double currentPlayers = Bukkit.getOnlinePlayers().size();//在线玩家数
        double n = currentPlayers / maxPlayers;
        copy mycopy = new copy();
        StringBuffer x = new StringBuffer();

        if (mycopy.memStatus() < 0.7 || n < 0.8){
            x.append(ChatColor.GREEN).append("低");
        }else if ((mycopy.memStatus() > 0.7 && mycopy.memStatus() < 0.9) || (n > 0.8 && n < 0.95)){
            x.append(ChatColor.YELLOW).append("中");
        }else {
            x.append(ChatColor.RED).append("高");
        }
        return x;
    }

    public StringBuffer sysFluency() throws InterruptedException {
        StringBuffer x = new StringBuffer();
        double maxPlayers = Bukkit.getMaxPlayers();//最大玩家数
        double currentPlayers = Bukkit.getOnlinePlayers().size();//在线玩家数
        double n = currentPlayers / maxPlayers;

        if (TPS > 19 && n < 0.95){
            x.append(ChatColor.GREEN).append("优");
        }else if ((TPS < 19 && TPS >17) || (n >0.95 && TPS > 19)){
            x.append(ChatColor.YELLOW).append("良");
        }else {
            x.append(ChatColor.RED).append("差");
        }
        return x;
    }

    public static class copy extends SystemStatus{
        @Override
        public double memStatus() {
            return super.memStatus();
        }

        @Override
        public double cpuStatus() {
            return super.cpuStatus();
        }

        @Override
        public double memValue() {
            return super.memValue();
        }
    }
}
