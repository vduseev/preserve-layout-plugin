package com.duseev.intellij.preservelayout;

import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.components.ApplicationComponent;
import org.jetbrains.annotations.NotNull;

public class PreserveLayoutPuginRegistration implements ApplicationComponent {
    @NotNull public String getComponentName() {
        return "Preserve Layout Plugin";
    }

    public void initComponent() {
        ActionManager am = ActionManager.getInstance();

        ExportLayoutAction exportAction = new ExportLayoutAction("_Export Project Layout...");
        ImportLayoutAction importLayoutAction = new ImportLayoutAction("_Import Project Layout...");

        DefaultActionGroup windowMenu = (DefaultActionGroup) am.getAction("WindowMenu");
        windowMenu.addSeparator();
        windowMenu.add(exportAction);
        windowMenu.add(importLayoutAction);
    }

    public void disposeComponent() {

    }
}
