package de.bitb.buttonbuddy.core

import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.semantics.getOrNull
import androidx.compose.ui.test.*
import androidx.compose.ui.text.TextLayoutResult

fun SemanticsNodeInteraction.assertTextOnChildren(
    text: String
): SemanticsNodeInteractionCollection =
    onChildren().assertAny(hasSemanticsProperty("Children text") { list ->
        list.any { it.layoutInput.text.text == text }
    })

private fun hasSemanticsProperty(
    propertyName: String,
    propertyFunction: (List<TextLayoutResult>) -> Boolean
): SemanticsMatcher = SemanticsMatcher(
    "$propertyName matches the expected property"
) {
    val textLayoutResults = mutableListOf<TextLayoutResult>()
    it.config.getOrNull(SemanticsActions.GetTextLayoutResult)
        ?.action
        ?.invoke(textLayoutResults)
    return@SemanticsMatcher textLayoutResults.isNotEmpty()
            && propertyFunction(textLayoutResults)
}
