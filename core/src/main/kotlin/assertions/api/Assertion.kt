package assertions.api

/**
 * Holds a subject of type [T] that you can then make assertions about.
 */
interface Assertion<T> {
  // TODO: these only make sense to be visible inside an assertion function, not sure how to hide them from other contexts
  /**
   * Used by assertion implementations to evaluate a single condition that may
   * pass or fail.
   *
   * @param description a description for the assertion.
   * @param assertion the assertion implementation that should result in a call
   * to [AtomicAssertionContext.success] or [AtomicAssertionContext.failure].
   */
  fun atomic(description: String, assertion: AtomicAssertionContext.(T) -> Unit)

  /**
   * Used by assertion implementations to evaluate a group of conditions that
   * may individually pass or fail and result in the passing or failure of the
   * overall assertion.
   *
   * @param description a description for the overall assertion.
   * @param assertions the assertion implementation that should perform a group
   * of assertions using [NestedAssertionContext.expect] and finall result in a
   * call to [NestedAssertionContext.success] or
   * [NestedAssertionContext.failure].
   */
  fun nested(description: String, assertions: NestedAssertionContext.(T) -> Unit)
}

/**
 * Allows reporting of success or failure by assertion implementations.
 */
interface AtomicAssertionContext {
  /**
   * Report that the assertion succeeded.
   */
  fun success()

  /**
   * Report that the assertion failed.
   */
  fun failure()
}

/**
 * Allows grouping of assertions under a single main assertion and reporting of
 * success or failure.
 * This class is used for more complex assertions such as on all elements of a
 * collection or multiple fields of an object.
 */
interface NestedAssertionContext : AtomicAssertionContext {
  /**
   * Start a chain of assertions under the current group.
   *
   * @param subject the subject of the chain of assertions, usually a property
   * or element of the subject of the surrounding assertion.
   * @return an assertion for [subject].
   */
  fun <T> expect(subject: T): Assertion<T>

  /**
   * Evaluate a block of assertions under the current group.
   *
   * @param subject the subject of the block of assertions, usually a property
   * or element of the subject of the surrounding assertion.
   * @param block a closure that can perform multiple assertions that will all
   * be evaluated regardless of whether preceding ones pass or fail.
   * @return an assertion for [subject].
   */
  fun <T> expect(subject: T, block: Assertion<T>.() -> Unit): Assertion<T>

  /**
   * Returns `true` if any assertions in this group failed, `false` otherwise.
   */
  val anyFailed: Boolean
  /**
   * Returns `true` if _all_ assertions in this group failed, `false` otherwise.
   */
  val allFailed: Boolean
  /**
   * Returns `true` if any assertions in this group passed, `false` otherwise.
   */
  val anySucceeded: Boolean
  /**
   * Returns `true` if _all_ assertions in this group passed, `false` otherwise.
   */
  val allSucceeded: Boolean
}