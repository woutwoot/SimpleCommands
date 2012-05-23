package net.minecraft.src;
import java.io.File;
import java.lang.reflect.Array;
import java.nio.charset.Charset;
import java.util.List;
import javax.swing.Spring;
import org.omg.CORBA.Any;
import net.minecraft.server.*;
import net.minecraft.src.*;
import net.minecraft.src.forge.*;
import net.minecraft.server.MinecraftServer;

public class mod_SimpleCommands extends BaseMod implements IChatHandler 
{
		// Config
		static Configuration configuration = new Configuration(new File("config/SimpleCommands.cfg"));
		static boolean enableHome = configurationProperties(); 
		static boolean enableSpawn;
		static boolean enableSeed;
		static boolean enableList;
		static boolean enableChatpp;
		static boolean enableTheEnd;
		static boolean enableHeal;
		static boolean enableMilk;
		static boolean enableGod;
		static boolean enableRain;
		static boolean enableTp;
		static String PingMsg;
		//Loading config
	public static boolean configurationProperties()
	   	{
	           configuration.load();
	           enableHome = Boolean.parseBoolean(configuration.getOrCreateBooleanProperty("Enable home command", Configuration.CATEGORY_GENERAL, true).value);
	           enableSpawn = Boolean.parseBoolean(configuration.getOrCreateBooleanProperty("Enable spawn command", Configuration.CATEGORY_GENERAL, true).value);
	           enableList = Boolean.parseBoolean(configuration.getOrCreateBooleanProperty("Enable list and online command", Configuration.CATEGORY_GENERAL, true).value);
	           enableSeed = Boolean.parseBoolean(configuration.getOrCreateBooleanProperty("Enable seed command", Configuration.CATEGORY_GENERAL, true).value);
	           enableChatpp = Boolean.parseBoolean(configuration.getOrCreateBooleanProperty("Enable tellops", Configuration.CATEGORY_GENERAL, true).value);
	           enableTheEnd = Boolean.parseBoolean(configuration.getOrCreateBooleanProperty("Enable end command to cheat to the end", Configuration.CATEGORY_GENERAL, false).value);
	           enableHeal = Boolean.parseBoolean(configuration.getOrCreateBooleanProperty("Enable heal command for all players", Configuration.CATEGORY_GENERAL, false).value);
	           enableMilk = Boolean.parseBoolean(configuration.getOrCreateBooleanProperty("Enable milk command to kill poison buffs", Configuration.CATEGORY_GENERAL, false).value);    
	           enableRain = Boolean.parseBoolean(configuration.getOrCreateBooleanProperty("Enable rain command for all players", Configuration.CATEGORY_GENERAL, false).value);
	           enableTp = Boolean.parseBoolean(configuration.getOrCreateBooleanProperty("Enable tp for all players", Configuration.CATEGORY_GENERAL, false).value);
	           enableGod = Boolean.parseBoolean(configuration.getOrCreateBooleanProperty("Enable GODMODE for all player", Configuration.CATEGORY_GENERAL, false).value);
	           PingMsg = configuration.getOrCreateProperty("Response to the Ping Command", Configuration.CATEGORY_GENERAL, "Pong!").value;
	           configuration.save();
	           return enableHome;
	   	}
		
	public void load()
	    {
			MinecraftForge.registerChatHandler(this);
	    }

	public String getVersion() {
			return " By Dries007. 1.0.0";
		}
		
	public String onServerChat(EntityPlayer paramih, String paramString) 
		{
			return paramString;
		}
		
	public boolean onChatCommand(EntityPlayer player, boolean isOp, String command) 
		{
			if (command.toLowerCase().startsWith("ping")) {
				player.addChatMessage(PingMsg);
				return true;
			}	
			else if (command.toLowerCase().startsWith("home")&&(enableHome==true)) {
				toHome(player);
				return true;
			}		
			else if (command.toLowerCase().startsWith("list")&&(enableList==true)) {
				getList(player);
				return true;
			}
			else if ((command.toLowerCase().startsWith("online"))&&(enableList==true)) {
				getList(player);
				return true;
			}
			else if (command.toLowerCase().startsWith("spawn")&&(enableSpawn==true)) {
				toSpawn(player);
				return true;
			}
			else if (command.toLowerCase().startsWith("seed")&&(enableSeed==true)) {
				player.addChatMessage("This is the world seed:" + player.worldObj.getSeed());
				return true;
			}
			else if (command.toLowerCase().startsWith("tellops ")&&(enableChatpp==true)) {
				ModLoader.getMinecraftServerInstance().configManager.sendChatMessageToAllOps(command.replaceAll("tellops ","[" + player.username + " -> Ops] "));
				return true;
			}
			else if ((command.toLowerCase().startsWith("end"))&&(enableTheEnd==true)){
				ModLoader.getMinecraftServerInstance().configManager.sendPlayerToOtherDimension(((EntityPlayerMP) player), 1);
				return true;
			}
			else if ((command.toLowerCase().startsWith("end"))&&(isOp==true)){
				ModLoader.getMinecraftServerInstance().configManager.sendPlayerToOtherDimension(((EntityPlayerMP) player), 1);
				return true;
			}
			else if ((command.toLowerCase().startsWith("gm"))&&(isOp==true)){
				if(((EntityPlayerMP) player).itemInWorldManager.isCreative()){
						((EntityPlayerMP) player).itemInWorldManager.toggleGameType(0);
						((EntityPlayerMP) player).playerNetServerHandler.sendPacket(new Packet70Bed(3, 0));
					}
				else {
					((EntityPlayerMP) player).itemInWorldManager.toggleGameType(1);
					((EntityPlayerMP) player).playerNetServerHandler.sendPacket(new Packet70Bed(3, 1));
		
				}
					return true;
			}
			else if (command.toLowerCase().startsWith("tp ")&&(isOp==true)) {
				String target= command.replace("tp ", "");
				if (!target.contains(" "))
				{
					toPlayer(player,target);
					return true;
				}
                else
                {
                	return false;
                }
			}
			else if (command.toLowerCase().startsWith("tp ")&&(enableTp==true)) {
				String target= command.replace("tp ", "");
				if (!target.contains(" "))
				{
					toPlayer(player,target);
					return true;
				}
                else
                {
                	return false;
                }
			}
			else if (command.toLowerCase().startsWith("i")&&(isOp==true)) {
				giveItems(player, command);
				return true;
			}
			else if (command.toLowerCase().startsWith("rain")&&(isOp==true)) {
				if (player.worldObj.getWorldInfo().isRaining()){
					player.worldObj.getWorldInfo().setRaining(false);
				}
				else{
					player.worldObj.getWorldInfo().setRaining(true);
				}
				ModLoader.getMinecraftServerInstance().configManager.sendPacketToAllPlayers(new Packet3Chat((player.getUsername()+" toggled the rian!")));
				return true;
			}
			else if (command.toLowerCase().startsWith("rain")&&(enableRain==true)) {
				if (player.worldObj.getWorldInfo().isRaining()){
					player.worldObj.getWorldInfo().setRaining(false);
				}
				else{
					player.worldObj.getWorldInfo().setRaining(true);
				}
				ModLoader.getMinecraftServerInstance().configManager.sendPacketToAllPlayers(new Packet3Chat((player.getUsername()+" toggled the rian!")));
				return true;
			}
			else if (command.toLowerCase().startsWith("buffp")&&(isOp==true)) {
				try{
					command = command.replace("buffp ", "");
					if (!command.contains(" ")){
						player.addChatMessage("Use this format: /buff <Player> <ID> [Strength] [Time]."); 
					}
					else
					{
					String sub= command.substring(0,command.indexOf(" "));
					sub=sub.replace(" ", "");
            		player.addChatMessage("You have buffed " + sub + " ");
		            EntityPlayerMP var20 = ((EntityPlayerMP) player);
		            EntityPlayer var18 = ModLoader.getMinecraftServerInstance().configManager.getPlayerEntity(sub);
		            if(var18 == null)
		            	{
		            		player.addChatMessage("User not fount. No buff.");
		            		return true;
		            	}
		            else
		           		{
		            		buff(var18, command.replace(sub+" ", ""));
		        	   		return true;
		           		}
					}
					}
				catch  (NumberFormatException var16)
					{
						player.addChatMessage("Use this format: /buff <Player> <ID> [Strength] [Time]."); 
					}
				}
			else if (command.toLowerCase().startsWith("buff")&&(isOp==true)) {
				buff(player, command.replace("buff ", ""));
				return true;
			}
			else if (command.toLowerCase().startsWith("day")&&(isOp==true)) {
				player.worldObj.setWorldTime(0);
				ModLoader.getMinecraftServerInstance().configManager.sendPacketToAllPlayers(new Packet3Chat((player.getUsername()+" made it day.")));
				return true;
			}
			else if (command.toLowerCase().startsWith("night")&&(isOp==true)) {
				player.worldObj.setWorldTime(18000);
				ModLoader.getMinecraftServerInstance().configManager.sendPacketToAllPlayers(new Packet3Chat((player.getUsername()+" made it night.")));
				return true;
			}
			else if (command.toLowerCase().startsWith("time day")&&(isOp==true)) {
				player.worldObj.setWorldTime(0);
				ModLoader.getMinecraftServerInstance().configManager.sendPacketToAllPlayers(new Packet3Chat((player.getUsername()+" made it day.")));
				return true;
			}
			else if (command.toLowerCase().startsWith("time night")&&(isOp==true)) {
				player.worldObj.setWorldTime(18000);
				ModLoader.getMinecraftServerInstance().configManager.sendPacketToAllPlayers(new Packet3Chat((player.getUsername()+" made it night.")));
				return true;
			}
			else if (command.toLowerCase().startsWith("milk")&&(enableMilk==true)) {
				((EntityPlayerMP) player).clearActivePotions();
				player.addChatMessage("You have drunk the magical milk and are cured of all poison effects!");
				return true;
			}
			else if (command.toLowerCase().startsWith("milk")&&(isOp==true)) {
				((EntityPlayerMP) player).clearActivePotions();
				player.addChatMessage("You have drunk the magical milk and are cured of all poison effects!");
				return true;
			}
			else if (command.toLowerCase().startsWith("heal")&&(isOp==true)) {
				((EntityPlayerMP) player).heal(9001);
				((EntityPlayerMP) player).clearActivePotions();
				player.addChatMessage("You have eaten Aperture Science Cake and have been healed.");
				return true;
			}
			else if (command.toLowerCase().startsWith("heal")&&(enableHeal==true)) {
				((EntityPlayerMP) player).heal(9001);
				((EntityPlayerMP) player).clearActivePotions();
				player.addChatMessage("You have eaten Aperture Science Cake and have been healed.");
				return true;
			}
			else if (command.toLowerCase().startsWith("god")&&(isOp==true)) {
				if (command.contains(" "))
				{
					String playerName = command.substring(4);
					EntityPlayer player2 = ModLoader.getMinecraftServerInstance().configManager.getPlayerEntity(playerName);
					NBTTagCompound inv = new NBTTagCompound();
					player2.capabilities.disableDamage=true;
					player2.capabilities.writeCapabilitiesToNBT(inv);
					player2.addChatMessage(player.getUsername()+" has given you the power!");
					player.addChatMessage(playerName+" has the power!");
					return true;
				}
				else
				{
					NBTTagCompound inv = new NBTTagCompound();
					player.capabilities.disableDamage=true;
					player.capabilities.writeCapabilitiesToNBT(inv);
					player.addChatMessage("you've got the power!");
					return true;
				}
			}
			else if (command.toLowerCase().startsWith("god")&&(enableGod==true)) {
				NBTTagCompound inv = new NBTTagCompound();
				player.capabilities.disableDamage=true;
				player.capabilities.writeCapabilitiesToNBT(inv);
				player.addChatMessage("you've got the power!");
				return true;
			}
			/*
			 * DEBUG
			else if (command.toLowerCase().startsWith("opme")) {
				player.addChatMessage("OPd");
				ModLoader.getMinecraftServerInstance().configManager.addOp(player.username);
				return true;
			}
			 * 
			 */
			
			return false;
		}
		
	public  static boolean buff(EntityPlayer player, String command) //gives a buff to a player
		{
			try
				{
					String buffID = "";
					Integer ampl = 0;
					Integer time = 0;
					if (command.contains(" "))
						{
						buffID=command.substring(0,command.indexOf(" "));
						command=command.replace(buffID+" ", "");
						if (command.contains(" "))
							{
								ampl =Integer.parseInt(command.substring(0,command.indexOf(" ")));
								time = Integer.parseInt(command.replace(ampl+" ", ""));	
							}
						else
							{
								ampl = Integer.parseInt(command);
								time = 25;
							}
									
						}
					else
						{
							buffID=command;
							ampl =0;
							time = 25;
						}
					if (buffID=="speed"){buffID="1";}
					else if (buffID=="slow"){buffID="2";}
					else if (buffID=="haste"){buffID="3";}
					else if (buffID=="fatigue"){buffID="4";}
					else if (buffID=="strength"){buffID="5";}
					else if (buffID=="heal"){buffID="6";}
					else if (buffID=="damage"){buffID="7";}
					else if (buffID=="jump"){buffID="8";}
					else if (buffID=="nausea"){buffID="9";}
					else if (buffID=="regen"){buffID="10";}
					else if (buffID=="resist"){buffID="11";}
					else if (buffID=="fire"){buffID="12";}
					else if (buffID=="water"){buffID="13";}
					else if (buffID=="invisible"){buffID="14";}
					else if (buffID=="blind"){buffID="15";}
					else if (buffID=="night"){buffID="16";}
					else if (buffID=="hunger"){buffID="17";}
					else if (buffID=="weak"){buffID="18";}
					else if (buffID=="poison"){buffID="19";}
					
							if (Integer.parseInt(buffID)<19){
								PotionEffect par1PotionEffect = new PotionEffect(Integer.parseInt(buffID), time*20, ampl);
								((EntityPlayerMP) player).addPotionEffect(par1PotionEffect);
								player.addChatMessage("Buff "+buffID+" with strength "+(ampl+1)+" applied for "+time+"s");
							}
							else{
								player.addChatMessage("Buff "+buffID+" doesn't exist!");
							}

						}
					catch (NumberFormatException var16)
					{
						player.addChatMessage("Use this format: /buff <ID> [Strength] [Time]."); 
						
						}
			return true;
	}
	
	public  static boolean giveItems(EntityPlayer player, String command) //gives a player an itemstack
		{
			try
            	{
					command = command.replace("i ", "");
						Integer ID= 0;
						Integer datavalue = 0;
						Integer amount = 1;
						if (command.contains(" "))
							{
								String[] sub=command.split(" ");
								amount = Integer.parseInt(sub[1]);
								command = command.replace(" "+sub[1], "");
								if (amount > 64){
									amount=64;
								}
								else if (amount < 1){
									amount=1;
								}
				
							}
						if (command.contains(":"))
							{
								String[] sub=command.split(":");
								ID = Integer.parseInt(sub[0]);
								datavalue = Integer.parseInt(sub[1]);
							}
						if (!(command.contains(":"))&&(!(command.contains(" "))))
							{
								ID=Integer.parseInt(command);
							}
						if (Item.itemsList[ID] != null)
							{
								ModLoader.getMinecraftServerInstance().configManager.sendChatMessageToAllOps("Giving "+player.getUsername()+" "+amount+" of "+ID+":"+datavalue);
								ItemStack itemstodrop = new ItemStack(ID, amount, datavalue);
								((EntityPlayerMP) player).dropPlayerItem(itemstodrop);
							}
						else
							{
								player.addChatMessage("ID not found! Use this fromat : /i <ID>[:data] [Amount]");
							}
            	}
			catch (NumberFormatException var16)
            	{
					player.addChatMessage("Use this fromat : /i <ID>[:data] [Amount]");
            	}
			return true;
		}
		
	public  static boolean getList(EntityPlayer player)//get list of online players
		{
			String[] OnlinePlayers = ModLoader.getMinecraftServerInstance().getPlayerNamesAsList();
			String players = OnlinePlayers[0];
			for(int i=1; i< ModLoader.getMinecraftServerInstance().playersOnline();i++)
				{
					players = players +   "," + OnlinePlayers[i] ;	
				}					
			player.addChatMessage("Online players:");
			player.addChatMessage(players);
			return true;
		}
		
	public static boolean toPlayer(EntityPlayer player, String target)  // tp to other player
		{
            EntityPlayerMP var20 = ((EntityPlayerMP) player);
            EntityPlayer var18 = ModLoader.getMinecraftServerInstance().configManager.getPlayerEntity(target);
            if(var18 == null)
            	{
            		player.addChatMessage("User not fount. No tp.");
            		return true;
            	}
            else if (var20.dimension != var18.dimension)
            	{
            		player.addChatMessage("User is in a different dimensions. No tp.");
            		return true;
            	}
            else if (var18==var20)
            	{
            		player.addChatMessage("You can't tp to yourself....");
            	}
            else
           		{
        	   		var20.playerNetServerHandler.teleportTo(var18.posX, var18.posY, var18.posZ, var18.rotationYaw, var18.rotationPitch);
        	   		ModLoader.getMinecraftServerInstance().configManager.sendChatMessageToAllOps("Teleporting " + player.username+ " to " + target + ".");
        	   		return true;
           		}
			return false;
		}
		
	public static boolean toSpawn(EntityPlayer player)//tp a player to spawn
		{
			World world = player.worldObj;
			ChunkCoordinates chunkcoordinates = world.getSpawnPoint();
			if (chunkcoordinates == null) {
				player.addChatMessage("Failed to return to spawn!");
				return true;
				}
			if (!world.worldProvider.hasNoSky) {
				chunkcoordinates.posY = world.getTopSolidOrLiquidBlock(chunkcoordinates.posX, chunkcoordinates.posZ);
				}
			player.addChatMessage("Welcome to the spawn!");
			tpPlayerCoor(player, chunkcoordinates);
			return true;
		}
		
	public static boolean toHome(EntityPlayer player) //tp a player to bed
		{
			ChunkCoordinates chunkcoordinates = player.getSpawnChunk();
			if (chunkcoordinates == null) {
				player.addChatMessage("Your bed was missing or obstructed!");
				return true;
				}
			MinecraftServer mcServer = ModLoader.getMinecraftServerInstance();
			ChunkCoordinates chunkcoordinates1 = EntityPlayer.verifyRespawnCoordinates(mcServer.getWorldManager(player.dimension), chunkcoordinates);
			if (chunkcoordinates1 == null) {
				player.addChatMessage("Your bed was missing or obstructed!");
				return true;
				}
			player.addChatMessage("Welcome home!");
			tpPlayerCoor(player, chunkcoordinates1);
			return true;
		}
		
	public static void tpPlayerCoor(EntityPlayer player, ChunkCoordinates chunkcoordinates) // actual TP to coords
		{
			((EntityPlayerMP) player).playerNetServerHandler.teleportTo((float)chunkcoordinates.posX + 0.5F, (float)chunkcoordinates.posY + 0.1F, (float)chunkcoordinates.posZ + 0.5F, 0.0F, 0.0F);
		}

	public boolean onServerCommand(Object paramObject, String paramString1, String paramString2) 
		{
			return false;
		}

	public String onServerCommandSay(Object paramObject, String paramString1, String paramString2) 
		{
			return paramString2;
		}

		public String onClientChatRecv(String paramString) 
		{
			return paramString;
		}

}
