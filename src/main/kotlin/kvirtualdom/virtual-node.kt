package kvirtualdom

sealed class VirtualNode

data class TagNode(
        val tag: String,
        val attrs: Attrs = attrs(),
        val children: List<VirtualNode> = emptyList()
) : VirtualNode()

data class TextNode(val content: String) : VirtualNode()

data class Attrs(val id: String? = null, val classes: Set<String> = emptySet(), val others: Map<String, String> = emptyMap())

fun attrs(vararg others: Pair<String, String>): Attrs {
    return attrs(others = others.toMap())
}

fun attrs(classes: Set<String> = emptySet()): Attrs {
    return attrs(null, classes)
}

fun attrs(id: String? = null, classes: Set<String> = emptySet(), others: Map<String, String> = emptyMap()): Attrs {
    return Attrs(id, classes, others)
}

fun tag(tagDef: String, vararg children: VirtualNode): VirtualNode {
    return tag(tagDef, attrs = attrs(), children = *children)
}

fun text(content: String): TextNode = TextNode(content)

fun tag(tagDef: String, attrs: Attrs, vararg children: VirtualNode): VirtualNode {
    val (tagName, attributes) = parseTag(tagDef, attrs)
    return TagNode(tagName, attributes, parseChildren(children.toList()))
}

private fun parseChildren(children: List<VirtualNode>): List<VirtualNode> {
    return children.map { child ->
        when (child) {
            is TextNode -> child
            is TagNode -> tag(child.tag, child.attrs, *child.children.toTypedArray())
        }
    }
}

fun parseTag(tagDef: String, attrs: Attrs): Pair<String, Attrs> {
    fun isClass(str: String) = str.startsWith(".")
    fun isId(str: String) = str.startsWith("#")
    fun removeSign(str: String) = str.substring(1)
    fun findClasses(parts: List<String>): Set<String> = parts.filter(::isClass).map(::removeSign).toSet()
    fun findId(parts: List<String>) = parts.firstOrNull(::isId)?.let(::removeSign)
    fun findProvidedTagName(parts: List<String>) = parts.filterNot { isId(it) || isClass(it) }.firstOrNull()
    fun splitToParts(tagName: String) = tagName.split("""\b(?=#|[.])""".toRegex()).filterNot { it.isBlank() }

    val parts = splitToParts(tagDef)
    val tag = findProvidedTagName(parts) ?: "div"
    val id = findId(parts) ?: attrs.id
    val classes = findClasses(parts) + attrs.classes
    return tag to attrs.copy(id = id, classes = classes)
}

