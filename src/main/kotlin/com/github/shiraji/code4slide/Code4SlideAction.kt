package com.github.shiraji.code4slide

import com.intellij.ide.scratch.*
import com.intellij.lang.Language
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.editor.impl.EditorImpl
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.fileEditor.impl.FileEditorManagerImpl
import com.intellij.openapi.vfs.VirtualFile
import java.util.*

class Code4SlideAction : AnAction() {
    override fun actionPerformed(e: AnActionEvent?) {
        e ?: return
        val project = e.getRequiredData(CommonDataKeys.PROJECT);
        val manager = FileEditorManager.getInstance(project);

        val scratchFile = ScratchRootType.getInstance().createScratchFile(project, "scratchfile.kt", Language.findLanguageByID("kotlin"),
                """
                |if (flag) {
                |    val foo = 1
                |}""".trimMargin()) ?: return

        val openFileInNewWindow = (manager as? FileEditorManagerImpl)?.openFileInNewWindow(scratchFile) ?: return

        openFileInNewWindow.first[0].component.getComponent(1)
    }
}