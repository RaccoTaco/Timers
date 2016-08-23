package tacohime.tacotime;

import java.util.List;
import org.bukkit.Bukkit;

public class RunTask implements Runnable 
{
    List<String> commands;
    
    RunTask(List<String> commands)
    {
        this.commands = commands;
    }
    
    public void run()
    {
        for (int i = 0; i < commands.size(); i++)
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), commands.get(i));
    }
}
