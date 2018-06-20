@file:Suppress("unused")

package kvirtualdom

import org.w3c.dom.events.Event
import kotlin.test.Test
import kotlin.test.assertEquals

class TagTest {

    @Test
    fun nested_simple_tags() {
        val tags = run {
            tag("div") {
                text("login")
                tag("form") {
                    tag("input")
                    tag("input")
                    tag("button")
                }
            }
        }
        val nodes = run {
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
        val tags = run {
            tag("div", id = "main1", classes = listOf("big", "panel"), props = mapOf("style" to "color: red")) {
                props["p1"] = "v1"
                classes.add("c1")
                onEvents["click"] = onClick

                text("login")
                tag("form", classes = listOf("my-form")) {
                    tag("input", classes = listOf("username"), props = mapOf("type" to "text"))
                    tag("input", classes = listOf("password"), props = mapOf("type" to "password"))
                    tag("button", classes = listOf("submit", "center"))
                }
            }
        }
        val nodes = run {
            TagNode("div", id = "main1", classes = mutableListOf("big", "panel", "c1"), props = mutableMapOf("style" to "color: red", "p1" to "v1"), onEvents = mutableMapOf("click" to onClick), children = mutableListOf(
                    TextNode("login"),
                    TagNode("form", classes = mutableListOf("my-form"), children = mutableListOf(
                            TagNode("input", classes = mutableListOf("username"), props = mutableMapOf("type" to "text")),
                            TagNode("input", classes = mutableListOf("password"), props = mutableMapOf("type" to "password")),
                            TagNode("button", classes = mutableListOf("submit", "center"))
                    ))
            ))
        }
        assertEquals(nodes, tags)
    }

}