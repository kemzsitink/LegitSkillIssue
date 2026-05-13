package com.legitskillissue.client.gui.components;

import com.legitskillissue.client.gui.GuiConstants;
import com.legitskillissue.client.gui.ElementaUtils;
import com.legitskillissue.client.module.Category;
import com.legitskillissue.client.module.Module;
import com.legitskillissue.client.module.ModuleManager;
import gg.essential.elementa.UIComponent;
import gg.essential.elementa.components.UIBlock;
import gg.essential.elementa.components.UIText;
import gg.essential.elementa.components.UIRoundedRectangle;
import gg.essential.elementa.constraints.CenterConstraint;
import gg.essential.elementa.constraints.PixelConstraint;
import gg.essential.elementa.constraints.RelativeConstraint;
import gg.essential.elementa.events.UIClickEvent;
import gg.essential.elementa.effects.OutlineEffect;
import gg.essential.universal.UMatrixStack;
import kotlin.Unit;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.functions.Function4;
import java.awt.Color;
import java.util.List;

public final class CategoryPanel extends UIRoundedRectangle {
    private boolean isDragging = false;
    private float dragOffsetX = 0f;
    private float dragOffsetY = 0f;
    private float scrollOffset = 0f;
    private float maxScroll = 0f;
    private final UIBlock listContainer;
    private boolean collapsed = false;
    private boolean uncollapsing = false;
    private final UIText arrow;
    private float targetListHeight = 0f;

    public CategoryPanel(Category category, float startX, float startY) {
        super(6.0f); // Prototype: rounded-xl (12px) -> reduced to 6px
        this.setX(new PixelConstraint(startX));
        this.setY(new PixelConstraint(startY));
        this.setWidth(new PixelConstraint(GuiConstants.PANEL_WIDTH));
        this.setColor(GuiConstants.BG_PANEL);
        this.enableEffect(new OutlineEffect(GuiConstants.BORDER, 1.0f));

        // --- HEADER ---
        UIBlock header = new UIBlock(GuiConstants.BG_HEADER);
        header.setWidth(new RelativeConstraint(1.0f));
        header.setHeight(new PixelConstraint(GuiConstants.HEADER_HEIGHT));
        
        UIText title = new UIText(category.name(), false);
        title.setX(new PixelConstraint(8.0f)); 
        title.setY(new CenterConstraint());
        title.setTextScale(new PixelConstraint(0.75f));
        title.setColor(GuiConstants.TEXT_MAIN);
        header.addChild(title);
        
        arrow = new UIText("▼", false);
        arrow.setX(new PixelConstraint(GuiConstants.PANEL_WIDTH - 12.0f));
        arrow.setY(new CenterConstraint());
        arrow.setTextScale(new PixelConstraint(0.6f));
        arrow.setColor(GuiConstants.TEXT_DIM);
        header.addChild(arrow);
        
        this.addChild(header);

        // --- MODULES ---
        List<Module> categoryModules = ModuleManager.INSTANCE.getModules().stream()
                .filter(m -> m.getCategory() == category)
                .toList();

        listContainer = new UIBlock(new Color(0,0,0,0));
        listContainer.setY(new PixelConstraint(GuiConstants.HEADER_HEIGHT));
        listContainer.setWidth(new RelativeConstraint(1.0f));
        listContainer.enableEffect(new gg.essential.elementa.effects.ScissorEffect());
        listContainer.onMouseClick(new Function2<UIComponent, UIClickEvent, Unit>() {
            @Override
            public Unit invoke(UIComponent c, UIClickEvent e) {
                return Unit.INSTANCE;
            }
        });
        
        listContainer.onMouseScroll(new Function2<UIComponent, gg.essential.elementa.events.UIScrollEvent, Unit>() {
            @Override
            public Unit invoke(UIComponent c, gg.essential.elementa.events.UIScrollEvent e) {
                scrollOffset += (float) e.getDelta() * 20f;
                if (scrollOffset > 0) scrollOffset = 0;
                if (scrollOffset < -maxScroll) scrollOffset = -maxScroll;
                return Unit.INSTANCE;
            }
        });
        
        this.addChild(listContainer);

        for (Module m : categoryModules) {
            ModuleComponent modComp = new ModuleComponent(m);
            listContainer.addChild(modComp);
        }

        // Drag & Collapse Logic
        header.onMouseClick(new Function2<UIComponent, UIClickEvent, Unit>() {
            @Override
            public Unit invoke(UIComponent c, UIClickEvent e) {
                e.stopPropagation();
                
                if (e.getMouseButton() == 0) {
                    isDragging = true;
                    // Calculate offset from panel's top-left to absolute mouse position
                    dragOffsetX = e.getAbsoluteX() - getLeft();
                    dragOffsetY = e.getAbsoluteY() - getTop();
                } else if (e.getMouseButton() == 1) {
                    collapsed = !collapsed;
                    arrow.setText(collapsed ? "▲" : "▼");
                    if (!collapsed) {
                        uncollapsing = true;
                    }
                    float h = collapsed ? 0f : targetListHeight;
                    ElementaUtils.animateHeight(listContainer, h, 0.4f);
                }
                return Unit.INSTANCE;
            }
        });

        header.onMouseRelease(new kotlin.jvm.functions.Function1<UIComponent, Unit>() {
            @Override
            public Unit invoke(UIComponent c) {
                isDragging = false;
                return Unit.INSTANCE;
            }
        });

        header.onMouseDrag(new Function4<UIComponent, Float, Float, Integer, Unit>() {
            @Override
            public Unit invoke(UIComponent c, Float mouseX, Float mouseY, Integer button) {
                if (button == 0 && isDragging) {
                    // c is the header. mouseX/mouseY are relative to the header.
                    // Convert relative mouse coords to absolute mouse coords.
                    float currentAbsoluteMouseX = c.getLeft() + mouseX;
                    float currentAbsoluteMouseY = c.getTop() + mouseY;
                    
                    // The panel's new absolute position should be the mouse absolute minus the initial offset
                    float newPanelAbsoluteX = currentAbsoluteMouseX - dragOffsetX;
                    float newPanelAbsoluteY = currentAbsoluteMouseY - dragOffsetY;
                    
                    // Translate new absolute to parent's local space for setX/setY
                    UIComponent parent = getParent();
                    float parentLeft = parent != null ? parent.getLeft() : 0f;
                    float parentTop = parent != null ? parent.getTop() : 0f;
                    
                    float finalX = newPanelAbsoluteX - parentLeft;
                    float finalY = newPanelAbsoluteY - parentTop;
                    
                    setX(new PixelConstraint(finalX));
                    setY(new PixelConstraint(finalY));
                    
                    com.legitskillissue.client.gui.ClickGuiScreen.savedX.put(category, finalX);
                    com.legitskillissue.client.gui.ClickGuiScreen.savedY.put(category, finalY);
                }
                return Unit.INSTANCE;
            }
        });
    }

    @Override
    public void beforeDraw(UMatrixStack matrixStack) {
        super.beforeDraw(matrixStack);
        
        float currentY = scrollOffset;
        float totalHeight = 0f;
        for (UIComponent child : listContainer.getChildren()) {
            child.setY(new PixelConstraint(currentY));
            currentY += child.getHeight();
            totalHeight += child.getHeight();
        }
        
        float maxVisibleHeight = 150f;
        maxScroll = Math.max(0f, totalHeight - maxVisibleHeight);
        float targetContainerHeight = Math.min(totalHeight, maxVisibleHeight);
        
        if (!collapsed) {
            if (targetListHeight != targetContainerHeight && uncollapsing) {
                ElementaUtils.animateHeight(listContainer, targetContainerHeight, 0.4f);
            }
            targetListHeight = targetContainerHeight;
            if (uncollapsing) {
                if (Math.abs(listContainer.getHeight() - targetListHeight) < 1.5f) {
                    uncollapsing = false;
                    listContainer.setHeight(new PixelConstraint(targetContainerHeight));
                }
            } else {
                listContainer.setHeight(new PixelConstraint(targetContainerHeight));
            }
        }
        this.setHeight(new PixelConstraint(GuiConstants.HEADER_HEIGHT + listContainer.getHeight()));
    }
}
