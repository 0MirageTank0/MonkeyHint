package org.monkey.data;

import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.notification.Notification;

import com.intellij.openapi.project.Project;
import com.intellij.ui.JBColor;
import org.monkey.util.MessageUtils;

import javax.swing.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class MonkeyHintStoragePanel {
    private JTabbedPane tabPane;
    private JPanel mainPanel;
    private JTextArea textArea;
    private JButton addKey;
    private JList<String> valueList;
    private JList<String> keyList;
    private JButton delKey;
    private JButton addValue;
    private JButton delValue;
    private JButton editValue;
    private JButton saveButton;
    private JLabel saveTip;

    private final DefaultListModel<String> keyListModel;
    private final DefaultListModel<String> valueListModel;
    private  Map<String, List<String>> dataMap;
    private Project project;
    private boolean mapChanged;
    private boolean is_example;
    private MapDataStorage instance;

    public MonkeyHintStoragePanel(Project project) {
        this.project = project;
        keyListModel = new DefaultListModel<>();
        valueListModel = new DefaultListModel<>();
        keyList.setModel(keyListModel);
        valueList.setModel(valueListModel);

        keyList.addListSelectionListener(e -> updateValueList());
        addKey.addActionListener(e -> addKey());
        delKey.addActionListener(e -> deleteKey());
        addValue.addActionListener(e -> addValue());
        delValue.addActionListener(e -> deleteValue());
        editValue.addActionListener(e -> editValue());
        saveButton.addActionListener(e -> {
            if(mapChanged){
                updateDataFromMap();
                writeBackToCache();
            }
        });

        textArea.addFocusListener(new FocusListener() {
            @Override public void focusGained(FocusEvent focusEvent) {
                if (is_example){
                    is_example = false;
                    textArea.setText("");
                    textArea.setForeground(JBColor.foreground());
                }
            }
            @Override public void focusLost(FocusEvent focusEvent) {
                if(mapChanged || textAreaChanged()){
                    if (tabPane.getSelectedIndex() == 0) {
                        updateMapFromData();
                        writeBackToCache();
                    } else {
                        updateDataFromMap();
                        writeBackToCache();
                    }
                }
            }
        });
        tabPane.addChangeListener(e -> {
            if(is_example){
                is_example = false;
                textArea.setForeground(JBColor.foreground());
                textArea.setText("");
                return;
            }
            if (mapChanged || textAreaChanged()){
                if (tabPane.getSelectedIndex() == 0){
                    updateDataFromMap();
                    writeBackToCache();
                }else{
                    updateMapFromData();
                    writeBackToCache();
                }
            }
        });
        saveButton.setEnabled(false);
        saveTip.setText(MessageUtils.message("use.tip"));
        loadData();
    }

    public JPanel getPanel() {
        return mainPanel;
    }

    public boolean isModified() {
        return mapChanged;
    }

    public void setProject(Project project){
        this.project = project;
        instance = MapDataStorage.getInstance(project);
    }
    private boolean textAreaChanged(){
        if(!is_example){

            if (textArea.getText().isEmpty() && (instance.getData() == null || instance.getData().isEmpty()))
                return false;//textArea is empty && data is empty
            return !Objects.equals(instance.getData(), textArea.getText());
        }
        return false;
    }
    public void setMapChangedState(boolean newState){
        if (mapChanged != newState){
            if(newState)
                saveTip.setText(MessageUtils.message("save.tip"));
            else
                saveTip.setText(MessageUtils.message("use.tip"));
            saveButton.setEnabled(newState);
        }
        mapChanged = newState;
    }


    public void updateDataFromMap(){
        textArea.setText("");
        StringBuilder builder = new StringBuilder();
        for (var entry : dataMap.entrySet()) {
            for (String member : entry.getValue()) {
                builder.append(entry.getKey()).append(".").append(member).append("\n");
            }
        }
        textArea.setText(builder.toString());
    }
    public void updateMapFromData(){
        instance.setData(textArea.getText());
        dataMap = instance.calculateMapData();
        updateKeyList();
    }


    private void writeBackToCache(){
        instance.setData(textArea.getText());
        instance.updateCache(dataMap);
        Notifications.Bus.notify(
                new Notification("Print", "", MessageUtils.message("update.info") , NotificationType.INFORMATION),
                project
        );
        setMapChangedState(false);
    }

    public void applyChanges() {
        setMapChangedState(false);
    }

    public void loadData() {
        instance = MapDataStorage.getInstance(project);
        String data = instance.getData();
        if (data == null || data.isEmpty()){
            is_example = true;
            data = MessageUtils.message("textarea-example");
            textArea.setForeground(JBColor.GRAY);
            textArea.setText(data);
            return;
        }
        textArea.setText(data);
        updateMapFromData();
        // no need to writeBack
        // writeBackToCache();
        setMapChangedState(false);
    }

    private void updateKeyList() {
        keyListModel.clear();
        dataMap.keySet().forEach(keyListModel::addElement);
    }

    private void updateValueList() {
        valueListModel.clear();
        String selectedKey = keyList.getSelectedValue();
        if (selectedKey != null) {
            List<String> values = dataMap.get(selectedKey);
            if (values != null) {
                values.forEach(valueListModel::addElement);
            }
        }
    }

    private void addKey() {
        String newKey = JOptionPane.showInputDialog(mainPanel, MessageUtils.message("add-key.info"));
        if (newKey != null && !newKey.trim().isEmpty() && !dataMap.containsKey(newKey)) {
            dataMap.put(newKey, new ArrayList<>());
            updateKeyList();
            keyList.setSelectedValue(newKey, true);
        }
        setMapChangedState(true);
    }

    private void deleteKey() {
        String selectedKey = keyList.getSelectedValue();
        if (selectedKey != null) {
            dataMap.remove(selectedKey);
            updateKeyList();
            valueListModel.clear();
        }
        setMapChangedState(true);
    }

    private void addValue() {
        String selectedKey = keyList.getSelectedValue();
        if (selectedKey != null) {
            String newValue = JOptionPane.showInputDialog(mainPanel, MessageUtils.message("add-value.info"));
            if (newValue != null && !newValue.trim().isEmpty()) {
                dataMap.get(selectedKey).add(newValue);
                updateValueList();
            }
        }
        setMapChangedState(true);
    }

    // 删除选中的Value
    private void deleteValue() {
        String selectedKey = keyList.getSelectedValue();
        String selectedValue = valueList.getSelectedValue();
        if (selectedKey != null && selectedValue != null) {
            dataMap.get(selectedKey).remove(selectedValue);
            updateValueList();
        }
        setMapChangedState(true);
    }

    // 编辑选中的Value
    private void editValue() {
        String selectedKey = keyList.getSelectedValue();
        String selectedValue = valueList.getSelectedValue();
        if (selectedKey != null && selectedValue != null) {
            String newValue = JOptionPane.showInputDialog(mainPanel, MessageUtils.message("edit-value.info"), selectedValue);
            if (newValue != null && !newValue.trim().isEmpty()) {
                List<String> values = dataMap.get(selectedKey);
                int index = values.indexOf(selectedValue);
                if (index != -1) {
                    values.set(index, newValue);
                    updateValueList();
                    valueList.setSelectedValue(newValue, true);
                    setMapChangedState(true);
                }
            }
        }
    }
}
