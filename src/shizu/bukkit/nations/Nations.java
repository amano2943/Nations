package shizu.bukkit.nations;

import java.util.logging.Logger;

import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import com.nijiko.permissions.PermissionHandler;
import com.nijikokun.bukkit.Permissions.Permissions;
import org.bukkit.plugin.Plugin;

import shizu.bukkit.nations.event.NationsBlockListener;
import shizu.bukkit.nations.event.NationsUserListener;
import shizu.bukkit.nations.manager.GroupManagement;
import shizu.bukkit.nations.manager.PlotManagement;
import shizu.bukkit.nations.manager.UserManagement;

/**
 * Nations At War plugin class
 * 
 * @author Shizukesa
 */
public class Nations extends JavaPlugin {
	
	// TODO: Add color to player notifications

	private static final Logger log = Logger.getLogger("Minecraft");
	
	public static PermissionHandler permissionHandler;
	public PlotManagement plotManager = new PlotManagement(this);
	public UserManagement userManager = new UserManagement(this);
	public GroupManagement groupManager = new GroupManagement(this);
	public NationsBlockListener blockListener = new NationsBlockListener(this);
	public NationsUserListener userListener = new NationsUserListener(this);
    
	public void onEnable() {
		
		setupPermissions();
		PluginManager pm = getServer().getPluginManager();

		pm.registerEvent(Event.Type.BLOCK_DAMAGE, blockListener, Event.Priority.Normal, this);
		pm.registerEvent(Event.Type.BLOCK_BREAK, blockListener, Event.Priority.Normal, this);
		pm.registerEvent(Event.Type.BLOCK_PLACE, blockListener, Event.Priority.Normal, this);
		pm.registerEvent(Event.Type.PLAYER_JOIN, userListener, Event.Priority.Normal, this);
		pm.registerEvent(Event.Type.PLAYER_QUIT, userListener, Event.Priority.Normal, this);
		pm.registerEvent(Event.Type.PLAYER_KICK, userListener, Event.Priority.Normal, this);
		pm.registerEvent(Event.Type.PLAYER_MOVE, userListener, Event.Priority.High, this);

		
		plotManager.loadAll();
		groupManager.loadAll();
		this.sendToLog("Nations At War Plugin Loaded");
	}

	public void onDisable() {
		plotManager.saveAll();
		groupManager.saveAll();
		userManager.saveAll();
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args){
		
		if (userManager.collection.containsKey(((Player)sender).getDisplayName())) {
		// TODO Commands: Make this not suck
			if (commandLabel.equalsIgnoreCase("naw")) {
				
				if (args[0].equalsIgnoreCase("plot")) {
					
					if (args[1].equalsIgnoreCase("claim")) {
						plotManager.claimPlot((Player)sender);
					}
					
					if (args[1].equalsIgnoreCase("raze")) {
						plotManager.razePlot((Player)sender);
					}
					
					if (args[1].equalsIgnoreCase("resell")) {
						plotManager.resellPlot((Player)sender);
					}
					
					if (args[1].equalsIgnoreCase("region")) {
						plotManager.setRegion((Player)sender, args[2]);
					}
				}
				
				if (args[0].equalsIgnoreCase("nation")) {
					
					if (args[1].equalsIgnoreCase("found")) {
						groupManager.foundNation((Player)sender, args[2]);
					}
				}
			}
			return true;
		}
		return false;
	}
	
	public void sendToLog(String message) {
		log.info("[NationsAtWar]: " + message);
	}
	
	public World getWorld() {
		return this.getServer().getWorld("world");
	}
	
	/**
	 * Tie in with the Permissions plugin
	 */
	@SuppressWarnings("static-access")
	private void setupPermissions() {
		
		Plugin permissionsPlugin = this.getServer().getPluginManager().getPlugin("Permissions");
	
			if (this.permissionHandler == null) {
				if (permissionsPlugin != null) {
					this.permissionHandler = ((Permissions) permissionsPlugin).getHandler();
				} else {
					log.info("Permission system not detected, defaulting to OP");
				}
			}
	  	}
}