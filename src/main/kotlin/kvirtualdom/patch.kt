package kvirtualdom

import org.w3c.dom.Node

fun patch(node: Node, patches: List<Patch>) {
    patches.forEach { patch ->
        when (patch) {
            is ReplaceNodePatch -> todo()
            is RemoveClassesPatch -> todo()
            is AddClassesPatch -> todo()
            is RemovePropsPatch -> todo()
            is AddPropsPatch -> todo()
            is RemoveNodePatch -> todo()
            is AddChildrenNodesPatch -> todo()
            is AddSiblingNodesPatch -> todo()
            is MoveAfterNodePatch -> todo()
        }

    }
}