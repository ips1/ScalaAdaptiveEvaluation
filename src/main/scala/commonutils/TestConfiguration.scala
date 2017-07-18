package commonutils

import scalaadaptive.core.configuration.BaseLongConfiguration
import scalaadaptive.core.configuration.blocks._
import scalaadaptive.core.configuration.blocks.logging.NoLogging
import scalaadaptive.core.configuration.blocks.persistence.BufferedPersistence
import scalaadaptive.core.configuration.blocks.selection.{LinearRegressionInputBasedStrategy, TTestMeanBasedStrategy}

/**
  * Created by Petr Kubat on 7/10/17.
  */
abstract class TestConfiguration extends BaseLongConfiguration
  with RunTimeMeasurement
  with LinearRegressionInputBasedStrategy
  with TTestMeanBasedStrategy
  with BufferedPersistence
  with NoLogging