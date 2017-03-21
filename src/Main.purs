module Main where

import Prelude
import Control.Monad.Eff (Eff)
import Control.Monad.Eff.Console (CONSOLE, log, logShow)
import GLMatrix (toRadians)

main :: forall e. Eff (console :: CONSOLE | e) Unit
main = do
  log "Hello soldier!"
  logShow (toRadians 360.0)
