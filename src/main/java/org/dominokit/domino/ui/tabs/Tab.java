package org.dominokit.domino.ui.tabs;

import elemental2.dom.*;
import org.dominokit.domino.ui.grid.flex.FlexItem;
import org.dominokit.domino.ui.grid.flex.FlexLayout;
import org.dominokit.domino.ui.icons.BaseIcon;
import org.dominokit.domino.ui.icons.Icons;
import org.dominokit.domino.ui.icons.MdiIcon;
import org.dominokit.domino.ui.utils.BaseDominoElement;
import org.dominokit.domino.ui.utils.DominoElement;
import org.dominokit.domino.ui.utils.HasClickableElement;
import org.dominokit.domino.ui.utils.TextNode;
import org.jboss.gwt.elemento.core.EventType;
import org.jboss.gwt.elemento.core.IsElement;

import static java.util.Objects.nonNull;
import static org.jboss.gwt.elemento.core.Elements.*;

public class Tab extends BaseDominoElement<HTMLLIElement, Tab> implements HasClickableElement {

    private HTMLAnchorElement clickableElement = a().asElement();
    private DominoElement<HTMLLIElement> tab = DominoElement.of(li().attr("role", "presentation").add(clickableElement));
    private DominoElement<HTMLDivElement> contentContainer = DominoElement.of(div().attr("role", "tabpanel").css("tab-pane", "fade"));
    private FlexItem closeContainer = FlexItem.create();
    private FlexLayout tabElementsContainer;
    private boolean active;
    private MdiIcon closeIcon;
    private TabsPanel parent;
    private CloseHandler closeHandler = tab -> true;

    public Tab(String text) {
        this(null, text);
    }

    public Tab(BaseIcon<?> icon) {
        this(icon, null);
    }

    public Tab(BaseIcon<?> icon, String text) {
        FlexItem iconContainer = FlexItem.create();
        FlexItem textContainer = FlexItem.create();
        tabElementsContainer = FlexLayout.create();

        if (nonNull(icon)) {
            tabElementsContainer
                    .appendChild(iconContainer.appendChild(icon));
        }
        if (nonNull(text)) {
            tabElementsContainer.appendChild(textContainer.appendChild(span().add(TextNode.of(text))));
        }

        closeIcon = Icons.ALL.close_mdi()
                .size18()
                .addClickListener(evt -> {
                    evt.stopPropagation();
                    close();

                })
                .addEventListener(EventType.mousedown.getName(), evt -> {
                    evt.stopPropagation();
                })
                .clickable();

        clickableElement.appendChild(tabElementsContainer.asElement());
        init(this);
        withWaves();
    }

    public static Tab create(String text) {
        return new Tab(text);
    }

    public static Tab create(BaseIcon<?> icon) {
        return new Tab(icon);
    }

    public static Tab create(BaseIcon<?> icon, String text) {
        return new Tab(icon, text);
    }

    public DominoElement<HTMLLIElement> getTab() {
        return DominoElement.of(tab);
    }

    public DominoElement<HTMLDivElement> getContentContainer() {
        return DominoElement.of(contentContainer);
    }

    /**
     * @deprecated use {@link #appendChild(Node)}
     */
    @Deprecated
    public Tab appendContent(Node content) {
        return appendChild(content);
    }


    public Tab appendChild(Node content) {
        contentContainer.appendChild(content);
        return this;
    }

    public Tab appendChild(IsElement content) {
        return appendChild(content.asElement());
    }

    public Tab activate() {
        tab.style().add("active");
        contentContainer.style().add("in", "active");
        this.active = true;
        return this;
    }

    public Tab deActivate() {
        tab.style().remove("active");
        contentContainer.style().remove("in", "active");
        this.active = false;
        return this;
    }

    public Tab setClosable(boolean closable) {
        if (closable) {
            closeContainer.clearElement();
            closeContainer.appendChild(closeIcon);
            tabElementsContainer.appendChild(closeContainer);
        } else {
            closeContainer.remove();
        }

        return this;
    }

    public Tab close() {
        if (nonNull(parent)) {
            if (closeHandler.onBeforeClose(this)) {
                parent.closeTab(this);
            }
        }

        return this;
    }

    public Tab closable() {
        return setClosable(true);
    }

    public Tab notClosable() {
        return setClosable(false);
    }

    public Tab setOnBeforeCloseHandler(CloseHandler closeHandler) {
        if (nonNull(closeHandler)) {
            this.closeHandler = closeHandler;
        }
        return this;
    }

    public boolean isActive() {
        return active;
    }

    @Override
    public HTMLAnchorElement getClickableElement() {
        return clickableElement;
    }

    @Override
    public HTMLLIElement asElement() {
        return tab.asElement();
    }

    void setParent(TabsPanel tabsPanel) {
        this.parent = tabsPanel;
    }

    public interface CloseHandler {
        boolean onBeforeClose(Tab tab);
    }
}
