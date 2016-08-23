package tacohime.tacotime;

import java.io.File;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class TacoTimer extends JavaPlugin
{
    public static TacoTimer plugin;
    public int numComm;
    
    Calendar cal = Calendar.getInstance();
    
    public void onEnable()
    {
        plugin = this;
	if (!new File(getDataFolder(), "config.yml").exists())
            this.saveDefaultConfig();
	saveConfig();

        try 
        {
            runData();
        } 
        catch (ParseException ex) 
        {
            Logger.getLogger(TacoTimer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
  
    public void onDisable()
    {
        Bukkit.getScheduler().cancelAllTasks();
    }
    
    private void runData() throws ParseException
    {
        Object[] commandNames = getConfig().getConfigurationSection("command-list").getKeys(false).toArray();
        
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date;
        int period = 1728000, dayofweek = 8, days = 1;
        String def = "test";
        
        for (int x = 0; x < commandNames.length; x++)
        {
            //1-8, Sun-Sat-All-Quart
            try 
            {
                if (getConfig().getString("command-list." + commandNames[x].toString() + ".day").toLowerCase().matches("all"))  
                {
                    days = 1;
                    def = "Every Day";
                }
                else if (!getConfig().getString("command-list." + commandNames[x].toString() + ".day").matches(".*\\d+.*"))
                {
                    period = 12096000; //86400 * 20 * 7
                    dayofweek = parseDayOfWeek(getConfig().getString("command-list." + commandNames[x].toString() + ".day"), Locale.US);
                    if ((dayofweek - cal.get(Calendar.DAY_OF_WEEK)) <= 0)
                        days = dayofweek - cal.get(Calendar.DAY_OF_WEEK) + 7;
                    else
                        days = dayofweek - cal.get(Calendar.DAY_OF_WEEK);
                    def = "Every " + getConfig().getString("command-list." + commandNames[x].toString() + ".day");
                }
            } 
            catch (ParseException ex) 
            {
                Logger.getLogger(TacoTimer.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            List <String> allcommands = getConfig().getStringList("command-list." + commandNames[x].toString() + ".commands");
            String fullDate = new SimpleDateFormat("yyyy-MM-dd").format(new Date()) + " " + getConfig().getString("command-list." + commandNames[x].toString() + ".time");
            
            if (getConfig().getString("command-list." + commandNames[x].toString() + ".day").matches(".*\\d+.*"))
            {
                int hours = Integer.parseInt(getConfig().getString("command-list." + commandNames[x].toString() + ".day").replaceAll("[^0-9]", ""));
                def = "Every " + hours + " Hours";
                period = hours * 3600 * 20;
                if (new SimpleDateFormat("HH:mm:ss").parse(getConfig().getString("command-list." + commandNames[x].toString() + ".time")).before(new Date()))
                    fullDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(new Date().getTime() + (3600 * 1000 * hours)));
                date = dateFormat.parse(fullDate);
            }
            else
            {
                if (new SimpleDateFormat("HH:mm:ss").parse(getConfig().getString("command-list." + commandNames[x].toString() + ".time")).before(new Date()))
                    fullDate = new SimpleDateFormat("yyyy-MM-dd").format(new Date(new Date().getTime() + (86400 * 1000 * days))) + " " + getConfig().getString("command-list." + commandNames[x].toString() + ".time");
                date = dateFormat.parse(fullDate);
            }
            
            long delay = TimeUnit.MILLISECONDS.toSeconds(date.getTime() - new Date().getTime()) * 20;
            
            getLogger().info("'" + commandNames[x].toString() + "' executes " + def + " on " + fullDate);
            Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new RunTask(allcommands), delay, period);
        }
    }
    
    private static int parseDayOfWeek(String day, Locale locale) throws ParseException {
        SimpleDateFormat dayFormat = new SimpleDateFormat("E", locale);
        Date date = dayFormat.parse(day);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        return dayOfWeek;
    }
}
