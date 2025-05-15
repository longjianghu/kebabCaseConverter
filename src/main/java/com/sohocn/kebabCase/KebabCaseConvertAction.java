package com.sohocn.kebabCase;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.command.WriteCommandAction;

public class KebabCaseConvertAction extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent e) {
        // TODO: Implement text conversion logic here
        Editor editor = e.getData(CommonDataKeys.EDITOR);
        if (editor == null) {
            return;
        }

        // Get selected text
        String selectedText = editor.getSelectionModel().getSelectedText();

        if (selectedText != null && !selectedText.isEmpty()) {
            // Perform conversion
            String convertedText = convertToKebabCase(selectedText);

            // Replace selected text with converted text
            WriteCommandAction.runWriteCommandAction(editor.getProject(), () -> {
                editor.getDocument().replaceString(
                        editor.getSelectionModel().getSelectionStart(),
                        editor.getSelectionModel().getSelectionEnd(),
                        convertedText
                );
            });
        }
    }

    private String convertToKebabCase(String text) {
        if (text == null || text.isEmpty()) {
            return "";
        }
        StringBuilder result = new StringBuilder();
        char[] chars = text.toCharArray();

        for (int i = 0; i < chars.length; i++) {
            char currentChar = chars[i];

            if (Character.isUpperCase(currentChar)) {
                // Insert hyphen before uppercase if not the first character and previous was lowercase or digit
                if (i > 0 && (Character.isLowerCase(chars[i - 1]) || Character.isDigit(chars[i - 1]))) {
                    result.append('-');
                }
                result.append(Character.toLowerCase(currentChar));
            } else if (Character.isDigit(currentChar)) {
                 // Insert hyphen before digit if not the first character and previous was a letter
                if (i > 0 && Character.isLetter(chars[i - 1])) {
                    result.append('-');
                }
                result.append(currentChar);
            } else if (currentChar == '_' || Character.isWhitespace(currentChar)) {
                // Replace underscore or whitespace with hyphen
                if (result.length() > 0 && result.charAt(result.length() - 1) != '-') {
                     result.append('-');
                }
            } else if (currentChar == '-') {
                 // Keep existing hyphens, but avoid multiple consecutive hyphens
                 if (result.length() > 0 && result.charAt(result.length() - 1) != '-') {
                     result.append('-');
                 }
            } else {
                result.append(currentChar);
            }
        }

        // Clean up multiple hyphens and leading/trailing hyphens
        String cleanedResult = result.toString().replaceAll("-+", "-");
        if (cleanedResult.startsWith("-")) {
            cleanedResult = cleanedResult.substring(1);
        }
        if (cleanedResult.endsWith("-")) {
            cleanedResult = cleanedResult.substring(0, cleanedResult.length() - 1);
        }

        return cleanedResult;
    }

    @Override
    public void update(AnActionEvent e) {
        // Enable the action only when text is selected in an editor
        Editor editor = e.getData(CommonDataKeys.EDITOR);
        boolean textSelected = editor != null && editor.getSelectionModel().hasSelection();
        e.getPresentation().setEnabledAndVisible(textSelected);
    }
}