package com.swacky.ohmega.client.screen;

import com.swacky.ohmega.api.AccessoryHelper;
import com.swacky.ohmega.client.screen.widget.AccessoryInventoryButton;
import com.swacky.ohmega.client.screen.widget.FlipPlayerButton;
import com.swacky.ohmega.client.screen.widget.VisibilityButton;
import com.swacky.ohmega.common.Ohmega;
import com.swacky.ohmega.common.menu.AccessoryInventoryMenu;
import com.swacky.ohmega.common.menu.AccessorySlot;
import com.swacky.ohmega.config.OhmegaConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.EffectsInInventory;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Inventory;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.jspecify.annotations.NonNull;

public final class AccessoryInventoryScreen extends AbstractContainerScreen<@NonNull AccessoryInventoryMenu> {
    private static final Identifier SLOT_LOCATION = Ohmega.id("textures/gui/container/accessory_inventory/slot.png");

    private final EffectsInInventory effects;
    private final int extraWidth;
    private final int mostSlotsPerColumn;
    private int xOffsBg;
    private int xOffsSlots;
    private boolean flipPlayer = false;

    @SuppressWarnings("unused")
    public AccessoryInventoryScreen(AccessoryInventoryMenu menu, Inventory inv, Component title) {
        int extraWidth = 2 + 4 * 2 + 18 * (int) Math.min(Math.ceil((double) AccessoryHelper.getSlotTypes().size() / Math.min(OhmegaConfig.Client.maxColumnRenderSlots(), OhmegaConfig.Client.maxColumnSlots())), OhmegaConfig.Client.maxColumns());

        super(menu, inv, Component.translatable("container.crafting"), 176 + extraWidth, 166);

        this.extraWidth = extraWidth;
        this.effects = new EffectsInInventory(this);

        if (OhmegaConfig.Client.side() == OhmegaConfig.Client.Service.Side.LEFT) {
            this.titleLabelX = 97 + extraWidth;
        } else {
            this.titleLabelX = 97;
        }

        this.mostSlotsPerColumn = Math.min(menu.renderSlots, AccessoryHelper.getSlotTypes().size());
    }

    private void addVisibilityWidgets() {
        int index = 0;

        for (int i = 0; i < menu.renderColumns; i++) {
            // Slots
            int slotsCreatedCurrentColumn = 0;

            for (int j = 0; true; j++) {
                addRenderableWidget(new VisibilityButton(menu.getPlayer(), j + i * mostSlotsPerColumn, xOffsSlots + 18 * (i + 1) + 1, topPos + 24 + j * 18 - 2));

                index++;

                if (++slotsCreatedCurrentColumn >= mostSlotsPerColumn || index >= menu.slotsAvailable) {
                    break;
                }
            }
        }
    }

    @Override
    protected void init() {
        renderables.clear();

        if (OhmegaConfig.Client.side() == OhmegaConfig.Client.Service.Side.LEFT) {
            leftPos = (width - imageWidth - extraWidth) / 2;
            xOffsBg = leftPos + extraWidth;
            xOffsSlots = leftPos;
        } else {
            leftPos = (width - imageWidth + extraWidth) / 2;
            xOffsBg = leftPos;
            // Default, 2px buffer from inv, 1px to align
            xOffsSlots = leftPos + 175 + 2 + 1;
        }

        topPos = (height - imageHeight) / 2;

        addRenderableWidget(new AccessoryInventoryButton(this, OhmegaConfig.Client.buttonStyle()));
        addRenderableWidget(new FlipPlayerButton(this, leftPos + 65, topPos + 9));

        if (OhmegaConfig.Server.allowHideAccessories()) {
            addVisibilityWidgets();
        }
    }


    @Override
    public void extractRenderState(@NonNull GuiGraphicsExtractor gui, int mx, int my, float partialTicks) {
        super.extractRenderState(gui, mx, my, partialTicks);
        effects.extractRenderState(gui, mx, my);
    }

    @Override
    public boolean showsActiveEffects() {
        return effects.canSeeEffects();
    }

    private void blit(GuiGraphicsExtractor gui, int x, int y, int u, int v, int width, int height) {
        gui.blit(RenderPipelines.GUI_TEXTURED, SLOT_LOCATION, x + xOffsSlots, y, u, v, width, height, 26, 26);
    }

    private void renderAccInv(GuiGraphicsExtractor gui) {
        int lastColumnSlots = menu.slotsAvailable % mostSlotsPerColumn == 0 ? mostSlotsPerColumn : menu.slotsAvailable % mostSlotsPerColumn;
        int index = 0;

        for (int i = 0; i < menu.renderColumns; i++) {
            // Slots
            int slotsCreatedCurrentColumn = 0;

            for (int j = 0; true; j++) {
                blit(gui, 4 + 18 * i, topPos + 24 + j * 18, 4, 4, 18, 18);

                index++;

                if (++slotsCreatedCurrentColumn >= mostSlotsPerColumn || index >= menu.slotsAvailable) {
                    break;
                }
            }

            // Top border
            blit(gui, 4 + 18 * i, topPos + 20, 4, 0, 18, 4);

            // Bottom border
            if (i >= menu.renderColumns - 1 && lastColumnSlots != mostSlotsPerColumn) {
                blit(gui, 4 + 18 * i, topPos + 24 + 18 * lastColumnSlots, 4, 22, 18, 4);
            } else {
                blit(gui, 4 + 18 * i, topPos + 24 + 18 * mostSlotsPerColumn, 4, 22, 18, 4);
            }
        }

        // Side borders
        for (int i = 0; i < mostSlotsPerColumn; i++) {
            // Left
            blit(gui, 0, topPos + 24 + 18 * i, 0, 4, 4, 18);

            // Right
            if (i >= lastColumnSlots) {
                blit(gui, 4 + 18 * (menu.renderColumns - 1), topPos + 24 + 18 * i, 22, 4, 4, 18);
            } else {
                blit(gui, 4 + 18 * menu.renderColumns, topPos + 24 + 18 * i, 22, 4, 4, 18);
            }
        }

        // Top left corner
        blit(gui, 0, topPos + 20, 0, 0, 4, 4);

        // Top right corner
        blit(gui, 4 + 18 * menu.renderColumns, topPos + 20, 22, 0, 4, 4);

        // Bottom left corner
        blit(gui, 0, topPos + 24 + 18 * mostSlotsPerColumn, 0, 22, 4, 4);

        // Bottom right corner
        blit(gui, 4 + 18 * menu.renderColumns, topPos + 24 + 18 * lastColumnSlots, 22, 22, 4, 4);

        if (lastColumnSlots != mostSlotsPerColumn) {
            blit(gui, 4 + 18 * (menu.renderColumns - 1), topPos + 24 + 18 * mostSlotsPerColumn, 22, 22, 4, 4);
        }

        // Intersecting corner
        if (lastColumnSlots != mostSlotsPerColumn) {
            blit(gui, 5 + 18 * (menu.renderColumns - 1), topPos + 24 + 18 * lastColumnSlots, 19, 22, 3, 3);
            blit(gui, 5 + 18 * (menu.renderColumns - 1), topPos + 24 + 18 * lastColumnSlots + 1, 21, 22, 1, 1);
        }
    }

    public void extractEntityInInventoryFollowsMouse(final GuiGraphicsExtractor graphics, int x0, int y0, int x1, int y1, int size, float offsetY, float mouseX, float mouseY, LivingEntity entity) {
        float xAngle = (float) Math.atan(((x0 + x1) / 2f - mouseX) / 40);
        float yAngle = (float) Math.atan(((y0 + y1) / 2f - mouseY) / 40);
        EntityRenderState state = Minecraft
                .getInstance()
                .getEntityRenderDispatcher()
                .getRenderer(entity)
                .createRenderState(entity, 1);
        state.outlineColor = 0;

        state.shadowPieces.clear();

        if (state instanceof LivingEntityRenderState livingState) {
            livingState.bodyRot = 180;
            livingState.yRot = xAngle * 20;

            if (flipPlayer) {
                livingState.bodyRot -= xAngle * 20;
                livingState.yRot = -livingState.yRot;
            } else {
                livingState.bodyRot += xAngle * 20;
            }

            if (livingState.pose != Pose.FALL_FLYING) {
                livingState.xRot = -yAngle * 20;
            } else {
                livingState.xRot = 0;
            }

            livingState.boundingBoxWidth /= livingState.scale;
            livingState.boundingBoxHeight /= livingState.scale;
            livingState.scale = 1;
        }

        Vector3f translation = new Vector3f(0, state.boundingBoxHeight / 2 + offsetY, 0);
        Quaternionf rot = new Quaternionf().rotateZ((float) Math.PI);
        Quaternionf cameraRot = new Quaternionf().rotateX((float) (yAngle * 20 * (Math.PI / 180)));

        if (flipPlayer) {
            rot.rotationX((float) Math.PI);
        }

        rot.mul(cameraRot);
        graphics.entity(state, size, translation, rot, cameraRot, x0, y0, x1, y1);
    }

    @Override
    public void extractBackground(@NonNull GuiGraphicsExtractor gui, int mx, int my, float partialTicks) {
        super.extractBackground(gui, mx, my, partialTicks);

        LocalPlayer player = minecraft.player;

        if (player != null) {
            int x;

            if (OhmegaConfig.Client.side() == OhmegaConfig.Client.Service.Side.LEFT) {
                x = leftPos + extraWidth;
            } else {
                x = leftPos;
            }

            // Main inventory
            gui.blit(RenderPipelines.GUI_TEXTURED, InventoryScreen.INVENTORY_LOCATION, xOffsBg, topPos, 0, 0, 176, 166, 256, 256);

            // Accessory Inventory
            renderAccInv(gui);

            // Entity rendering
            extractEntityInInventoryFollowsMouse(gui, xOffsBg + 26, topPos + 8, xOffsBg + 75, topPos + 78, 30, 0.0625f, mx, my, player);
        }
    }

    @Override
    protected void extractLabels(@NonNull GuiGraphicsExtractor gui, int mx, int my) {
        gui.text(font, title, titleLabelX, titleLabelY, -12566464, false);
    }

    @Override
    protected void extractTooltip(@NonNull GuiGraphicsExtractor gui, int mx, int my) {
        if (menu.getCarried().isEmpty() && hoveredSlot instanceof AccessorySlot accSlot && accSlot.getType().displayHoverText() && OhmegaConfig.Client.showHoverTooltip() && !hoveredSlot.hasItem()) {
            gui.setTooltipForNextFrame(accSlot.getType().getTranslation(), mx, my);
        } else {
            super.extractTooltip(gui, mx, my);
        }
    }

    // todo: check for an "incomplete" last column
    @Override
    protected boolean hasClickedOutside(double x, double y, int left, int top) {
        if (super.hasClickedOutside(x, y, left, top)) {
            return true;
        }

        if (OhmegaConfig.Client.side() == OhmegaConfig.Client.Service.Side.LEFT) {
            // Ensure on correct side
            if (x < leftPos + extraWidth) {
                // Above
                if (y > topPos && y < topPos + 20) {
                    return true;
                }

                // Below
                return y > topPos + 20 + 4 + mostSlotsPerColumn * 18 + 4;
            }
        } else {
            // Ensure on correct side
            if (x > leftPos + 175) {
                // Above
                if (y > topPos && y < topPos + 20) {
                    return true;
                }

                // Below
                return y > topPos + 20 + 4 + mostSlotsPerColumn * 18 + 4;
            }
        }

        return false;
    }

    public int getExtraWidth() {
        return extraWidth;
    }

    public boolean isPlayerFlipped() {
        return flipPlayer;
    }

    public void toggleFlipPlayer() {
        flipPlayer = !flipPlayer;
    }
}
