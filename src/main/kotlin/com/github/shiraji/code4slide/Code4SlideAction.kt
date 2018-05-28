package com.github.shiraji.code4slide

import com.intellij.ide.scratch.*
import com.intellij.lang.Language
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.fileEditor.impl.FileEditorManagerImpl
import com.intellij.psi.PsiWhiteSpace
import com.intellij.psi.util.*

class Code4SlideAction : AnAction() {
    override fun actionPerformed(e: AnActionEvent?) {
        e ?: return
        val project = e.getRequiredData(CommonDataKeys.PROJECT);
        val manager = FileEditorManager.getInstance(project);
        val editor = e.getData(CommonDataKeys.EDITOR) ?: return
        val file = e.getData(CommonDataKeys.PSI_FILE) ?: return

        val selectionStart = editor.selectionModel.selectionStart
        val firstElement = file.findElementAt(selectionStart) ?: return
        val selectionEnd = editor.selectionModel.selectionEnd
        val endElement = file.findElementAt(selectionEnd) ?: return
        val firstParents = firstElement.parents().asIterable().plus(firstElement)
        val endParents = endElement.parents().asIterable().plus(endElement)

        val commonParent = firstParents.firstOrNull { parent ->
            endParents.firstOrNull {endParent ->
                parent == endParent
            } != null
        } ?: return

        val scratchFile = ScratchRootType.getInstance().createScratchFile(project, "selected_text.${file.language.associatedFileType?.defaultExtension}", file.language, editor.selectionModel.selectedText) ?: return

        val pair = (manager as? FileEditorManagerImpl)?.openFileInNewWindow(scratchFile) ?: return
        pair.getSecond()

        val stringBuilder = StringBuilder()

        commonParent.children.forEach { child ->
            val startOffset = child.textOffset
            val endOffset = startOffset + child.textLength

            if (!isChildInRange(editor, startOffset = startOffset, endOffset = endOffset, selectionStart = selectionStart, selectionEnd = selectionEnd)) return@forEach

            if (stringBuilder.isEmpty()) {
                val prevSibling = child.prevSibling
                if (prevSibling is PsiWhiteSpace) {
                    val indent= prevSibling.text.split("\n").last()
                    stringBuilder.append(indent)
                }
            }

            stringBuilder.append(child.text)

            if (child !is PsiWhiteSpace) {
                val virtualFile = ScratchRootType.getInstance().createScratchFile(
                        project,
                        "${file.name}_generated.${file.language.associatedFileType?.defaultExtension}",
                        file.language,
                        stringBuilder.toString()
                ) ?: return@forEach

                manager.openFileImpl2(manager.windows.last(), virtualFile, false)
            }

        }

    }

    private fun isChildInRange(editor: Editor, startOffset: Int, endOffset: Int, selectionStart: Int, selectionEnd: Int): Boolean {
        if (editor.document.getLineNumber(startOffset) == editor.document.getLineNumber(selectionStart)) return true

        if ((startOffset..endOffset).contains(selectionStart)) return true
        if ((startOffset..endOffset).contains(selectionEnd)) return true
        return (selectionStart..selectionEnd).contains(startOffset)
    }

}