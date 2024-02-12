package com.ideacarry.rocketstartup

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.coroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        test()
    }

    /**
     * a        b         c        d       i
     * |        |         |        |
     * e        f         +-- h ---+
     * |        |             |
     * +--- g --|-------------+
     *      |   |             |
     *      |   +------ j ----+
     *      |           |
     *      +---- k ----+
     */
    private fun test() {
        val time = System.currentTimeMillis()
        TaskCreator().apply {
            val map = TaskMap()
            map.add(a, Dispatchers.IO)
            map.add(b, Dispatchers.Main)
            map.add(c)
            map.add(d)
            map.add(i)
            map.add(e, a)
            map.add(f, b)
            map.add(h, c, d)
            map.add(g, e, h)
            map.add(j, f, h)
            map.add(k, Dispatchers.IO, g, j)
            println("hasCircularDepend = ${map.hasCircularDepend()}")
            lifecycle.coroutineScope.launch {
                val launcher = TaskLauncher.start(map)
                println("time = ${System.currentTimeMillis() - time}")
                launcher.await()
                println("time = ${System.currentTimeMillis() - time}")
            }
        }
    }
}