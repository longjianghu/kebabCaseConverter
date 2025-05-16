package com.sohocn.kebabCase;

import java.util.stream.Stream;

import org.jetbrains.annotations.NotNull;

import com.intellij.openapi.actionSystem.ActionUpdateThread;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;

public class KebabCaseConvertAction extends AnAction {
    @Override
    public void actionPerformed(AnActionEvent e) {
        Editor editor = e.getData(CommonDataKeys.EDITOR);
        PsiFile psiFile = e.getData(CommonDataKeys.PSI_FILE);

        if (editor == null || psiFile == null) {
            return;
        }

        Project project = editor.getProject();

        if (project == null) {
            return;
        }

        String selectedText = editor.getSelectionModel().getSelectedText();

        WriteCommandAction.runWriteCommandAction(project, () -> {
            PsiElementFactory factory = JavaPsiFacade.getElementFactory(project);

            if (selectedText != null && !selectedText.isEmpty()) {
                String convertedText = BaseUtil.convertToKebabCase(selectedText);
                editor
                    .getDocument()
                    .replaceString(editor.getSelectionModel().getSelectionStart(),
                        editor.getSelectionModel().getSelectionEnd(), convertedText);
            } else {
                psiFile.accept(new JavaRecursiveElementWalkingVisitor() {
                    @Override
                    public void visitAnnotation(@NotNull PsiAnnotation annotation) {
                        super.visitAnnotation(annotation);
                        String annotationName = annotation.getQualifiedName();

                        if (annotationName == null) {
                            return;
                        }

                        boolean noneMatch = Stream
                            .of(".GetMapping", ".PostMapping", ".PutMapping", ".DeleteMapping", ".PatchMapping",
                                ".RequestMapping")
                            .noneMatch(annotationName::endsWith);

                        if (noneMatch) {
                            return;
                        }

                        PsiNameValuePair[] attributes = annotation.getParameterList().getAttributes();

                        for (PsiNameValuePair attribute : attributes) {
                            String attributeName = attribute.getName();

                            if (attributeName == null || attributeName.equals("value")
                                || attributeName.equals("path")) {
                                PsiAnnotationMemberValue value = attribute.getValue();

                                if (value instanceof PsiLiteralExpression literalExpression) {
                                    if (literalExpression.getValue() instanceof String originalValue) {
                                        String convertedValue = BaseUtil.convertToKebabCase(originalValue);
                                        if (!originalValue.equals(convertedValue)) {
                                            PsiExpression newExpression = factory
                                                .createExpressionFromText("\"" + convertedValue + "\"",
                                                    literalExpression);
                                            literalExpression.replace(newExpression);
                                        }
                                    }
                                }
                            }
                        }
                    }
                });
            }
        });
    }

    @Override
    public void update(AnActionEvent e) {
        e.getPresentation().setEnabledAndVisible(true);
    }

    @Override
    public @NotNull ActionUpdateThread getActionUpdateThread() {
        return ActionUpdateThread.BGT;
    }
}