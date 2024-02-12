package com.ideacarry.rocketstartup

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Job
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

/**
 * 用于执行任务
 *
 * @author Raining
 * @date 2024-02-10 22:20
 */
class TaskLauncher private constructor(
    private val taskMap: TaskMap
) {

    private val jobMap = mutableMapOf<Int, Job>()
    private val leafNodeList = mutableListOf<Node>() // 记录叶子节点

    private fun start(scope: CoroutineScope) {
        val cloneMap = taskMap.cloneNodeMap().toMutableMap()
        sortPriority(cloneMap)
        val cloneHeader = cloneMap[taskMap.header.key()] ?: throw RuntimeException("header not found.")
        // 按照依赖顺序构造job
        deleteZeroInDegreeNode(cloneMap, cloneHeader) { delKey ->
            val node = taskMap.nodeMap[delKey] ?: throw RuntimeException("node not found.")
            if (node.outDegree.isEmpty()) {
                leafNodeList.add(node)
            }
            val nodeJob = scope.launch(context = node.data.dispatcher, start = CoroutineStart.LAZY) {
                val jobList = node.inDegree.map { dependNode ->
                    jobMap[dependNode.key()] ?: throw RuntimeException("job not found.")
                }
                // 启动依赖task
                jobList.forEach { it.start() }
                // 等待依赖的task执行完成
                jobList.forEach { it.join() }
                // 最后执行目标任务
                node.data.task.run()
            }
            jobMap[node.key()] = nodeJob
        }
        // 检测是否存在循环依赖
        if (cloneMap.isNotEmpty()) {
            throw RuntimeException("exist circular depend.")
        }
        // 优先级排序
        sortPriority(leafNodeList)
        // 启动任务
        leafNodeList.forEach { node ->
            scope.launch {
                jobMap[node.key()]?.join()
            }
        }
    }

    /**
     * 优先级排序
     */
    private fun sortPriority(list: MutableList<Node>) {
        list.sortBy { it.data.priority }
    }

    private fun sortPriority(nodeMap: Map<Int, Node>) {
        nodeMap.forEach { (_, node) ->
            sortPriority(node.inDegree)
            sortPriority(node.outDegree)
        }
    }

    /**
     * 等待所有任务执行完成
     */
    suspend fun await() {
        leafNodeList.forEach { node ->
            jobMap[node.key()]?.join()
        }
    }

    /**
     * 等待某个/多个任务执行完成
     */
    suspend fun awaitTask(vararg taskTags: String) {
        taskTags.forEach { tag ->
            val key = taskMap.getNode(tag)?.key() ?: return@forEach
            jobMap[key]?.join()
        }
    }

    /**
     * 获取任务job
     */
    fun getTaskJob(taskTag: String): Job? {
        val key = taskMap.getNode(taskTag)?.key() ?: return null
        return jobMap[key]
    }

    /**
     * 查询任务是否完成
     */
    fun isTaskCompleted(vararg taskTags: String): Boolean {
        return taskTags.all { getTaskJob(it)?.isCompleted ?: false }
    }

    /**
     * 查询所有任务是否完成
     */
    fun isCompleted(): Boolean {
        return leafNodeList.all { jobMap[it.key()]?.isCompleted ?: false }
    }

    companion object {
        fun start(scope: CoroutineScope, map: TaskMap): TaskLauncher {
            return TaskLauncher(map).apply {
                this.start(scope)
            }
        }

        suspend fun start(map: TaskMap): TaskLauncher {
            return coroutineScope {
                start(this, map)
            }
        }
    }
}