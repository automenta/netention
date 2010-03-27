package automenta.netention.swing.property;

import automenta.netention.Detail;
import automenta.netention.Mode;
import automenta.netention.Property;
import automenta.netention.PropertyValue;
import automenta.netention.Self;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import automenta.netention.swing.util.JHyperLink;
import automenta.netention.swing.widget.DetailEditPanel;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionListener;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;


abstract public class PropertyOptionPanel extends JPanel {

	private JComboBox typeSelect;

	private JPanel editPanel;
	private PropertyValue value;

	private List<PropertyOption> options = new ArrayList();

	private PropertyOption currentOption;
    private final Self self;
    private final Detail detail;
    private final String propertyID;
    private final Property property;
    private boolean editable;
    private final JLabel typeLabel;
    private JHyperLink nameLabel;

	public PropertyOptionPanel(Self s, Detail d, PropertyValue v, boolean editable) {
        //super(new FlowLayout(FlowLayout.LEFT));
        super(new GridBagLayout());

        setOpaque(false);

        this.self = s;
        this.detail = d;
        this.propertyID = v.getProperty();
        this.property = s.getProperty(propertyID);
        this.value = v;


        typeLabel = new JLabel("");
        
        setValue(value);

        initOptions(options);

        setEditable(editable);

	}

	protected void refresh() {
        GridBagConstraints gc = new GridBagConstraints();
        gc.gridx = 1;
        gc.gridy = 1;
        gc.weightx = 0.0;

        
		//super.initPropertyPanel();
        removeAll();
		
        //add(new JLabel(property.getName()));
        nameLabel = new JHyperLink(property.getName(), "");
        
        add(nameLabel, gc);
        gc.gridx++;

		typeSelect = new JComboBox();
		for (PropertyOption po : options) {
			typeSelect.addItem(po.getName());			
		}
        typeSelect.addActionListener(new ActionListener() {
            @Override public void actionPerformed(ActionEvent e) {
				int x = typeSelect.getSelectedIndex();
				
				PropertyOption po = options.get(x);
				setCurrentOption(po);

                if (!po.accepts(getValue()))
                    setValue(po.newDefaultValue());

				JPanel p = po.newEditPanel(value);
				
				editPanel.removeAll();
				editPanel.add(p);

                updateUI();
			}

		});

		add(typeSelect, gc);
        gc.gridx++;

        add(typeLabel, gc);
        gc.gridx++;
		
        editPanel = new JPanel(new GridLayout(1,1));
        editPanel.setOpaque(false);

        gc.weightx = 1.0;
        gc.fill = gc.HORIZONTAL;
		add(editPanel, gc);

		valueToWidget();

        updateUI();

	}

    public Mode getMode() {
        return getDetail().getMode();
    }
    
    protected void setIs() {

    }
    protected void setWillBe() {

    }

    protected void setValue(PropertyValue val) {
        this.value = val;
    }

	abstract protected void initOptions(List<PropertyOption> options);

	/** load */
	private void valueToWidget() {
		if (value==null)
			return;

        if (options.size() >=2) {
            typeSelect.setVisible( true );
            typeLabel.setVisible( false );
        }
        else {
            typeSelect.setVisible( false );
            typeLabel.setVisible( true );
        }

		for (int i = 0; i < options.size(); i++) {
			PropertyOption po = options.get(i);
			if (po.accepts(value)) {

				typeSelect.setSelectedIndex(i);
                typeLabel.setText(typeSelect.getSelectedItem().toString());
				
				setCurrentOption(po);
				
				JPanel p = po.newEditPanel(value);
				editPanel.removeAll();
				editPanel.add(p);
				
				return;
			}
		}

        updateUI();

        //System.out.println("unknown option for: " + value);
	}

	private void setCurrentOption(PropertyOption po) {
		this.currentOption = po;		
	}

//	protected void setValue(PropertyValue newValue) {
//		PropertyValue oldValue = this.value;
//		this.value = newValue;
//
//		this.value.setProperty(getProperty());
//
//		//TODO replace old with new value, at original index
//		if (getNode()!=null) {
//			if (oldValue!=newValue) {
//				synchronized (getNode().getProperties()) {
//					getNode().getProperties().remove(oldValue);
//					getNode().getProperties().add(newValue);
//				}
//			}
//		}
//
//	}

//	@Override
//	public void setNode(DetailData node) {
//		super.setNode(node);
//		setValue(getValue());
//	}
	
	/** save */
	public void widgetToValue() {
		if (currentOption!=null) {
			//causes value to be updated by data presently in the widgets

			setValue(currentOption.getValue());
		}
	}

	public PropertyValue getValue() {
		return value;
	}

    public Property getProperty() {
        return property;
    }

    private void setEditable(boolean editable) {
        this.editable = editable;
        refresh();
    }

    public Detail getDetail() {
        return detail;
    }

    public JHyperLink getNameLabel() {
        return nameLabel;
    }


    
}
