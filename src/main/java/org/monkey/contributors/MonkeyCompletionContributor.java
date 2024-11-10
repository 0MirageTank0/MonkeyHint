package org.monkey.contributors;

import com.intellij.codeInsight.completion.*;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.psi.PsiElement;
import com.intellij.util.ProcessingContext;
import org.monkey.data.MapDataStorage;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Objects;

public class MonkeyCompletionContributor extends CompletionContributor {

    public MonkeyCompletionContributor() {
        extend(CompletionType.BASIC,
                PlatformPatterns.psiElement(),
                new CompletionProvider<>() {
                    @Override
                    protected void addCompletions(@NotNull CompletionParameters parameters, @NotNull ProcessingContext context, @NotNull CompletionResultSet result) {
                        PsiElement element = parameters.getPosition();
                        result.addAllElements(GetMembers(element));
                    }
                });
    }

    private @NotNull Iterable<LookupElement> GetMembers(PsiElement PsiElement) {
        var ret = new ArrayList<LookupElement>();
        var parent = PsiElement.getParent();
        if (parent == null) return ret;
        var last = parent.getLastChild().getText();
        var text = parent.getFirstChild().getText();
        if (Objects.equals(last, text)) return ret;
        var dataMap = MapDataStorage.getInstance(PsiElement.getProject()).getCacheData();
        var members = dataMap.get(text);
        if (members == null)return ret;
        for (String[] member : members) {
            ret.add(PrioritizedLookupElement.withPriority(
                    LookupElementBuilder.create(member[0]).withTypeText(member[1]).withItemTextUnderlined(true).appendTailText(member[2],true),
                    1000));
        }
        return ret;
    }
}
