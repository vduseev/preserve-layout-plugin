package com.duseev.intellij.preservelayout;

import com.intellij.ide.ui.EditorOptionsTopHitProvider;
import com.intellij.notification.Notification;
import com.intellij.notification.Notifications;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.fileChooser.FileChooser;
import com.intellij.openapi.fileChooser.FileChooserFactory;
import com.intellij.openapi.fileChooser.FileSaverDescriptor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileWrapper;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.openapi.wm.impl.DesktopLayout;
import com.intellij.openapi.wm.impl.ToolWindowManagerImpl;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import java.io.FileWriter;
import java.io.IOException;

public class ExportLayoutAction extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent e) {
        try {
            if (e.getProject() == null) throw new Exception("No project selected");

            ToolWindowManagerImpl mgr = (ToolWindowManagerImpl) ToolWindowManager.getInstance(e.getProject());
            // Element layout = mgr.getState();
            DesktopLayout dl = mgr.getLayout();
            Element layout = dl.writeExternal("layout");

            Document doc = new Document();
            doc.addContent(layout);

            XMLOutputter outputter = new XMLOutputter(Format.getPrettyFormat());
            String exportContent = outputter.outputString(doc);

            FileSaverDescriptor descriptor = new FileSaverDescriptor(
                    "Save Layout",
                    "Choose path for layout export...",
                    "xml");

            VirtualFileWrapper wrapper = FileChooserFactory
                    .getInstance()
                    .createSaveFileDialog(descriptor, e.getProject())
                    .save(e.getProject().getBaseDir(), "layout.xml");

            if (wrapper == null) return;
            VirtualFile file = wrapper.getVirtualFile(true);

            if (file == null) throw new Exception("Couldn't create new file");

            new WriteCommandAction.Simple(e.getProject()) {
                @Override
                protected void run() throws Throwable {
                    VfsUtil.saveText(file, exportContent);

                    Notifications.Bus.notify(new Notification(
                            "Preserve Layout",
                            "Successful Export",
                            "Saved to " + file.getCanonicalPath(),
                            NotificationType.INFORMATION
                    ), e.getProject());
                }
            }.execute();

        } catch (Exception ex) {
            Notifications.Bus.notify(new Notification(
                    "Preserve Layout",
                    "Export Failed",
                    ex.getMessage(),
                    NotificationType.ERROR
            ), e.getProject());
        }
    }

    @Override
    public void update(AnActionEvent e) {
        e.getPresentation().setEnabled(e.getProject() != null);
        super.update(e);
    }

}
