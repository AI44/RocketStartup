package com.ideacarry.rocketstartup

import kotlin.math.sign

private const val REPEAT_TIME = 1000000

fun aTaskFunc() {
    val time = System.currentTimeMillis()
    Thread.sleep(5000)
    println("ATask - ${System.currentTimeMillis() - time} - ${Thread.currentThread().name}")
}

fun bTaskFunc() {
    val time = System.currentTimeMillis()
    Thread.sleep(500)
    println("BTask - ${System.currentTimeMillis() - time} - ${Thread.currentThread().name}")
}

fun cTaskFunc() {
    val time = System.currentTimeMillis()
    var num = 97.0
    repeat(REPEAT_TIME) {
        num = num * Math.random() / Math.PI
    }
    println("CTask${num.sign} - ${System.currentTimeMillis() - time} - ${Thread.currentThread().name}")
}

fun dTaskFunc() {
    val time = System.currentTimeMillis()
    var num = 87.0
    repeat(REPEAT_TIME) {
        num = num * Math.random() / Math.PI
    }
    println("DTask${num.sign} - ${System.currentTimeMillis() - time} - ${Thread.currentThread().name}")
}

fun eTaskFunc() {
    val time = System.currentTimeMillis()
    var num = 103.0
    repeat(REPEAT_TIME) {
        num = num * Math.random() / Math.PI
    }
    println("ETask${num.sign} - ${System.currentTimeMillis() - time} - ${Thread.currentThread().name}")
}

fun fTaskFunc() {
    val time = System.currentTimeMillis()
    var num = 71.0
    repeat(REPEAT_TIME) {
        num = num * Math.random() / Math.PI
    }
    println("FTask${num.toInt()} - ${System.currentTimeMillis() - time} - ${Thread.currentThread().name}")
}

fun gTaskFunc() {
    val time = System.currentTimeMillis()
    var num = 67.0
    repeat(REPEAT_TIME) {
        num = num * Math.random() / Math.PI
    }
    println("GTask${num.sign} - ${System.currentTimeMillis() - time} - ${Thread.currentThread().name}")
}

fun hTaskFunc() {
    val time = System.currentTimeMillis()
    Thread.sleep(1000)
    println("HTask - ${System.currentTimeMillis() - time} - ${Thread.currentThread().name}")
}

fun iTaskFunc() {
    val time = System.currentTimeMillis()
    var num = 51.0
    repeat(REPEAT_TIME) {
        num = num * Math.random() / Math.PI
    }
    println("ITask${num.toInt()} - ${System.currentTimeMillis() - time} - ${Thread.currentThread().name}")
}

fun jTaskFunc() {
    val time = System.currentTimeMillis()
    var num = 61.0
    repeat(REPEAT_TIME) {
        num = Math.PI * Math.random()
    }
    println("JTask${num.sign} - ${System.currentTimeMillis() - time} - ${Thread.currentThread().name}")
}

fun kTaskFunc() {
    val time = System.currentTimeMillis()
    var num = 71.0
    repeat(REPEAT_TIME) {
        num = Math.PI * Math.random()
    }
    println("KTask${num.sign} - ${System.currentTimeMillis() - time} - ${Thread.currentThread().name}")
}