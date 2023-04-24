package com.arya.submission3.utils

import com.arya.submission3.data.remote.response.Story

object DataDummy {

    fun generateDummyStoryResponse(): List<Story> {
        val items: MutableList<Story> = arrayListOf()
        for (i in 0..100) {
            val item = Story(
                id = "$i",
                name = "Name $i"
            )
            items.add(item)
        }
        return items
    }
}