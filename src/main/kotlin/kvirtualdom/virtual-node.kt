package kvirtualdom

data class VirtualNode(
        val tag: String,
        val props: Map<String, String>,
        val childNodes: List<VirtualNode>,
        val key: String,
        val namespace: String
)


fun h(tagName: String, children: List<VirtualNode>) {
    h(tagName, children = children)
}

fun h(tagName: String, properties: Map<String, String>, children: List<VirtualNode> = emptyList()) {
    val tag = parseTag(tagName, properties)

}

fun parseTag(tagName: String, properties: Map<String, String>): Pair<String, Map<String, String>> {
    fun isClass(str: String) = str.startsWith(".")
    fun isId(str: String) = str.startsWith("#")
    fun removeSign(str: String) = str.substring(1)
    val parts = tagName.split("""\b(?=#|[.])""".toRegex()).filterNot { it.isBlank() }
    println("parts: $parts")

    val tag = parts.firstOrNull()?.let { tag ->
        if (isId(tag) || isClass(tag)) "div" else tag
    } ?: "div"

    val props = properties.toMutableMap()
    (parts.firstOrNull(::isId)?.let(::removeSign) ?: properties["id"])?.let { id ->
        props["id"] = id
    }
    (parts.filter(::isClass).map(::removeSign) + (properties["class"]?.split("""\s+""".toRegex())?.toList()
            ?: emptyList()))
            .joinToString(" ").let { classes ->
                props["class"] = classes
            }
    return tag to props.toMap()
}


