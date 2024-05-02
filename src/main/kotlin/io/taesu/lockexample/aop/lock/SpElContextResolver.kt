package io.taesu.lockexample.aop.lock

import org.springframework.expression.spel.support.StandardEvaluationContext
import java.lang.reflect.Type

class SpElContext(
    val key: String,
    val context: StandardEvaluationContext,
    val valueClass: Class<*>?,
    val returnRawType: Type?,
)
