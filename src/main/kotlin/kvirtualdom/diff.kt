package kvirtualdom

sealed class Patch
data class ReplaceNodePatch(val baseInternalId: Int, val newNode: VirtualNode) : Patch()
data class RemoveClassesPatch(val baseInternalId: Int, val classes: Set<String>) : Patch()
data class AddClassesPatch(val baseInternalId: Int, val classes: Set<String>) : Patch()
data class RemovePropsPatch(val baseInternalId: Int, val propNames: Set<String>) : Patch()
data class AddPropsPatch(val baseInternalId: Int, val props: Map<String, String>) : Patch()
data class RemoveNodePatch(val baseInternalId: Int) : Patch()
data class AddChildrenNodesPatch(val baseInternalId: Int, val children: List<VirtualNode>) : Patch()
data class AddSiblingNodesPatch(val baseInternalId: Int, val node: List<VirtualNode>) : Patch()
data class MoveAfterNodePatch(val baseInternalId: Int, val targetInternalId: Int) : Patch()

fun diff(a: VirtualNode, b: VirtualNode): List<Patch> {
    generateInternalIds(a)
    return diffNode(a, b)
}

@Suppress("unused")
fun doNothing() {
}

@Suppress("unused")
fun todo() = Unit

private fun diffNode(a: VirtualNode, b: VirtualNode): List<Patch> = if (maybeSameNode(a, b)) {
    when {
        a is TagNode && b is TagNode -> diffTagNode(a, b)
        else -> emptyList()
    }
} else {
    val patch = ReplaceNodePatch(a.__internalId!!, b)
    listOf(patch)
}

private fun diffTagNode(a: TagNode, b: TagNode): List<Patch> {
    val patches = mutableListOf<Patch>()
    val propPatches = diffProperties(a, b)
    if (propPatches.isNotEmpty()) {
        patches.addAll(propPatches)
    }
    val childrenPatches = diffChildren(a, b)
    if (childrenPatches.isNotEmpty()) {
        patches.addAll(childrenPatches)
    }
    return patches.toList()
}

fun maybeSameNode(a: VirtualNode, b: VirtualNode): Boolean = when {
    a is TagNode && b is TagNode -> a.tag == b.tag && a.id == b.id && a.key == b.key
    a is TextNode && b is TextNode -> a.content == b.content
    else -> false
}


fun diffChildren(a: TagNode, b: TagNode): List<Patch> {
    fun findSameChildren(x: TagNode, y: TagNode): Map<VirtualNode, VirtualNode> {
        if (x.children.isEmpty() || y.children.isEmpty()) return emptyMap()
        val result = mutableMapOf<VirtualNode, VirtualNode>()
        x.children.forEach { xChild ->
            y.children.find { yChild -> maybeSameNode(xChild, yChild) }?.let { yChild ->
                result[xChild] = yChild
            }
        }
        return result
    }

    val baSameChildren = findSameChildren(b, a)
    if (baSameChildren.isEmpty()) {
        val remove = a.children.map { child -> RemoveNodePatch(child.__internalId!!) }
        val add = AddChildrenNodesPatch(a.__internalId!!, b.children)
        return remove + add
    }

    val remove = a.children.filterNot(baSameChildren.values::contains).map { child -> RemoveNodePatch(child.__internalId!!) }
    val moved = movedPatches(baSameChildren, b.children)
    val add = run {
        val children = b.children.toMutableList()
        fun isExistingNode(node: VirtualNode) = baSameChildren.keys.contains(node)
        val patches = mutableListOf<Patch>()
        while (children.isNotEmpty()) {
            val existingNode = takeAway(children, ::isExistingNode).lastOrNull()
            val newNodes = takeAway(children, { !isExistingNode(it) })
            if (existingNode == null) {
                val patch = AddChildrenNodesPatch(a.__internalId!!, newNodes)
                patches.add(patch)
            } else if (newNodes.isNotEmpty()) {
                val patch = AddSiblingNodesPatch(existingNode.__internalId!!, newNodes)
                patches.add(patch)
            }
        }
        patches.toList()
    }

    val patchesOfSameChildren = baSameChildren.map { (a, b) -> diffNode(a, b) }.flatten()

    return remove + moved + add + patchesOfSameChildren
}

fun takeAway(nodes: MutableList<VirtualNode>, predicate: (VirtualNode) -> Boolean): List<VirtualNode> {
    val result = nodes.takeWhile(predicate)
    repeat(result.size, { nodes.removeAt(0) })
    return result
}

fun movedPatches(baSameChildren: Map<VirtualNode, VirtualNode>, orderBase: List<VirtualNode>): List<Patch> {
    if (baSameChildren.size == 1) return emptyList()
    val sortB = sortNodes(baSameChildren.keys, orderBase)
    return sortB.zipWithNext().map { (x, y) -> baSameChildren[x]!! to baSameChildren[y]!! }.map { (x, y) ->
        MoveAfterNodePatch(y.__internalId!!, x.__internalId!!)
    }
}

fun sortNodes(nodes: Collection<VirtualNode>, base: List<VirtualNode>): List<VirtualNode> {
    return nodes.sortedWith(object : Comparator<VirtualNode> {
        override fun compare(a: VirtualNode, b: VirtualNode): Int = base.indexOf(a) - base.indexOf(b)
    })
}


fun diffProperties(a: TagNode, b: TagNode): List<Patch> {
    return diffClasses(a, b) + diffProps(a, b)
}

fun diffProps(a: TagNode, b: TagNode): List<Patch> {
    val result = mutableListOf<Patch>()
    val commonKeys = a.props.keys.intersect(b.props.keys).filter { key -> a.props[key] == b.props[key] }
    val toRemove = a.props.keys.filterNot(commonKeys::contains)
    if (toRemove.isNotEmpty()) {
        result.add(RemovePropsPatch(a.__internalId!!, toRemove.toSet()))
    }
    val toAdd = b.props.filterNot { commonKeys.contains(it.key) }
    if (toAdd.isNotEmpty()) {
        result.add(AddPropsPatch(a.__internalId!!, toAdd))
    }
    return result.toList()
}

private fun diffClasses(a: TagNode, b: TagNode): List<Patch> {
    val patches = mutableListOf<Patch>()
    val common = a.classes.intersect(b.classes)

    val toRemove = a.classes.filterNot { common.contains(it) }
    if (toRemove.isNotEmpty()) {
        patches.add(RemoveClassesPatch(a.__internalId!!, toRemove.toSet()))
    }

    val toAdd = b.classes.filterNot { common.contains(it) }
    if (toAdd.isNotEmpty()) {
        patches.add(AddClassesPatch(a.__internalId!!, toAdd.toSet()))
    }
    return patches.toList()
}

fun generateInternalIds(a: VirtualNode) {
    var id = 0
    fun genId(node: VirtualNode) {
        node.__internalId = id
        id += 1
        when (node) {
            is TagNode -> node.children.forEach(::genId)
        }
    }
    genId(a)
}
