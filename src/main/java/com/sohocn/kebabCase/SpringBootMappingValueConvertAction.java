package com.sohocn.kebabCase;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.*;

public class SpringBootMappingValueConvertAction extends AnAction {
    @Override
    public void actionPerformed(AnActionEvent e) {
        Editor editor = e.getData(CommonDataKeys.EDITOR);
        PsiFile psiFile = e.getData(CommonDataKeys.PSI_FILE);
        if (editor == null || psiFile == null) {
            return;
        }

        String selectedText = editor.getSelectionModel().getSelectedText();

        WriteCommandAction.runWriteCommandAction(editor.getProject(), () -> {
            PsiElementFactory factory = JavaPsiFacade.getElementFactory(editor.getProject());

            if (selectedText != null && !selectedText.isEmpty()) {
                // If text is selected, convert only the selected text
                String convertedText = convertToKebabCase(selectedText);
                editor.getDocument().replaceString(editor.getSelectionModel().getSelectionStart(), editor.getSelectionModel().getSelectionEnd(), convertedText);
            } else {
                // If no text is selected, find and convert all mapping values
                psiFile.accept(new JavaRecursiveElementWalkingVisitor() {
                    @Override
                    public void visitAnnotation(PsiAnnotation annotation) {
                        super.visitAnnotation(annotation);
                        String annotationName = annotation.getQualifiedName();
                        if (annotationName != null && (annotationName.endsWith(".GetMapping") ||
                                annotationName.endsWith(".PostMapping") ||
                                annotationName.endsWith(".PutMapping") ||
                                annotationName.endsWith(".DeleteMapping") ||
                                annotationName.endsWith(".PatchMapping") ||
                                annotationName.endsWith(".RequestMapping"))) {

                            PsiNameValuePair[] attributes = annotation.getParameterList().getAttributes();
                            for (PsiNameValuePair attribute : attributes) {
                                String attributeName = attribute.getName();
                                if (attributeName == null || attributeName.equals("value") || attributeName.equals("path")) {
                                    PsiAnnotationMemberValue value = attribute.getValue();
                                    if (value instanceof PsiLiteralExpression) {
                                        PsiLiteralExpression literalExpression = (PsiLiteralExpression) value;
                                        if (literalExpression.getValue() instanceof String) {
                                            String originalValue = (String) literalExpression.getValue();
                                            String convertedValue = convertToKebabCase(originalValue);
                                            if (!originalValue.equals(convertedValue)) {
                                                PsiExpression newExpression = factory.createExpressionFromText("\"" + convertedValue + "\"", literalExpression);
                                                literalExpression.replace(newExpression);
                                            }
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
        // Always enable and show the action
        e.getPresentation().setEnabledAndVisible(true);
    }

    // Reusing the conversion logic from the original class
    private String convertToKebabCase(String text) {
        if (text == null || text.isEmpty()) {
            return "";
        }
        StringBuilder result = new StringBuilder();
        char[] chars = text.toCharArray();

        for (int i = 0; i < chars.length; i++) {
            char currentChar = chars[i];

            if (Character.isUpperCase(currentChar)) {
                if (i > 0 && (Character.isLowerCase(chars[i - 1]) || Character.isDigit(chars[i - 1]))) {
                    result.append('-');
                }
                result.append(Character.toLowerCase(currentChar));
            } else if (Character.isDigit(currentChar)) {
                if (i > 0 && Character.isLetter(chars[i - 1])) {
                    result.append('-');
                }
                result.append(currentChar);
            } else if (currentChar == '_' || Character.isWhitespace(currentChar)) {
                if (result.length() > 0 && result.charAt(result.length() - 1) != '-') {
                    result.append('-');
                }
            } else if (currentChar == '-') {
                if (result.length() > 0 && result.charAt(result.length() - 1) != '-') {
                    result.append('-');
                }
            } else {
                result.append(currentChar);
            }
        }

        String cleanedResult = result.toString().replaceAll("-+", "-");
        if (cleanedResult.startsWith("-")) {
            cleanedResult = cleanedResult.substring(1);
        }
        if (cleanedResult.endsWith("-")) {
            cleanedResult = cleanedResult.substring(0, cleanedResult.length() - 1);
        }

        return cleanedResult;
    }
}