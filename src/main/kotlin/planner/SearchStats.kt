package planner

data class SearchStats(
    var expandedStates: Int = 0,
    var generatedStates: Int = 0,
    var duplicateStates: Int = 0,
    var maxQueueSize: Int = 0
)
