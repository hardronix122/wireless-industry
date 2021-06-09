package ru.wirelesstools;

import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.EntityRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import gravisuite.GraviSuite;
import ic2.api.item.IC2Items;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.Item.ToolMaterial;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.oredict.OreDictionary;
import ru.wirelesstools.blocks.*;
import ru.wirelesstools.config.ConfigWI;
import ru.wirelesstools.entityarrow.ArrowVampEUNew;
import ru.wirelesstools.fluidmachines.BlockExpGen;
import ru.wirelesstools.fluidmachines.ItemBlockEG;
import ru.wirelesstools.fluidmachines.TextureHooks;
import ru.wirelesstools.fluidmachines.TileXPGenPublic;
import ru.wirelesstools.handlerwireless.WirelessChargerHandler;
import ru.wirelesstools.handlerwireless.WirelessTransfer;
import ru.wirelesstools.handlerwireless.WirelessTransmitHandler;
import ru.wirelesstools.item.ItemEnderModule;
import ru.wirelesstools.item.ItemPlayerModule;
import ru.wirelesstools.item.ItemTransformerKit;
import ru.wirelesstools.item.ItemWirelessModule;
import ru.wirelesstools.item.armor.*;
import ru.wirelesstools.item.tool.LuckyVajra;
import ru.wirelesstools.item.weapon.ItemSaberLoot3;
import ru.wirelesstools.item.weapon.ItemSaberLoot5;
import ru.wirelesstools.item.weapon.VampireQuantumBow;
import ru.wirelesstools.itemblock.*;
import ru.wirelesstools.packets.WIPacketHandler;
import ru.wirelesstools.proxy.ClientProxy;
import ru.wirelesstools.proxy.ServerProxy;
import ru.wirelesstools.tiles.*;
import ru.wirelesstools.utils.RecipeUtil;

@Mod(modid = Reference.NAME, name = "Wireless Industry", version = "0.7.9", dependencies = "required-after:IC2;after:OpenBlocks;after:GraviSuite;after:CoFHCore;after:DraconicEvolution")
public class MainWI {

    @SidedProxy(clientSide = "ru.wirelesstools.proxy.ClientProxy", serverSide = "ru.wirelesstools.proxy.ServerProxy")
    public static ServerProxy proxy;

    public static final String categoryOTHER = "Other";
    public static final String categoryXPSENDER = "XPSender";
    public static final String categoryVAMPIREWEAPONS = "Vampire Weapons";
    public static final String categoryWIRELESSCHARGER = "Wireless Chargers";

    public static Item saber5;
    public static Item saber3;

    public static Item quantumVampBowEu;

    public static Item luckyVajra;
    public static Block iridMach;
    public static Item wirelessChestPlate;
    public static Item wirelessEuRfHelmet;

    public static Item enderQuantumHelmet;
    public static Item enderQuantumChest;
    public static Item enderQuantumLegs;
    public static Item enderQuantumBoots;

    public static final EnumRarity RARITY_RF = EnumHelper.addRarity("RF Wirelessly", EnumChatFormatting.GOLD,
            "Wireless RF Transmission");
    public static final EnumRarity RARITY_EU = EnumHelper.addRarity("Vampire_EU", EnumChatFormatting.DARK_AQUA,
            "EU Vampire");

    public static Block blockxpsender;

    public static Item playermodule;

    public static Item transformkit_upgrade;
    public static Item transformkit_changeowner;

    public static Block blockvajracharger;
    public static Block blockwirelessreceiverpersonal;
    public static Block armorcharger;
    public static Block wirelessasppersonal;
    public static Block wirelesshsppersonal;
    public static Block wirelessuhsppersonal;
    public static Block wirelessqsppersonal;
    public static Block wirelessspsppersonal;
    public static Block wirelessprotonsppersonal;
    public static Block wirelesssingsppersonal;
    public static Block wirelessabssppersonal;
    public static Block wirelessphotonicsppersonal;
    public static Block wirelessneutronsppersonal;

    public static Block blockwirelesschargerpublic;
    public static Block blockwirelesschargerprivate;

    public static Block blockcreativepedestal;

    public static Block wirelessmachinescharger;

    public static Block expgen;

    public static Block blockwirelessqgen;

    public static Item endermodule;
    public static Item wirelessmodule;
    public static TickHandlerWI th;
    public static CreativeTabWI tabwi = new CreativeTabWI();

    public static class FluidXP {
        public static Fluid xpJuice = new Fluid("xpjuicewv");
    }

    @Instance(Reference.NAME)
    public static MainWI instance = new MainWI();

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        Configuration config = new Configuration(event.getSuggestedConfigurationFile());
        try {
            config.load();
            int eutorf = config.get("Energy balance", "The EU to RF multiplier, not less than 1", 4).getInt(4);
            ConfigWI.EuToRfmultiplier = eutorf < 1 ? 1 : eutorf;

            ConfigWI.EuRfSolarHelmetGenDay = config.get("Energy balance", "Helmet Day Generation", 4096).getInt(4096);
            ConfigWI.EuRfSolarHelmetGenNight = config.get("Energy balance", "Helmet Night Generation", 1024)
                    .getInt(1024);

            ConfigWI.waspgenday = config.get("Advanced Solar", "Day Generation", 16).getInt(16);
            ConfigWI.waspgennight = config.get("Advanced Solar", "Night Generation", 8).getInt(8);
            ConfigWI.waspoutput = config.get("Advanced Solar", "Output", 16).getInt(16);
            ConfigWI.wasptier = config.get("Advanced Solar", "Tier", 1).getInt(1);
            ConfigWI.waspstorage = config.get("Advanced Solar", "Storage", 4096).getInt(4096);
            ConfigWI.wasptransfer = config.get("Advanced Solar", "Wireless Transfer", 16).getInt(16);

            ConfigWI.whspgenday = config.get("Hybrid Solar", "Day Generation", 64).getInt(64);
            ConfigWI.whspgennight = config.get("Hybrid Solar", "Night Generation", 32).getInt(32);
            ConfigWI.whspoutput = config.get("Hybrid Solar", "Output", 64).getInt(64);
            ConfigWI.whsptier = config.get("Hybrid Solar", "Tier", 2).getInt(2);
            ConfigWI.whspstorage = config.get("Hybrid Solar", "Storage", 10000).getInt(10000);
            ConfigWI.whsptransfer = config.get("Hybrid Solar", "Wireless Transfer", 64).getInt(64);

            ConfigWI.wuhspgenday = config.get("Ultimate Solar", "Day Generation", 256).getInt(256);
            ConfigWI.wuhspgennight = config.get("Ultimate Solar", "Night Generation", 128).getInt(128);
            ConfigWI.wuhspoutput = config.get("Ultimate Solar", "Output", 256).getInt(256);
            ConfigWI.wuhsptier = config.get("Ultimate Solar", "Tier", 3).getInt(3);
            ConfigWI.wuhspstorage = config.get("Ultimate Solar", "Storage", 100000).getInt(100000);
            ConfigWI.wuhsptransfer = config.get("Ultimate Solar", "Wireless Transfer", 256).getInt(256);

            ConfigWI.wqspgenday = config.get("Quantum Solar", "Day Generation", 1024).getInt(1024);
            ConfigWI.wqspgennight = config.get("Quantum Solar", "Night Generation", 512).getInt(512);
            ConfigWI.wqspoutput = config.get("Quantum Solar", "Output", 1024).getInt(1024);
            ConfigWI.wqsptier = config.get("Quantum Solar", "Tier", 4).getInt(4);
            ConfigWI.wqspstorage = config.get("Quantum Solar", "Storage", 200000).getInt(200000);
            ConfigWI.wqsptransfer = config.get("Quantum Solar", "Wireless Transfer", 1024).getInt(1024);

            ConfigWI.wspspgenday = config.get("Spectral Solar", "Day Generation", 3584).getInt(3584);
            ConfigWI.wspspgennight = config.get("Spectral Solar", "Night Generation", 2048).getInt(2048);
            ConfigWI.wspspoutput = config.get("Spectral Solar", "Output", 3584).getInt(3584);
            ConfigWI.wspsptier = config.get("Spectral Solar", "Tier", 5).getInt(5);
            ConfigWI.wspspstorage = config.get("Spectral Solar", "Storage", 500000).getInt(500000);
            ConfigWI.wspsptransfer = config.get("Spectral Solar", "Wireless Transfer", 3584).getInt(3584);

            ConfigWI.wpspgenday = config.get("Proton Solar", "Day Generation", 12288).getInt(12288);
            ConfigWI.wpspgennight = config.get("Proton Solar", "Night Generation", 6144).getInt(6144);
            ConfigWI.wpspoutput = config.get("Proton Solar", "Output", 12288).getInt(12288);
            ConfigWI.wpsptier = config.get("Proton Solar", "Tier", 6).getInt(6);
            ConfigWI.wpspstorage = config.get("Proton Solar", "Storage", 5000000).getInt(5000000);
            ConfigWI.wpsptransfer = config.get("Proton Solar", "Wireless Transfer", 12288).getInt(12288);

            ConfigWI.wsingspgenday = config.get("Singular Solar", "Day Generation", 57344).getInt(57344);
            ConfigWI.wsingspgennight = config.get("Singular Solar", "Night Generation", 57344).getInt(57344);
            ConfigWI.wsingspoutput = config.get("Singular Solar", "Output", 57344).getInt(57344);
            ConfigWI.wsingsptier = config.get("Singular Solar", "Tier", 7).getInt(7);
            ConfigWI.wsingspstorage = config.get("Singular Solar", "Storage", 10000000).getInt(10000000);
            ConfigWI.wsingsptransfer = config.get("Singular Solar", "Wireless Transfer", 57344).getInt(57344);

            ConfigWI.wdifrspgenday = config.get("Diffraction Solar", "Day Generation", 196608).getInt(196608);
            ConfigWI.wdifrspgennight = config.get("Diffraction Solar", "Night Generation", 196608).getInt(196608);
            ConfigWI.wdifrspoutput = config.get("Diffraction Solar", "Output", 196608).getInt(196608);
            ConfigWI.wdifrsptier = config.get("Diffraction Solar", "Tier", 8).getInt(8);
            ConfigWI.wdifrspstorage = config.get("Diffraction Solar", "Storage", 20000000).getInt(20000000);
            ConfigWI.wdifrsptransfer = config.get("Diffraction Solar", "Wireless Transfer", 196608).getInt(196608);

            ConfigWI.wphspgenday = config.get("Photon Solar", "Day Generation", 786432).getInt(786432);
            ConfigWI.wphspgennight = config.get("Photon Solar", "Night Generation", 786432).getInt(786432);
            ConfigWI.wphspoutput = config.get("Photon Solar", "Output", 786432).getInt(786432);
            ConfigWI.wphsptier = config.get("Photon Solar", "Tier", 9).getInt(9);
            ConfigWI.wphspstorage = config.get("Photon Solar", "Storage", 80000000).getInt(80000000);
            ConfigWI.wphsptransfer = config.get("Photon Solar", "Wireless Transfer", 786432).getInt(786432);

            ConfigWI.wneuspgenday = config.get("Neutron Solar", "Day Generation", 3145728).getInt(3145728);
            ConfigWI.wneuspgennight = config.get("Neutron Solar", "Night Generation", 3145728).getInt(3145728);
            ConfigWI.wneuspoutput = config.get("Neutron Solar", "Output", 3145728).getInt(3145728);
            ConfigWI.wneusptier = config.get("Neutron Solar", "Tier", 10).getInt(10);
            ConfigWI.wneuspstorage = config.get("Neutron Solar", "Storage", 200000000).getInt(200000000);
            ConfigWI.wneusptransfer = config.get("Neutron Solar", "Wireless Transfer", 3145728).getInt(3145728);

            ConfigWI.wstorageoutput = config.get("Wireless Receiver Storage", "Output", 16384).getInt(16384);
            ConfigWI.wstoragemaxstorage = config.get("Wireless Receiver Storage", "Storage", 100000000)
                    .getInt(100000000);
            ConfigWI.wstoragetier = config.get("Wireless Receiver Storage", "Tier", 4).getInt(4);

            ConfigWI.wirelessqgenoutput = config.get("Wireless Quantum Generator", "Output", 32768).getInt(32768);
            ConfigWI.wirelessqgentier = config.get("Wireless Quantum Generator", "Tier", 4).getInt(4);
            ConfigWI.wirelessqgentransfer = config.get("Wireless Quantum Generator", "Wireless transfer limit", 32768)
                    .getInt(32768);

            ConfigWI.maxstorageofchargers = config
                    .get(categoryWIRELESSCHARGER, "Maximum storage of wireless chargers (EU)", 100000000)
                    .getInt(100000000);
            ConfigWI.tierofchargers = config.get(categoryWIRELESSCHARGER, "Tier of wireless chargers", 10).getInt(10);

            int secondslocal = config.get(categoryXPSENDER, "Interval in seconds to send xp to players", 1).getInt(1);
            ConfigWI.secondsXPSender = secondslocal < 1 ? 1 : secondslocal;

            int xpgivenlocal = config.get(categoryXPSENDER, "XP amount given to player per 1 operation", 2).getInt(2);
            ConfigWI.amountXPsent = xpgivenlocal < 1 ? 1 : xpgivenlocal;

            ConfigWI.maxstorageXPSender = config.get(categoryXPSENDER, "Maximum storage of energy", 40000000)
                    .getInt(40000000);
            ConfigWI.energyperxppointXPSender = config.get(categoryXPSENDER, "EU energy consumed per 1 xp point", 25000)
                    .getInt(25000);

            ConfigWI.tierXPSender = config.get(categoryXPSENDER, "Tier of XP Sender", 7)
                    .getInt(7);

            int stolenEUlocal = config
                    .get(categoryVAMPIREWEAPONS, "Stolen amount of EU energy from player armor part", 100000)
                    .getInt(100000);
            ConfigWI.stolenEnergyEUFromArmor = stolenEUlocal > 0 ? stolenEUlocal : 0;

            int vbowenergycost = config.get(categoryVAMPIREWEAPONS, "EU cost per shot", 1000).getInt(1000);
            ConfigWI.vampBowShotEnergyCost = vbowenergycost < 0 ? 0 : vbowenergycost;

            ConfigWI.vampBowMaxCharge = config.get(categoryVAMPIREWEAPONS, "Maximum charge of vampire bow", 300000).getInt(300000);

            int vbowxpvampired = config.get(categoryVAMPIREWEAPONS, "XP amount vampired per shot", 4).getInt(4);
            if(vbowxpvampired < 0)
                ConfigWI.vampBowXPVampiredAmount = 0;
            else if (vbowxpvampired > 20)
                ConfigWI.vampBowXPVampiredAmount = 20;
            else
                ConfigWI.vampBowXPVampiredAmount = vbowxpvampired;

            int chargevaluelocal = config
                    .get(categoryOTHER, "Ender Quamtum Armor self-charging EU/t amount, not more than 16", 8).getInt(8);
            if (chargevaluelocal > 16)
                ConfigWI.enderChargeArmorValue = 16;
            else if (chargevaluelocal < 0)
                ConfigWI.enderChargeArmorValue = 0;
            else
                ConfigWI.enderChargeArmorValue = chargevaluelocal;

        } catch (Exception e) {
            System.out.println("{Wireless Solar Panels Mod} error occurred while reading config file");
            throw new RuntimeException(e);
        } finally {
            config.save();
        }

        WirelessTransfer.transmithandler = new WirelessTransmitHandler();
        WirelessTransfer.chargeplayerhandler = new WirelessChargerHandler();

        luckyVajra = new LuckyVajra(ToolMaterial.EMERALD);
        saber3 = new ItemSaberLoot3();
        saber5 = new ItemSaberLoot5();
        quantumVampBowEu = new VampireQuantumBow("qvampboweu");

        wirelessChestPlate = new QuantumChestplateWirelessCharge("wirelesschargingchestplate");
        wirelessEuRfHelmet = new ItemSolarWirelessEURFHelmet();

        enderQuantumHelmet = new QuantumEnderHelmet("eqHelmet");
        enderQuantumChest = new QuantumEnderChestplate("eqChestplate");
        enderQuantumLegs = new QuantumEnderLeggings("eqLeggings");
        enderQuantumBoots = new QuantumEnderBoots("eqBoots");

        wirelessmodule = new ItemWirelessModule();
        endermodule = new ItemEnderModule();
        playermodule = new ItemPlayerModule();

        transformkit_upgrade = new ItemTransformerKit(0);
        transformkit_changeowner = new ItemTransformerKit(1);

        blockvajracharger = new BlockVajraCharger("vajracharger", Material.rock);
        armorcharger = new BlockArmorCharger("creativearmorcharger", Material.rock);

        blockxpsender = new BlockXPSenderElectric("electricxpsender");

        wirelessasppersonal = new BlockWirelessASP("wirelessAdvancedPanelPersonal");
        wirelesshsppersonal = new BlockWirelessHSP("wirelessHybridPanelPersonal");
        wirelessuhsppersonal = new BlockWirelessUHSP("wirelessUltimatePanelPersonal");
        wirelessqsppersonal = new BlockWirelessQSP("wirelessQuantumPanelPersonal");
        wirelessspsppersonal = new BlockWirelessSpSPPersonal("wirelessSpectralPanelPersonal", Material.rock);
        wirelessprotonsppersonal = new BlockWirelessProtonSP("wirelessProtonPanelPersonal");
        wirelesssingsppersonal = new BlockWirelessSingSPPersonal("wirelessSingularPanelPersonal", Material.rock);
        wirelessabssppersonal = new BlockWirelessAbsSPPersonal("wirelessAbsorbtionPanelPersonal", Material.rock);
        wirelessphotonicsppersonal = new BlockWirelessPhSPPersonal("wirelessPhotonicPanelPersonal", Material.rock);
        wirelessneutronsppersonal = new BlockWirelessNeuSPPersonal("wirelessNeutronSPPersonal", Material.rock);
        blockwirelessreceiverpersonal = new BlockWStoragePersonal("wirelessStoragePersonal1Tier", Material.rock);

        blockwirelesschargerpublic = new BlockWirelessChargerPublic("wirelesschargerpublic");
        blockwirelesschargerprivate = new BlockWirelessChargerPrivate("wirelesschargerprivate");

        blockcreativepedestal = new BlockCreativePedestal("creativepedestal", Material.rock);

        wirelessmachinescharger = new BlockMachinesCharger("machinescharger");

        blockwirelessqgen = new BlockWirelessQuantumGenerator("wirelessqgen");

        expgen = new BlockExpGen("expGen", Material.rock);
        iridMach = new BlockIridiumMachine();

        if (FluidRegistry.isFluidRegistered("xpjuice")) {
            FluidXP.xpJuice = FluidRegistry.getFluid("xpjuice");
        } else {
            FluidRegistry.registerFluid(FluidXP.xpJuice);
            FluidXP.xpJuice.setIcons(TextureHooks.Icons.xpJuiceStill, TextureHooks.Icons.xpJuiceFlowing);
        }

        GameRegistry.registerBlock(wirelessasppersonal, ItemBlockWASP.class, "WASPPersonal");
        GameRegistry.registerBlock(wirelesshsppersonal, ItemBlockWHSP.class, "WHSPPersonal");
        GameRegistry.registerBlock(wirelessuhsppersonal, ItemBlockWUHSP.class, "WUHSPPersonal");
        GameRegistry.registerBlock(wirelessqsppersonal, ItemBlockWQSP.class, "WQSPPersonal");
        GameRegistry.registerBlock(wirelessspsppersonal, ItemBlockWSpSPPersonal.class, "WSpSPPersonal");
        GameRegistry.registerBlock(wirelessprotonsppersonal, ItemBlockWProtonSP.class, "WProtonSPPersonal");
        GameRegistry.registerBlock(wirelesssingsppersonal, ItemBlockWSingSPPersonal.class, "WSingSPPersonal");
        GameRegistry.registerBlock(wirelessabssppersonal, ItemBlockWAbsSPPersonal.class, "WAbsSPPersonal");
        GameRegistry.registerBlock(wirelessphotonicsppersonal, ItemBlockWPhSPPersonal.class, "WPhSPPersonal");
        GameRegistry.registerBlock(wirelessneutronsppersonal, ItemBlockNeuSPPersonal.class, "WNeuSPPersonal");
        GameRegistry.registerBlock(blockwirelessreceiverpersonal, ItemBlockWirelessStoragePersonal.class,
                "WRes1Personal");

        GameRegistry.registerItem(luckyVajra, "LuckyVajra");
        GameRegistry.registerItem(saber5, "LootSaber5");
        GameRegistry.registerItem(saber3, "LootSaber3");
        GameRegistry.registerItem(quantumVampBowEu, "QuantumVampBow");

        GameRegistry.registerItem(wirelessmodule, "WirelessModule");
        GameRegistry.registerItem(endermodule, "EnderModule");
        GameRegistry.registerItem(playermodule, "PlayerModule");

        GameRegistry.registerItem(transformkit_upgrade, "KitUpgradeModule");
        GameRegistry.registerItem(transformkit_changeowner, "KitChangeownerModule");

        GameRegistry.registerItem(wirelessEuRfHelmet, "WirelessHelmet");
        GameRegistry.registerItem(wirelessChestPlate, "WirelessChestPlate");

        GameRegistry.registerItem(enderQuantumHelmet, "QuantumEnderHelmet");
        GameRegistry.registerItem(enderQuantumChest, "QuantumEnderChestplate");
        GameRegistry.registerItem(enderQuantumLegs, "QuantumEnderLeggings");
        GameRegistry.registerItem(enderQuantumBoots, "QuantumEnderBoots");

        GameRegistry.registerBlock(blockwirelesschargerpublic, ItemBlockWChPublic.class, "WChargerPublic");
        GameRegistry.registerBlock(blockwirelesschargerprivate, ItemBlockWChPrivate.class, "WChargerPrivate");

        GameRegistry.registerBlock(blockcreativepedestal, ItemBlockCreativePedestal.class, "CreativePedestal");

        GameRegistry.registerBlock(wirelessmachinescharger, ItemBlockMachCharger.class, "MachinesCharger");

        GameRegistry.registerBlock(blockwirelessqgen, ItemBlockWQGen.class, "WirelessQGen");

        GameRegistry.registerBlock(blockxpsender, ItemBlockXPSender.class, "ExpSender1");

        GameRegistry.registerBlock(expgen, ItemBlockEG.class, "ExpGen");
        GameRegistry.registerBlock(iridMach, ItBlIridMach.class, "IridMach");
        GameRegistry.registerBlock(blockvajracharger, ItemBlockVCh.class, "WCh");
        GameRegistry.registerBlock(armorcharger, ItemBlockArmorCharger.class, "ArCh");

        GameRegistry.registerTileEntity(TileVajraCharger.class, "TileVajraCharger");
        GameRegistry.registerTileEntity(TileXPGenPublic.class, "TileEG");

        GameRegistry.registerTileEntity(TileWirelessStoragePersonal1.class, "TileStorageWireless1Personal");

        GameRegistry.registerTileEntity(TileMachinesCharger.class, "TileEntityMachinesCharger");

        GameRegistry.registerTileEntity(WirelessQGen.class, "TileEntityWirelessQGen");

        GameRegistry.registerTileEntity(TileXPSender.class, "TileEntityXPSender");

        GameRegistry.registerTileEntity(TileWirelessASP.class, "TileWirelessASPPersonal");
        GameRegistry.registerTileEntity(TileWirelessHSP.class, "TileWirelessHSPPersonal");
        GameRegistry.registerTileEntity(TileWirelessUHSP.class, "TileWirelessUHSPPersonal");
        GameRegistry.registerTileEntity(TileWirelessQSP.class, "TileWirelessQSPPersonal");
        GameRegistry.registerTileEntity(TileWirelessSpSPPersonal.class, "TileWirelessSpectralSPPersonal");
        GameRegistry.registerTileEntity(TileWirelessProtonSP.class, "TileWirelessProtonSPPersonal");
        GameRegistry.registerTileEntity(TileWirelessSingSPPersonal.class, "TileWirelessSingularSPPersonal");
        GameRegistry.registerTileEntity(TileWirelessDifractSPPersonal.class, "TileWirelessAbsSPPersonal");
        GameRegistry.registerTileEntity(TileWirelessPhSPPersonal.class, "TileWirelessPhotonicSPPersonal");
        GameRegistry.registerTileEntity(TileWirelessNeutronSPPersonal.class, "TileWirelessNeutronSPPersonal");

        GameRegistry.registerTileEntity(TileEntityWirelessChargerPrivate.class, "TileWChargerPrivate");
        GameRegistry.registerTileEntity(TileEntityWirelessChargerPublic.class, "TileWChargerPublic");

        NetworkRegistry.INSTANCE.registerGuiHandler(this, proxy);
    }

    @EventHandler
    public void serverStart(FMLServerStartingEvent event) {

        proxy.serverStart(event);
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
        WIPacketHandler.load();
        th = new TickHandlerWI();

        ServerProxy.Init();
        ClientProxy.Init();

        EntityRegistry.registerGlobalEntityID(ArrowVampEUNew.class, "ArrowVampEUNew",
                EntityRegistry.findGlobalUniqueEntityId());
    }

    @EventHandler
    public void afterModsLoaded(FMLPostInitializationEvent event) {
        OreDictionary.registerOre("helmetwireless", wirelessEuRfHelmet);
        OreDictionary.registerOre("modulewirelessindustry", wirelessmodule);
        OreDictionary.registerOre("iridiummachinecasing", iridMach);
        OreDictionary.registerOre("luckyvajra", luckyVajra);
        OreDictionary.registerOre("saberloot3", saber3);
        OreDictionary.registerOre("saberloot5", saber5);
        OreDictionary.registerOre("chestplatewireless", wirelessChestPlate);
        OreDictionary.registerOre("vajrachargerblock", blockvajracharger);
        OreDictionary.registerOre("wirelessreceiverpersonal", blockwirelessreceiverpersonal);
        OreDictionary.registerOre("armorchargercreative", armorcharger);

        OreDictionary.registerOre("aspwireless", wirelessasppersonal);
        OreDictionary.registerOre("hspwireless", wirelesshsppersonal);
        OreDictionary.registerOre("uhspwireless", wirelessuhsppersonal);
        OreDictionary.registerOre("qspwireless", wirelessqsppersonal);
        OreDictionary.registerOre("spectralspwireless", wirelessspsppersonal);
        OreDictionary.registerOre("protonspwireless", wirelessprotonsppersonal);
        OreDictionary.registerOre("singularspwireless", wirelesssingsppersonal);
        OreDictionary.registerOre("difractspwireless", wirelessabssppersonal);
        OreDictionary.registerOre("photonicspwireless", wirelessphotonicsppersonal);
        OreDictionary.registerOre("neutronspwireless", wirelessneutronsppersonal);

        OreDictionary.registerOre("wirelesschargerpublic", blockwirelesschargerpublic);
        OreDictionary.registerOre("wirelesschargerprivate", blockwirelesschargerprivate);

        // if(!Loader.isModLoaded("industrialupgrade")) {

        GameRegistry.addRecipe(new ItemStack(wirelessEuRfHelmet, 1, OreDictionary.WILDCARD_VALUE), // OreDictionary.WILDCARD_VALUE
                new Object[]{"C C", "BAB", "C C", 'A',
                        RecipeUtil.copyWithWildCard(!OreDictionary.getOres("helmetSpectral").isEmpty()
                                ? OreDictionary.getOres("helmetSpectral").get(0)
                                : new ItemStack(IC2Items.getItem("quantumHelmet").getItem(), 1,
                                OreDictionary.WILDCARD_VALUE)),
                        'B', new ItemStack(wirelessmodule), 'C',
                        RecipeUtil.copyWithWildCard(new ItemStack(IC2Items.getItem("lapotronCrystal").getItem(), 1,
                                OreDictionary.WILDCARD_VALUE))});

        GameRegistry.addRecipe(new ItemStack(wirelessasppersonal, 1),
                new Object[]{" A ", " B ", " C ", Character.valueOf('A'), new ItemStack(wirelessmodule),
                        Character.valueOf('B'), IC2Items.getItem("solarPanel"), Character.valueOf('C'),
                        new ItemStack(iridMach)});

        GameRegistry.addRecipe(new ItemStack(wirelesshsppersonal, 1),
                new Object[]{"AAA", "A A", "AAA", Character.valueOf('A'), new ItemStack(wirelessasppersonal)});

        GameRegistry.addRecipe(new ItemStack(wirelessuhsppersonal, 1),
                new Object[]{"AAA", "A A", "AAA", Character.valueOf('A'), new ItemStack(wirelesshsppersonal)});

        GameRegistry.addRecipe(new ItemStack(wirelessqsppersonal, 1),
                new Object[]{"AAA", "A A", "AAA", Character.valueOf('A'), new ItemStack(wirelessuhsppersonal)});

        GameRegistry.addRecipe(new ItemStack(wirelessspsppersonal, 1),
                new Object[]{"AAA", "A A", "AAA", Character.valueOf('A'), new ItemStack(wirelessqsppersonal)});

        GameRegistry.addRecipe(new ItemStack(wirelessprotonsppersonal, 1),
                new Object[]{"AAA", "A A", "AAA", Character.valueOf('A'), new ItemStack(wirelessspsppersonal)});

        GameRegistry.addRecipe(new ItemStack(wirelesssingsppersonal, 1),
                new Object[]{"AAA", "A A", "AAA", Character.valueOf('A'), new ItemStack(wirelessprotonsppersonal)});

        GameRegistry.addRecipe(new ItemStack(wirelessabssppersonal, 1),
                new Object[]{"AAA", "A A", "AAA", Character.valueOf('A'), new ItemStack(wirelesssingsppersonal)});

        GameRegistry.addRecipe(new ItemStack(wirelessphotonicsppersonal, 1),
                new Object[]{"AAA", "A A", "AAA", Character.valueOf('A'), new ItemStack(wirelessabssppersonal)});

        GameRegistry.addRecipe(new ItemStack(wirelessneutronsppersonal, 1), new Object[]{"AAA", "A A", "AAA",
                Character.valueOf('A'), new ItemStack(wirelessphotonicsppersonal)});

        GameRegistry.addRecipe(new ItemStack(luckyVajra, 1),
                new Object[]{"ABA", "CDE", "FBF", 'A', IC2Items.getItem("iridiumPlate"), 'B',
                        RecipeUtil.copyWithWildCard(new ItemStack(
                                IC2Items.getItem("miningLaser").getItem(), 1, OreDictionary.WILDCARD_VALUE)),
                        'C', Blocks.lapis_block, 'D',
                        RecipeUtil.copyWithWildCard(new ItemStack(IC2Items.getItem("iridiumDrill").getItem(), 1,
                                OreDictionary.WILDCARD_VALUE)),
                        'E', Blocks.emerald_block, 'F', IC2Items.getItem("advancedCircuit")});

        GameRegistry.addRecipe(new ItemStack(saber3, 1),
                new Object[]{"ACF", "ACF", "BDE", 'A', new ItemStack(Items.dye, 1, 4), 'B', Items.diamond, 'C',
                        IC2Items.getItem("advancedAlloy"), 'D', IC2Items.getItem("electronicCircuit"), 'E',
                        IC2Items.getItem("reBattery"), 'F', Items.redstone});

        GameRegistry.addRecipe(new ItemStack(saber5, 1), new Object[]{"ACF", "BDF", "AEG", 'A', Items.glowstone_dust,
                'B', IC2Items.getItem("iridiumPlate"), 'C', Items.emerald, 'D',
                RecipeUtil.copyWithWildCard(new ItemStack(saber3, 1, OreDictionary.WILDCARD_VALUE)), 'E',
                IC2Items.getItem("advancedCircuit"), 'F', Items.diamond, 'G', RecipeUtil.copyWithWildCard(
                new ItemStack(IC2Items.getItem("energyCrystal").getItem(), 1, OreDictionary.WILDCARD_VALUE))});

        // }

        GameRegistry.addRecipe(new ItemStack(iridMach, 1), new Object[]{" A ", "ABA", " A ", Character.valueOf('A'),
                IC2Items.getItem("iridiumPlate"), Character.valueOf('B'), IC2Items.getItem("advancedMachine")});

        GameRegistry.addRecipe(new ItemStack(blockvajracharger, 1),
                new Object[]{" A ", "BCB", " A ", Character.valueOf('A'), IC2Items.getItem("advancedMachine"),
                        Character.valueOf('B'), Blocks.redstone_block, Character.valueOf('C'),
                        !OreDictionary.getOres("storageMFSU").isEmpty() ? OreDictionary.getOres("storageMFSU").get(0)
                                : IC2Items.getItem("mfsUnit")});

        GameRegistry.addRecipe(new ItemStack(blockwirelesschargerpublic, 1),
                new Object[]{" A ", "BCB", " A ", Character.valueOf('A'), Items.ender_pearl, Character.valueOf('B'),
                        new ItemStack(wirelessmodule), Character.valueOf('C'), new ItemStack(iridMach)});

        GameRegistry.addShapelessRecipe(new ItemStack(blockwirelesschargerprivate, 1),
                new ItemStack(blockwirelesschargerpublic), Items.book, Blocks.emerald_block);

        // TODO �������� ��� ����������� � industrial upgrade
        if (Loader.isModLoaded("GraviSuite")) {
            GameRegistry.addRecipe(new ItemStack(wirelessChestPlate, 1, OreDictionary.WILDCARD_VALUE),
                    new Object[]{"C C", "BAB", "C C", 'A',
                            RecipeUtil.copyWithWildCard(
                                    new ItemStack(GraviSuite.graviChestPlate, 1, OreDictionary.WILDCARD_VALUE)),
                            'B', new ItemStack(wirelessmodule), 'C',
                            RecipeUtil.copyWithWildCard(new ItemStack(IC2Items.getItem("lapotronCrystal").getItem(), 1,
                                    OreDictionary.WILDCARD_VALUE))});
        } else {
            GameRegistry.addRecipe(new ItemStack(wirelessChestPlate, 1, OreDictionary.WILDCARD_VALUE),
                    new Object[]{"C C", "BAB", "C C", 'A',
                            RecipeUtil.copyWithWildCard(new ItemStack(IC2Items.getItem("quantumBodyarmor").getItem(), 1,
                                    OreDictionary.WILDCARD_VALUE)),
                            'B', new ItemStack(wirelessmodule), 'C',
                            RecipeUtil.copyWithWildCard(new ItemStack(IC2Items.getItem("lapotronCrystal").getItem(), 1,
                                    OreDictionary.WILDCARD_VALUE))});
        }

        GameRegistry.addRecipe(new ItemStack(wirelessmodule, 1),
                new Object[]{"ABA", "CDC", "ABA", Character.valueOf('A'), IC2Items.getItem("iridiumOre"),
                        Character.valueOf('B'), Blocks.redstone_block, Character.valueOf('C'),
                        IC2Items.getItem("advancedCircuit"), Character.valueOf('D'), Items.ender_pearl});

        GameRegistry.addRecipe(new ItemStack(endermodule, 1),
                new Object[]{"DAD", "BCB", "DAD", Character.valueOf('A'), Items.ender_pearl, Character.valueOf('B'),
                        IC2Items.getItem("advancedCircuit"), Character.valueOf('C'),
                        IC2Items.getItem("iridiumPlate"), Character.valueOf('D'), Items.redstone});

        GameRegistry.addRecipe(new ItemStack(transformkit_upgrade, 1),
                new Object[]{" A ", "ABA", " A ",
                        Character.valueOf('A'), IC2Items.getItem("wrench"),
                        Character.valueOf('B'), IC2Items.getItem("iridiumPlate")});

        GameRegistry.addRecipe(new ItemStack(playermodule, 1),
                new Object[]{" A ", "BCB", " A ", Character.valueOf('A'), Items.paper, Character.valueOf('B'),
                        IC2Items.getItem("advancedCircuit"), Character.valueOf('C'),
                        IC2Items.getItem("advancedAlloy")});

        GameRegistry.addRecipe(new ItemStack(quantumVampBowEu, 1),
                new Object[]{" A ", "BCB", " A ", Character.valueOf('A'), IC2Items.getItem("advancedCircuit"),
                        Character.valueOf('B'), new ItemStack(endermodule), Character.valueOf('C'), Items.bow});

        GameRegistry.addRecipe(new ItemStack(enderQuantumHelmet, 1, OreDictionary.WILDCARD_VALUE),
                new Object[]{"   ", "BAB", "   ", Character.valueOf('A'), RecipeUtil.copyWithWildCard(
                        new ItemStack(IC2Items.getItem("quantumHelmet").getItem(), 1, OreDictionary.WILDCARD_VALUE)),
                        Character.valueOf('B'), new ItemStack(endermodule)});

        GameRegistry.addRecipe(new ItemStack(enderQuantumChest, 1, OreDictionary.WILDCARD_VALUE),
                new Object[]{"   ", "BAB", "   ", Character.valueOf('A'), RecipeUtil.copyWithWildCard(
                        new ItemStack(IC2Items.getItem("quantumBodyarmor").getItem(), 1, OreDictionary.WILDCARD_VALUE)),
                        Character.valueOf('B'), new ItemStack(endermodule)});

        GameRegistry.addRecipe(new ItemStack(enderQuantumLegs, 1, OreDictionary.WILDCARD_VALUE),
                new Object[]{"   ", "BAB", "   ", Character.valueOf('A'), RecipeUtil.copyWithWildCard(
                        new ItemStack(IC2Items.getItem("quantumLeggings").getItem(), 1, OreDictionary.WILDCARD_VALUE)),
                        Character.valueOf('B'), new ItemStack(endermodule)});

        GameRegistry.addRecipe(new ItemStack(enderQuantumBoots, 1, OreDictionary.WILDCARD_VALUE),
                new Object[]{"   ", "BAB", "   ", Character.valueOf('A'), RecipeUtil.copyWithWildCard(
                        new ItemStack(IC2Items.getItem("quantumBoots").getItem(), 1, OreDictionary.WILDCARD_VALUE)),
                        Character.valueOf('B'), new ItemStack(endermodule)});

        GameRegistry.addRecipe(new ItemStack(blockwirelessreceiverpersonal, 1),
                new Object[]{"A A", "CDC", "A A", Character.valueOf('A'), new ItemStack(wirelessmodule),
                        Character.valueOf('C'), IC2Items.getItem("glassFiberCableItem"), Character.valueOf('D'),
                        !OreDictionary.getOres("storageMFSU").isEmpty() ? OreDictionary.getOres("storageMFSU").get(0)
                                : IC2Items.getItem("mfsUnit")});

        GameRegistry.addRecipe(new ItemStack(wirelessmachinescharger, 1),
                new Object[]{"AAA", "ABA", "AAA", Character.valueOf('A'), new ItemStack(wirelessmodule),
                        Character.valueOf('B'),
                        !OreDictionary.getOres("storageMFSU").isEmpty() ? OreDictionary.getOres("storageMFSU").get(0)
                                : IC2Items.getItem("mfsUnit")});

        GameRegistry.addRecipe(new ItemStack(expgen, 1),
                new Object[]{"BAB", "BCB", "BAB", Character.valueOf('A'), new ItemStack(iridMach),
                        Character.valueOf('B'), IC2Items.getItem("cell"), Character.valueOf('C'),
                        !OreDictionary.getOres("mechanismAdvMatter").isEmpty()
                                ? OreDictionary.getOres("mechanismAdvMatter").get(0)
                                : IC2Items.getItem("massFabricator")});
        GameRegistry.addRecipe(new ItemStack(blockxpsender, 1),////////
                new Object[]{"BAB", "BCB", "BAB", Character.valueOf('A'), new ItemStack(expgen),
                        Character.valueOf('B'), Blocks.redstone_block, Character.valueOf('C'),
                        !OreDictionary.getOres("mechanismAdvMatter").isEmpty()
                                ? OreDictionary.getOres("mechanismAdvMatter").get(0)
                                : IC2Items.getItem("teleporter")});

        GameRegistry.addRecipe(new ItemStack(blockcreativepedestal, 1),
                new Object[]{" A ", "AAA", " A ", Character.valueOf('A'), Blocks.bedrock});

    }

}