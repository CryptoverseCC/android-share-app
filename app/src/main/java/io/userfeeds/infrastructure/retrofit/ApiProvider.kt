package io.userfeeds.infrastructure.retrofit

import io.userfeeds.infrastructure.common.Provider
import kotlin.reflect.KClass

abstract class ApiProvider<T : Any>(clazz: KClass<T>) : Provider<T>({ RetrofitProvider.get().create(clazz.java) })
