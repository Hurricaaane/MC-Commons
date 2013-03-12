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
import eu.ha3.easy.EdgeModel;
import eu.ha3.easy.EdgeTrigger;
import eu.ha3.mc.haddon.SupportsFrameEvents;

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

public class DVHaddon extends HaddonImpl implements SupportsFrameEvents, EdgeModel
{
	private EdgeTrigger key;
	
	@Override
	public void onLoad()
	{
		this.key = new EdgeTrigger(this);
		manager().hookFrameEvents(true);
	}
	
	@Override
	public void onFrame(float f)
	{
		this.key.signalState(util().areKeysDown(29, 42, 32));
	}
	
	@Override
	public void onTrueEdge()
	{
	}
	
	@Override
	public void onFalseEdge()
	{
		generateDataTable();
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void generateDataTable()
	{
		Map toJsonify = new LinkedHashMap();
		toJsonify.put("minecraft_blocks", generateDTItems(0, 256, "item"));
		toJsonify.put("minecraft_items", generateDTItems(256, 4096, "item"));
		
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
	
	/*@SuppressWarnings({ "unchecked", "rawtypes" })
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
	}*/
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private Object generateDTItems(int fromInclusive, int toExclusive, String atSuffix)
	{
		Item[] itemsList = Item.itemsList;
		
		List<ItemStack> allItemVariants = new ArrayList<ItemStack>();
		
		for (Item item : itemsList)
		{
			if (item != null)
			{
				int id = item.itemID;
				if (id >= fromInclusive && id < toExclusive)
				{
					item.getSubItems(id, (CreativeTabs) null, allItemVariants);
				}
				
			}
		}
		
		Map<String, Map> itemMap = new LinkedHashMap<String, Map>();
		for (ItemStack stack : allItemVariants)
		{
			Map map = new LinkedHashMap();
			map.put("handler", "I");
			
			String name = populateMapWithStackInfo(map, stack, atSuffix);
			
			itemMap.put(name, map);
			
		}
		
		return itemMap;
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private String populateMapWithStackInfo(Map map, ItemStack stack, String atSuffix)
	{
		int id = stack.itemID;
		int md = stack.getItemDamage();
		
		boolean subs = stack.getHasSubtypes();
		
		String rawName = stack.getItem().getLocalizedName(stack);
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
		
		map.put("id", id);
		map.put("md", md);
		
		if (subs)
		{
			map.put("hassubtypes", true);
		}
		
		/*if (id == 373) // Potions need to be treated differently
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
		}*/
		
		return name;
	}
}
