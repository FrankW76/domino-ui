package org.dominokit.domino.ui.forms;

import elemental2.dom.Event;
import elemental2.dom.EventListener;
import elemental2.dom.HTMLAnchorElement;
import elemental2.dom.HTMLInputElement;

import org.dominokit.domino.ui.keyboard.KeyboardEvents;
import org.dominokit.domino.ui.style.Color;
import org.dominokit.domino.ui.utils.Checkable;
import org.dominokit.domino.ui.utils.DominoElement;
import org.jboss.elemento.IsElement;

import static java.util.Objects.isNull;
import static org.jboss.elemento.Elements.input;

public class CheckBox extends AbstractValueBox<CheckBox, HTMLInputElement, Boolean> implements Checkable<CheckBox> {

    public static final String READONLY = "readonly";
    private Color color;
    private String checkedReadonlyLabel = "Yes";
    private String unCheckedReadonlyLabel = "No";
    private String label;

    public CheckBox() {
        this("");
    }

    public CheckBox(String label) {
        super("checkbox", label);
        this.label = label;
        css("d-checkbox");
        setLabel(label);
        getInputElement().addEventListener("change", evt -> onCheck());

        EventListener listener = evt -> {
            evt.stopPropagation();
            evt.preventDefault();
            if (isEnabled() && !isReadOnly())
                toggle();
        };

        getLabelElement().addEventListener("click", listener);

        KeyboardEvents.listenOn(getInputElement()).onEnter(listener);
    }

    private void onCheck() {
        changeHandlers.forEach(handler -> handler.onValueChanged(isChecked()));
    }

    public static CheckBox create(String label) {
        return new CheckBox(label);
    }

    public static CheckBox create() {
        return new CheckBox();
    }

    public static CheckBox create(IsElement<HTMLAnchorElement> link) {
        return create(DominoElement.of(link));
    }

    public static CheckBox create(HTMLAnchorElement link) {
        return create(DominoElement.of(link));
    }

    public static CheckBox create(DominoElement<HTMLAnchorElement> link) {
        CheckBox checkBox = new CheckBox();
        checkBox.setLabel(link.element());
        link.addClickListener(Event::stopPropagation);
        return checkBox;
    }

    public CheckBox toggle() {
        if (isChecked()) {
            uncheck();
            element.removeCss("checked");
        } else {
            check();
            element.css("checked");
        }
        return this;
    }

    @Override
    public CheckBox check() {
        return check(false);
    }

    @Override
    public CheckBox uncheck() {
        return uncheck(false);
    }

    @Override
    public CheckBox check(boolean silent) {
        getInputElement().element().checked = true;
        element.css("checked");
        if (!silent)
            onCheck();
        if (isReadOnly())
            changeReadOnlyText();
        return this;
    }

    @Override
    public CheckBox uncheck(boolean silent) {
        getInputElement().element().checked = false;
        element.removeCss("checked");
        if (!silent)
            onCheck();
        if (isReadOnly())
            changeReadOnlyText();
        return this;
    }

    @Override
    public boolean isChecked() {
        return getInputElement().element().checked;
    }

    @Override
    public CheckBox addChangeHandler(ChangeHandler<? super Boolean> changeHandler) {
        changeHandlers.add(changeHandler);
        return this;
    }

    @Override
    public CheckBox removeChangeHandler(ChangeHandler<? super Boolean> changeHandler) {
        if (changeHandler != null)
            changeHandlers.remove(changeHandler);
        return this;
    }

    @Override
    public boolean hasChangeHandler(ChangeHandler<? super Boolean> changeHandler) {
        return changeHandlers.contains(changeHandler);
    }

    public CheckBox filledIn() {
        element.style().add("filled-in");
        return this;
    }

    public CheckBox filledOut() {
        element.style().remove("filled-in");
        return this;
    }

    public CheckBox setColor(Color color) {
        if (this.color != null) {
           element.style().remove(this.color.getStyle());
        }
        element.style().add(color.getStyle());
        this.color = color;
        return this;
    }

    @Override
    public CheckBox value(Boolean value) {
        if (value != null && value) {
            check();
        } else {
            uncheck();
        }
        return this;
    }

    @Override
    public Boolean getValue() {
        return isChecked();
    }

    @Override
    public boolean isEmpty() {
        return !isChecked();
    }

    @Override
    public CheckBox setReadOnly(boolean readOnly) {
        super.setReadOnly(readOnly);
        if (readOnly) {
            getInputElement().setReadOnly(true);
            css(READONLY);
            changeReadOnlyText();
        } else {
            getInputElement().setReadOnly(false);
            removeCss(READONLY);
            getLabelTextElement().setTextContent(label);
        }
        return this;
    }

    private void changeReadOnlyText() {
        if (isChecked()) {
            getLabelTextElement().setTextContent(label + getCheckedReadonlyLabel());
        } else {
            getLabelTextElement().setTextContent(label + getUnCheckedReadonlyLabel());
        }
    }

    private String getCheckedReadonlyLabel() {
        return isNull(checkedReadonlyLabel) || checkedReadonlyLabel.isEmpty() ? "" : ": " + checkedReadonlyLabel;
    }

    private String getUnCheckedReadonlyLabel() {
        return isNull(unCheckedReadonlyLabel) || unCheckedReadonlyLabel.isEmpty() ? "" : ": " + unCheckedReadonlyLabel;
    }

    public CheckBox setCheckedReadonlyLabel(String checkedReadonlyLabel) {
        this.checkedReadonlyLabel = checkedReadonlyLabel;
        return this;
    }

    public CheckBox setUnCheckedReadonlyLabel(String unCheckedReadonlyLabel) {
        this.unCheckedReadonlyLabel = unCheckedReadonlyLabel;
        return this;
    }

    @Override
    public String getStringValue() {
        return Boolean.toString(getValue());
    }

    @Override
    protected HTMLInputElement createInputElement(String type) {
        return DominoElement.of(input("checkbox")).element();
    }

    @Override
    protected void clearValue() {
        value(false);
    }

    @Override
    protected void doSetValue(Boolean value) {

    }

    @Override
    protected boolean isAddFocusColor() {
        return false;
    }

    @Override
    protected AutoValidator createAutoValidator(AutoValidate autoValidate) {
        return new CheckBoxAutoValidator<>(this, autoValidate);
    }

    private static class CheckBoxAutoValidator<T> extends AutoValidator {

        private CheckBox checkBox;
        private ChangeHandler<Boolean> changeHandler;

        public CheckBoxAutoValidator(CheckBox checkBox, AutoValidate autoValidate) {
            super(autoValidate);
            this.checkBox = checkBox;
        }

        @Override
        public void attach() {

            changeHandler = value -> autoValidate.apply();
            checkBox.addChangeHandler(changeHandler);
        }

        @Override
        public void remove() {
            checkBox.removeChangeHandler(changeHandler);
        }
    }
}