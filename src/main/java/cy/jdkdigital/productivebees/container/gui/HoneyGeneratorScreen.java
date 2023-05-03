package cy.jdkdigital.productivebees.container.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.container.HoneyGeneratorContainer;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class HoneyGeneratorScreen extends AbstractContainerScreen<HoneyGeneratorContainer>
{
    private static final ResourceLocation GUI_TEXTURE = new ResourceLocation(ProductiveBees.MODID, "textures/gui/container/honey_generator.png");

    public HoneyGeneratorScreen(HoneyGeneratorContainer container, Inventory inv, Component titleIn) {
        super(container, inv, titleIn);
    }

    @Override
    public void render(@Nonnull PoseStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(matrixStack);
        super.render(matrixStack, mouseX, mouseY, partialTicks);
        this.renderTooltip(matrixStack, mouseX, mouseY);
    }

    @Override
    protected void renderLabels(PoseStack matrixStack, int mouseX, int mouseY) {
        this.font.draw(matrixStack, this.title, -5.0f, 6.0F, 4210752);
        this.font.draw(matrixStack, this.playerInventoryTitle, -5.0f, (float) (this.getYSize() - 96 + 2), 4210752);

        this.menu.tileEntity.getCapability(ForgeCapabilities.FLUID_HANDLER).ifPresent(handler -> {
            FluidStack fluidStack = handler.getFluidInTank(0);

            // Fluid level tooltip
            if (isHovering(129, 16, 6, 54, mouseX, mouseY)) {
                List<FormattedCharSequence> tooltipList = new ArrayList<>();

                if (fluidStack.getAmount() > 0) {
                    tooltipList.add(Component.translatable("productivebees.screen.fluid_level", Component.translatable(fluidStack.getTranslationKey()).getString(), fluidStack.getAmount() + "mB").getVisualOrderText());
                } else {
                    tooltipList.add(Component.translatable("productivebees.screen.empty").getVisualOrderText());
                }

                renderTooltip(matrixStack, tooltipList, mouseX - getGuiLeft(), mouseY - getGuiTop());
            }
        });

        this.menu.tileEntity.getCapability(ForgeCapabilities.ENERGY).ifPresent(handler -> {
            int energyAmount = handler.getEnergyStored();

            // Energy level tooltip
            if (isHovering(-5, 16, 6, 54, mouseX, mouseY)) {
                List<FormattedCharSequence> tooltipList = new ArrayList<>();
                tooltipList.add(Component.translatable("productivebees.screen.energy_level", energyAmount + "FE").getVisualOrderText());

                renderTooltip(matrixStack, tooltipList, mouseX - getGuiLeft(), mouseY - getGuiTop());
            }
        });
    }

    @Override
    protected void renderBg(@Nonnull PoseStack poseStack, float partialTicks, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, GUI_TEXTURE);

        // Draw main screen
        blit(poseStack, getGuiLeft() - 13, getGuiTop(), 0, 0, this.getXSize() + 26, this.getYSize());

        // Draw energy level
        blit(poseStack, getGuiLeft() - 5, getGuiTop() + 17, 206, 0, 4, 52);
        this.menu.tileEntity.getCapability(ForgeCapabilities.ENERGY).ifPresent(handler -> {
            float energyAmount = (float) handler.getEnergyStored();
            int energyLevel = (int) (energyAmount * (52f / (float) handler.getMaxEnergyStored()));
            blit(poseStack, getGuiLeft() - 5, getGuiTop() + 17, 8, 0, 4, 52 - energyLevel);
        });

        // Draw fluid tank
        this.menu.tileEntity.getCapability(ForgeCapabilities.FLUID_HANDLER).ifPresent(handler -> {
            FluidStack fluidStack = handler.getFluidInTank(0);

            if (fluidStack.getAmount() > 0) {
                FluidContainerUtil.renderFluidTank(poseStack, this, fluidStack, handler.getTankCapacity(0), 127, 69, 4, 52, 0);
            }
        });
    }
}
