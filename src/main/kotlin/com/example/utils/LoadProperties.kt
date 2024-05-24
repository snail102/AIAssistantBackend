package com.example.utils

import java.io.FileInputStream
import java.util.*

fun loadProperties(): Properties {
    val properties = Properties()
    val propertiesFile = "local.properties"
    FileInputStream(propertiesFile).use { fileInputStream ->
        properties.load(fileInputStream)
    }
    return properties
}