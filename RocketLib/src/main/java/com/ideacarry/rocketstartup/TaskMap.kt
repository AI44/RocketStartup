package com.ideacarry.rocketstartup

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

/**
 * 存储task的有向图
 *
 * @author Raining
 * @date 2024-02-10 22:15
 */
class TaskMap {

    internal val nodeMap = mutableMapOf<Int, Node>()
    private val tagMap = mutableMapOf<String, Node>()
    internal val header = createHeaderNode()

    /**
     * @param task 目标任务
     * @param dispatcher 任务执行在哪个线程
     * @param priority 任务优先级，越小优先级越高
     * @param taskTag 任务tag，用于查询任务执行情况。注意：不能使用空字符串
     * @param depend 执行该任务需要依赖哪些任务
     */
    fun add(
        task: Task,
        dispatcher: CoroutineDispatcher = Dispatchers.Default,
        priority: Int = 0,
        taskTag: String = "",
        vararg depend: Task = emptyArray(),
    ): TaskMap {
        val newNode = getNode(task)?.apply {
            // 如果已存在则更新数据
            data.dispatcher = dispatcher
            data.priority = priority
            data.taskTag = taskTag
        } ?: createNode(task, dispatcher, priority, taskTag)
        if (depend.isEmpty()) {
            return this
        }
        // 移除默认节点依赖
        newNode.deleteDepend(header)
        // 添加依赖节点
        depend.forEach {
            newNode.depend(getOrCreateNode(it))
        }
        return this
    }

    /**
     * @see [TaskMap.add]
     */
    fun add(
        task: Task,
        dispatcher: CoroutineDispatcher,
        vararg depend: Task,
    ): TaskMap {
        return add(task = task, dispatcher = dispatcher, priority = 0, taskTag = "", depend = depend)
    }

    /**
     * @see [TaskMap.add]
     */
    fun add(
        task: Task,
        vararg depend: Task,
    ): TaskMap {
        return add(task = task, dispatcher = Dispatchers.Default, priority = 0, taskTag = "", depend = depend)
    }

    /**
     * @see [TaskMap.add]
     */
    fun add(
        task: Task,
        taskTag: String,
        vararg depend: Task,
    ): TaskMap {
        return add(task = task, dispatcher = Dispatchers.Default, priority = 0, taskTag = taskTag, depend = depend)
    }

    /**
     * clone一个所有节点的图
     */
    internal fun cloneNodeMap(): Map<Int, Node> {
        val newMap = nodeMap.mapValues { Node(it.value.data) }
        // 关系链复制
        nodeMap.forEach { (key, node) ->
            val dstNode = newMap[key] ?: return@forEach
            val inDegreeList = node.inDegree.mapNotNull { newMap[it.key()] }
            dstNode.inDegree.addAll(inDegreeList)
            val outDegreeList = node.outDegree.mapNotNull { newMap[it.key()] }
            dstNode.outDegree.addAll(outDegreeList)
        }
        return newMap
    }

    /**
     * 是否有循环依赖
     *
     * 思路：循环删除有向图入度为0的节点，直到不能删除为止，如果图中节点数为0则没有循环依赖，否则存在循环依赖
     */
    fun hasCircularDepend(): Boolean {
        val cloneMap = cloneNodeMap().toMutableMap()
        val cloneHeader = cloneMap[header.key()] ?: return false
        deleteZeroInDegreeNode(cloneMap, cloneHeader) {}
        return cloneMap.isNotEmpty()
    }

    internal fun getNode(taskTag: String): Node? {
        return tagMap[taskTag]
    }

    private fun getOrCreateNode(task: Task): Node {
        return getNode(task) ?: createNode(task)
    }

    private fun getNode(task: Task): Node? {
        return nodeMap[task.key()]
    }

    private fun getNode(taskInfo: TaskInfo): Node? {
        return getNode(taskInfo.task)
    }

    private fun createNode(
        task: Task,
        dispatcher: CoroutineDispatcher = Dispatchers.Default,
        priority: Int = 0,
        taskTag: String = "",
    ): Node {
        return Node(TaskInfo(task, dispatcher, priority, taskTag)).apply {
            if (taskTag.isNotEmpty()) {
                tagMap[taskTag] = this
            }
            nodeMap[key()] = this
            // 所有节点默认依赖header
            this.depend(header)
        }
    }

    private fun createHeaderNode(): Node {
        return Node(
            TaskInfo(
                task = object : Task() {
                    override fun run() {
                    }
                },
                dispatcher = Dispatchers.Default,
                priority = 0,
                taskTag = "",
            )
        ).apply {
            nodeMap[key()] = this
        }
    }
}

internal data class TaskInfo(
    val task: Task,
    var dispatcher: CoroutineDispatcher,
    var priority: Int,
    var taskTag: String,
)

internal class Node(
    val data: TaskInfo,
    val inDegree: MutableList<Node> = mutableListOf(), // 存放该节点的所有入度节点
    val outDegree: MutableList<Node> = mutableListOf(), // 存放该节点的所有出度节点
)

internal fun Task.key(): Int = hashCode()
internal fun TaskInfo.key(): Int = task.key()
internal fun Node.key(): Int = data.key()

private fun Node.depend(dst: Node) {
    if (this.inDegree.contains(dst).not()) {
        this.inDegree.add(dst)
    }
    if (dst.outDegree.contains(this).not()) {
        dst.outDegree.add(this)
    }
}

private fun Node.deleteDepend(dst: Node) {
    this.inDegree.remove(dst)
    dst.outDegree.remove(this)
}

/**
 * 递归删除所有入度为0的节点
 */
internal fun deleteZeroInDegreeNode(map: MutableMap<Int, Node>, node: Node, deleteFunc: (Int) -> Unit) {
    val key = node.key()
    if (node.inDegree.isNotEmpty() || map[key] == null) {
        return
    }
    map.remove(key)
    deleteFunc(key)
    node.outDegree.forEach {
        it.inDegree.remove(node)
        deleteZeroInDegreeNode(map, it, deleteFunc)
    }
}