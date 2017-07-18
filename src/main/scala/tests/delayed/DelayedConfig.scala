package tests.delayed

/**
  * Created by Petr Kubat on 4/23/17.
  */
abstract class DelayedConfig

case class SlowConfig() extends DelayedConfig
case class FastConfig() extends DelayedConfig
