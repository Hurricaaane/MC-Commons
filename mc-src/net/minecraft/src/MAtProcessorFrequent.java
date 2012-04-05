package net.minecraft.src;

import net.minecraft.client.Minecraft;
import eu.ha3.matmos.engine.MAtmosData;

/*
 * ----------------------------------------------------------------------------
 * "THE COLA-WARE LICENSE" (Revision 0):
 * Hurricaaane wrote this file. As long as you retain this notice you
 * can do whatever you want with this stuff. If we meet some day, and you think
 * this stuff is worth it, you can buy me a cola in return
 * Georges "Hurricaaane" Yam
 * ----------------------------------------------------------------------------
 */

public class MAtProcessorFrequent extends MAtProcessorModel
{
	
	MAtProcessorFrequent(MAtMod modIn, MAtmosData dataIn, String normalNameIn,
			String deltaNameIn)
			{
		super(modIn, dataIn, normalNameIn, deltaNameIn);
		
			}
	
	@Override
	void doProcess()
	{
		Minecraft mc = mod().manager().getMinecraft();
		World w = mc.theWorld;
		WorldInfo worldinfo = w.worldInfo;
		EntityPlayerSP player = mc.thePlayer;
		
		int x = (int) Math.floor(player.posX);
		int y = (int) Math.floor(player.posY);
		int z = (int) Math.floor(player.posZ);
		
		int mx = (int) Math.round(player.motionX * 1000);
		int my = (int) Math.round(player.motionY * 1000);
		int mz = (int) Math.round(player.motionZ * 1000);
		
		setValue(0, w.getSavedLightValue(EnumSkyBlock.Sky, x, y, z));
		setValue(1, w.getSavedLightValue(EnumSkyBlock.Block, x, y, z));
		setValue(2, w.getBlockLightValue(x, y, z));
		setValue(3, (int) (worldinfo.getWorldTime() % 24000L));
		setValue(4, y);
		//
		setValue(6, (player.isInWater() ? 1 : 0));
		setValue(7, (worldinfo.isRaining() ? 1 : 0));
		setValue(8, (worldinfo.isThundering() ? 1 : 0));
		setValue(9, (w.canBlockSeeTheSky(x, y, z) ? 1 : 0));
		setValue(10, (/*w.worldProvider.isNether*/player.dimension == -1 ? 1
				: 0));
		setValue(11, w.skylightSubtracted);
		//
		//
		//
		//
		//
		//
		//
		// ---- / --- // / / setValue( 19, (player.isInsideOfMaterial(Material.water) ? 1 : 0) );
		setValue(19, player.isWet() ? 1 : 0);
		setValue(20, x);
		setValue(21, z);
		setValue(22, player.onGround ? 1 : 0);
		setValue(23, player.getAir()); // air (oxygen)
		setValue(24, player.health);
		setValue(25, player.dimension);
		setValue(26, w.canBlockSeeTheSky(x, y, z)
				&& !(w.getTopSolidOrLiquidBlock(x, z) > y) ? 1 : 0);
		setValue(27, w.getTopSolidOrLiquidBlock(x, z));
		setValue(28, w.getTopSolidOrLiquidBlock(x, z) - y);
		// 29
		// 30
		// 31
		setValue(32, player.inventory.getCurrentItem() != null
				? player.inventory.getCurrentItem().itemID : -1);
		//setValue( 32, player.getHeldItem() != null ? player.getHeldItem().itemID : -1 );
		setValue(33, mx);
		setValue(34, my);
		setValue(35, mz);
		setValue(36, y >= 1 && y < mod().corn().util().getWorldHeight()
				? getTranslatedBlockId(mc.theWorld
				.getBlockId(x, y - 1, z)) : -1); //FIXME difference in Altitude notion
		setValue(37, y >= 2 && y < mod().corn().util().getWorldHeight()
				? getTranslatedBlockId(mc.theWorld
				.getBlockId(x, y - 2, z)) : -1); //FIXME difference in Altitude notion
		setValue(38, mod().corn().util().getClientTick());
		setValue(39, player.isBurning() ? 1 : 0); // XXX ERROR NOW IS A PRIVATE VALUE
		setValue(40, player.swingProgressInt);
		setValue(41, player.isSwinging ? 1 : 0);
		setValue(42, player.isJumping ? 1 : 0);
		setValue(43, (int) (player.fallDistance * 1000));
		setValue(44, player.isInWeb ? 1 : 0);
		setValue(45, (int) Math.floor(Math.sqrt(mx * mx + mz * mz)));
		setValue(46, player.inventory.currentItem);
		setValue(47, mc.objectMouseOver != null ? 1 : 0);
		setValue(48, mc.objectMouseOver != null ? mc.objectMouseOver.typeOfHit
				.ordinal() : -1);
		setValue(49, player.isBurning() ? 1 : 0);
		setValue(50, player.getTotalArmorValue());
		setValue(51, player.foodStats.getFoodLevel()); //(getFoodStats())
		setValue(52, (int) (player.foodStats.getSaturationLevel() * 1000)); //(getFoodStats())
		setValue(53, 0); // TODO (Food Exhaustion Level)
		setValue(54, (player.experienceValue * 1000));
		setValue(55, player.experienceLevel);
		setValue(56, player.experienceTotal);
		setValue(57, player.isOnLadder() ? 1 : 0);
		setValue(58, player.getItemInUseDuration());
		// ---- / --- // / / setValue( 59, player.inventory.func_35157_d(Item.arrow.shiftedIndex) ? 1 : 0);
		setValue(59, 0);
		setValue(60, player.isBlocking() ? 1 : 0);
		setValue(61, 72000 - player.getItemInUseDuration());
		setValue(62, player.inventory.getCurrentItem() == null ? -1
				: player.inventory.getCurrentItem().getItemDamage());
		setValue(63, player.isSprinting() ? 1 : 0);
		setValue(64, player.isSneaking() ? 1 : 0);
		setValue(65, player.isAirBorne ? 1 : 0);
		setValue(66, player.isUsingItem() ? 1 : 0);
		setValue(67, player.isRiding() ? 1 : 0);
		setValue(
				68,
				(player.ridingEntity != null)
				&& (player.ridingEntity.getClass() == EntityMinecart.class)
				? 1 : 0);
		setValue(
				69,
				(player.ridingEntity != null)
				&& (player.ridingEntity.getClass() == EntityBoat.class)
				? 1 : 0);
		setValue(70, (mc.playerController != null)
				&& mc.playerController.isInCreativeMode() ? 1 : 0);
		
		int rmx = (player.ridingEntity != null ? (int) Math
				.round(player.ridingEntity.motionX * 1000) : 0);
		int rmy = (player.ridingEntity != null ? (int) Math
				.round(player.ridingEntity.motionY * 1000) : 0);
		int rmz = (player.ridingEntity != null ? (int) Math
				.round(player.ridingEntity.motionZ * 1000) : 0);
		
		setValue(71, rmx);
		setValue(72, rmy);
		setValue(73, rmz);
		setValue(74, player.ridingEntity != null ? (int) Math.floor(Math
				.sqrt(rmx * rmx + rmz * rmz)) : 0);
		
		// Remember to increase the data size.
		
	}
	
	private int getTranslatedBlockId(int dataValue)
	{
		// XXX Crash prevention in case of data value system hack
		if (dataValue < 0)
			return 0;
		
		if (dataValue >= MAtDataGatherer.COUNT_WORLD_BLOCKS)
			return 0;
		
		return dataValue;
	}
	
}
