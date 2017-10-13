package com.duseev.intellij.preservelayout;

import com.intellij.ide.ui.EditorOptionsTopHitProvider;
import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.fileChooser.FileChooser;
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.openapi.wm.impl.ToolWindowManagerImpl;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import java.io.File;
import java.io.IOException;

public class ImportLayoutAction extends AnAction {

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
        Element layout = doc.getRootElement();

        XMLOutputter outputter = new XMLOutputter(Format.getPrettyFormat());
        outputter.output(doc, System.out);

        return layout;
    }

    private void applyLayoutToProject(Element layout, Project project) {
        ToolWindowManagerImpl mgr = (ToolWindowManagerImpl) ToolWindowManager.getInstance(project);
        mgr.loadState(layout);
        System.out.println("State loaded: " + layout.getName());
    }

}
