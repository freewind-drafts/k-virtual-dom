@file:Suppress("unused")

package kvirtualdom

import kotlin.test.Test
import kotlin.test.assertEquals

class TagTest {

    @Test
    fun nested_simple_tags() {
        val tags = let {
            tag("div",
                text("login"),
                tag("form",
                    tag("input"),
                    tag("input"),
                    tag("button")
                )
            )
        }
        val nodes = let {
            TagNode("div", children = listOf(
                TextNode("login"),
                TagNode("form", children = listOf(
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
        val tags = let {
            tag("div#main1.big", attrs("main2", setOf("panel"), mapOf("style" to "color: red")),
                text("login"),
                tag("form.my-form",
                    tag("input.username", attrs("type" to "text")),
                    tag("input.password", attrs("type" to "password")),
                    tag("button.submit.center")
                )
            )
        }
        val nodes = let {
            TagNode("div", Attrs("main1", setOf("big", "panel"), mapOf("style" to "color: red")), children = listOf(
                TextNode("login"),
                TagNode("form", Attrs(classes = setOf("my-form")), listOf(
                    TagNode("input", Attrs(classes = setOf("username"), others = mapOf("type" to "text"))),
                    TagNode("input", Attrs(classes = setOf("password"), others = mapOf("type" to "password"))),
                    TagNode("button", Attrs(classes = setOf("submit", "center")))
                ))
            ))
        }
        assertEquals(nodes, tags)
    }

}