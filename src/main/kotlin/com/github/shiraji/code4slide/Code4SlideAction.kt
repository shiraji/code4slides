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

        val parent = PsiTreeUtil.getParentOfType(element, PsiIfStatement::class.java, PsiMethod::class.java, PsiClass::class.java) ?: return

//        val parent = element.parentOfType(PsiIfStatement::class, PsiMethod::class, PsiClass::class) ?: return

//        val selectedText = editor.selectionModel.selectedText ?: return
        val scratchFile = ScratchRootType.getInstance().createScratchFile(project, "scratchfile.${file.language.associatedFileType?.defaultExtension}", file.language, parent.text) ?: return

        val openFileInNewWindow = (manager as? FileEditorManagerImpl)?.openFileInNewWindow(scratchFile) ?: return


        openFileInNewWindow.first[0]
//        container.add(EditorTabbedContainer.createDockableEditor(myProject, null, file, new Presentation(file.getName()), editorWindow), null);


        val scratchFile2 = ScratchRootType.getInstance().createScratchFile(project, "scratchfile.kt", Language.findLanguageByID("kotlin"),
                parent.text.replace("\n", "")) ?: return
//
//        manager.openFile(scratchFile2, false)
        manager.openFileImpl2(manager.windows[1],scratchFile2, false)
//                val fileEditor = openFileInNewWindow.second[0].createEditor(project, scratchFile2)
//        openFileInNewWindow.first.set(1, fileEditor)
    }
}