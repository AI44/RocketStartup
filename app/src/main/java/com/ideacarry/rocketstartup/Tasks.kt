package com.ideacarry.rocketstartup

class ATask : Task() {
    override fun run() {
        aTaskFunc()
    }
}

class BTask : Task() {
    override fun run() {
        bTaskFunc()
    }
}

class CTask : Task() {
    override fun run() {
        cTaskFunc()
    }
}

class DTask : Task() {
    override fun run() {
        dTaskFunc()
    }
}

class ETask : Task() {
    override fun run() {
        eTaskFunc()
    }
}

class FTask : Task() {
    override fun run() {
        fTaskFunc()
    }
}

class GTask : Task() {
    override fun run() {
        gTaskFunc()
    }
}

class HTask : Task() {
    override fun run() {
        hTaskFunc()
    }
}

class ITask : Task() {
    override fun run() {
        iTaskFunc()
    }
}

class JTask : Task() {
    override fun run() {
        jTaskFunc()
    }
}

class KTask : Task() {
    override fun run() {
        kTaskFunc()
    }
}

class TaskCreator {
    val a = ATask()
    val b = BTask()
    val c = CTask()
    val d = DTask()
    val e = ETask()
    val f = FTask()
    val g = GTask()
    val h = HTask()
    val i = ITask()
    val j = JTask()
    val k = KTask()
}