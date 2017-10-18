package com.duseev.intellij.preservelayout;

import com.intellij.icons.AllIcons;
import com.intellij.ide.plugins.PluginManagerMain;
import com.intellij.ide.ui.UISettings;
import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.fileChooser.FileChooser;
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.openapi.wm.impl.DesktopLayout;
import com.intellij.openapi.wm.impl.ToolWindowManagerImpl;
import com.intellij.util.ui.UIUtil;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;

import javax.swing.*;
import java.io.File;

public class ImportLayoutAction extends AnAction {

    ImportLayoutAction(String name) {
        super(name);
    }

    @Override
    public void actionPerformed(AnActionEvent e) {
        FileChooser.chooseFile(
                FileChooserDescriptorFactory.createSingleFileDescriptor(),
                e.getProject(),
                null,
                file -> importLayoutFileToProject(file.getCanonicalPath(), e.getProject()));

    }

    @Override
    public void update(AnActionEvent e) {
        e.getPresentation().setEnabled(e.getProject() != null);
        e.getPresentation().setIcon(AllIcons.Actions.Install);
        super.update(e);
    }

    private void importLayoutFileToProject(String path, Project project) {
        if (path == null) return;
        if (project == null) return;

        try {
            Element layout = parseLayoutFile(path);
            applyLayoutToProject(layout, project);

            Notifications.Bus.notify(new Notification(
                    "Preserve Layout",
                    "Successful Import",
                    "Imported from " + path,
                    NotificationType.INFORMATION
            ), project);

        } catch (Exception ex) {
            Notifications.Bus.notify(new Notification(
                    "Preserve Layout",
                    "Import Failed",
                    ex.getMessage(),
                    NotificationType.ERROR
            ), project);
        }
    }

    private Element parseLayoutFile(String path) throws Exception {
        Document doc = new SAXBuilder().build(new File(path));
        return doc.getRootElement();
    }

    private void applyLayoutToProject(Element layout, Project project) {
        ToolWindowManagerImpl mgr = (ToolWindowManagerImpl) ToolWindowManager.getInstance(project);
        DesktopLayout dl = mgr.getLayout();
        dl.readExternal(layout);
    }

}
