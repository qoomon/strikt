package strikt.assertions

import strikt.api.Assertion

/**
 * Asserts that the result of an action did throw an exception and maps to
 * an assertion on the exception. The assertion fails if the subject's
 * [Result.isFailure] returns `false`.
 *
 * @see runCatching
 *
 * @author [Bengt Brodersen](https://github.com/qoomon)
 */
fun <R> Assertion.Builder<Result<R>>.isFailure(): Assertion.Builder<Throwable> =
  assert("is failure") {
    when {
      it.isFailure -> pass()
      else -> fail(
        description = "returned %s",
        actual = it.getOrThrow()
      )
    }
  }
    .get("exception") {
      exceptionOrNull()!!
    }

/**
 * Asserts that the result of an action did not throw any exception and maps to
 * an assertion on the result value. The assertion fails if the subject's
 * [Result.isSuccess] returns `false`.
 *
 * @see runCatching
 *
 * @author [Bengt Brodersen](https://github.com/qoomon)
 */
fun <R> Assertion.Builder<Result<R>>.isSuccess(): Assertion.Builder<R> =
  assert("is success") {
    when {
      it.isSuccess -> pass()
      else -> fail(
        description = "threw %s",
        actual = it.exceptionOrNull(),
        cause = it.exceptionOrNull()
      )
    }
  }
    .get("value") {
      // WORKAROUND - Handle inline class bug. (This will also work when this bug is fixed)
      val value = getOrThrow()
      if (value is Result<*>)
        @Suppress("UNCHECKED_CAST")
        return@get value.getOrThrow() as R
      // WORKAROUND - END

      getOrThrow()
    }

/**
 * Asserts that the subject is a successful result and maps this assertion to
 * an assertion over the result value.
 *
 * @see [Result.isSuccess].
 */
@Deprecated(
  message = "Replaced with isSuccess()",
  replaceWith = ReplaceWith("isSuccess()")
)
fun <T : Any?> Assertion.Builder<Result<T>>.succeeded(): Assertion.Builder<T> =
  isSuccess()

/**
 * Asserts that the subject is a failed result and maps this assertion to an
 * assertion over the exception that was thrown
 *
 * @see [Result.isFailure].
 */
@Deprecated(
  message = "Replaced with isFailure()",
  replaceWith = ReplaceWith("isFailure()")
)
fun <T : Any?> Assertion.Builder<Result<T>>.failed(): Assertion.Builder<Throwable> =
  isFailure()

/**
 * Asserts that the subject is a isFailure result that threw an exception
 * assignable to [E] and maps this assertion to an assertion over that
 * exception.
 *
 * @see [Result.isFailure].
 */
inline fun <reified E : Throwable> Assertion.Builder<Result<*>>.failedWith() =
  isFailure().isA<E>()

/**
 * Deprecated form of [isSuccess]`()`.
 *
 * @see isSuccess()
 */
@Deprecated(
  "Use isSuccess instead",
  replaceWith = ReplaceWith("isSuccess()")
)
@Suppress("UNCHECKED_CAST")
fun <R : Any> Assertion.Builder<Result<R>>.doesNotThrow(): Assertion.Builder<R> =
  assert("did not throw an exception") {
    when {
      it.isSuccess -> pass()
      else -> fail(
        description = "threw %s",
        actual = it.exceptionOrNull(),
        cause = it.exceptionOrNull()
      )
    }
  }
    .get { requireNotNull(getOrNull()) }
