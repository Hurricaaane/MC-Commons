package net.minecraft.src;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.client.Minecraft;
import net.sf.json.JSONObject;

import org.lwjgl.opengl.GL11;

import eu.ha3.easy.EdgeModel;
import eu.ha3.easy.EdgeTrigger;
import eu.ha3.mc.haddon.PrivateAccessException;
import eu.ha3.mc.haddon.SupportsFrameEvents;
import eu.ha3.mc.haddon.SupportsTickEvents;

/*
            DO WHAT THE FUCK YOU WANT TO PUBLIC LICENSE 
                    Version 2, December 2004 

 Copyright (C) 2004 Sam Hocevar <sam@hocevar.net> 

 Everyone is permitted to copy and distribute verbatim or modified 
 copies of this license document, and changing it is allowed as long 
 as the name is changed. 

            DO WHAT THE FUCK YOU WANT TO PUBLIC LICENSE 
   TERMS AND CONDITIONS FOR COPYING, DISTRIBUTION AND MODIFICATION 

  0. You just DO WHAT THE FUCK YOU WANT TO. 
*/

public class DVHaddon extends HaddonImpl implements SupportsFrameEvents, SupportsTickEvents, EdgeModel
{
	private EdgeTrigger key;
	private DVGuiCapture capturer;
	
	private int acc;
	
	private boolean isRunning;
	
	@Override
	public void onLoad()
	{
		this.key = new EdgeTrigger(this);
		this.capturer = new DVGuiCapture(manager().getMinecraft());
		
		manager().hookFrameEvents(true);
		manager().hookTickEvents(true);
	}
	
	@Override
	public void onTick()
	{
		if (this.isRunning)
		{
			this.acc = this.acc + 1;
		}
		
	}
	
	@Override
	public void onFrame(float f)
	{
		this.key.signalState(util().areKeysDown(29, 42, 32));
		
		if (this.isRunning)
		{
			try
			{
				GL11.glEnable(3042 /*GL_BLEND*/);
				GL11.glDisable(3008 /*GL_ALPHA_TEST*/);
				GL11.glDisable(3553 /*GL_TEXTURE_2D*/);
				GL11.glBlendFunc(770, 771);
				
				int size = 256;
				
				Tessellator tessellator = Tessellator.instance;
				tessellator.startDrawingQuads();
				tessellator.setColorRGBA(0, 255, 0, 255);
				tessellator.addVertex(0, 0, 0.0D);
				tessellator.addVertex(0, size, 0.0D);
				tessellator.addVertex(size, size, 0.0D);
				tessellator.addVertex(size, 0, 0.0D);
				tessellator.draw();
				
				GL11.glDisable(3042 /*GL_BLEND*/);
				GL11.glEnable(3008 /*GL_ALPHA_TEST*/);
				GL11.glEnable(3553 /*GL_TEXTURE_2D*/);
				
				int speado = this.acc;
				
				int mod = 16;
				
				int id = speado / mod % 136;
				int meta = speado % mod;
				
				this.capturer.render(new ItemStack(id, 1, meta));
				util().drawString(id + "", 0.55f, 0.5f, 0, 0, '6', 255, 255, 0, 255, false);
				util().drawString(meta + "", 0.55f, 0.5f, 0, 16, '6', 0, 255, 0, 255, false);
			}
			catch (Exception e)
			{
			}
			
		}
	}
	
	@Override
	public void onTrueEdge()
	{
	}
	
	@Override
	public void onFalseEdge()
	{
		generateDataTable();
		//this.capturer.render();
		//this.isRunning = !this.isRunning;
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void generateDataTable()
	{
		Map toJsonify = new LinkedHashMap();
		toJsonify.put("minecraft_blocks", generateDTItems(0, 256, "item"));
		toJsonify.put("minecraft_items", generateDTItems(256, 4096, "item"));
		toJsonify.put("minecraft_recipes", generateRecipes("item"));
		
		JSONObject jsonObject = JSONObject.fromObject(toJsonify);
		System.out.println(jsonObject);
		
		try
		{
			FileWriter fw;
			fw = new FileWriter(new File(Minecraft.getMinecraftDir(), "jsonified.txt"));
			fw.write(jsonObject.toString(2));
			fw.close();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private Object generateRecipes(String atSuffix)
	{
		List<Map> listOfMaps = new ArrayList<Map>();
		
		List recipes = CraftingManager.getInstance().getRecipeList();
		for (Object obj : recipes)
		{
			try
			{
				if (obj instanceof ShapedRecipes)
				{
					ShapedRecipes recipe = (ShapedRecipes) obj;
					Map map = new LinkedHashMap<String, String>();
					
					map.put("handler", "R+");
					
					populateMapWithStackInfo(map, recipe.getRecipeOutput(), atSuffix);
					
					ItemStack[] ingredients = (ItemStack[]) util().getPrivateValue(ShapedRecipes.class, recipe, 2);
					
					map.put("recipe_w", util().getPrivateValue(ShapedRecipes.class, recipe, 0));
					map.put("recipe_h", util().getPrivateValue(ShapedRecipes.class, recipe, 1));
					
					int slot = 0;
					for (ItemStack stack : ingredients)
					{
						if (stack != null)
						{
							map.put("slot_" + slot, stack.itemID + ":" + stack.getItemDamage());
						}
						
						slot = slot + 1;
					}
					
					listOfMaps.add(map);
				}
				else if (obj instanceof ShapelessRecipes)
				{
					ShapelessRecipes recipe = (ShapelessRecipes) obj;
					Map map = new LinkedHashMap<String, String>();
					
					map.put("handler", "R-");
					
					populateMapWithStackInfo(map, recipe.getRecipeOutput(), atSuffix);
					
					List ingredients = (List) util().getPrivateValue(ShapelessRecipes.class, recipe, 1);
					
					int slot = 0;
					for (Object stackObject : ingredients)
					{
						ItemStack stack = (ItemStack) stackObject;
						if (stack != null)
						{
							map.put("slot_" + slot, stack.itemID + ":" + stack.getItemDamage());
						}
						
						slot = slot + 1;
					}
					
					listOfMaps.add(map);
				}
				
			}
			catch (PrivateAccessException e)
			{
				e.printStackTrace();
			}
			
		}
		
		Object[] arrayOfMaps = listOfMaps.toArray();
		
		return arrayOfMaps;
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void populateMapWithStackInfo(Map map, ItemStack stack, String atSuffix)
	{
		int id = stack.itemID;
		int md = stack.getItemDamage();
		
		map.put("id", id + ":" + md);
		
		boolean subs = stack.getHasSubtypes();
		
		String rawName = stack.getItem().getLocalItemName(stack);
		String name = rawName;
		
		if (name == null || name.equals("null") || name.equals(""))
		{
			name = "_TN.reg_item";
		}
		else
		{
			name = name + ".name";
			
		}
		name = name + "@" + atSuffix + "-" + id + "-" + md;
		
		if (id == 373) // Potions need to be treated differently
		{
			map.put("name", name);
		}
		else if (id == 383) // Monster Eggs
		{
			map.put("name", name);
		}
		else
		{
			map.put("name", name);
		}
		
		//map.put("indice", ":" + md);
		if (subs)
		{
			map.put("hassubtypes", true);
		}
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private Object generateDTItems(int fromIncluded, int toExcluded, String atSuffix)
	{
		Item[] itemsList = Item.itemsList;
		
		List<ItemStack> allItemVariants = new ArrayList<ItemStack>();
		
		for (Item item : itemsList)
		{
			if (item != null)
			{
				int id = item.shiftedIndex;
				if (id >= fromIncluded && id < toExcluded)
				{
					item.getSubItems(id, (CreativeTabs) null, allItemVariants);
				}
				
			}
		}
		
		List<Map> listOfMaps = new ArrayList<Map>();
		for (ItemStack stack : allItemVariants)
		{
			/*int id = stack.itemID;
			int md = stack.getItemDamage();
			boolean subs = stack.getHasSubtypes();*/
			
			Map map = new LinkedHashMap();
			map.put("handler", "I");
			
			populateMapWithStackInfo(map, stack, atSuffix);
			
			/*if (subs)
			{
				map.put("illus", "/images-dir/item-100-" + id + "-d" + md + ".png");
			}
			else
			{
				map.put("illus", "/images-dir/item-100-" + id + ".png");
			}*/
			
			listOfMaps.add(map);
			
			boolean first = true;
			
			List infoList = stack.getItemNameandInformation();
			for (Object objInfo : infoList)
			{
				String info = (String) objInfo;
				//System.out.println(info);
				
				if (first)
				{
					String rawName = stack.getItem().getLocalItemName(stack);
					first = false;
					
					if (!info.equals(StatCollector.translateToLocal(rawName + ".name").trim()) && !rawName.equals(""))
					{
						System.out.println("!!! :: " + info);
						
					}
				}
				else
				{
					System.out.println(info);
				}
			}
			
		}
		
		Object[] arrayOfMaps = listOfMaps.toArray();
		
		return arrayOfMaps;
		
	}
}
