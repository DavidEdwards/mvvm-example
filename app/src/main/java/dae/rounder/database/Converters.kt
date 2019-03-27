@file:Suppress("unused")

package dae.rounder.database

import androidx.room.TypeConverter
import dae.rounder.database.entity.RoundLog

import org.threeten.bp.Instant

import java.util.regex.Pattern

class Converters {
    @TypeConverter
    fun fromTimestamp(value: Long?): Instant? {
        return if (value == null) null else Instant.ofEpochMilli(value)
    }

    @TypeConverter
    fun datetimeToTimestamp(date: Instant?): Long? {
        return date?.toEpochMilli()
    }

    @TypeConverter
    fun fromString(value: String?): Pattern? {
        return if (value == null) null else Pattern.compile(value)
    }

    @TypeConverter
    fun patternToString(pattern: Pattern?): String? {
        return pattern?.pattern()
    }

    @TypeConverter
    fun stringToAttribute(value: String?): RoundLog.Attribute? {
        return if (value == null) null else RoundLog.Attribute.valueOf(value)
    }

    @TypeConverter
    fun attributeToString(attribute: RoundLog.Attribute?): String? {
        return attribute?.toString()
    }
}