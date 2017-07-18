package spark

/**
  * Created by pk250187 on 6/6/17.
  */
class SparkAdaptiveConfiguration(val adaptiveEnabled: Boolean,
                                 val targetPostShuffleInputSize: Int,
                                 val minNumPostShufflePartitions: Int)
