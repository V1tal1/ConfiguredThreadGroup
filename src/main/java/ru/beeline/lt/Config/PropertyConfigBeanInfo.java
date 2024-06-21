package ru.beeline.lt.Config;


import org.apache.jmeter.testbeans.BeanInfoSupport;
import org.apache.jmeter.testbeans.gui.FileEditor;

import java.beans.PropertyDescriptor;

public class PropertyConfigBeanInfo extends BeanInfoSupport {

    // These names must agree case-wise with the variable and property names
    private static final String FILENAME = "filename";




    public PropertyConfigBeanInfo() {
        super(PropertyConfig.class);

        createPropertyGroup("yaml_config",             //$NON-NLS-1$
                new String[] { FILENAME });

        PropertyDescriptor p = property(FILENAME);
        p.setValue(NOT_UNDEFINED, Boolean.TRUE);
        p.setValue(DEFAULT, "");        //$NON-NLS-1$
        p.setValue(NOT_EXPRESSION, Boolean.TRUE);
        p.setPropertyEditorClass(FileEditor.class);

    }





    //вариант с таблицей из варсов
//    public PropertyConfigBeanInfo() {
//        super(PropertyConfig.class);
//
//        PropertyDescriptor headerTable = property("messageHeaders", TypeEditor.TableEditor);
//        headerTable.setValue(TableEditor.CLASSNAME, VariableSettings.class.getName());
//        headerTable.setValue(TableEditor.HEADERS, new String[]{ "Key", "Value" } );
//        headerTable.setValue(TableEditor.OBJECT_PROPERTIES, new String[]{ VariableSettings.KEY, VariableSettings.VALUE } );
//        headerTable.setValue(DEFAULT, new ArrayList<>());
//        headerTable.setValue(NOT_UNDEFINED, Boolean.TRUE);
//        headerTable.setDisplayName("Message Headers (Optional)");
//
//    }
}






