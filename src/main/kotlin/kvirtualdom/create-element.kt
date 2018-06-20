package kvirtualdom

import org.w3c.dom.Node
import kotlin.browser.document
import kotlin.dom.createElement

fun createElement(node: VirtualNode): Node = when (node) {
    is TextNode -> document.createTextNode(node.content)
    is TagNode -> document.createElement(node.tag) {
        this.setAttribute("__internalId", node.__internalId?.toString()!!)
        node.id?.let { id -> this.setAttribute("id", id) }
        this.setAttribute("class", node.classes.joinToString(" "))
        node.props.forEach { (name, value) ->
            this.setAttribute(name, value)
        }
        node.onEvents.forEach { (event, handler) ->
            this.addEventListener(event, handler)
        }
        node.children.forEach { child ->
            val childElement = createElement(child)
            this.appendChild(childElement)
        }
    }
}
