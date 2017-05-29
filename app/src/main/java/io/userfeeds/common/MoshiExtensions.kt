package io.userfeeds.common

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import java.lang.reflect.Type

inline fun <reified E> Moshi.listAdapter(elementType: Type = E::class.java): JsonAdapter<List<E>> {
    return adapter(listType<E>(elementType))
}

inline fun <reified E> listType(elementType: Type = E::class.java): Type {
    return Types.newParameterizedType(List::class.java, elementType)
}
