@file:Suppress("unused")

import kvirtualdom.Attributes
import kvirtualdom.parseTag
import kotlin.test.Test
import kotlin.test.assertEquals

class ParseTagTest {

    @Test
    fun tag_is_provided() {
        val (tagName, _) = parseTag("form", Attributes())
        assertEquals("form", tagName)
    }

    @Test
    fun tag_should_be_div_if_not_provided() {
        val (tagName, _) = parseTag(".name", Attributes())
        assertEquals("div", tagName)
    }

    @Test
    fun id_should_be_parsed_from_tag_name() {
        val (_, attrs) = parseTag("#myid", Attributes())
        assertEquals("myid", attrs.id)
    }

    @Test
    fun classes_should_be_parsed_from_tag_name() {
        val (_, attrs) = parseTag(".name1.name2", Attributes())
        assertEquals(setOf("name1", "name2"), attrs.classes)
    }

    @Test
    fun classes_should_be_merged_from_tag_name_and_properties() {
        val (_, attrs) = parseTag(".name1.name2", Attributes(classes = setOf("name3")))
        assertEquals(setOf("name1", "name2", "name3"), attrs.classes)
    }

    @Test
    fun complex_tag_id_classes() {
        val (tagName, attrs) = parseTag("My-tag#my-id.Name1.name-2", Attributes(classes = setOf("name3")))
        assertEquals("My-tag", tagName)
        assertEquals("my-id", attrs.id)
        assertEquals(setOf("Name1", "name-2", "name3"), attrs.classes)
    }

}