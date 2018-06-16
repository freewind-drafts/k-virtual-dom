@file:Suppress("unused")

import kvirtualdom.parseTag
import kotlin.test.*

class ParseTagTest {

    @Test
    fun tag_is_provided() {
        val (tagName, _) = parseTag("form", emptyMap())
        assertEquals("form", tagName)
    }

    @Test
    fun tag_should_be_div_if_not_provided() {
        val (tagName, _) = parseTag(".name", emptyMap())
        assertEquals("div", tagName)
    }

    @Test
    fun id_should_be_parsed_from_tag_name() {
        val (_, properties) = parseTag("#myid", emptyMap())
        assertEquals("myid", properties["id"])
    }

    @Test
    fun classes_should_be_parsed_from_tag_name() {
        val (_, properties) = parseTag(".name1.name2", emptyMap())
        assertEquals("name1 name2", properties["class"])
    }

    @Test
    fun classes_should_be_merged_from_tag_name_and_properties() {
        val (_, properties) = parseTag(".name1.name2", mapOf("class" to "name3"))
        assertEquals("name1 name2 name3", properties["class"])
    }

    @Test
    fun complex_tag_id_classes() {
        val (tagName, properties) = parseTag("My-tag#my-id.Name1.name-2", mapOf("class" to "name3"))
        assertEquals("My-tag", tagName)
        assertEquals("my-id", properties["id"])
        assertEquals("Name1 name-2 name3", properties["class"])
    }

}