package kvirtualdom

import org.w3c.dom.events.Event

sealed class VirtualNode

data class TagNode(
        val tag: String,
        val id: String? = null,
        val classes: MutableSet<String> = mutableSetOf(),
        val props: MutableMap<String, String> = mutableMapOf(),
        val onEvents: MutableMap<String, (Event) -> Unit> = mutableMapOf(),
        val children: MutableList<VirtualNode> = mutableListOf()
) : VirtualNode()

data class TextNode(val content: String) : VirtualNode()

fun tag(tag: String, id: String? = null, classes: Set<String> = emptySet(), props: Map<String, String> = emptyMap(), applyChindren: (TagNode.() -> Unit)? = null): VirtualNode {
    val node = TagNode(tag, id, classes.toMutableSet(), props.toMutableMap())
    if (applyChindren != null) node.applyChindren()
    return node
}

fun TagNode.tag(tag: String, id: String? = null, classes: Set<String> = emptySet(), props: Map<String, String> = emptyMap(), applyChindren: (TagNode.() -> Unit)? = null) {
    val newNode = TagNode(tag, id, classes.toMutableSet(), props.toMutableMap())
    if (applyChindren != null) newNode.applyChindren()
    this.children.add(newNode)
}

fun TagNode.text(content: String) {
    this.children.add(TextNode(content))
}
