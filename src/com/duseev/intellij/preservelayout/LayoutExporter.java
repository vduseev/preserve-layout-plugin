package com.duseev.intellij.preservelayout;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.*;
import com.intellij.openapi.vfs.VirtualFile;

public class LayoutExporter extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent e) {
        // TODO: insert action logic here
        Project project = e.getProject();
        VirtualFile workspace = project.getWorkspaceFile();
    }
}