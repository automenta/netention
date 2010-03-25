/**
 * 
 */
package automenta.netention.swing.property;

import automenta.netention.Detail;
import automenta.netention.Mode;
import automenta.netention.PropertyValue;
import automenta.netention.Self;
import automenta.netention.Value;
import automenta.netention.value.integer.IntegerBetween;
import automenta.netention.value.integer.IntegerEquals;
import automenta.netention.value.integer.IntegerIs;
import automenta.netention.value.integer.IntegerLessThan;
import automenta.netention.value.integer.IntegerMoreThan;
import java.awt.Label;
import java.util.List;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class IntPropertyPanel extends PropertyOptionPanel {

    private JLabel unitLabel;

    public IntPropertyPanel(Self s, Detail d, PropertyValue v, boolean editable) {
        super(s, d, v, editable);
    }

    @Override protected void initOptions(List<PropertyOption> options) {
        if (getMode() == Mode.Real) {
            options.add(new PropertyOption<IntegerIs>("is") {

                private JTextField isBox;

                @Override public JPanel newEditPanel(IntegerIs v) {
                    setValue(v);
                    setIs();

                    JPanel p = new TransparentFlowPanel();
                    isBox = new JTextField(Integer.toString(v.getValue()));
                    p.add(isBox);
                    return p;
                }

                @Override public IntegerIs widgetToValue(IntegerIs r) {
                    r.setValue(Integer.valueOf(isBox.getText()));
                    return r;
                }

                @Override public boolean accepts(Value v) {
                    return v.getClass().equals(IntegerIs.class);
                }

                @Override public IntegerIs newDefaultValue() {
                    return new IntegerIs(0);
                }
            });

        } else if (getMode() == Mode.Imaginary) {

            options.add(new PropertyOption<IntegerEquals>("will equal") {

                private JTextField equalsBox;

                @Override public JPanel newEditPanel(IntegerEquals v) {
                    setValue(v);
                    setWillBe();

                    JPanel p = new TransparentFlowPanel();
                    equalsBox = new JTextField(Integer.toString(v.getValue()));
                    p.add(equalsBox);
                    return p;
                }

                @Override public boolean accepts(Value v) {
                    return v.getClass().equals(IntegerEquals.class);
                }

                @Override public IntegerEquals widgetToValue(IntegerEquals r) {
                    r.setValue(Integer.valueOf(equalsBox.getText()));
                    return r;
                }

                @Override public IntegerEquals newDefaultValue() {
                    return new IntegerEquals(0);
                }
            });

            options.add(new PropertyOption<IntegerMoreThan>("will be greater than") {

                private JTextField moreThanBox;

                @Override public JPanel newEditPanel(IntegerMoreThan v) {
                    setValue(v);
                    setWillBe();

                    JPanel p = new TransparentFlowPanel();
                    moreThanBox = new JTextField();
                    moreThanBox.setText(Double.toString(v.getValue()));
                    p.add(moreThanBox);
                    return p;
                }

                @Override public IntegerMoreThan widgetToValue(IntegerMoreThan r) {
                    r.setValue(Integer.valueOf(moreThanBox.getText()));
                    return r;
                }

                @Override public boolean accepts(Value v) {
                    return v.getClass().equals(IntegerMoreThan.class);
                }

                @Override public IntegerMoreThan newDefaultValue() {
                    return new IntegerMoreThan(0);
                }
            });

            options.add(new PropertyOption<IntegerLessThan>("will be less than") {

                private JTextField lessThanBox;

                @Override public JPanel newEditPanel(IntegerLessThan v) {
                    setValue(v);
                    setWillBe();

                    JPanel p = new TransparentFlowPanel();
                    lessThanBox = new JTextField();
                    lessThanBox.setText(Double.toString(v.getValue()));
                    p.add(lessThanBox);
                    return p;
                }

                @Override
                public IntegerLessThan widgetToValue(IntegerLessThan r) {
                    r.setValue(Integer.valueOf(lessThanBox.getText()));
                    return r;
                }

                @Override public boolean accepts(Value v) {
                    return v.getClass().equals(IntegerLessThan.class);
                }

                @Override public IntegerLessThan newDefaultValue() {
                    return new IntegerLessThan(0);
                }
            });

            options.add(new PropertyOption<IntegerBetween>("will be between") {

                private JTextField minBox;
                private JTextField maxBox;

                @Override public JPanel newEditPanel(IntegerBetween v) {
                    setValue(v);
                    setWillBe();

                    JPanel p = new TransparentFlowPanel();
                    minBox = new JTextField();
                    minBox.setText(Integer.toString(v.getMin()));
                    p.add(minBox);

                    p.add(new Label(" and "));

                    maxBox = new JTextField();
                    maxBox.setText(Integer.toString(v.getMax()));
                    p.add(maxBox);

                    return p;
                }

                @Override
                public IntegerBetween widgetToValue(IntegerBetween r) {
                    //...
                    return r;
                }

                @Override public boolean accepts(Value v) {
                    return v.getClass().equals(IntegerBetween.class);
                }

                @Override public IntegerBetween newDefaultValue() {
                    return new IntegerBetween(0, 0, true);
                }
            });
        }

    }
}
