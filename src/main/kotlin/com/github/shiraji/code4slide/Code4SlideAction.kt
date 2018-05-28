package com.github.shiraji.code4slide

import com.intellij.ide.scratch.*
import com.intellij.lang.Language
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.editor.impl.EditorImpl
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.fileEditor.impl.FileEditorManagerImpl
import com.intellij.openapi.fileEditor.impl.text.PsiAwareTextEditorImpl
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.*
import com.intellij.psi.util.*
import org.intellij.plugins.relaxNG.compact.psi.util.PsiFunction
import java.util.*

class Code4SlideAction : AnAction() {
    override fun actionPerformed(e: AnActionEvent?) {
        e ?: return
        val project = e.getRequiredData(CommonDataKeys.PROJECT);
        val manager = FileEditorManager.getInstance(project);
        val editor = e.getData(CommonDataKeys.EDITOR) ?: return
        val file = e.getData(CommonDataKeys.PSI_FILE) ?: return
        val offset = editor.caretModel.offset
        val element = file.findElementAt(offset) ?: return

        // diff by language
        val parent = PsiTreeUtil.getParentOfType(element, PsiIfStatement::class.java, PsiMethod::class.java, PsiClass::class.java, PsiSwitchStatement::class.java) ?: return

        val scratchFile = ScratchRootType.getInstance().createScratchFile(project, "scratchfile.${file.language.associatedFileType?.defaultExtension}", file.language, parent.text) ?: return

        (manager as? FileEditorManagerImpl)?.openFileInNewWindow(scratchFile) ?: return

        val scratchFile2 = ScratchRootType.getInstance().createScratchFile(project, "scratchfile.kt", Language.findLanguageByID("kotlin"), parent.text.replace("\n", "")) ?: return

        manager.openFileImpl2(manager.windows.last(), scratchFile2, false)
    }
}