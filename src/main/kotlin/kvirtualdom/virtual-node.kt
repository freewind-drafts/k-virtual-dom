package kvirtualdom

data class VirtualNode(
        val tag: String,
        val attrs: Attributes,
        val children: List<VirtualNode>
)

data class Attributes(val id: String? = null, val classes: Set<String> = emptySet()) : HashMap<String, String>()

fun tag(tagDef: String, children: List<VirtualNode>): VirtualNode {
    return tag(tagDef, children = children)
}

fun tag(tagDef: String, properties: Attributes = Attributes(), children: List<VirtualNode> = emptyList()): VirtualNode {
    val (tagName, attributes) = parseTag(tagDef, properties)
    return VirtualNode(tagName, attributes, children.map { tag(it.tag, it.attrs, it.children) })
}

fun parseTag(tagDef: String, properties: Attributes): Pair<String, Attributes> {
    fun isClass(str: String) = str.startsWith(".")
    fun isId(str: String) = str.startsWith("#")
    fun removeSign(str: String) = str.substring(1)
    fun findClasses(parts: List<String>): Set<String> = parts.filter(::isClass).map(::removeSign).toSet()
    fun findId(parts: List<String>) = parts.firstOrNull(::isId)?.let(::removeSign)
    fun findProvidedTagName(parts: List<String>) = parts.filterNot { isId(it) || isClass(it) }.firstOrNull()
    fun splitToParts(tagName: String) = tagName.split("""\b(?=#|[.])""".toRegex()).filterNot { it.isBlank() }

    val parts = splitToParts(tagDef)
    val tag = findProvidedTagName(parts) ?: "div"
    val id = findId(parts) ?: properties.id
    val classes = findClasses(parts) + properties.classes
    return tag to properties.copy(id = id, classes = classes)
}

