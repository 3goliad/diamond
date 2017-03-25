module Main where

import Prelude
import Data.Array (replicate)
-- import Control.Monad.Eff.Random (random)


type Point = { x :: Number, y :: Number, z :: Number}
type Corner = { height :: Number }
type Edge = { start :: Point, end :: Point, heights :: Array Number }
type Tile = { center :: Point, heights :: Array (Array Number) }
data Chunk = Chunk { size :: Int
             , tiles :: Array (Array Tile)
             , corners :: Array (Array Corner)
             , edges :: Array (Array Edge) }

point :: Number -> Number -> Number -> Point
point x y z = {x, y, z}

generateTile :: Int -> Tile
generateTile size =
  { center: point 0.0 0.0 0.0
  , heights: replicate size (replicate size 0.0)
}

generateEdge :: Int -> Edge
generateEdge size =
  { start: point 0.0 0.0 0.0
  , heights: replicate (size - 2) 0.0
  , end: point 0.0 0.0 0.0
}

{-- generate :: Int -> Eff (random :: RANDOM) Chunk --}
{-- generate size = Chunk { size, tiles, corners, edges } --}
  {-- where --}
  {--   tiles = unfoldr (\size -> --} 
