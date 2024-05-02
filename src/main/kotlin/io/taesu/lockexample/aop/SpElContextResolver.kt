package io.taesu.lockexample.aop

import io.taesu.lockexample.aop.lock.SpElContext
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.reflect.MethodSignature
import org.springframework.core.ResolvableType
import org.springframework.expression.spel.standard.SpelExpressionParser
import org.springframework.expression.spel.support.StandardEvaluationContext
import java.lang.reflect.ParameterizedType

/**
 * Created by itaesu on 2024/05/02.
 *
 * SpElContext를 생성하기 위한 Resolver
 *
 * @author Lee Tae Su
 */
object SpElContextResolver {
    private val expressionParser = SpelExpressionParser()

    fun resolve(joinPoint: ProceedingJoinPoint, expressionString: String): SpElContext? {
        val signature = joinPoint.signature as? MethodSignature ?: return null
        val method = signature.method
        val returnType = method.genericReturnType as? ParameterizedType
        val returnRawType = returnType?.rawType

        val context = resolveEvaluationContext(signature, joinPoint)
        val key = resolveKey(context, expressionString)
        val valueClass =
            returnType?.actualTypeArguments?.firstOrNull()?.let(ResolvableType::forType)?.resolve()

        return SpElContext(
            key = key,
            context = context,
            valueClass = valueClass,
            returnRawType = returnRawType,
        )
    }

    fun resolveKey(
        context: StandardEvaluationContext,
        expressionString: String,
    ): String {
        val expression = expressionParser.parseExpression(expressionString)
        return expression.getValue(context, String::class.java) ?: throw IllegalArgumentException("Key를 생성할 수 없습니다.")
    }

    private fun resolveEvaluationContext(
        signature: MethodSignature,
        joinPoint: ProceedingJoinPoint,
    ): StandardEvaluationContext {
        return StandardEvaluationContext().apply {
            val parameterMap = mutableMapOf<String, Any>()
            signature.parameterNames.mapIndexed { index, it ->
                parameterMap[it] = joinPoint.args[index]
            }
            this.setVariables(parameterMap)
        }
    }
}
