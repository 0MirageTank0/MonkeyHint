package org.monkey.data;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import org.jetbrains.annotations.NotNull;

public class MapDataToolWindowFactory implements ToolWindowFactory, DumbAware {

    MonkeyHintStoragePanel monkeyHintStoragePanel;
    @Override
    public boolean shouldBeAvailable(@NotNull Project project) {
        if(monkeyHintStoragePanel != null)
            monkeyHintStoragePanel.setProject(project);
        return true;
    }

    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        monkeyHintStoragePanel = new MonkeyHintStoragePanel(project);
        ContentFactory contentFactory = ContentFactory.getInstance();
        Content content = contentFactory.createContent(monkeyHintStoragePanel.getPanel(), "", false);
        toolWindow.getContentManager().addContent(content);
    }

}
