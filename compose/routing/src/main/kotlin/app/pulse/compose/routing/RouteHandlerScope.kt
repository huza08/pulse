package app.pulse.compose.routing

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable

@Stable
class RouteHandlerScope(
    val child: Route?,
    val args: Array<Any?>,
    val replace: (Route?) -> Unit,
    val pop: () -> Unit,
    val root: RootRouter
) {
    operator fun Route0.invoke() = replace(this)

    operator fun <P0> Route1<P0>.invoke(p0: P0) {
        args[0] = p0
        replace(this@invoke)
    }

    operator fun <P0, P1> Route2<P0, P1>.invoke(p0: P0, p1: P1) {
        args[0] = p0
        args[1] = p1
        replace(this@invoke)
    }

    operator fun <P0, P1, P2> Route3<P0, P1, P2>.invoke(p0: P0, p1: P1, p2: P2) {
        args[0] = p0
        args[1] = p1
        args[2] = p2
        replace(this@invoke)
    }

    operator fun <P0, P1, P2, P3> Route4<P0, P1, P2, P3>.invoke(
        p0: P0,
        p1: P1,
        p2: P2,
        p3: P3
    ) {
        args[0] = p0
        args[1] = p1
        args[2] = p2
        args[3] = p3
        replace(this@invoke)
    }
    @Composable
    inline fun Content(content: @Composable () -> Unit) {
        if (child == null) content()
    }
}
