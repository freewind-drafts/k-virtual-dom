@file:Suppress("unused")

package kvirtualdom

import org.w3c.dom.events.Event
import kotlin.test.Test
import kotlin.test.assertEquals

class TagTest {

    @Test
    fun nested_simple_tags() {
        val tags = let {
            tag("div") {
                text("login")
                tag("form") {
                    tag("input")
                    tag("input")
                    tag("button")
                }
            }
        }
        val nodes = let {
            TagNode("div", children = mutableListOf(
                    TextNode("login"),
                    TagNode("form", children = mutableListOf(
                            TagNode("input"),
                            TagNode("input"),
                            TagNode("button")
                    ))
            ))
        }
        assertEquals(nodes, tags)
    }

    @Test
    fun nested_complex_tags() {
        val onClick = { event: Event -> println("clicked event: $event") }
        val tags = let {
            tag("div", id = "main1", classes = setOf("big", "panel"), props = mapOf("style" to "color: red")) {
                props["p1"] = "v1"
                classes.add("c1")
                onEvents["click"] = onClick

                text("login")
                tag("form", classes = setOf("my-form")) {
                    tag("input", classes = setOf("username"), props = mapOf("type" to "text"))
                    tag("input", classes = setOf("password"), props = mapOf("type" to "password"))
                    tag("button", classes = setOf("submit", "center"))
                }
            }
        }
        val nodes = let {
            TagNode("div", id = "main1", classes = mutableSetOf("big", "panel", "c1"), props = mutableMapOf("style" to "color: red", "p1" to "v1"), onEvents = mutableMapOf("click" to onClick), children = mutableListOf(
                    TextNode("login"),
                    TagNode("form", classes = mutableSetOf("my-form"), children = mutableListOf(
                            TagNode("input", classes = mutableSetOf("username"), props = mutableMapOf("type" to "text")),
                            TagNode("input", classes = mutableSetOf("password"), props = mutableMapOf("type" to "password")),
                            TagNode("button", classes = mutableSetOf("submit", "center"))
                    ))
            ))
        }
        assertEquals(nodes, tags)
    }

}