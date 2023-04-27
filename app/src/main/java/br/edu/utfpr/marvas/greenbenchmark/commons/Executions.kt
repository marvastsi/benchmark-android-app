package br.edu.utfpr.marvas.greenbenchmark.commons

import br.edu.utfpr.marvas.greenbenchmark.R
import br.edu.utfpr.marvas.greenbenchmark.data.model.Config
import kotlin.random.Random

typealias Scenario = Int

interface IExecution {
    fun hasNext(): Boolean
    fun next(): Scenario
    fun isRunning(): Boolean
    fun start()
    fun stop()
}

/**
 * Use
 * @sample br.edu.utfpr.marvas.greenbenchmark.commons.TestExecution.getInstance
 */
class TestExecution private constructor(private val config: Config) : IExecution {
    private var index: Int = 0
    private var running: Boolean = false
    private val executions: List<Scenario> = generateExecutions(config)

    override fun hasNext(): Boolean {
        return index < config.testLoad
    }

    override fun next(): Scenario {
        return executions[index++]
    }

    override fun isRunning(): Boolean = running

    override fun start() {
        running = true
    }

    override fun stop() {
        running = false
        instance = null
    }

    companion object {
        private val scenarios: Map<Int, Scenario> = mapOf(
            1 to R.id.action_ExecutionFragment_to_LoginFragment,
            2 to R.id.action_ExecutionFragment_to_AccountFragment,
            3 to R.id.action_ExecutionFragment_to_DownloadFragment,
            4 to R.id.action_ExecutionFragment_to_UploadFragment,
            5 to R.id.action_ExecutionFragment_to_MediaFragment
        )

        private var instance: IExecution? = null

        fun getInstance(config: Config): IExecution {
            if (instance == null)
                instance = TestExecution(config)

            return instance!!
        }

        private fun generateExecutions(config: Config): List<Scenario> {
            return if (config.specificScenario > 0) {
                val list = mutableListOf<Scenario>()
                for (i in 1..config.testLoad) {
                    scenarios[config.specificScenario]?.let { list.add(it) }
                }
                list
            } else
                randomList(config.testLoad)
        }

        private fun randomList(length: Int): List<Scenario> {
            val first = 1
            val last = 5
            val list = mutableListOf<Scenario>()
            for (i in 1..length) {
                val key = rand(first, last)
                scenarios[key]?.let { list.add(it) }
            }
            return list
        }

        private fun rand(first: Int, last: Int): Int {
            require(!(first > last || last - first + 1 > Int.MAX_VALUE)) { "Illegal Argument" }
            return Random(System.nanoTime()).nextInt(last - first + 1) + first
        }
    }
}