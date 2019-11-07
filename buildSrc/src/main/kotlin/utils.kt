import org.gradle.api.Task
import org.gradle.api.internal.TaskInternal

val TaskInternal.shouldRun
    get() = enabled && onlyIf.isSatisfiedBy(this)
