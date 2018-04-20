package de.canitzp.energeticboots;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.resources.I18n;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.ShapelessRecipes;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.ShapedOreRecipe;

import javax.annotation.Nonnull;

/**
 * @author canitzp
 */
@Mod.EventBusSubscriber
@Mod(modid = EnergeticBoots.MODID, name = EnergeticBoots.MODNAME, version = EnergeticBoots.MODVERSION, acceptedMinecraftVersions = EnergeticBoots.MC_VERSIONS)
public class EnergeticBoots {

    public static final String MODID = "energeticboots";
    public static final String MODNAME = "EnergeticBoots";
    public static final String MODVERSION = "@Version@";
    public static final String MC_VERSIONS = "1.12,1.12.1,1.12.2";

    public static final ItemEnergticModule energeticModule = new ItemEnergticModule();

    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> reg){
        reg.getRegistry().register(energeticModule);
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public static void registerModels(ModelRegistryEvent event){
        ModelLoader.setCustomModelResourceLocation(energeticModule, 0, new ModelResourceLocation(energeticModule.getRegistryName(), "inventory"));
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void renderTooltips(ItemTooltipEvent event){
        if(!event.getItemStack().isEmpty()){
            NBTTagCompound nbt = event.getItemStack().getTagCompound();
            if(nbt != null && nbt.hasKey("EnergeticBoots", Constants.NBT.TAG_BYTE)){
                event.getToolTip().add(TextFormatting.DARK_AQUA.toString() + TextFormatting.ITALIC.toString() + I18n.format("item.energeticboots:energetic_boots_module_installed.text") + TextFormatting.RESET.toString());
            }
        }
    }

    @SubscribeEvent
    public static void registerRecipes(RegistryEvent.Register<IRecipe> reg){
        reg.getRegistry().register(new ShapedOreRecipe(energeticModule.getRegistryName(), energeticModule, " s ", "sbs", "iii", 's', "stickWood", 'b', Items.BOWL, 'i', "ingotIron").setRegistryName(energeticModule.getRegistryName()));
        ForgeRegistries.ITEMS.getValuesCollection().stream()
                .filter(item -> item instanceof ItemArmor)
                .filter(item -> ((ItemArmor) item).armorType == EntityEquipmentSlot.HEAD)
                .forEach(item -> reg.getRegistry().register(new ShapelessRecipes(MODID + ":energetic_" + item.getUnlocalizedName(), new ItemStack(item), NonNullList.from(Ingredient.EMPTY, Ingredient.fromItems(item), Ingredient.fromItem(energeticModule))){
                    @Nonnull
                    @Override
                    public ItemStack getCraftingResult(InventoryCrafting inv) {
                        NBTTagCompound nbt = new NBTTagCompound();
                        for(int i = 0; i < inv.getSizeInventory(); i++) {
                            ItemStack stack = inv.getStackInSlot(i);
                            if (!stack.isEmpty() && stack.getItem() instanceof ItemArmor) {
                                if(stack.hasTagCompound()){
                                    nbt = stack.getTagCompound();
                                }
                            }
                        }
                        ItemStack out = super.getCraftingResult(inv);
                        nbt.setBoolean("EnergeticBoots", true);
                        out.setTagCompound(nbt);
                        return out;
                    }

                    @Override
                    public boolean matches(InventoryCrafting inv, World worldIn) {
                        if(super.matches(inv, worldIn)){
                            for(int i = 0; i < inv.getSizeInventory(); i++){
                                ItemStack stack = inv.getStackInSlot(i);
                                if(!stack.isEmpty() && stack.getItem() instanceof ItemArmor){
                                    NBTTagCompound nbt = stack.getTagCompound();
                                    if(nbt != null && nbt.hasKey("EnergeticBoots", Constants.NBT.TAG_BYTE)){
                                        return false;
                                    }
                                }
                            }
                        }
                        return super.matches(inv, worldIn);
                    }
                }.setRegistryName(MODID, "energetic_" + item.getUnlocalizedName())));
    }

    @SubscribeEvent
    public static void updatePlayer(TickEvent.PlayerTickEvent event){
        if(event.phase == TickEvent.Phase.END){
            ItemStack helmet = event.player.inventory.armorInventory.get(EntityEquipmentSlot.HEAD.getIndex());
            boolean energetic = false;
            if(!helmet.isEmpty() && helmet.hasTagCompound()){
                NBTTagCompound nbt = helmet.getTagCompound();
                if(nbt.hasKey("EnergeticBoots", Constants.NBT.TAG_BYTE)){
                    energetic = true;
                }
            }
            if(energetic){

            }
        }
    }

}
