package com.builtbroken.creation;

import com.builtbroken.creation.content.glove.ItemGlove;
import com.builtbroken.creation.content.forge.cast.ItemCast;
import com.builtbroken.mc.lib.mod.AbstractMod;
import com.builtbroken.mc.lib.mod.AbstractProxy;
import com.builtbroken.mc.lib.mod.ModCreativeTab;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

/**
 * Created by robert on 10/1/2014.
 */

@Mod(name = "Creations: Builder's Toolkit", modid = Creation.DOMAIN, version = "@VERSION@", dependencies = "required-after:voltzengine")
public class Creation extends AbstractMod
{
    public static final String DOMAIN = "creationsbt";
    public static final String PREFIX = DOMAIN + ":";

    @SidedProxy(clientSide = "com.builtbroken.creation.client.ClientProxy", serverSide = "com.builtbroken.creation.ServerProxy")
    public static CommonProxy proxy;

    @Instance(DOMAIN)
    public static Creation INSTANCE;
    public static ModCreativeTab creativeTab;

    //Settings
    /** Number of buckets each meter of the sphere can contain, controlls volume of the sphere */
    public static int FORGE_BUCKETS_PER_METER = 2;


    //Content
    public static Item itemGlove;
    public static Item itemCast;

    public static Block blockFireChannel;
    public static Block blockCast;

    public Creation()
    {
        super(DOMAIN, "Creations");
    }

    @EventHandler
    public void preInit(FMLPreInitializationEvent e)
    {
        creativeTab = new ModCreativeTab(DOMAIN);
        getManager().setTab(creativeTab);
        super.preInit(e);
        // TODO re-enabled when we have more items


        itemGlove = getManager().newItem(ItemGlove.class);
        creativeTab.itemStack = new ItemStack(itemGlove);
        itemCast = getManager().newItem(ItemCast.class);

    }

    @EventHandler
    public void init(FMLInitializationEvent e)
    {
        super.init(e);
    }

    @EventHandler
    public void postInit(FMLPostInitializationEvent e)
    {
        super.postInit(e);
    }

    @Override
    public AbstractProxy getProxy()
    {
        return proxy;
    }
}